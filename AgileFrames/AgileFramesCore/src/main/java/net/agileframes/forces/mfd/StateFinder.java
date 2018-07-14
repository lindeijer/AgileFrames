package net.agileframes.forces.mfd;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.server.AgileSystem;
/**
 * <b>Interface for the MFD-Object that takes care of estimation of the state of execution.</b>
 * <p>
 * The StateFinder-object is (almost) generic and ready-to-use. User programmed
 * specific code should be in interfaces {@link LocalInformation LocalInformation}
 * and {@link ResponseInformation ResponseInformation}.
 * The {@link #update(double) update} method should be called every MFD-cycle
 * by the ManoeuvreDriver.
 * <p>
 * The StateFinder is responsable for estimating or calculating the current state
 * of the machine.
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.forces.StateFinderIB StateFinder Implementation Base}
 * (<code>StateFinderIB</code>). This class has all the (minimal) functionality
 * needed for a StateFinder. If another implementation is needed,
 * it is advised to extend <code>StateFinderIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * @see ManoeuvreDriver
 * @see Instructor
 * @see PhysicalDriver
 * @see net.agileframes.core.traces.Actor
 * @see net.agileframes.core.forces.MachineRemote
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface StateFinder {
  /**
   * Initializes the StateFinder after creation. <p>
   * When all MFD-Objects are created, they need to have cross-references to
   * each other. To create the cross-reference-structure, this method should be
   * called.
   * @param   manoeuvreDriver   the MFD-ManoeuvreDriver object
   * @param   physicalDriver    the MFD-PhysicalDriver object
   */
  public void initialize(ManoeuvreDriver manoeuvreDriver, PhysicalDriver physicalDriver);
  /**
   * Triggers an update-cycle.<p>
   * Should calculate or estimate observed state and evolution.
   * @param   prevCalcEvol      the latest calculated evolution
   */
  public void update(double prevCalcEvol);
   /**
   * Returns time of last update-cycle.<p>
   * @return  the time in milliseconds
   */
  public long getTimeStamp();
  /**
   * Returns the latest observed function state. <p>
   * The observed state is either really observed, calculated or estimated.
   * @return  the observed function state.
   */
  public FuSpace getObservedState();
  /**
   * Returns the value of the evolution-parameter of the latest observedState. <p>
   * The observed evolution can either be calculated from the current observed
   * state or really being observed.
   * @see     #getObservedState()
   * @return  the observed evolution
   */
  public double getObservedEvolution();
  //-------------------------- Inner-Interfaces -----------------------------------
  /**
   * <b>Interface for the specific objects containing observation models.</b>
   * <p>
   * Inner-Interface of StateFinder.<br>
   * User-programmed. Original observed function-states might be obtained by
   * special instruments and observation models. The external date about the
   * observed state will be collected here and will be read by StateFinder
   * using the method getObservedState.
   */
  public interface LocalInformation {}
  /**
   * <b>Interface for the specific objects containing response models.</b>
   * <p>
   * Inner-Interface of StateFinder.<br>
   * User-programmed. Response infomation settings and model. The class that
   * implements this interface can contain predictions- and learning
   * mechanisms.
   */
  public interface ResponseInformation {}
}