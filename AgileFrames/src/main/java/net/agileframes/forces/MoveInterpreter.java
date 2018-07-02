package net.agileframes.forces;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Rule;
import net.agileframes.brief.MoveBrief;
import net.agileframes.brief.BooleanBrief;
import net.agileframes.core.forces.Constraint;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Machine.NotTrustedException;
import net.agileframes.core.forces.Flag;

/**
Is resonsible for accepting trajectories and rules passed to the machine as moves to execute,
CURRENT NAME = MoveDriver.
Is responsible for interpreting the the trajectories specified in the moves wrt g
Is responsible for evaluating the rules wrt g and executing their associated handlers if necessary.
*/
public interface MoveInterpreter {

  /**
  Interpret the machines current moves wrt current functional state g.
  First interpretTrajectory to get the move-step state.
  Second compute a scriptSpeed and scriptEvolution according to the rules
  @param g the current functional state
  @return move-step, the desired functional state one unit=meter up (to) the trajectory.
  */
  public State interpretMove(Trajectory trajectory,TrajectoryState trajectoryState,State g); // interpreting results in dynamic behaviour known as the menuever

}