package net.agileframes.forces.mfd;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>Interface for the MFD-Object that takes care of the deduction of the next execution-step.</b>
 * <p>
 * ManoeuvreDriver is completely programmed and its code is invisible for the
 * end-user. Methods anticipate and begin are used to give instructions to
 * ManoeuvreDriver. The attribute nextManoeuvre is used only for obtaining
 * pilotCourse and Speed. <br>
 * The MachineFunctionDriver-Thread  runs in cycles in run. The MFD-Thread
 * will take care of updating the 3 other MFD-objects.
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.forces.ManoeuvreDriverIB ManoeuvreDriver Implementation Base}
 * (<code>ManoeuvreDriverIB</code>). This class has all the (minimal) functionality
 * needed for a ManoeuvreDriver. If another implementation is needed,
 * it is advised to extend <code>ManoeuvreDriverIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * @see StateFinder
 * @see Instructor
 * @see PhysicalDriver
 * @see net.agileframes.core.traces.Actor
 * @see net.agileframes.core.forces.MachineRemote
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface ManoeuvreDriver {
  /**
   * Triggers preparation of a manoeuvre.
   * <p>
   * Makes manoeuvre available under {@link #getNextManoeuvre() getNextManoeuvre}.<br>
   * Will be called by {@link net.agileframes.core.forces.Move#prepare() Move.prepare}.<br>
   * @see     net.agileframes.core.forces.Move
   * @see     net.agileframes.core.forces.MachineRemote
   * @param   manoeuvre   the manoeuvre to prepare
   */
  public void prepare(Manoeuvre manoeuvre);
  /**
   * Triggers actual start of a manoeuvre.
   * <p>
   * Makes manoeuvre available under {@link #getManoeuvre() getManoeuvre}.<br>
   * Will be called by {@link net.agileframes.core.forces.Move#run(Ticket[]) Move.run}.<br>
   * @see     net.agileframes.core.forces.Move
   * @see     net.agileframes.core.forces.MachineRemote
   * @param   manoeuvre   the manoeuvre to start
   */
  public void begin(Manoeuvre manoeuvre);
  /**
   * Runs the MFD-Thread.
   * <p>
   * The MFD-Thread runs as long as {@link #shutDown() shutDown} is not called.
   */
  public void run();
  /**
   * Stops the MFD-Thread.
   * <p>
   * Makes the ManoeuvreDriver inactive and lets the MFD-Thread die.
   * @see #begin(Manoeuvre)
   */
  public void shutDown();
  /**
   * Returns the manoeuvre that is currently being driven.
   * <p>
   * @return  the current manoeuvre
   */
  public Manoeuvre getManoeuvre();
  /**
   * Returns the next manoeuvre that will be driven.
   * <p>
   * @return  the next manoeuvre
   */
  public Manoeuvre getNextManoeuvre();
  /**
   * Returns the direction in which the actor should go.
   * <p>
   * @see     net.agileframes.core.forces.Manoeuvre
   * @return  the pilot course
   */
  public FuSpace.FuPath getPilotCourse();
  /**
   * Returns the reference (evolution) acceleration of the actor.
   * <p>
   * @see     net.agileframes.core.forces.Manoeuvre
   * @return  the reference acceleration
   */
  public double getReferenceAcceleration();
}
