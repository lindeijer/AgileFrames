package net.agileframes.forces.mfd;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.Mechatronics;
/**
 * <b>Interface for the MFD-Object that takes care of deduction of the specific circumstantial dependent internal directives.</b>
 * <p>
 * The PhysicalDriver-object is specific for any type of machine.<br>
 * The task of the PhysicalDriver is to translate the generic, but machine-type
 * dependent, instructions of the Instructor into the specific (lowest level)
 * machine-type dependent nstructions (for instance byte-streams for a
 * mechatronics interface).<br>
 * During translation, the pilot-course and reference acceleration might not be
 * able to set due to machine-dependent constraints. To obtain the (possible altered)
 * course and acceleration, use getMechatronicCourse and getMechatronicAcceleration.<br>
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.forces.PhysicalDriverIB PhysicalDriver Implementation Base}
 * (<code>PhysicalDriverIB</code>). This class has all the (minimal) functionality
 * needed for a PhysicalDriver. If another implementation is needed,
 * it is advised to extend <code>PhysicalDriverIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * @see ManoeuvreDriver
 * @see StateFinder
 * @see Instructor
 * @see net.agileframes.core.traces.Actor
 * @see net.agileframes.core.forces.MachineRemote
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface PhysicalDriver {
  /**
   * Initializes the PhysicalDriver after creation. <p>
   * When all MFD-Objects are created, they need to have cross-references to
   * each other. To create the cross-reference-structure, this method should be
   * called.
   * @param   instructor    the MFD-Instructor object
   * @param   mechatronics  the Mechatronics object
   */
  public void initialize(Instructor instructor, Mechatronics mechatronics);
  /**
   * Triggers an update-cycle.<p>
   * Should calculate the control-settings, using {@link Instructor.MachineInstruction Instructor.MachineInstruction}.
   */
  public void update();
  /**
   * Returns the actually set mechatronic course. <p>
   * This course can alter from the pilot-course from ManoeuvreDriver due to
   * machine-dependent constraints.
   * @return  the mechatronic course.
   */
  public FuSpace.FuPath getMechatronicCourse();
  /**
   * Returns the actually set mechatronic acceleration. <p>
   * This course can alter from the reference acceleration from ManoeuvreDriver due to
   * machine-dependent constraints.
   * @return  the mechatronic acceleration.
   */
  public double getMechatronicAcceleration();
  /**
   * Returns the 'theoretical' speed as deduced from internal model. <p>
   * This evolution speed can be observed, estimated or calculated.
   * machine-dependent constraints.
   * @return  the induced speed.
   */
  public double getInducedSpeed();
   /**
   * Returns time of last update-cycle.<p>
   * @return  the time in milliseconds
   */
  public long getTimeStamp();
  /**
   * <b>Interface for specific data-objects containing machine-settings.</b>
   * <p>
   * Inner-Interface of Instructor.<br>
   * User-programmed. Contains the data for the settings with respect to
   * a specific machine.
   */
  public interface MachineSetting {}
}