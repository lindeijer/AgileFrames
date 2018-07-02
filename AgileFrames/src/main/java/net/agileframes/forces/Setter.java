package net.agileframes.forces;
import net.agileframes.core.forces.State;

/*
Is responsible for setting the machines control in order to achieve the machine step.
In general this would involve the use of a (adaptable) machine response model
*/

public interface Setter {

  /**
  Remember predicted responseValues in order to be able to adapt.
  @deprecated, use MechatronicsDriver.getControls
  */
  public float[] computeControls(State machineStep);

  /**
  Compare predicted responseValues with realizsed responsValues and adapt the response-model.
  @deprecated, use MechatronicsDriver.getResponseModel
  */
  public float[] adaptResponseModel(float[] responseValues);


  /**
  Remember predicted responseValues in order to be able to adapt.
  @deprecated, use MechatronicsDriver.getControls
  */
  public void setControls(float[] controls);

}


