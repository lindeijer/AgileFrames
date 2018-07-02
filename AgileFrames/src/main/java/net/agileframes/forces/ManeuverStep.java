package net.agileframes.forces;
import net.agileframes.core.forces.State;

public class ManeuverStep {

  public ManeuverStep() {}

  /** the state one evolution-unit ahead (state.u = machine.g.u+1) */
  public State state = new State();
  /** evolution-velocity satisfying the constraints of the maneuver-script */
  public float referenceVelocity;
  /** evolution-accelleration satisfying the constraints of the maneuver-script */
  public float referenceAccelleration;

}