package net.agileframes.traces;

import net.agileframes.traces.SceneImplBase;
import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.brief.Brief;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Constraint;

import net.jini.core.lookup.ServiceID;

import java.rmi.RemoteException;

/**

 * Created: Wed Jan 12 13:02:18 2000
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1

Defines a trajectory through the state space together with a set of rules
that a machine must interpret/execute autonomously.

When a scene is created its constructor also creates instances of the the moves
that can be invoked by the scene-actions. These moves are called "scene-moves".
Scene-moves can not be executed, they are not associated with a mover which could
accept the moves trajectory and rules.

An actor is associated with a mover.
When an actor requests a scene-action the scene creates the appropriate instance
with a reference to the scene and the actor. Within the constructor the
scene-action aquires a reference to the mover via the actor and references to
the scene-moves via the scene. Of all the scene-moves (it needs) the scene-action
makes clones which it passes a reference to the mover, such moves are called
"real-moves".

Real-moves are thus associated with a mover which can accept its trajectory and rules.

When the scene-action thread executes the script it calls execute upon the
machine-moves, this results in the passing of the trajectory and the rules
defined within the move to the mover (via the move-space).
*/


public class MoveImplBase extends ActionImplBase implements Cloneable,Move,Action {

  /** called during scene construction */
  public MoveImplBase() {}

  public ServiceID serviceID; // only assigned to real-moves

  //////////////////////// clone //////////////////////////////////////

  public Object clone() {
    MoveImplBase clone = null;
    try {
      clone = (MoveImplBase)super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      System.exit(1);
    }
    clone.constraints = null;
    clone.rules = null;
    clone.trajectory = null;
    if (this.trajectory != null) {
      clone.trajectory = this.trajectory.getClone();
    }
    return clone;
  }

  /**
  Creates a machine-move out of a scene-move. This object is a scene-move
  @return a machine-move;
  */
  public MoveImplBase clone(Actor actor,Action superSceneAction) {
    MoveImplBase clone = (MoveImplBase)this.clone();
    clone.assimilate(actor,superSceneAction);
    return clone;
  }

  public void assimilate(Actor actor,Action superSceneAction) {
    this.initialize(actor,superSceneAction);
    this.createRules();
  }

  protected Action superSceneAction;

  public void initialize(Actor actor,Action superSceneAction) {
    this.actor = actor;
    this.superSceneAction = superSceneAction;
    this.serviceID = AgileSystem.getServiceID();
    setState(INITIALIZED);
  }

  ////////////////////////////////////////////////////////////////////

  protected int[] entryIndexes = null;
  protected Ticket[] entryTickets = null;
  protected Brief[] entryBriefs = null;

  protected int[] exitIndexes = null;
  protected Ticket[] exitTickets = null;
  protected Brief[] exitBriefs = null;

  ////////////////////////////////////////////////////////////////////

  /** note that the array-indexes are not adjusted */
  public synchronized void exec(int[] indexes,Ticket[] tickets,Brief[] briefs) {
    entryIndexes = indexes;
    entryTickets = tickets;
    entryBriefs = briefs;
    execute();
  }

  /**
   * Boolean that is true when the move already is notified.
   * This variable is needed if the move is notified before reaching its wait statement.
   */
  protected boolean notified = false;

  /**
   * Sends the move to the actor.
   * This method will wait until the move is notified (but maximal 30 seconds).
   */
  public synchronized void execute() {
    setState(EXECUTING);
    try{
      actor.acceptMove(this.serviceID,this.trajectory,this.rules,this.constraints);
    } catch(Exception e) {
      System.out.println("Error in Move.execute() part 1:"+e.getMessage());
      e.printStackTrace();
    }
    try{
      long time=System.currentTimeMillis();
      if (!notified) {
        wait(60*1000);
      }
      notified = false;
      if (System.currentTimeMillis()-time>59*1000) {
        System.out.println(this.actor.toString()+"  waited for 30 seconds to be notified...Now he will continue anyway");
      }
    } catch(Exception e) {
      System.out.println("Error in Move.execute() part 2:"+e.getMessage());
      e.printStackTrace();
    }
  }

  ////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////

  /**
   * Clears the move.
   * All trajectories, briefs, indexes, tickets and rules are deleted.
   * New rules are created by calling the method creatRules.
   */
  public void reset(){
    trajectory.reset();
    this.entryBriefs = null;
    this.entryIndexes = null;
    this.entryTickets = null;
    this.notified = false;

    this.rules = null;
    this.createRules();
  }

  /**
  The maneuver that the machine must execute, it is described as a trajectoty.
  */
  public Trajectory trajectory;

  /** rules associated with this move */
  public Rule[] rules;

  /** constraints associated with this move */
  public Constraint[] constraints;

  //////////////////////////////////////////////////////////////////////////

  /**
  Overload if you use move.EvolutionRules
  */
  public void event(int eventID,int seqNum) { System.out.println("notify by MoveImplBase");synchronized(this) { this.notify(); }  }

  /**
   * Overload in Moves
   */
  public void createRules() {}


}


