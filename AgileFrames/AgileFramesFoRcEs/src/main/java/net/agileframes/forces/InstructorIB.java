package net.agileframes.forces;

import net.agileframes.forces.mfd.Instructor;
import net.agileframes.forces.mfd.ManoeuvreDriver;
import net.agileframes.forces.mfd.PhysicalDriver;
import net.agileframes.forces.mfd.StateFinder;
/**
 * <b>Basic Implementation of Instructor Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality in Instructor.
 * @see net.agileframes.forces.mfd.Instructor
 * @author  H.J. Wierenga
 * @version 0.1
 */
public abstract class InstructorIB implements Instructor {
  //------------------------- Attributes -------------------------
  /** Last time (in milli-seconds) the update-method was called. */
  protected long timeStamp;
  /** A reference to the manoeuvreDriver-object of this MFD-Thread. */
  protected ManoeuvreDriver manoeuvreDriver;

  //------------------------- Methods ----------------------------
  /**
   * Only sets the reference to manoeuvreDriver.
   * @see   ManoeuvreDriverIB#ManoeuvreDriverIB(StateFinder, Instructor, PhysicalDriver, Mechatronics)
   * @param manoeuvreDriver reference to the manoeuvreDriver-object
   */
  public void initialize(ManoeuvreDriver manoeuvreDriver) { this.manoeuvreDriver = manoeuvreDriver; }
  /** To be overloaded */
  public abstract void update();

  //------------------------- Getters and Setters ----------------
  public long getTimeStamp() { return timeStamp; }
}

