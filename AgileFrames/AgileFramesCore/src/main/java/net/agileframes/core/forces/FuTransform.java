package net.agileframes.core.forces;
import java.io.Serializable;
/**
 * <b>The Transformation for the Function Space.</b>
 * <p>
 * This object is used to transform a point in the {@link FuSpace function-space} to
 * another point in the same space.
 * Note that this object should be extended to implement the methods in a certain
 * space (for example: 2DSpace, 3DSpace, XYSpace, XYASpace, etc.).
 * <p>
 * Abstract class.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public abstract class FuTransform implements Cloneable, Serializable {
  /**
   * Calculates the transformed point.<p>
   * The current transform is used to calculate the transformed
   * form of a point.
   * <p>
   * Use {@link #inverseTransform(FuSpace) inverseTransform} to do this calculation backwards.
   * @param   point   the state of which the transform will be calculated
   * @return  the transformed state using this <code>FuTransform</code>
   */
  public abstract FuSpace transform (FuSpace point);
  /**
   * Calculates the original of a transformed point.<p>
   * The current transform is used to calculate the original
   * form of a transformed point.
   * <p>
   * This calculation is the inverse of {@link #transform(FuSpace) transform}.
   * @param   point   the state of which the original will be calculated
   * @return  the original state using this <code>FuTransform</code>
   */
  public abstract FuSpace inverseTransform (FuSpace point);
  /**
   * Calculates the concatenation of two transforms.
   * The result of this calculation is the transform being formed by adding two
   * transforms.
   * <p>
   * <b>Example:</b><br>
   * Say we have a zero-state and 2 transforms:<br>
   * <code>FuSpace p0; FuTransform t1, t2;</code><p>
   * Now look at the next calculation:<br>
   * <code>FuSpace p1 = p0.transform(t1);</code><br>
   * <code>FuSpace p2 = p1.transform(t2);</code><p>
   * This is the same as:<br>
   * <code>FuTransform t3 = t1.transformT1T2(t1, t2);</code><br>
   * <code>FuSpace p2 = p1.transform(t3);</code><br>
   * @param   transform1  the first transform
   * @param   transform2  the second transform (that will be concatenated with the first)
   * @return  the result of adding <code>transform2</code> to <code>transform1</code>
   */
  public abstract FuTransform transformT1T2 (FuTransform transform1, FuTransform transform2);
  /**
   * Creates a copy of this object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @return  an object that is a copy of this <code>FuTransform</code> object.
   */
  public Object clone() throws CloneNotSupportedException { return super.clone(); }
}
