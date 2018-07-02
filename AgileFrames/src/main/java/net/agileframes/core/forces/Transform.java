package net.agileframes.core.forces;
import net.agileframes.forces.space.Position;
import java.io.Serializable;

/**
Class that implements transformations of a state-space.
I suppos you could consider these to be matrix implementing linear transformations.
Such as translation, rotation, and scaling.

 * Created: Wed Jan 12 13:02:18 2000
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1

*/

public abstract class Transform implements Serializable {

  public Transform(){}

  /**
  Computes the corresponding  state in the transformed state-space.
  */
  public abstract State transform(State state);

  /**
  Adds a transform to this transform. The result is a transform that is
  equivalent to that.transform(transform(this.transform(state)))
  */
  public abstract Transform add(Transform transform);
}