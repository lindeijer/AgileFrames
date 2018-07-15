package net.agileframes.forces;
import net.agileframes.forces.mfd.PhysicalDriver;
import net.agileframes.forces.mfd.Instructor;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.Mechatronics;
/**
 * <b>Basic Implementation of PhysicalDriver Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality in PhysicalDriver.
 * @see net.agileframes.forces.mfd.PhysicalDriver
 * @author  H.J. Wierenga
 * @version 0.1
 */
public abstract class PhysicalDriverIB implements PhysicalDriver {
  //------------------------- Attributes -------------------------
  /** Last time (in milli-seconds) the update-method was called. */
  protected long timeStamp;
  /** The latest mechatronic course, set in update.*/
  protected FuSpace.FuPath mechatronicCourse = null;
  /** The latest mechatronic acceleration, set in update.*/
  protected double mechatronicAcceleration = Double.NaN;
  /** The latest induced speed, set in update.*/
  protected double inducedSpeed = Double.NaN;
  /** A reference to the instructor-object of this MFD-Thread. */
  protected Instructor instructor;
  /** A reference to the mechatronics-object. */
  protected Mechatronics mechatronics = null;

  //------------------------- Methods ----------------------------
  /**
   * Only sets the references to instructor and mechatronics.
   * @see   ManoeuvreDriverIB#ManoeuvreDriverIB(StateFinder, Instructor, PhysicalDriver, Mechatronics)
   * @param instructor    reference to the instructor-object
   * @param mechatronics  reference to the mechatronics-object
   */
  public void initialize(Instructor instructor, Mechatronics mechatronics) {
    this.instructor = instructor;
    this.mechatronics = mechatronics;
  }
  /** To be overloaded. */
  public abstract void update();

  //------------------------- Getters and Setters ----------------
  public long getTimeStamp() { return timeStamp; }
  public FuSpace.FuPath getMechatronicCourse() { return mechatronicCourse; }
  public double getMechatronicAcceleration() { return mechatronicAcceleration; }
  public double getInducedSpeed() { return inducedSpeed; }
}

