package net.agileframes.traces;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.SemaphoreRemote;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.forces.JoinMove;
import net.agileframes.forces.flags.FinishedFlag;
import net.agileframes.forces.precautions.TimedStop;
import net.agileframes.core.forces.Precaution;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.forces.xyaspace.trajectories.GoStraight;
/**
 * <b>Pre-defined SceneAction to join a Scene.</b>
 * <p>
 * Drives to the closest Logistic Position. To be used when an actor starts
 * up and has to enter a Scene (important: to claim the Semaphore).
 * If this SceneAction is executed while the Actor is already on a Semaphore,
 * the only result of this SceneAction is that the Semaphore will be claimed.<br>
 * After executing this SceneAction, other SceneActions can be executed.
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class JoinSceneAction extends SceneAction {
  //-- Attributes --
  LogisticPosition lp;
  Scene scene;
  SemaphoreRemote semaphore;
  FuSpace location;

  //-- Constructor --
  /**
   * Default Constructor.<p>
   * Creates tickets, (join-)moves and signs.
   * @param lp  the logistic position to which the actor wants to join.
   */
  public JoinSceneAction(LogisticPosition lp) throws java.rmi.RemoteException {
    this.lp = lp;
    this.scene = lp.scene;
    this.semaphore = lp.semaphore;
    this.location = lp.location;

    this.tickets = new Ticket[1];
    tickets[0] = new PrimeTicket(this, semaphore);

    this.moves = new Move[2];
    moves[0] = new JoinMove(new XYATransform(0,0,0), location, 0);
    moves[1] = new JoinMove(new XYATransform(0,0,0), location, 1);

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  /** Empty Initializer. */
  protected void initialize() {
  }

  /** The Script. */
  protected void sceneActionScript() {
    try {
      //System.out.println("JOINSA: about to insist");
      tickets[0].insist();
      //System.out.println("JOINSA: insist done");
      double dev = Double.MAX_VALUE;
      do {

        Move move0 = (Move)moves[0].clone();
        Move move1 = (Move)moves[1].clone();
        move0.run(new Ticket[] {} );
        //System.out.println("JOINSA: move 0 running");
          watch(move0.getSign(0));
        //System.out.println("JOINSA: mov0.sign 0 received");
        move1.run(new Ticket[] {} );
        //System.out.println("JOINSA: move 1 running");
        watch(move1.getSign(0));
        //System.out.println("JOINSA: mov1.sign 0 received");
        dev = move1.getManoeuvre().getCalcDeviation();
        System.out.println("JoinSceneAction.sceneActionScript: dev="+dev);
      } while (dev > 2);
    } catch (Exception e) {
      System.out.println("Exception in JoinSceneAction: The scene-action will be aborted.");
      e.printStackTrace();
    }
    finish(tickets[0]);
    signs[0].broadcast();
    //System.out.println("JOINSA: action done!");
  }
  
  public String toString() {
	  return "JoinSceneAction[lp="+lp+"]";
  }

}

