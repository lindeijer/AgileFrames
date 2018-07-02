package net.agileframes.forces.mfd;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Rule;
//import net.agileframes.core.traces.Move;
import net.jini.core.entry.Entry;
import net.agileframes.core.forces.Constraint;
import net.agileframes.core.forces.Flag;

import net.agileframes.server.AgileSystem;
//import net.agileframes.core.forces.State;
//import net.agileframes.core.forces.Trajectory;
import net.agileframes.brief.MoveBrief;
//import net.agileframes.core.forces.Rule;
import net.agileframes.brief.BooleanBrief;
//import net.jini.space.JavaSpace;
//import net.jini.core.lookup.ServiceID;
//import com.sun.jini.lookup.JoinManager;
//import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;
//import java.io.IOException;
//import java.rmi.RemoteException;
//import net.jini.core.transaction.TransactionException;
//import net.jini.core.entry.UnusableEntryException;
//import com.sun.jini.lease.LeaseRenewalManager;
//import net.jini.core.lookup.ServiceItem;
import net.agileframes.server.ServerImplBase;
import net.agileframes.brief.MoveSpace;
import net.agileframes.forces.space.POS;
import net.agileframes.forces.MoveInterpreter;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.forces.TrajectoryState;
import net.agileframes.forces.ManeuverStep;

//import net.agileframes.forces.Satisfyer.Satisfaction;
// import net.agileframes.forces.constraint.Satisfyer;
import net.jini.core.lookup.ServiceID;
//import net.agileframes.core.forces.Machine.NotTrustedException;

import net.agileframes.forces.space.POS;



/**
Abstract base-class for MoveInterpreter implementations.
Uploads the Actor, the thread takes/accepts trajectories out of the movespace.

An AGV uploads an Actor (serialized interface) to the JLS,
the Actor service may be used to send the AGV trajectories and rules,
or rather maneuvers, for execution. An AGV-Actor is associated with the following attributes:
<ul>
<li>a Name attribute containing the name of the AGV
<li>a Name attribute containing the name of the loginbase
<li>a GUI for human interaction with the Actor service
</ul>

Default implementation :
<ul>
<li>uploads the actor to the loginbase, the actor sends back moves to the space.
<li>starts a thread to retrieve moves from the space, assumes they are coherent.
<li>interpretMove consists of a set of sub-methods:
  <ul>
  <li>interpretTrajectory: computes the move-step
  <li>evaluateRules: evaluates the rules defined in the context of the manueverand executes their associated actions if they become true.
  <li>satisfyRules: computes a velocity satisfying the constaints. This task is delegated to the satisfyer.
  </ul>
<li>The order in which these sub-methods are executed is not specified, but only once every cycle.
</ul>
*/

public class MoveInterpreterImplBase implements MoveInterpreter {

  protected MachineImplBase machine = null;

  public MoveInterpreterImplBase(MachineImplBase machine) {
    // super(machine.name); // we need our own serviceID
    this.machine = machine;
    // this.moveGetter = new MoveGetter(machine,machine.driverID,machine.actorID);
  }

  ///////////////////////////////////////////////////////////////////////////
  /////////////// implementation of MoveInterpreter /////////////////////////
  ///////////////////////////////////////////////////////////////////////////

  private State maneuverPilot;
  private State maneuverStepState;

  /**
  Interpret the machines current moves wrt current functional state g. This implies
  interpreting the trajectories and the evaluation the rules.
  @param g the current functional state
  @return move-step, the interpretation of the trjajectory = desired functional state one unit=meter up (to) the trajectory.
  */
  public State interpretMove(Trajectory trajectory,TrajectoryState trajectoryState,State g) {
    State pilot            = computeManeuverPilot(trajectory,trajectoryState);
    if (pilot != null) {
      maneuverPilot = pilot;
      //System.out.println("    MoveInterpreterImplBase.interpretMove, maneuverPilot="+maneuverPilot.toString());
      maneuverStepState      = computeManeuverStepState(g,maneuverPilot);
    }
    else {
      maneuverStepState      = computeManeuverStepStateExtrapolated(g,maneuverPilot);
      //System.out.println("    MoveInterpreterImplBase.interpretMoveEXTR, maneuverStepState="+maneuverStepState.toString());

    }
    //System.out.println(" ");
    return maneuverStepState;
  }

  private float alpha = 1.5f;
  private float beta = 1.0f;

  /*
  Should do what the forces document states, for now it returns F(u+1).
  @return pilot
  */
  public State computeManeuverPilot(Trajectory trajectory,TrajectoryState trajectoryState) {
    float evolution = trajectoryState.state.u;
    //System.out.println("    MoveInterpreterImplBase.computeManeuverPilot, trajectoryState.state.u="+trajectoryState.state.u);
    float pilotEvolution = evolution + alpha * trajectoryState.distance + beta;
    //System.out.println("    MoveInterpreterImplBase.computeManeuverPilot, pilot evolution="+pilotEvolution);
    State pilotContainer = maneuverPilot;
    State pilot = trajectory.compute(pilotEvolution,pilotContainer);
    // System.out.println("    MoveInterpreterImplBase.computeManeuverPilot, pilot="+pilot.toString());
    return pilot;
  }

  /*
  Should do what the forces document states.
  At this time it does not interpret driving reverse correctly.
  @return moveStep
  */
  public State computeManeuverStepState(State g,State pilot) {
    //System.out.println("    MoveInterpreterImplBase.computeManeuverStepState:");
    POS G = (POS)g;
    POS P = (POS)pilot;
    double dx = P.x - G.x;
    double dy = P.y - G.y;
    double gamma = Math.atan( Math.abs(dy)/Math.abs(dx) );
    int signX = 1;
    int signY = 1;
    if (dx < 0) signX = -1;
    if (dy < 0) signY = -1;
    double x = G.x + signX*Math.cos(gamma);
    double y =  G.y + signY*Math.sin(gamma);
    P.x = (float)x;
    P.y = (float)y;
    P.alpha = adjustPilotAlphaToMachineAlpha(P.alpha,G.alpha);
    /*
    double alpha = Float.NaN;
    if ( (dx>=0.0) && (dy>=0.0) ) { alpha = gamma;           }  // 1e
    if ( (dx< 0.0) && (dy>=0.0) ) { alpha = Math.PI - gamma; }  // 2e
    if ( (dx< 0.0) && (dy< 0.0) ) { alpha = Math.PI + gamma; }  // 3e
    if ( (dx>=0.0) && (dy< 0.0) ) { alpha = - gamma;         }  // 4e
    */
    P.alpha = (P.alpha + G.alpha)/2;
    // thanks to inforcement
    return P; // P has become the maneuverstepstate
  }

  /**
  The machine is symmetric, pilot-alpha may +- Math.PI to be better oriented wrt to machine.
  */
  private double  adjustPilotAlphaToMachineAlpha(double pilotAlpha,double machineAlpha) {
    // double diff_alperence



    while (pilotAlpha >  Math.PI) { pilotAlpha = pilotAlpha - 2*Math.PI; }
    while (pilotAlpha < -Math.PI) { pilotAlpha = pilotAlpha + 2*Math.PI; }
    System.out.println("    MoveInterpreterImplBase.ADJUST(1): pilotAlpha=" +pilotAlpha+ " machineAlpha=" + machineAlpha);
    double initial_alpha_diff = Math.abs(Math.abs(pilotAlpha) - Math.abs(machineAlpha));
    double reverse_pilot_alpha;
    if (pilotAlpha > 0) {
      reverse_pilot_alpha = pilotAlpha - Math.PI;
    } else {
      reverse_pilot_alpha = pilotAlpha + Math.PI;
    }
    double reverse_alpha_diff = Math.abs(Math.abs(reverse_pilot_alpha) - Math.abs(machineAlpha));
    if (reverse_alpha_diff < initial_alpha_diff) {
      pilotAlpha = reverse_pilot_alpha;
    }
    System.out.println("    MoveInterpreterImplBase.ADJUST(2): pilotAlpha=" +pilotAlpha+ " machineAlpha=" + machineAlpha);
    return pilotAlpha;
  }

  //////////////////////////////////////////////////////////////////

  /*
  @return moveStep
  */
  public State computeManeuverStepStateExtrapolated(State g,State pilot) {
    //System.out.println("    MoveInterpreterImplBase.computeManeuverStepStateExtrapolated:");
    if (pilot == null) { return g; }
    POS G = (POS)g;
    POS P = (POS)pilot;
    double dx = P.x - G.x;
    double dy = P.y - G.y;
    double gamma = Math.atan( Math.abs(dy)/Math.abs(dx) );
    double alpha = Float.NaN;
    if ( (dx>=0.0) && (dy>=0.0) ) { alpha = gamma;           }  // 1e
    if ( (dx< 0.0) && (dy>=0.0) ) { alpha = Math.PI - gamma; }  // 2e
    if ( (dx< 0.0) && (dy< 0.0) ) { alpha = Math.PI + gamma; }  // 3e
    if ( (dx>=0.0) && (dy< 0.0) ) { alpha = - gamma;         }  // 4e
    int signX = 1;
    int signY = 1;
    if (dx < 0) signX = -1;
    if (dy < 0) signY = -1;
    double facor = 1/Math.sqrt(dx*dx + dy*dy);
    double x = G.x + signX*facor*Math.cos(gamma);
    double y =  G.y + signY*facor*Math.sin(gamma);
    return new POS((float)x,(float)y,alpha);
  }


}