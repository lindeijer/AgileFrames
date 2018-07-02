package net.agileframes.forces.constraint;
//import net.agileframes.core.forces.Rule;
//import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
//import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Flag;


/**
 * Created: Mod Feb 7 13:02:18 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

 at the horizon the speed must be zero
 true for trajectories : du/dt = 0 !!
*/

public final class SafetyConstraint extends VelocityConstraint {

  public SafetyConstraint() {}

  public SafetyConstraint(Trajectory trajectory,float evolution) {
    super(trajectory,evolution,Flag.GREATER_EQUAL,0.0f,Flag.EQUALS);
  }

  public SafetyConstraint(Trajectory trajectory) {
    super(trajectory,trajectory.domain,Flag.GREATER_EQUAL,0.0f,Flag.EQUALS);
  }

  public String toString() {
    return "SafetyConstraint for " + this.trajectory.toString() + " with (absolute) evolution=" + this.getEvolution();
  }

}



