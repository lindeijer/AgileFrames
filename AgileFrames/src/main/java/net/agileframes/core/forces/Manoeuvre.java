package net.agileframes.core.forces;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.server.AgileSystem;
import java.io.Serializable;
/**
 * <b>Specification of the desired physical execution-behaviour.</b><p>
 * A Manoeuvre specifies how to follow a desired trajectory in the real time-domain.
 * <p>
 * A manoeuvre is an information-object containing flags, precautions and a
 * trajectory. The user-commands defined on a manoeuvre enable influencing
 * a factor with which the calculated speed is multiplied.
 * <p>
 * <b>Extensions:</b>
 * This <code>Manoeuvre</code> class provides the methods to be used by any
 * manoeuvre. The specific manoeuvres for specific environments should extend
 * this class and override the constructor only.
 * <p>
 * <b>Tasks:</b>
 * The <code>updateCalculatedState</code> method in this object is called every cycle by the
 * <code>ManoeuvreDriver</code> object and has three tasks:
 * <ul>
 * <li> Calculating the reference-speed and -course
 * <li> Evaluating flags and precuations
 * <li> Calculating the pilotcourse and the reference-acceleration.
 * </ul>
 * @see Flag
 * @see Precaution
 * @see FuTrajectory
 * @see net.agileframes.forces.mfd.ManoeuvreDriver
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class Manoeuvre implements Cloneable, Serializable {
  //----------------- Attributes -------------------------
  /**
   * Parameter to be used to debug this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;
  /** The trajectory of this manoeuvre. To be defined in extension. */
  protected FuTrajectory trajectory = null;
  /** The array of flags of this manoevre. To be defined in extension. */
  protected Flag[] flags;
  /** The array of precautions of this manoeuvre. To be defined in extension. */
  protected Precaution[] precautions;

  /** The maximal speed on this manoeuvre. To be defined in extension. */
  protected double maxSpeed = Double.NaN;
  /** The maximal deceleration on this manoeuvre. Always > 0. To be defined in extension. */
  protected double maxDeceleration = Double.NaN;
  /** The maximal acceleration on this manoeuvre. Always > 0. To be defined in extension. */
  protected double maxAcceleration = Double.NaN;
  /** The maximal deviation on this manoeuvre. To be defined in extension. */
  protected double maxDeviation = Double.NaN;
  /** The transform used to position this manoeuvre in the function space. To be defined in extension. */
  protected FuTransform transform = null;
  /** The cycle-time of the mfd-thread in seconds. Must be set in user-specific code, if not, this parameter has no value (Double.NaN). */
  public double cycleTime = Double.NaN;

  private static double projectionErrorMargin = 0.01; // where to declare ?//was : 0.1
  private double speedMultiplier = 1.0; // init with 0.0?
  private double calcEvolution = 0;
  private double calcDeviation = 0;
  private double calcSpeed = 0;
  private long updatingTime;
  private double pilotEvolution = Double.NaN;
  private FuSpace.FuPath pilotCourse = null;
  private double pilotSpeed = Double.NaN;
  private double refAcceleration = Double.NaN;
  private double prevCalcEvolution = 0;
  private double prevCalcSpeed = 0;
  private long prevUpdatingTime;
  /**/private double prevObsEvolution = 0.0;

  //------------------ Constructor -------------------------
  /**
   * Standard Constructor which can be called by extensions.
   * @param transform         transform of this manoeuvre
   * @param maxSpeed          maximal speed of this manoeuvre
   * @param maxAcceleration   maximal acceleration of this manoeuvre
   * @param maxDeceleration   maximal deceleration of this manoeuvre
   * @param maxDeviation      maximal deviation of this manoeuvre
   */
  public Manoeuvre(FuTransform transform, double maxSpeed, double maxAcceleration, double maxDeceleration, double maxDeviation) {
    this.transform = transform;
    this.maxAcceleration = maxAcceleration;
    this.maxDeceleration = maxDeceleration;
    this.maxDeviation = maxDeviation;
    this.maxSpeed = maxSpeed;
  }

  //------------------ Methods -----------------------------
  /**
   * Activates updating-cycle.
   * <p>
   * This method evaluates flags and precautions and calculates reference-course and -speed as well
   * as pilot-course and acceleration.
   * <p>
   * If the protected fields in the extended classes of this manoeuvre are not all set,
   * calling this method can result in a <code>java.lang.NullPointerException</code>.
   * <p>
   * User commands (see below: <code>startExecution adaptSpeed interruptExecution resumeExecution
   * cancelExecution</code>) can be used to influence this calculations.
   * <p>
   * Use the getters (see below) of this class to get the updated values.
   * <p>
   * As input the observations of the current state of the machine must be given.
   * (see {@link net.agileframes.forces.mfd.StateFinder#getObservedState() StateFinder.getObservedState}
   * and {@link net.agileframes.forces.mfd.StateFinder#getObservedEvolution() StateFinder.getObservedEvolution}).
   * <p>
   * The third input is the next manoeuvre, that is, the manoeuvre that will follow this
   * manoeuvre, as specified by {@link net.agileframes.forces.mfd.ManoeuvreDriver#prepare(Manoeuvre) ManoeuvreDriver.prepare}.
   * This input will be used to anticipate on the next manoeuvre, if necessary. If the input
   * is <b><code>null</code></b>, no anticpation will be done.
   * If no next manoeuvre is specified, the extension of the {@link FuTrajectory trajectory}
   * of this manoeuvre will be used if the observed evolution exceeds the evolution end.
   * @param obsState      the last observed state
   * @param obsEvolution  the last observed evolution
   * @param nextManoeuvre the manoeuvre that will follow this manoeuvre
   *                      (<b><code>null</b></code> if not available or known)
   */
  public void updateCalculatedState(FuSpace obsState, double obsEvolution, Manoeuvre nextManoeuvre){
    prevUpdatingTime = updatingTime;
    prevCalcEvolution = calcEvolution;
    prevCalcSpeed = calcSpeed;
    updatingTime = AgileSystem.getTime();
    double dT = ((double)(updatingTime - prevUpdatingTime))/1000;// in seconds

    if (DEBUG) System.out.println("*D* Manoeuvre(0): dT="+dT+"  this="+this.toString());

    if ((obsState != null) && (dT > 0)) {
      calcEvolution = getProjection(obsState, obsEvolution, projectionErrorMargin);
      if (DEBUG) System.out.println("*D* Manoeuvre(1): calcEvolution="+calcEvolution);
      double pilotAlpha = trajectory.getPilotAlpha(calcEvolution);
      double pilotBeta = trajectory.getPilotBeta(calcEvolution);
      double speedGamma = trajectory.getSpeedGamma(calcEvolution);
      double speedMu = trajectory.getSpeedMu(calcEvolution);
      if (DEBUG) System.out.println("*D* Manoeuvre(2): pilotAlpha="+pilotAlpha+"  pilotBeta="+pilotBeta+"  speedGamma="+speedGamma+"  speedMu="+speedMu);
      calcDeviation = obsState.stateDistance( trajectory.getTrajectPoint(calcEvolution) );
//OLD//      calcSpeed =  speedGamma * (calcEvolution - prevCalcEvolution) / dT + (1 - speedGamma) * prevCalcSpeed;
      calcSpeed =  speedGamma * (obsEvolution - prevObsEvolution) / dT + (1 - speedGamma) * prevCalcSpeed;
      pilotEvolution = calcEvolution + pilotAlpha + calcDeviation * pilotBeta;

      if ( (pilotEvolution > getTrajectory().getEvolutionEnd()) && (nextManoeuvre != null) ) {
        // use next manoeuvre
        FuSpace pilotState = nextManoeuvre.getTrajectory().getTrajectPoint(pilotEvolution - trajectory.getEvolutionEnd());
        pilotCourse = obsState.createPath(pilotState);
        if (DEBUG) System.out.println("*D* (next)Manoeuvre(2A): speedMultiplier="+speedMultiplier+"  maxSpeed="+maxSpeed);
        pilotSpeed = speedMultiplier * maxSpeed * (
                     speedMu *       trajectory.getProfileSpeed(calcEvolution) +
                     (1 - speedMu) * nextManoeuvre.getTrajectory().getProfileSpeed(pilotEvolution - trajectory.getEvolutionEnd()) );
        if (DEBUG) System.out.println("*D* (next)Manoeuvre(3): pilotState="+pilotState.toString()+"  obsState="+obsState.toString()+"  pilotCourse="+pilotCourse.toString());
      } else {
        // use current manoeuvre
        FuSpace pilotState = trajectory.getTrajectPoint(pilotEvolution);
        pilotCourse = obsState.createPath(pilotState);
        if (DEBUG) System.out.println("*D* Manoeuvre(2A): speedMultiplier="+speedMultiplier+"  maxSpeed="+maxSpeed);
        pilotSpeed = speedMultiplier * maxSpeed * (
                     speedMu *       trajectory.getProfileSpeed(calcEvolution) +
                     (1 - speedMu) * trajectory.getProfileSpeed(pilotEvolution) );
        if (DEBUG) System.out.println("*D* Manoeuvre(3): pilotState="+pilotState.toString()+"  obsState="+obsState.toString()+"  pilotCourse="+pilotCourse.toString());
      }
      if (DEBUG) System.out.println("*D* Manoeuvre(4): calcDeviation="+calcDeviation+"  calcSpeed="+calcSpeed+"  pilotEvolution="+pilotEvolution+"  pilotSpeed="+pilotSpeed);
      for (int i = 0; i < flags.length; i++) { flags[i].evaluate(); }
      refAcceleration = (pilotSpeed - calcSpeed) / this.cycleTime

      ;// formula: a = (vt - v0) / t
      for (int i = 0; i < precautions.length; i++) {
        double decel = precautions[i].getDeceleration();// decel is already scaled!
        if ( !(Double.isNaN(decel)) && (refAcceleration > -decel) ) { refAcceleration = -decel; }
        if (decel == 0) { refAcceleration = Double.NaN; }//decel = 0: safety-stop!!
      }
      if (refAcceleration >  maxAcceleration) { refAcceleration =  maxAcceleration; }
      if (refAcceleration < -maxDeceleration) { refAcceleration = -maxDeceleration; }
      if (Double.isNaN(refAcceleration)) { refAcceleration = Double.NEGATIVE_INFINITY; }// safety-stop
      if (DEBUG) System.out.println("*D* Manoeuvre(5): refAcceleration="+refAcceleration);
    }
    prevObsEvolution = obsEvolution;
  }

  private double getProjection(FuSpace obsState, double obsEvolution, double errorMargin) {
    double minEvolution = prevCalcEvolution;
    FuSpace minState = trajectory.getTrajectPoint(minEvolution);
    double dU = obsState.evolutionDistance(minState);
    if (prevCalcEvolution >= obsEvolution) { dU = 0; }
    if (DEBUG) System.out.println("getProjection: obsEvol="+obsEvolution+"  errorMargin="+errorMargin+"  minEvol="+minEvolution+"  dU="+dU+"  obsState="+obsState.toString());
    FuSpace maxState = trajectory.getTrajectPoint(minEvolution + dU);
    double distanceToMaxState = obsState.stateDistance(maxState);//Math.pow(obsState.stateDistance(maxState), 2);// + Math.pow(minEvolution + dU - obsEvolution, 2);
    double distanceToMinState = obsState.stateDistance(minState);//Math.pow(obsState.stateDistance(minState), 2);// + Math.pow(minEvolution - obsEvolution, 2);
    //System.out.println("distanceToMaxState="+distanceToMaxState+"  distanceToMinState="+distanceToMinState+"  dU="+dU);
    if (obsEvolution == prevObsEvolution) { return minEvolution; }
    while (dU > errorMargin) {
      dU = dU/2;
      if (distanceToMinState < distanceToMaxState) {
        maxState = trajectory.getTrajectPoint(minEvolution + dU);
        distanceToMaxState = obsState.stateDistance(maxState);//Math.pow(obsState.stateDistance(maxState), 2) + Math.pow(minEvolution + dU - obsEvolution, 2);
        //System.out.println("maxState="+maxState.toString());
      } else {
        minEvolution += dU;
        minState = trajectory.getTrajectPoint(minEvolution);
        distanceToMinState = obsState.stateDistance(minState);//Math.pow(obsState.stateDistance(minState), 2) + Math.pow(minEvolution - obsEvolution, 2);
        //System.out.println("minState="+minState.toString());
      }
      //System.out.println("distanceToMaxState="+distanceToMaxState+"  distanceToMinState="+distanceToMinState+"  dU="+dU);
    }
    //System.out.println("distanceToMaxState="+distanceToMaxState+"  distanceToMinState="+distanceToMinState+"  dU="+dU);
    //System.out.println("return: "+(minEvolution + dU/2)+"   (minEvolution="+minEvolution+",  dU="+dU+")");
    return minEvolution + dU/2;
  }

  /**
   * Creates a copy of this <code>Manoeuvre</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @return  an object that is a copy of this <code>Manoeuvre</code> object.
   */
  public Object clone() throws CloneNotSupportedException {
    Manoeuvre clone = (Manoeuvre)super.clone();
    clone.trajectory = (FuTrajectory)trajectory.clone();
    clone.flags = new Flag[flags.length];
    for (int i = 0; i < flags.length; i++) { clone.flags[i] = (Flag)flags[i].clone(clone); }
    clone.precautions = new Precaution[precautions.length];
    for (int i = 0; i < precautions.length; i++) {  clone.precautions[i] = (Precaution)precautions[i].clone(clone);  }
    return clone;
  }

  /**
   * Initializes this manoeuvre.
   * Must be called in order to remember the last calculated speed and updating
   * time of the previous manoeuvre.
   * @param prevManoeuvre the manoeuvre that was driven before this one
   */
  public void initialize(Manoeuvre prevManoeuvre) {
    // to copy the (history-)values of speed etcetera.
    this.calcSpeed = prevManoeuvre.calcSpeed;
    this.updatingTime = prevManoeuvre.updatingTime;
    this.prevObsEvolution = prevManoeuvre.prevObsEvolution;
    for (int i = 0; i < prevManoeuvre.flags.length; i++) {
      prevManoeuvre.flags[i].evaluate();
      if (!prevManoeuvre.flags[i].isRaised()) {
        System.out.println("deleting manoeuvre before flag "+i+" was raised");
        prevManoeuvre.flags[i].raise();
      }
    }

  }

  //---------------------- 'User Commands' ----------------------------
  private boolean started = false;
  private boolean interrupted = false;
  private double lastSpeedMultiplier;
  private boolean canceled = false;
  /**
   * User-command to start this manoeuvre.
   * Has effect only if the manoeuvre was not started before.
   */
  public void startExecution() { if (!started) { speedMultiplier = 1;  started = true;} }
  /**
   * User-command to adapt the speed of this manoeuvre.
   * Has effect only if manoeuvre was started and not interrupted and not canceled.
   * @param rate  a number between 0 and 1 that indicates the percentage of
   *              the prescribed speed to be calculated
   *              0: speed will be zero.
   *              1: speed will be as prescribed.
   */
  public void adaptSpeed(double rate) { if ((rate >= 0) && (rate <= 1) && (started) && (!interrupted) && (!canceled)) { speedMultiplier = rate; } }
  /**
   * User-command to interrupt the execution of this manoeuvre.
   * Has effect only if the manoeuvre was started and not canceled.
   */
  public void interruptExecution() { if ((started) && (!canceled)) { interrupted = true; lastSpeedMultiplier = speedMultiplier; speedMultiplier = 0; } }
  /**
   * User-command to resume the execution of this manoeuvre.
   * Has effect only if the manoeuvre was interrupted and not canceled.
   */

  public void resumeExecution() { if ((interrupted) && (!canceled)) { speedMultiplier = lastSpeedMultiplier; interrupted = false; } }
  /**
   * User-command to cancel this manoeuvre.
   * Has effect only if the manoeuvre was started.
   */
  public void cancelExecution() { if (started) { canceled = true; speedMultiplier = 0; } }

  //------------------- Getters and Setters ------------------------
  /**
   * Returns the calculated evolution.
   * The evolution is calculated in {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState}
   * @return the latest calculated evolution
   */
  public double getCalcEvolution() { return calcEvolution; }
  /**
   * Returns the calculated deviation.
   * The deviation is calculated in {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState}
   * @return the latest calculated deviation
   */
  public double getCalcDeviation() { return calcDeviation; }
  /**
   * Returns the calculated speed.
   * The speed is calculated in {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState}
   * @return the latest calculated speed
   */
  public double getCalcSpeed() { return calcSpeed; }
  /**
   * Returns the last updating time.
   * The last updating time is the last time {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState} was called.
   * @return the last updating time in milliseconds
   */
  public long getLastUpdatingTime() { return updatingTime; }
//*D*//  public double getPilotEvolution() { return pilotEvolution; }
  /**
   * Returns the last pilot course.
   * The pilot course is calculated in {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState}
   * @return the latest pilot course
   */
  public FuSpace.FuPath getPilotCourse() { return pilotCourse; }
//*D*//  public double getPilotSpeed() { return pilotSpeed; }
  /**
   * Returns the reference acceleration.
   * The acceleration is calculated in {@link #updateCalculatedState(FuSpace,double,Manoeuvre)  updateCalculatedState}
   * @return the latest reference acceleration
   */
  public double getReferenceAcceleration() { return refAcceleration; }
  /**
   * Returns a reference to the indicated flag.
   * The flags of the manoeuvre must be set in the constructor. A manoeuvre can
   * also have zero flags.<p>
   * Necessary to be able to watch on one of this Manoeuvre's Flags.
   * If this method is called with an index which is out of bounds, a <code>java.lang.NullPointerException</code>
   * will be thrown.
   * @see     #flags
   * @see     Move#watch(Flag)
   * @param   index   the number of the flag
   * @return  a reference to the flag asked for
   */
  public Flag getFlag(int index) { return flags[index]; }
  /**
   * Returns a reference to the indicated precaution.
   * The precautions of the manoeuvre must be set in the constructor. A manoeuvre can
   * also have zero precautions.
   * <p>
   * If this method is called with an index which is out of bounds, a <code>java.lang.NullPointerException</code>
   * will be thrown.
   * @see     #precautions
   * @param   index   the number of the precaution
   * @return  a reference to the precaution asked for
   */
  public Precaution getPrecaution(int index) { return precautions[index]; }
  /**
   * Returns a reference to trajectory of this manoeuvre.
   * The trajectory of the manoeuvre must be set in the constructor. Every manoeuvre must
   * have a trajectory.
   * @see #trajectory
   * @return  a reference to the trajectory
   */
  public FuTrajectory getTrajectory() { return trajectory; }// to be able to draw in Virtuality
}