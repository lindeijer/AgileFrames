package net.agileframes.forces.xyaspace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyaspace.XYASpace;
/**
 * <b>Implementation of FuTransform in the X-Y-A Space.</b>
 * <p>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class XYATransform extends FuTransform {
  /** Identity-Transform is XYATranform(0,0,0) */
  public static final XYATransform IDENTITY = new XYATransform(0,0,0);
  //--------------------- Attributes --------------------------------------
  private double sinR = Double.NaN;
  private double cosR = Double.NaN;
  private double xTransl = Double.NaN;
  private double yTransl = Double.NaN;
  private double rotation = Double.NaN;

  //--------------------- Constructors ------------------------------------
  /**
   * Creates an XYATransform object.<p>
   * Sets the translation and rotation parameters and calculates the
   * sin(rotation) and cos(rotation) to be used later on.
   * @param xTransl     the x-translation of this transform
   * @param yTransl     the y-translation of this transform
   * @param rotation    the rotation of this transform
   */
  public XYATransform(double xTransl, double yTransl, double rotation) {
    this.rotation = rotation;
    this.sinR = Math.sin(rotation);
    this.cosR = Math.cos(rotation);
    this.xTransl = xTransl;
    this.yTransl = yTransl;
  }
  /**
   * Creates an XYATransform object using an XYASpace object.<p>
   * The x, y and alpha coordinates of the XYASpace-object will be used creating
   * an XYATransform.
   * @param transformationPoint the point to create a transform to
   */
  public XYATransform(XYASpace transformationPoint) {
    this(transformationPoint.getX(), transformationPoint.getY(), transformationPoint.getAlpha());
  }
  /**
   * Creates an XYATransform concatenating two other XYATransforms.<p>
   * The created transform will be the the second transform added to the first.
   * @see   #transformT1T2(XYATransform, XYATransform)
   * @param xyaTransform1 the first transform
   * @param xyaTransform2 the second transform (that will be concatenated with the first)
   */
  public XYATransform(XYATransform xyaTransform1, XYATransform xyaTransform2) {
    this(XYATransform.transformT1T2(xyaTransform1, xyaTransform2));
  }
  /**
   * Creates an XYATransform that is a copy of an existing XYATransform.<p>
   * @param xyaTranform the transform that will be copied
   */
  public XYATransform(XYATransform xyaTransform) {
    this(xyaTransform.xTransl, xyaTransform.yTransl, xyaTransform.rotation);
  }
  //--------------------- Methods -----------------------------------------
  /**
   * Calculates the transformed point.
   * The current transform is used to calculate the transformed
   * form of a point.<p>
   * Calculation:
   * <blockquote><code>
   * x = p.x * cos(rotation) - p.y * sin(rotation) + xTrans<br>
   * y = p.x * sin(rotation) + p.y * cos(rotation) + yTrans<br>
   * a = p.a + rotation
   * </blockquote></code>
   * @param   point   the state of which the transform will be calculated
   * @return  the transformed state using this <code>Transform</code>
   */
  public FuSpace transform (FuSpace point) {
    if (point == null) { return null; }
    XYASpace xyaPoint = (XYASpace) point;
    double x = xyaPoint.getX() * cosR - xyaPoint.getY() * sinR + xTransl;
    double y = xyaPoint.getX() * sinR + xyaPoint.getY() * cosR + yTransl;
    double alpha = xyaPoint.getAlpha() + rotation;
    return new XYASpace(x,y, alpha);
  }

  /**
   * Calculates the the original of a transformed point.<p>
   * Calculation:
   * <blockquote><code>
   * x =  (p.x - xTrans) * cos(rotation) + (p.y - yTrans) * sin(rotation)<br>
   * y = -(p.x - xTrans) * sin(rotation) + (p.y - yTrans) * cos(rotation)<br>
   * a = p.a - rotation
   * </blockquote></code>
   * @param   point   the state of which the original will be calculated
   * @return  the original state using this <code>Transform</code>
   */
  public FuSpace inverseTransform (FuSpace point) {
    if (point == null) { return null; }
    XYASpace xyaPoint = (XYASpace) point;
    double x =  (xyaPoint.getX() - xTransl) * cosR + (xyaPoint.getY() - yTransl) * sinR;
    double y = -(xyaPoint.getX() - xTransl) * sinR + (xyaPoint.getY() - yTransl) * cosR;
    double alpha = xyaPoint.getAlpha() - rotation;
    return new XYASpace(x,y, alpha);
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
  public static XYATransform transformT1T2 (XYATransform xyaTransform1, XYATransform xyaTransform2) {
    if ((xyaTransform1 == null) || (xyaTransform2 == null)) { return null; }
    double xt = xyaTransform2.xTransl * xyaTransform1.cosR -
                xyaTransform2.yTransl * xyaTransform1.sinR + xyaTransform1.xTransl;
    double yt = xyaTransform2.xTransl * xyaTransform1.sinR +
                xyaTransform2.yTransl * xyaTransform1.cosR + xyaTransform1.yTransl;
    double rot = xyaTransform1.rotation + xyaTransform2.rotation;
    return new XYATransform(xt, yt, rot);
  }
  /**
   * Calls the static method.<p>
   * @see #transformT1T2(XYATransform,XYATransform)
   */
  public FuTransform transformT1T2 (FuTransform transform1, FuTransform transform2) {
    return transformT1T2((XYATransform)transform1,(XYATransform)transform2);
  }

  public String toString(){
    return super.toString()+": x-translation = "+xTransl+"; y-translation = "+yTransl+"; rotation = "+rotation+"[rad]; = "+(180*rotation/Math.PI)+"[deg]";
  }

}
