package net.agileframes.core.forces;

import java.rmi.RemoteException;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.Ticket;
import java.io.Serializable;
import net.agileframes.forces.MachineIB;
import net.agileframes.traces.ActionIB;

/**
 * <b>Specification of the interaction between physical execution and the environment.</b>
 * <p>
 * A <code>Move</code> has two important parts:
 * <ul>
 * <li> Move-Script
 * <li> Manoeuvre
 * </ul>
 * <p>
 * The move-script describes the interaction with the scene. The interaction is event-oriented.
 * <p>
 * The Manoeuvre, as information object, specifies how to follow the desired trajectory
 * in the real time-domain.
 * <p>
 * <b>Communication:</b><br>
 * Signs, defined on the <code>Move</code>, are used to communicate with the SceneAction.
 * Flags will be raised by the Manoeuvre and can be watched by the Move.
 * <p>
 * <b>Extensions:</b><br>
 * This <code>Move</code> class provides the methods to be used by any move
 * The specific moves for specific environments should extend this class and
 * override the constructor and the <code>moveScript</code>.
 * @see Manoeuvre
 * @see net.agileframes.core.traces.Scene
 * @see net.agileframes.core.traces.SceneAction
 * @see Sign
 * @see Flag
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public abstract class Move extends ActionIB implements Cloneable, Serializable {
  /**
   * Standard Constructor which can be called by extensions.
   * @param transform   transform of this move
   */
  public Move(FuTransform transform) {
    this.transform = transform;
  }
  //------------------------ Attributes ---------------------------
  /** The manoeuvre associated with this move. To be defined in extension. */
  protected Manoeuvre manoeuvre = null;
  /** The tickets of this move, either to claim or to free. To be defined in {@link #run(Ticket[]) run}. */
  protected Ticket[] entryTickets;
  /** The signs of this move, to be raised at specific moments. To be defined in extension. */
  protected Sign[] signs;
  /** The transform used to position this move in the function space. To be defined in extension. */
  protected FuTransform transform;

  //------------------------ Methods ------------------------------
  /**
   * Let the system anticipate on the succeeding manoeuvre.
   * By calling this method, the next manoeuvre will be available in the
   * <code>ManoeuvreDriver</code> (it calls: {@link net.agileframes.forces.mfd.ManoeuvreDriver#prepare(Manoeuvre) ManoeuvreDriver.prepare}).
   * <p>
   * It is recommended to use this method if possible.
   * <p>
   * A <code>java.lang.NullPointerException</code> will occur if the
   * actor of this move is not set.
   * @see net.agileframes.forces.mfd.ManoeuvreDriver
   * @see Manoeuvre#updateCalculatedState(FuSpace,double,Manoeuvre)
   */
  public void prepare() {
    try { actor.getMachine().prepare(manoeuvre); }
    catch (Exception e) { e.printStackTrace(); }
  }

  /**
   * Starts the move with tickets by creating a new <code>Thread</code>.
   * The new <code>Thread</code> will run in {@link #moveScript() moveScript} and will die
   * when it comes out of it.
   * <p>
   * Note that normally this method is called in the {@link net.agileframes.core.traces.SceneAction#sceneActionScript() SceneAction-script}.
   * <p>
   * By calling this method, the manoeuvre of this move will be available in the
   * <code>ManoeuvreDriver</code> (it calls: {@link net.agileframes.forces.mfd.ManoeuvreDriver#begin(Manoeuvre) ManoeuvreDriver.prepare}).
   * The {@link #entryTickets entryTickets} will be set in this method.
   * <p>
   * A <code>java.lang.NullPointerException</code> will occur if the
   * actor of this move is not set.
   * @see net.agileframes.forces.mfd.ManoeuvreDriver
   * @see #moveScript()
   * @param tickets the tickets needed in this move
   */
  public void run(Ticket[] tickets) {
    try { actor.getMachine().begin(manoeuvre); }
    catch (Exception e) { e.printStackTrace(); }
    entryTickets = new Ticket[tickets.length];
    for (int i = 0; i < tickets.length; i++) { entryTickets[i] = tickets[i]; }
    Thread moveScriptThread = new Thread("MoveScriptThread@"+this.toString()){
      public void run() { moveScript(); }
    };
    moveScriptThread.start();
  }
  /**
   * Describes the event-oriented interaction with the scene.
   * Tickets and Signs are used for the interaction.
   * <p>
   * To execute the moveScript, {@link #run(Ticket[]) run} should be called
   * <p>
   * This method should be overloaded in the specific moves.
   * The <code>moveScript</code> is user-defined and specific for each move.
   */
  protected abstract void moveScript();

  /**
   * Blocks until specified flags are raised.
   * <p>
   * This method registers this <code>Move</code> as a listener with the
   * specified flags and will be notified when one of the specified flags is raised.
   * @see #watch(Flag)
   * @see Flag#addListener(Object)
   * @see Flag#isRaised()
   * @param flags the array of flags to be watched
   */
  protected synchronized void watch(Flag[] flags) {
    boolean raised = false;
    for (int i = 0; i < flags.length; i++) {
    	  flags[i].addListener(this);
      if (!raised) { raised = flags[i].isRaised();}
      }
    while (!raised) {
      try{ synchronized(this) { this.wait();  }  }
      catch (Exception e) {
        System.out.println("Exception in Move.watch():"+e.getMessage());
        e.printStackTrace();
      }
      for (int i = 0; i < flags.length; i++) { if (!raised) { raised = flags[i].isRaised();} }
    }
  }
  /**
   * Blocks until specified flag is raised.
   * <p>
   * This method registers this <code>Move</code> as a listener with the
   * specified flag and will be notified when the specified flag is raised.
   * @see #watch(Flag[])
   * @param flag the flag to be watched
   */
  protected void watch(Flag flag) { watch(new Flag[] {flag}); }
  /**
   * Creates a copy of this <code>Move</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @return  an object that is a copy of this <code>Move</code> object.
   */
  public Object clone() throws CloneNotSupportedException {
    Move clone = (Move)super.clone();
    clone.manoeuvre = (Manoeuvre)manoeuvre.clone();
    clone.signs = new Sign[signs.length];
    for (int i = 0; i < signs.length; i++) { clone.signs[i] = new Sign(); }
    clone.actor = actor;
    return clone;
  }
  /**
   * Sets the actor of this move.
   * <p>
   * Actor needs to be set after cloning at the side of the actor.
   * This Move probably will be cloned at the side of the Scene
   * and then send to a remote Actor. After being received by the actor,
   * the actor should be set on the Move.
   * <p>
   * Note that this method will be called by {@link net.agileframes.core.traces.SceneAction#setActor(Actor) SceneAction.setActor}.
   * @param actor the actor that owns this move
   */
  public void setActor(Actor actor) { this.actor = actor; }

  //-------------------------------- Getters ------------------------------------------------
  /**
   * Returns a reference to the indicated sign.
   * The signs of the move must be set in the constructor. A move can
   * also have zero signs.
   * <p>
   * If this method is called with an index which is out of bounds, a <code>java.lang.NullPointerException</code>
   * will be thrown.
   * @see     #signs
   * @param   index   the number of the sign
   * @return  a reference to the sign asked for
   */
  public Sign getSign(int index) { return signs[index]; }
  /**
   * Returns a reference to the manoeuvre of this move.
   * The manoeuvre of the move must be set in the constructor. A move must
   * contain a manoeuvre.
   * @see     #manoeuvre
   * @return  a reference to the manoeuvre
   */
  public Manoeuvre getManoeuvre() { return manoeuvre; }
}
