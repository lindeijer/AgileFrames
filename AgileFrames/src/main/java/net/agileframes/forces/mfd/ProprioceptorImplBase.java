package net.agileframes.forces.mfd;

import net.agileframes.core.forces.State;
//import net.agileframes.core.forces.Rule;
//import net.agileframes.core.forces.Infrastructure;
import net.agileframes.server.ServerImplBase;
//import net.jini.core.entry.Entry;
//import net.jini.lookup.entry.Name;
//import net.agileframes.server.AgileSystem;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.Proprioceptor;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.forces.TrajectoryState;
import java.rmi.RemoteException;


/**
Abstract base-class for Proprioceptor implementations.
Is responsible for maintaining the current functional state.
This is achieved using internal information provided by internal sensors,
external sensors, and the infrastructure.

Default implementation :
<ul>
<li>downloads the infrastructure associated with the loginbase
<li>all methods are null
</ul>

*/

public class ProprioceptorImplBase extends ServerImplBase implements Proprioceptor {

  protected MachineImplBase machine = null;

  public ProprioceptorImplBase(MachineImplBase machine) throws RemoteException {
    super("ProprioceptorImplBase@" + machine.getName());
    this.machine = machine;
    downloadInfrastructureProxy();
    f1 = machine.trajectory.getBegin();
  }

  // temporary defined for usage with MiniAgvDriver
  public ProprioceptorImplBase(String name) throws RemoteException {
    super(name);
  }

  // Infrastructure infrastructure = null;

  public void downloadInfrastructureProxy() {
    if (false) {
      // Entry[] attributeSets = { new Name(AgileSystem.getLoginbaseName()) };
      // infrastructure = (Infrastructure) AgileSystem.lookup(
      //   null,
      //   new Class[]{net.agileframes.core.forces.Infrastructure.class},
      //   attributeSets
      // );
      // infrastructure.addMachine(machine);
      // System.out.println(machine.getName() + " downloaded InfrastructureProxy and added successfully");
    }
    else {
      System.out.println(machine.getName() + " did not download InfrastructureProxy");
    }
  }

  ////////////// implementation of Proprioceptor /////////////////

  /**
  Compute the current functional state and set its associated evolution and time.
  Use all data available. Default implementation is null.
  @return g=currentState, t and u are set.
  */
  public State computeState() {
    // get all available data from the (odometry) sensors
    // do some dead-reconing or something like it.
    // compute the new machine.g=currentState  // position and orientation
    // compute the new machine.g.uVelocity=currentVelocity
    // compute the new machine.g.uAccelleration=currentAccelleration
    // System.out.println("  ProprioceptorImplBase.computeState not implemented");
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////

  static int historyLength = 8;
  static float[] du;
  static long[] dt;

  static {
    du = new float[historyLength];
    dt = new long[historyLength];
    for (int i=0;i<historyLength;i++) {
      du[i] = 0.0f;
      dt[i] = 0;
    }
  }

  /**
  @param deltaU in u, possibly meters !!
  @param dTime in milliseconds
  */
  public void computeEvolutionDynamics(float deltaU,long deltaTime) {
    System.out.println("  ");
    for (int i=0;i<historyLength-1;i++) {
      dt[i] = dt[i+1];
      du[i] = du[i+1];
    }
    dt[historyLength-1] = deltaTime ;
    du[historyLength-1] = deltaU ;
    float dU = 0.0f;
    long dT = 0;
    for (int i=0;i<historyLength;i++) {
      dU = dU + du[i];    // sum of milliseconds
      dT = dT + dt[i];    // sum of u in meters
    }
    u$s_old = u$s;
    u$s = (dU/(((float)dT)/1000));
    u$s2 = (u$s - u$s_old)/(((float)deltaTime)/1000);
    // System.out.println("  ProprioceptorImplBase: evolutionVelocity_u$s=" + u$s + " evolutionAcceleration_u$s2=" + u$s2);
  }

  float u$s = 0.0f;
  float u$s_old = 0.0f;
  float u$s2 = 0.0f;

  /**
  @return current evolution acceleration in u/s, s is in seconds. 
  */
  public double get_u$s() { return u$s; }

  /**
  @return current evolution acceleration in u/s^2, s is in seconds.
  */
  public double get_u$s2() { return u$s2; }

  ////////////////////////////////////////////////////////////////////////////



  public TrajectoryState computeTrajectoryState(Trajectory trajectory,State g) {
    // System.out.println("  ProprioceptorImplBase.computeTrajectoryState with initial estimatedEvolution=" + g.u);
    return computeTrajectoryState(trajectory,g,machine.trajectoryState);
  }

  State f1 = null;
  State helper = null;

  /**
  Project g onto the trajectory and find f, projection is minimal distance
  */
  public TrajectoryState computeTrajectoryState(Trajectory trajectory,State g,TrajectoryState trajectoryStateContainer) {
    float estimatedEvolution = g.u;
    State f = trajectoryStateContainer.state;  // the previous value, is the initialization correct?
    if (estimatedEvolution < trajectory.initialEvolution) {
      estimatedEvolution = trajectory.initialEvolution;
    }
    if (estimatedEvolution > trajectory.initialEvolution + trajectory.domain) {
      estimatedEvolution = trajectory.initialEvolution + trajectory.domain;
    }
    trajectoryStateContainer.errorCost = 0;
    f = trajectory.compute(estimatedEvolution,f);
    // f.u falls within the domain of the trajectory, therefore compute never returns f.u==NaN.
    float d = 0.0f;
    d = g.distance(f);
    float abit = 0.001f;
    //
    float d1 = Float.POSITIVE_INFINITY;
    // try backevolving
    boolean backEvolving = false;
    f1 = trajectory.compute(estimatedEvolution - abit,f1);
    if (f1.u != Float.NaN) { d1 = g.distance(f1); }
    while ( (f1.u != Float.NaN) && (d1 < d)) {
      trajectoryStateContainer.errorCost++;
      backEvolving = true;
      helper = f;
      f = f1;
      f1 = helper;
      d = d1;
      estimatedEvolution = estimatedEvolution - abit;
      // System.out.println("  ProprioceptorImplBase.computeTrajectoryState backEvolved to estimatedEvolution=" + estimatedEvolution + " distance=" + d);
      f1 = trajectory.compute(estimatedEvolution - abit,f1);
      if (f1.u != Float.NaN) { d1 = g.distance(f1); }
      else { d1 = Float.POSITIVE_INFINITY; }
    }
    if (backEvolving == false) {
      f1 = trajectory.compute(estimatedEvolution + abit,f1);
      if (f1.u != Float.NaN) { d1 = g.distance(f1); }
      while ( (f1.u != Float.NaN) && (d1 < d)) {
        trajectoryStateContainer.errorCost--;
        helper = f;
        f = f1;
        f1 = helper;
        d = d1;
        estimatedEvolution = estimatedEvolution + abit;
        // System.out.println("  ProprioceptorImplBase.computeTrajectoryState forwardEvolved to estimatedEvolution=" + estimatedEvolution + " distance=" + d);
        f1 = trajectory.compute(estimatedEvolution + abit,f1);
        if (f1.u != Float.NaN) { d1 = g.distance(f1); }
        else { d1 = Float.POSITIVE_INFINITY; }
      }
    }
    f.u = estimatedEvolution;
    f.t = g.t;
    trajectoryStateContainer.trajectory = trajectory;
    trajectoryStateContainer.accelleration = Float.NaN; // should be observed and set
    trajectoryStateContainer.velocity = Float.NaN;      // should be observed and set
    trajectoryStateContainer.state = f;
    trajectoryStateContainer.distance = d;
    //System.out.println("  Proprioceptor final estimatedEvolution=" + estimatedEvolution);
    //System.out.println("  Proprioceptor distance=" + d);
    // System.out.println(" ");
    return trajectoryStateContainer;
  }

  /////////////////////////////////////////////////////////////////////

  /** the avatar wants to know where we are */
  public State getState() {
    // System.out.println("  ProprioceptorImplBase.getState not implemented");
    return null;
  }

  /** the avatar wants to know where we are, implementation is null */
  public float getEvolution() {
    // System.out.println("  ProprioceptorImplBase.getEvolution not implemented");
    return Float.NaN;
  }

  /////////////////////////////////////////////////////////////////////

  /**
  Computes SpeedAtRules to model the dynamic functional state of external
  entities observed by sensors.
  */
  public Object[] computeEnvironmentalInformation() {
    // get all available data from the (infra-red) sensors
    // of the external objects observed, estimate their distance and speed
    // for every external object, formulate a (enforced) rule to avoid it,
    // I propose a speedAtRule of infront-driving vehicles
    // System.out.println("  ProprioceptorImplBase.computeEnvironmentalInformation not implemented");
    return null;
  }

  /**
  The setter sets the controls according to a response model with which it makes
  predictions of vehicle responses (such as how it will accellerate).
  These predicted values must be compared with result-values in order to learn.
  The proprioceptor must compute the result-values from observations.
  */
  public float[] computeResponseInformation() {
    // get all available data from sensors, beit internal or external
    // get any data you can get your hands on.
    // responseinfo is any measured/computed value that was also predicted
    // by the Setter. Example could be expected accelleration.
    // usually we dont have accelleration-meters on board so its probably better
    // for the setter to predict a position or number of wheel-revolutions
    // because these values are easily measured/computed.
    // System.out.println("  ProprioceptorImplBase.computeResponseInformation not implemented");
    return null;
   }

  /**
  An external information-source has provided an update of the functional state.
  @param functional state of this machine observed by the external source
  @param time at which the functional state was observed.
  */
  public void setState(State state,long time) {
    // Thanks to adding the machine to the infrastructure the infrastructure is
    // now sending the machine functional state updates. The infrastructure
    // measured the functional state with some magical means, possibly
    // a system of infre-red cameras.
    //
    // It could be that the machine itself is counting magnets in the ground
    // and that the infrastructure informed the machine of the magnet-locations
    //
    // It could be that the machine listens to GPS satellites and that the
    // infrastructure informed the machine of the satellite trajectories
    // or the infrastructure sends a correcting value to make the GPS readings
    // more precise.
    //
    // Anyway, when
    // System.out.println("ProprioceptorImplBase.setState not implemented");
  }

  /**
  The internal state is an extension of the functional state
  */
  public State getInternalState() {
    // System.out.println("ProprioceptorImplBase.getInternalState not implemented");
    return null;
  }

}