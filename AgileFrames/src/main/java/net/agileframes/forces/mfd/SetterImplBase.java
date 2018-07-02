package net.agileframes.forces.mfd;
import net.agileframes.core.forces.State;
import net.agileframes.forces.Setter;
import net.agileframes.forces.MachineImplBase;

/*
Abstract base-class for Setter implementations.
Is responsible for setting the machines control in order to achieve the machine step.
In general this would involve the use of a (adaptable) machine response model

Default implementation is null.
*/

public class SetterImplBase implements Setter {

  protected MachineImplBase machine = null;

  public SetterImplBase(MachineImplBase machine) {
    this.machine = machine;
  }

  ////////////// implementation of Setter //////////////////////////

  /**
  Remember predicted responseValues in order to be able to adapt.
  */
  public float[] computeControls(State machineStep) {
    // using the current responsemodel, compute the settings of the machine.
    // in case steering occurs with servos then this is too elaborate
    // iff you want a certail wheel setting, thats what you get.
    // There is no question about response.
    // In case your wheels are powered by too powerfull electric motors than again,
    // there is no question abour response, ask and get immediately.
    return null;
  }

  /**
  Compare predicted responseValues with realizsed responsValues and adapt the response-model.
  */
  public float[] adaptResponseModel(float[] responseValues) {
    // the forces document says stuff about response-model in
    // section 4.5 deduction on internal instruction on speed
    // and how to use the responsevalues to adapt.
    return null;
  }

  public void setControls(float[] controls) {
     System.out.println("SetterImplBase.setControls is a generic method, should be overloaded");
  }


}