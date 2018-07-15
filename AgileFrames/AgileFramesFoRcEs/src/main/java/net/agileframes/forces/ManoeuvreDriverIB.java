package net.agileframes.forces;
import net.agileframes.forces.mfd.ManoeuvreDriver;
import net.agileframes.forces.Mechatronics;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.mfd.*;
import net.agileframes.server.AgileSystem;
/**
 * <b>Basic Implementation of ManoeuvreDriver Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality in ManoeuvreDriver.
 * @see net.agileframes.forces.mfd.ManoeuvreDriver
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class ManoeuvreDriverIB implements ManoeuvreDriver, Runnable {
  //---------------------- Attributes --------------------
  protected Manoeuvre manoeuvre = null;
  protected Manoeuvre nextManoeuvre = null;
  /** The latest pilot-course, must be updated every cycle. */
  protected FuSpace.FuPath pilotCourse = null;
  /** The latest reference acceleration, must be updated every cycle. */
  protected double refAcceleration = Double.NaN;
  private boolean active = true;
  /** A reference to the instructor-object of this MFD-Thread. */
  protected Instructor instructor = null;
  /** A reference to the stateFinder-object of this MFD-Thread. */
  protected StateFinder stateFinder = null;
  /** A reference to the physicalDriver-object of this MFD-Thread. */
  protected PhysicalDriver physicalDriver = null;
  /** A reference to the mechatronics-object. */
  protected Mechatronics mechatronics = null;
  /**
   * Parameter to be used to debug this object.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;
  //---------------------- Constructor -------------------
  /**
   * Constructor.
   * <p>
   * Sets the references to the other Machine Function Driver objects and Mechatronics.
   * Initializes the other MFD-object by calling their initialize-methods.
   * @see   net.agileframes.forces.mfd.StateFinder#initialize(ManoeuvreDriver,PhysicalDriver)
   * @see   net.agileframes.forces.mfd.Instructor#initialize(ManoeuvreDriver)
   * @see   net.agileframes.forces.mfd.PhysicalDriver#initialize(Instructor,Mechatronics)
   * @param stateFinder     reference to stateFinder-object
   * @param instructor      reference to instructor-object
   * @param physicalDriver  reference to physicalDriver-object
   * @param mechatronics    reference to mechatronics-object
   */
  public ManoeuvreDriverIB(StateFinder stateFinder, Instructor instructor, PhysicalDriver physicalDriver, Mechatronics mechatronics) {
    this.stateFinder = stateFinder;
    this.instructor = instructor;
    this.physicalDriver = physicalDriver;
    this.mechatronics = mechatronics;

    this.stateFinder.initialize(this, this.physicalDriver);
    this.instructor.initialize(this);
    this.physicalDriver.initialize(this.instructor, this.mechatronics);
  }

  //---------------------- Methods -----------------------
  /**
   * Only sets the value of nextManoeuvre.
   * @param m the manoeuvre to prepare
   */
  public synchronized void prepare(Manoeuvre m) {
    if (DEBUG) { System.out.println("*D* ManoeuvreDriverIB: preparing manoeuvre"); }
    this.nextManoeuvre = m;
  }
  /**
   * Sets the value of manoeuvre and initializes the manoeuvre.<p>
   * Will also reset the value of nextManoeuvre.
   * @see   net.agileframes.core.forces.Manoeuvre#initialize(Manoeuvre)
   * @param m the manoeuvre to prepare
   */
  public synchronized void begin(Manoeuvre m) {
    if (DEBUG) { System.out.println("*D* ManoeuvreDriverIB: beginning manoeuvre:"+m.toString()); }
    this.nextManoeuvre = null;
    if (manoeuvre != null) { m.initialize(manoeuvre); }// get right start-values
    this.manoeuvre = m;
  }

  /**
   * Runs the MFD-Thread.<p>
   * Calls {@link #cycle() cycle}.<br>
   * Will stop if {@link #shutDown() shutDown} is called.
   * @see MachineIB#start()
   */
  public void run() {
	if (DEBUG) { System.out.println("*D* ManoeuvreDriverIB: run"); }
    try { Thread.currentThread().setPriority(Thread.MAX_PRIORITY); }
    catch (Exception e) { e.printStackTrace(); }
    while (active){ cycle(); }
  }
  /**
   * The cycle in which the MFD-Thread lives.<p>
   * The cycle runs continuously and will quit if during run-time
   * d+enter is entered.<br>
   * Calls the update-methods on all MFD-objects and Manoeuvre.
   * @see #run()
   */
  protected void cycle() {
	
    try {
      if (System.in.available() > 0) {
        char c = (char)System.in.read();
        int value = Character.getNumericValue(c);
        if (value == 13) {
          System.out.println("Button d pressed: this agv will be logged out!");
          AgileSystem.dispose();
          System.exit(0);
        }
      }
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }

    if (manoeuvre == null) {
      stateFinder.update(0);
      return;
    }

    synchronized (this) {
      stateFinder.update(manoeuvre.getCalcEvolution());
      manoeuvre.updateCalculatedState(stateFinder.getObservedState(), stateFinder.getObservedEvolution(), nextManoeuvre);
      pilotCourse = manoeuvre.getPilotCourse();
      refAcceleration = manoeuvre.getReferenceAcceleration();
      instructor.update();
      physicalDriver.update();
    }
  }
  /**
   * Kills this MFD-Thread.
   * @see #run()
   */
  public void shutDown() { active = false; }

  //---------------------- Getters and Setters -----------
  public Manoeuvre getManoeuvre() { return manoeuvre; }
  public Manoeuvre getNextManoeuvre() { return nextManoeuvre; }
  public FuSpace.FuPath getPilotCourse(){ return pilotCourse; }
  public double getReferenceAcceleration() { return refAcceleration; }
}

