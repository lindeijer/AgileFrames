package net.agileframes.forces;
//import net.agileframes.core.forces.State;

/**
An implementation of this interface must deduce the controls/mechatronic-instructions
according to the machine-step. Furthermore, the machine-step should be achieved at the
time indicated by the machine-step.
<p>
The indicated time will be the time the next machine-step is expected to have been computed.
In a cyclic-threading-context this time is expected to be now+cycleTime.
<p>
Well, what can one say about threading. Not much so the implementation of this interface
must take into account illogical method invocation sequences. This is a good idea in general
but illogical machine-software-threading is not. Maneuver-programming is simple upto
a certain level then its simply becomes complicated. Therefore I do not believe that the threading should be
considered to be within the domain of maneuver programming and it need not be.
*/

public interface MechatronicsDriver {

  /**
  Sets the next machine-step which must be achieved.
  The machine-step is computed by the Interpreter.
  @param machineStep to be achieved at machineStep.time
  @see Interpreter.getMachineStep
  */
//  public void setMachineStep(State machineStep);

  /**
  Gets the new response-model according to observed machine responses.
  The instructions for the mechatronic interface/module of the machine
  are computed using a response model of the machine, intermediate results of this computation
  are predicted response values. By comparing the predicted response values with
  observed response values a new response model is inferred.
  @param responseValues the observed machine responses.
  @see StateFinder.getResponseInformation
  */
  public void setResponseInformation(float[] responseInformation);

  /**
  Gets the mechatronic instructions deduced for the mechatronic interface/module of the machine.
  @return machatronic instructions, these values are to be set on the machatronic-interface.
  @see Mechatronics.setControls()
  */
  public float[] getMechatronicInstructions();


  /**
  Deduces the controls/mechatronic-instructions for the mechatronic interface/module of
  the machine according to the machine-step and the machine response-model.
  The invocation of this method should be preceded by setMachineStep and setResponseInformation,
  and followed by getMechatronicInstructions.
  */
  public void deduceControls();
} 