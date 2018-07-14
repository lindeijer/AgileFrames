package net.agileframes.forces.mfd;
/**
 * <b>Interface for the MFD-Object that takes care of deduction of the machine-type dependent instructions.</b>
 * <p>
 * The Instructor-object is specific for any type of machine.<br>
 * The task of the Instructor is to translate pilot-course and reference
 * acceleration (obtained from ManoeuvreDriver) in machine-type dependent
 * machine-instructions.
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.forces.InstructorIB Instructor Implementation Base}
 * (<code>InstructorIB</code>). This class has all the (minimal) functionality
 * needed for a Instructor. If another implementation is needed,
 * it is advised to extend <code>InstructorIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * @see ManoeuvreDriver
 * @see StateFinder
 * @see PhysicalDriver
 * @see net.agileframes.core.traces.Actor
 * @see net.agileframes.core.forces.MachineRemote
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface Instructor {
  /**
   * Initializes the Instructor after creation. <p>
   * When all MFD-Objects are created, they need to have cross-references to
   * each other. To create the cross-reference-structure, this method should be
   * called.
   * @param   manoeuvreDriver   the MFD-ManoeuvreDriver object
   */
  public void initialize(ManoeuvreDriver manoeuvreDriver);
  /**
   * Triggers an update-cycle.<p>
   * Should calculate machine-type dependent instructions.
   */
  public void update();
  /**
   * Returns time of last update-cycle.<p>
   * @return  the time in milliseconds
   */
  public long getTimeStamp();
  /**
   * <b>Interface for specific data-objects containing machine-instructions.</b>
   * <p>
   * Inner-Interface of Instructor.<br>
   * User-programmed. Contains the data for the instructions with respect to
   * a specific machine.
   */
  public interface MachineInstruction {}
}

