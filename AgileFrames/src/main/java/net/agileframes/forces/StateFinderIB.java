package net.agileframes.forces;
import net.agileframes.forces.mfd.*;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.server.AgileSystem;
/**
 * <b>Basic Implementation of StateFinder Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality in StateFinder.
 * @see net.agileframes.forces.mfd.StateFinder
 * @author  H.J. Wierenga
 * @version 0.1
 */
public abstract class StateFinderIB implements StateFinder {
  //-------------------------- Attributes --------------------------------
  /** Last time (in milli-seconds) the update-method was called. */
  protected long timeStamp;
  /** The latest observed state, set in update.*/
  protected FuSpace observedState;
  /** The latest observed evolution, set in update.*/
  protected double observedEvolution = 0.0;
  /** A reference to the manoeuvreDriver-object of this MFD-Thread. */
  protected ManoeuvreDriver manoeuvreDriver = null;
  /** A reference to the physicalDriver-object of this MFD-Thread. */
  protected PhysicalDriver physicalDriver = null;

  //-------------------------- Methods -----------------------------------
  /**
   * Only sets the reference to manoeuvreDriver.
   * @see   ManoeuvreDriverIB#ManoeuvreDriverIB(StateFinder, Instructor, PhysicalDriver, Mechatronics)
   * @param manoeuvreDriver reference to the manoeuvreDriver-object
   * @param physicalDriver  reference to the physicalDriver-object
   */
  public void initialize(ManoeuvreDriver manoeuvreDriver, PhysicalDriver physicalDriver){
    this.manoeuvreDriver = manoeuvreDriver;
    this.physicalDriver = physicalDriver;
  }
  /** To be overloaded. */
  public abstract void update(double prevCalcEvol);

  //-------------------------- Getters and Setters -----------------------
  public long getTimeStamp() { return timeStamp; }
  public FuSpace getObservedState() { return observedState; }
  public double getObservedEvolution() { return observedEvolution; }
}


