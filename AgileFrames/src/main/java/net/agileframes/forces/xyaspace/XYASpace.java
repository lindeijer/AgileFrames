package net.agileframes.forces.xyaspace;
import net.agileframes.core.forces.FuSpace;
/**
 * <b>Implementation of FuSpace in the X-Y-A Space.</b>
 * <p>
 * The XYASpace can be considered to be the same as 2D Space.
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class XYASpace extends FuSpace {
  //-------------------------- Attributes ---------------------------------
  private double x = Double.NaN;
  private double y = Double.NaN;
  private double alpha = Double.NaN;
  private static final double LABDA = 0.2;
  /** Debug parameter, default = false. */
  public static boolean DEBUG = false;

  //-------------------------- Constructor --------------------------------
  /**
   * Creates an XYASpace-object.<p>
   * Only sets the x, y and alpha parameters.
   * @param x     the x-coordinate of this object
   * @param y     the y-coordinate of this object
   * @param alpha the alpha-coordinate of this object
   */
  public XYASpace(double x, double y, double alpha) {
    this.x = x;
    this.y = y;
    this.alpha = alpha;
  }

  //-------------------------- Methods ------------------------------------
  /**
   * Static method containing the code to calculate state-distance.<p>
   * The method is static, because it is desired that the contents of this
   * code is loaded into the computer's memory only once. If this method
   * would not be static, the code would be loaded with every instance
   * of XYASpace.<p>
   * Delta-Alpha will always be kept in the domain [0..PI}.<br>
   * The state-distance will be calculated with the following formula:
   * <blockquote><code>
   * state-distance = square-root (dx<sup>2</sup> + dy<sup>2</sup> + LABDA * da<sup>2</sup>)
   * </code></blockquote>
   * In which LABDA = 0.2
   * @param   xyaPoint1 first state
   * @param   xyaPoint2 second state
   * @return  the state-distance between the two points
   */
  public static double stateDistance (XYASpace xyaPoint1, XYASpace xyaPoint2) {
    if ((xyaPoint1 == null) || (xyaPoint2 == null)) { return Double.NaN; }
    double dx = xyaPoint2.x - xyaPoint1.x;
    double dy = xyaPoint2.y - xyaPoint1.y;
    double da = xyaPoint2.alpha - xyaPoint1.alpha;
    while ( (da < 0) || (da >= Math.PI) ) {
      if (da >=  Math.PI) { da -= Math.PI; }
      if (da < 0)         { da += Math.PI; }
    }
    return Math.sqrt(dx*dx + dy*dy + LABDA * da*da);
  }
  /**
   * Calls the static state-distance-method.<p>
   * @see #stateDistance(XYASpace,XYASpace)
   */
  public double stateDistance (FuSpace point1, FuSpace point2) { return stateDistance ((XYASpace)point1,(XYASpace)point2); }

  /**
   * Static method containing the code to calculate evolution-distance.<p>
   * The evolution-distance will be calculated with the following formula:
   * <blockquote><code>
   * evolution-distance = square-root (dx<sup>2</sup> + dy<sup>2</sup>)
   * </code></blockquote>
   * In which LABDA = 0.2
   * @see     #stateDistance(XYASpace,XYASpace)
   * @param   xyaPoint1 first state
   * @param   xyaPoint2 second state
   * @return  the state-distance between the two points
   */
  public static double evolutionDistance (XYASpace xyaPoint1, XYASpace xyaPoint2) {
    if ((xyaPoint1 == null) || (xyaPoint2 == null)) { return Double.NaN; }
    double dx = xyaPoint2.x - xyaPoint1.x;
    double dy = xyaPoint2.y - xyaPoint1.y;
    return Math.sqrt(dx*dx + dy*dy);
  }
  /**
   * Calls the static evolution-distance-method.<p>
   * @see #evolutionDistance(XYASpace,XYASpace)
   */
  public double evolutionDistance (FuSpace point1, FuSpace point2) { return evolutionDistance((XYASpace)point1,(XYASpace)point2); }

  public FuPath createPath (FuSpace point1, FuSpace point2) {
    return new XYAPath((XYASpace)point1,(XYASpace)point2);
  }

  public String toString() { return "x = "+x+"\t y = "+y+"\t alpha = "+alpha; }

  //-------------------------- Getters ------------------------------------
  /**
   * Returns the x-coordinate of this point.<p>
   * @return the x-coordinate
   */
  public double getX() { return x; }
  /**
   * Returns the y-coordinate of this point.<p>
   * @return the y-coordinate
   */
  public double getY() { return y; }
  /**
   * Returns the alpha-coordinate of this point.<p>
   * @return the alpha-coordinate
   */
  public double getAlpha() { return alpha; }

  //-------------------------- ConnectionPath ------------------------------
  /**
   * <b>Implementation of FuPath in the X-Y-A Space.</b>
   * <p>
   * The XYAPath is a straight path.
   * @author  H.J. Wierenga
   * @version 0.1
   */
  public class XYAPath extends FuPath {
    private XYASpace point1 = null;
    private double coeffX = 0; private double coeffY = 0; private double coeffA = 0;
    /**
     * Creates a straight path between two points.
     * @param point1  the start point
     * @param point2  the end point
     */
    public XYAPath(XYASpace point1, XYASpace point2) {
      this.point1 = point1;
      double dx = point2.getX() - point1.getX();
      double dy = point2.getY() - point1.getY();
      double da = point2.getAlpha() - point1.getAlpha();

      // da should be in domain [0..pi} (because agv is symmetric)
      while ( (da <= -Math.PI/2) || (da > Math.PI/2) ) {
        if (da >  Math.PI/2)  { da -= Math.PI; }
        if (da <= -Math.PI/2) { da += Math.PI; }
      }

      double length = Math.sqrt(dx*dx + dy*dy);
      coeffX = dx / length; if (Double.isNaN(coeffX)) { coeffX = 0; }
      coeffY = dy / length; if (Double.isNaN(coeffY)) { coeffY = 0; }
      coeffA = da / length; if (Double.isNaN(coeffA)) { coeffA = 0; }

      if (DEBUG) System.out.println("*D* XYAPath: point1: "+point1.toString());
      if (DEBUG) System.out.println("*D* XYAPath: point2: "+point2.toString());
      if (DEBUG) System.out.println("*D* XYAPath: coeff:  x="+coeffX+"  y="+coeffY+"  a="+coeffA);
    }
    /**
     * Calculates a point on the path. <p>
     * If d=0, the start-point of the path will be given.
     * If d=1, the point at 1 meter distance from the start point on the path will be given.
     * If d=path-length, the end-point of the path will be given.
     * @param   d   distance on the path, measured from the start-state
     * @return  the state located at a distance d on the path
     */
    public FuSpace getConnectionPoint(double d) {
      double x = point1.getX() + coeffX * d;
      double y = point1.getY() + coeffY * d;
      double alpha = point1.getAlpha() + coeffA * d;
      if (DEBUG) System.out.println("*D* XYAPath.getConnectionPoint: point1: x="+point1.getX()+"  y="+point1.getY()+"  a="+point1.getAlpha());
      if (DEBUG) System.out.println("*D* XYAPath.getConnectionPoint: coeff:  x="+coeffX+"  y="+coeffY+"  a="+coeffA);
      if (DEBUG) System.out.println("*D* XYAPath.getConnectionPoint: result: x="+x+"  y="+y+"  a="+alpha);
      return new XYASpace(x, y, alpha);
    }
  }
}
