package net.agileframes.forces;

import net.agileframes.forces.mfd.*;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.server.AgileSystem;

/**
 * <b>Basic Implementation of StateFinder Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality in StateFinder.
 * 
 * @see net.agileframes.forces.mfd.StateFinder
 * @author H.J. Wierenga
 * @version 0.1
 */
public class StateFinderIB implements StateFinder {
	// -------------------------- Attributes --------------------------------
	/** Last time (in milli-seconds) the update-method was called. */
	protected long timeStamp;
	/** The latest observed state, set in update. */
	protected FuSpace observedState;
	/** The latest observed evolution, set in update. */
	protected double observedEvolution = 0.0;
	/** A reference to the manoeuvreDriver-object of this MFD-Thread. */
	protected ManoeuvreDriver manoeuvreDriver = null;
	/** A reference to the physicalDriver-object of this MFD-Thread. */
	protected PhysicalDriver physicalDriver = null;

	// -------------------------- Methods -----------------------------------

	/**
	 * Constructs a StateFinderIB and sets observedState.
	 * 
	 * The observedStateInitial may be null, but then then an extension class must
	 * provide a method to set it after construction.
	 * 
	 * @param observedStateInitial
	 */
	public StateFinderIB(FuSpace observedStateInitial) {
		this.observedState = observedStateInitial;
	}

	/**
	 * @see ManoeuvreDriverIB#ManoeuvreDriverIB(StateFinder, Instructor,
	 *      PhysicalDriver, Mechatronics)
	 * @param manoeuvreDriver
	 *            reference to the manoeuvreDriver-object
	 * @param physicalDriver
	 *            reference to the physicalDriver-object
	 */
	public void initialize(ManoeuvreDriver manoeuvreDriver, PhysicalDriver physicalDriver) {
		this.manoeuvreDriver = manoeuvreDriver;
		this.physicalDriver = physicalDriver;
	}

	/**
	 * Updates the state by simply assuming that the machine follows the trajectory
	 * precisely.
	 * 
	 * So, the observed evolution corresponds with the trajectories profile speed
	 * and the observed state is the trajectory-state for the computed observed
	 * evolution.
	 * 
	 * Note: override this method for your machine which will deviate from the
	 * trajectory.
	 * 
	 * @param prevCalcEvol
	 *            the (previous) evolution projected on to the trajectory
	 */
	public void update(double prevCalcEvol) {
		
		Manoeuvre aManoeuvre = manoeuvreDriver.getManoeuvre();
		if (aManoeuvre == null) {
			return;
		}
		// prevCalcEvol ==  manoeuvreDriver.getManoeuvre().getCalcEvolution() ... 
		// so it need not be paassed as parameter
		if (prevCalcEvol==0.0) {
			// a next maneuver is started, and the evolution is at the beginning.
			observedEvolution = 0.0;
		}
		FuTrajectory aFuTrajectory = aManoeuvre.getTrajectory();
		double prevSpeed_ms = aFuTrajectory.getProfileSpeed(prevCalcEvol) * aManoeuvre.maxSpeed;
		long dT_ms = physicalDriver.getCycleTime();
		double dT_s = dT_ms / 1000.0;
		double dEvolution_m = dT_s * prevSpeed_ms;
		System.out.println("StateFinderIB.update: prevCalcEvol="+prevCalcEvol+",prevSpeed_ms="+prevSpeed_ms+",dT_s="+dT_s+",dEvolution_m="+dEvolution_m+",aFuTrajectory="+aFuTrajectory);
		observedEvolution = observedEvolution + dEvolution_m;
		this.observedState = aFuTrajectory.getTrajectPoint(observedEvolution);
	}

	// -------------------------- Getters and Setters -----------------------
	public long getTimeStamp() {
		return timeStamp;
	}

	public FuSpace getObservedState() {
		return observedState;
	}

	public double getObservedEvolution() {
		return observedEvolution;
	}
}
