package net.agileframes.forces;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.State;
import java.text.DecimalFormat;
import net.agileframes.server.AgileSystem;

public class TrajectoryState {
  public TrajectoryState() {}
  public Trajectory trajectory = null;
  public State state = null;  // contains u
  public float distance = Float.NaN;
  public float velocity = Float.NaN;      // can not be negative
  public float accelleration = Float.NaN; // can not be negative

  public int errorCost = 0;

  // DecimalFormat-declaraties zijn naar binnen de methode verschoven.
  // DecimalFormats blijken veel rekentijd te kosten (40% van de rekentijd
  // bij opstarten werd gebruikt voor het maken van DecimalFormats)

  public String toString() {
    DecimalFormat dotTwo = AgileSystem.dotTwo;
    DecimalFormat dotThree = AgileSystem.dotThree;
    return "[state=" + state.toString()             +   "\n     " +
            " dist="  + distance      +
            " vel="   + velocity      +
            " acc="   + accelleration +
            " cost="  + errorCost + "]";


  }


}