package net.agileframes.traces.viewer;
/**
 * <b>Data-object containing visual properties of the Semaphores to be viewed.</b>
 * <p>
 * This object can be extended to create user-defined semaphore-properties.
 * The implemented code is to be used in 2d-visualization.
 * @see     SemaphoreViewer
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class SemaphoreProperties implements java.io.Serializable {
  /** The x-coordinate of the visual presentation of the semaphore */
  public double x = 0;
  /** The y-coordinate of the visual presentation of the semaphore */
  public double y = 0;
  /** The width of the visual presentation of the semaphore */
  public double width = 10;
  /** The height of the visual presentation of the semaphore */
  public double height = 10;
  /**
   * The shape of the visual presentation of the semaphore<p>
   * 2 shapes are pre-defined: rectangle and oval. Default value
   * is Rectangle. To be given a value of one of the shape-constants.
   * @see #RECTANGLE
   * @see #OVAL
   */
  public int shape = RECTANGLE;
  /** Shape-constant for a rectangle. */
  public final static int RECTANGLE = 0;
  /** Shape-constant for an oval. */
  public final static int OVAL = 1;
  /** Empty Constructor. */
  public SemaphoreProperties() {}
  /**
   * Default Constructor defining all parameters.<p>
   * @param x       the x-coordinate of the visual presentation of the semaphore
   * @param y       the y-coordinate of the visual presentation of the semaphore
   * @param width   the width of the visual presentation of the semaphore
   * @param height  the height of the visual presentation of the semaphore
   * @param shape   the shape of the visual presentation of the semaphore (use shape-constants)
   */
  public SemaphoreProperties(double x, double y, double width, double height, int shape) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.shape = shape;
  }
}