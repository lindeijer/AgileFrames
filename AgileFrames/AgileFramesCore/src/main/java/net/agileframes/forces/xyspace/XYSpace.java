package net.agileframes.forces.xyspace;
import net.agileframes.core.forces.FuSpace;
/**
 * <b>Implementation of FuSpace in the X-Y Space.</b>
 * <p>
 * @author  H.J. Wierenga
 * @version 0.1
 */

public class XYSpace extends FuSpace {
  //-------------------------- Attributes ---------------------------------
  private double x = Double.NaN;
  private double y = Double.NaN;

  //-------------------------- Constructor --------------------------------
  /**
   * Creates an XYSpace-object.<p>
   * Only sets the x and y parameters.
   * @param x     the x-coordinate of this object
   * @param y     the y-coordinate of this object
   */
  public XYSpace(double x, double y) {
    this.x = x;
    this.y = y;
  }
  //-------------------------- Methods ------------------------------------
  /**
   * Static method containing the code to calculate state-distance.<p>
   * The method is static, because it is desired that the contents of this
   * code is loaded into the computer's memory only once. If this method
   * would not be static, the code would be loaded with every instance
   * of XYSpace.<p>
   * <blockquote><code>
   * state-distance = square-root (dx<sup>2</sup> + dy<sup>2</sup>)
   * </code></blockquote>
   * @param   xyaPoint1 first state
   * @param   xyaPoint2 second state
   * @return  the state-distance between the two points
   */
  public static double stateDistance (XYSpace xyPoint1, XYSpace xyPoint2) {
    if ((xyPoint1 == null) || (xyPoint2 == null)) { return Double.NaN; }
    double dx = xyPoint2.x - xyPoint1.x;
    double dy = xyPoint2.y - xyPoint1.y;
    return Math.sqrt(dx*dx + dy*dy);
  }
  /**
   * Calls the static state-distance-method.<p>
   * @see #stateDistance(XYSpace,XYSpace)
   */
  public double stateDistance (FuSpace point1, FuSpace point2) { return stateDistance ((XYSpace)point1,(XYSpace)point2); }

  /**
   * Same as state-distance.<p>
   * The evolution distance in XYSpace is the same as the state-distance. This
   * method will call {@link  #stateDistance(XYSpace,XYSpace) stateDistance}.
   */
  public static double evolutionDistance (XYSpace xyPoint1, XYSpace xyPoint2) { return stateDistance(xyPoint1, xyPoint2); }
  /**
   * Calls the static evolution-distance-method.<p>
   * @see #evolutionDistance(XYSpace,XYSpace)
   */
  public double evolutionDistance (FuSpace point1, FuSpace point2) { return evolutionDistance((XYSpace)point1,(XYSpace)point2); }


  public FuPath createPath (FuSpace point1, FuSpace point2) {
    return new XYPath((XYSpace)point1, (XYSpace)point2);
  }

  public String toString() { return super.toString() + ":\t x = "+x+"\t y = "+y; }

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

  //-------------------------- ConnectionPath ------------------------------
  /**
   * <b>Implementation of FuPath in the X-Y Space.</b>
   * <p>
   * The XYPath is a straight path.
   * @author  H.J. Wierenga
   * @version 0.1
   */
  public class XYPath extends FuPath {
    private XYSpace point1 = null;
    private double length = Double.NaN;
    private double coeffX = Double.NaN;
    private double coeffY = Double.NaN;
    /**
     * Creates a straight path between two points.
     * @param point1  the start point
     * @param point2  the end point
     */
    public XYPath(XYSpace point1, XYSpace point2) {
      this.point1 = point1;
      double dx = point2.getX() - point1.getX();
      double dy = point2.getY() - point1.getY();
      length = Math.sqrt(dx*dx + dy*dy);
      coeffX = dx / length;
      coeffY = dy / length;
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
      return new XYSpace(x, y);
    }
  }

}
