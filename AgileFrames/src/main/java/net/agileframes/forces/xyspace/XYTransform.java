package net.agileframes.forces.xyspace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyspace.XYSpace;
/**
 * <b>Implementation of FuTransform in the X-Y Space.</b>
 * <p>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class XYTransform extends FuTransform {
  //--------------------- Attributes --------------------------------------
  private double sinR = Double.NaN;
  private double cosR = Double.NaN;
  private double xTransl = Double.NaN;
  private double yTransl = Double.NaN;
  private double rotation = Double.NaN;
  //--------------------- Constructors ------------------------------------
  /**
   * Creates an XYTransform that is a copy of an existing XYTransform.<p>
   * @param xyTranform the transform that will be copied
   */
  public XYTransform(XYTransform xyTransform) {
    this(xyTransform.xTransl, xyTransform.yTransl, xyTransform.rotation);
  }
  /**
   * Creates an XYTransform object.<p>
   * Sets the translation and rotation parameters and calculates the
   * sin(rotation) and cos(rotation) to be used later on.
   * @param xTransl     the x-translation of this transform
   * @param yTransl     the y-translation of this transform
   * @param rotation    the rotation of this transform
   */
  public XYTransform(double xTransl, double yTransl, double rotation) {
    this.rotation = rotation;
    this.sinR = Math.sin(rotation);
    this.cosR = Math.cos(rotation);
    this.xTransl = xTransl;
    this.yTransl = yTransl;
  }
  /**
   * Creates an XYTransform object using an XYSpace object.<p>
   * The x and y coordinates of the XYSpace-object will be used creating
   * an XYTransform.
   * @param translation the point to create a transform to
   * @param rotation    the rotation of that point
   */
  public XYTransform(XYSpace translation, double rotation) {
    this(translation.getX(), translation.getY(), rotation);
  }
  /**
   * Creates an XYTransform concatenating two other XYTransforms.<p>
   * The created transform will be the the second transform added to the first.
   * @see   #transformT1T2(XYTransform, XYTransform)
   * @param xyTransform1 the first transform
   * @param xyTransform2 the second transform (that will be concatenated with the first)
   */
  public XYTransform(XYTransform xyTransform1, XYTransform xyTransform2) {
    this(XYTransform.transformT1T2(xyTransform1, xyTransform2));
  }

  //--------------------- Methods -----------------------------------------
  /**
   * Calculates the transformed point.
   * The current transform is used to calculate the transformed
   * form of a point.<p>
   * Calculation:
   * <blockquote><code>
   * x = p.x * cos(rotation) - p.y * sin(rotation) + xTrans<br>
   * y = p.x * sin(rotation) + p.y * cos(rotation) + yTrans
   * </blockquote></code>
   * @param   point   the state of which the transform will be calculated
   * @return  the transformed state using this <code>Transform</code>
   */
  public FuSpace transform (FuSpace point) {
    if (point == null) { return null; }
    XYSpace xyPoint = (XYSpace) point;
    double x = xyPoint.getX() * cosR - xyPoint.getY() * sinR + xTransl;
    double y = xyPoint.getX() * sinR + xyPoint.getY() * cosR + yTransl;
    return new XYSpace(x,y);
  }
  /**
   * Calculates the the original of a transformed point.<p>
   * Calculation:
   * <blockquote><code>
   * x =  (p.x - xTrans) * cos(rotation) + (p.y - yTrans) * sin(rotation)<br>
   * y = -(p.x - xTrans) * sin(rotation) + (p.y - yTrans) * cos(rotation)
   * </blockquote></code>
   * @param   point   the state of which the original will be calculated
   * @return  the original state using this <code>Transform</code>
   */
  public FuSpace inverseTransform (FuSpace point) {
    if (point == null) { return null; }
    XYSpace xyPoint = (XYSpace) point;
    double x =  (xyPoint.getX() - xTransl) * cosR + (xyPoint.getY() - yTransl) * sinR;
    double y = -(xyPoint.getX() - xTransl) * sinR + (xyPoint.getY() - yTransl) * cosR;
    return new XYSpace(x,y);
  }
  /**
   * Static method that calculates the concatenation of two transforms.<p>
   * The result of this calculation is the transform being formed by adding two
   * transforms.
   * <p>
   * Calculation:
   * <blockquote><code>
   * xt  = t2.xt * t1.cos(rot) - t2.yt * t1.sin(rot) + t1.xt<br>
   * yt  = t2.xt * t1.sin(rot) - t2.yt * t1.cos(rot) + t1.yt<br>
   * rot = t1.rot + t2.rot
   * </blockquote></code>
   * @see     net.agileframes.core.forces.FuTransform#transformT1T2(FuTransform,FuTransform)
   * @param   transform1  the first transform
   * @param   transform2  the second transform (that will be concatenated with the first)
   * @return  the result of adding <code>transform2</code> to <code>transform1</code>
   */
  public static XYTransform transformT1T2 (XYTransform xyTransform1, XYTransform xyTransform2) {
    if ((xyTransform1 == null) || (xyTransform2 == null)) { return null; }
    double xt = xyTransform2.xTransl * xyTransform1.cosR -
                xyTransform2.yTransl * xyTransform1.sinR + xyTransform1.xTransl;
    double yt = xyTransform2.xTransl * xyTransform1.sinR +
                xyTransform2.yTransl * xyTransform1.cosR + xyTransform1.yTransl;
    double rot = xyTransform1.rotation + xyTransform2.rotation;
    return new XYTransform(xt, yt, rot);
  }
  /**
   * Calls the static method.<p>
   * @see #transformT1T2(XYTransform,XYTransform)
   */
  public FuTransform transformT1T2 (FuTransform transform1, FuTransform transform2) {
    return transformT1T2((XYTransform)transform1,(XYTransform)transform2);
  }

  public String toString(){
    return super.toString()+": x-translation = "+xTransl+"; y-translation = "+yTransl+"; rotation = "+rotation+"[rad]; = "+(180*rotation/Math.PI)+"[deg]";
  }

}
