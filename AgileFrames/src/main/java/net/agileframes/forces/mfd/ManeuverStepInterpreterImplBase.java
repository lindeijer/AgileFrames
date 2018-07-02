package net.agileframes.forces.mfd;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Rule;
import net.agileframes.forces.ManeuverStepInterpreter;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.forces.ManeuverStep;

/**
Abstract base-class for MoveStepInterpreter implementations.
Is responsible for interpreting the moveStep wrt: i) g=currentState and ii)
environmental information=external obstacles and speed directives.
The interpretation results in directives on the course and the execution speed.

Default implementation :
<ul>
<li>associates itself with the machineImplBase, thats it.
<li>all methods are null
</ul>

*/
public class ManeuverStepInterpreterImplBase implements ManeuverStepInterpreter {

  protected MachineImplBase machine = null;

  public ManeuverStepInterpreterImplBase(MachineImplBase machine) {
    this.machine = machine;
  }

  ///////////////////////////////////////////////////////////////////////////
  /////////////// implementation of MoveStepInterpreter /////////////////////
  ///////////////////////////////////////////////////////////////////////////

  /**
  @param moveStep, the desired functional state one unit=meter up (to) the trajectory from g=currentState.
  @param envInfo = environmentalInformation, information about external entities that are to be avoided (possibly the end of the trajectory).
  @return object containing directives on course and execution speed :: the translateMoveStep object + advised accelleration.
  */
  public State interpretManeuverStep(ManeuverStep maneuverStep,Object[] environmentInformation) {
    System.out.println("      ManeuverStepInterpreterImplBase.interpretManeuverStep not implemented");
    return null;
  }

}