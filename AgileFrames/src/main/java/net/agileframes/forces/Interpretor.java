package net.agileframes.forces;
import net.agileframes.core.forces.State;

/**
An implementation of this interface must deduce the machine-step
according to the move-step which is the functional state one evolution-unit (1 meter)
ahead from the current evolution. Furthermore, the move-step indicates the desired
velocity and accelleration.
<p>
The machine-step first takes the desired speed and accelleration and the informaltion
about the local environment and computes the actual speed and accelleration that must be used.
Then the time must be chosen/computed/select when the machine-step which is going to be computed
must be achieved, and then the machine-step must be computed.
<p>
Well, what can one say about threading. Not much so the implementation of this interface
must take into account illogical method invocation sequences. This is a good idea in general
but illogical machine-software-threading is not. Maneuver-programming is simple upto
a certain level then its simply becomes complicated. Therefore I do not believe that the threading should be
considered to be within the domain of maneuver programming and it need not be.
*/

public interface Interpretor {

  /**
  Sets the next move-step which must be achieved within one evolution-unit
  The mave-step is computed by the MoveDriver.
  @param machineStep to be achieved at machineStep.time
  @see Interpreter.getMachineStep
  */
  public void setMoveStep(State moveStep);

  /**
  Sets the local environmental information, this information is used by the Interpretor
  to compute the advised speed and accelleration with which the computed machine-step
  must be realized.
  @param localInfo a set of objects, every object is a statement about an external object.
    Such a statement should be the approximate distance and approximate speed.
  @see StateFinder.getLocalInfo
  */
  public void setLocalInfo(Object[] localInfo);

  /**
  Sets the local environmental information, this information is used by the Interpretor
  to compute the advised speed and accelleration with which the computed machine-step
  must be realized.
  @param localInfo a set of objects, every object is a statement about an external object.
    Such a statement should be the approximate distance and approximate speed.
  @return the machine-step
  @see MechatronicsDriver.setMachineStep
  */
  public State getMachineStep();

  /**
  This method invocation should be preceded by setMoveStep and setLocalInfo,
  and followed by getMachineStep.
  */
  public void deduceMachineStep();

} 