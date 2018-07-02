package net.agileframes.forces;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Rule;

/**
Is responsible for interpreting the moveStep wrt: i) g=currentState and ii)
environmental information=external obstacles and speed directives,
CURRENT NAME = Interpreter.
The interpretation results in directives on the course and the execution speed.
*/
public interface ManeuverStepInterpreter {

  /**
  @param moveStep, the desired functional state one unit=meter up (to) the trajectory from g=currentState.
  @param speedAtRules = environmentalInformation, information about external entities that are to be avoided (possibly the end of the trajectory).
  @return object containing directives on course and execution speed :: the translateMoveStep object + advised accelleration.
  @deprecated, use Interpretor
  */
  public State interpretManeuverStep(ManeuverStep maneuverStep,Object[] environmentInformation);

}

