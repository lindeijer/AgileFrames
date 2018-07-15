package net.agileframes.core.forces;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import java.io.Serializable;
/**
 * <b>The Trajectory in the Function Space.</b>
 * <p>
 * This object is used to describe a trajectory - track - in the
 * {@link FuSpace function-space}. Note that this object should be extended
 * to specify trajectories in certain spaces (for example: 2DSpace, 3DSpace,
 * XYSpace, XYASpace, etc.).
 * <p>
 * <b>Extensions:</b>
 * Extensions should implement a version of {@link #getTrajectPoint(double) getTrajectPoint}
 * and {@link #getProfileSpeed(double) getProfileSpeed}. The other methods in this object
 * are written to be used in any function-space.
 * <p>
 * <b>Elementary and Composed Trajectories:</b>
 * A trajectory may be either <i>elementary</i> or <i>composed</i>. A composed trajectory
 * contains sub-trajectories that may be composed themselves. This <code>FuTrajectory</code>
 * object can be used to form composed trajectories for any kind of function-space, as long
 * as all sub-trajectories are in the same function-space.
 * Elementary trajectories should be extensions of this <code>FuTrajectory</code> object.
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class FuTrajectory implements Cloneable, Serializable {
  //--------------------------- Attributes -----------------------------
  private FuTrajectory afterTrajectory = null;
  private FuTrajectory beginTrajectory = null;
  /**
   * The transform used to position this trajectory in the function-space.
   * This field is set in the constructor of the trajectory.
   * Extensions of the FuTrajectory need to have their transform set as well.
   */
  protected FuTransform transform = null;
  /**
   * The extension of this trajectory, used for extrapolation.
   * This field must be set in the constructor of the extension.
   */
  protected FuSpace.FuPath extension = null;
  /**
   * The maximum evolution value for this trajectory.
   * If the evolution is higher than the <code>evolutionEnd</code>, then the
   * {@link #extension extension} must be used to calculate the state.
   * This field must be set in the constructor of the extension.
   */
  protected double evolutionEnd = Double.NaN;
  /**
   * Parameter used to calculate the pilot.
   * This field must be set in constructor of the extension.
   * @see Manoeuvre
   */
  protected double pilotAlpha = Double.NaN;
  /**
   * Parameter used to calculate the pilot.
   * This field must be set in constructor of the extension.
   * @see Manoeuvre
   */
  protected double pilotBeta = Double.NaN;
  /**
   * Parameter used to calculate the current speed.
   * This field must be set in constructor of the extension.
   * @see Manoeuvre
   */
  protected double speedGamma = Double.NaN;
  /**
   * Parameter used to calculate the pilot speed.
   * This field must be set in constructor of the extension.
   * @see Manoeuvre
   */
  protected double speedMu = Double.NaN;

  //--------------------------- Constructors ---------------------------
  /** Empty Constructor is not used */
  public FuTrajectory () {}
  /**
   * Constructor used to create a composed trajectory.
   * <p>
   * The trajectories will be automatically linked to each other with respect
   * to order of execution and their transforms. The
   * {@link FuTransform#transformT1T2(FuTransform,FuTransform) FuTransform.transformT1T2}
   * will be used to concatenate the trajectories.
   * <p>
   * The {@link #evolutionEnd evolutionEnd} and {@link #transform transform} fields of
   * this composed trajectory will be set.
   * @param   trajectories  array of trajectory-objects that will form this composed
   *                        trajectory. The trajactories in this array may be both
   *                        elementary and composed. All trajectories must have a
   *                        transform and an evolutionEnd defined on them.
   * @param   transform     the transform that will be used to position this transform
   *                        in the function-space
   */
  public FuTrajectory (FuTrajectory[] trajectories, FuTransform transform) {
    beginTrajectory = trajectories[0];
    evolutionEnd = 0;
    for (int i = 0; i < trajectories.length; i++) {
      evolutionEnd = evolutionEnd + trajectories[i].evolutionEnd;
      if (i < trajectories.length-1) { trajectories[i].afterTrajectory = trajectories[i+1]; }
      trajectories[i].transform = transform.transformT1T2(transform, trajectories[i].transform);
    }
    this.transform = transform;
  }

  //--------------------------- Methods --------------------------------
  // Returns the sub-trajectory on which evolution u is defined
  private FuTrajectory getSubTrajectory(double u) {
    if ( u < 0) { return null; }
    FuTrajectory helper = beginTrajectory;
    while (helper != null) {
      if ( (u > helper.evolutionEnd ) && (helper.afterTrajectory != null)) {
        u = u - helper.evolutionEnd;
        helper = helper.afterTrajectory;
      } else { return helper.getSubTrajectory(u); }
    }
    return this;
  }
  /**
   * Calculates the state at a certain distance from the beginning of this trajectory.
   * This method will call the <code>getTrajectPoint</code> method on the concerned
   * sub-trajectory.
   * <p>
   * This method needs to be overloaded in an extension of this <code>FuTrajectory</code> object.
   * Otherwise <code><b>null</b></code> will be returned.
   * @param   u   evolution value measured from the beginning of this trajectory
   * @return  state at a distance <code>u</code> on this trajectory
   */
  public FuSpace getTrajectPoint (double u) {
    if ( u < 0) { return null; }
    FuTrajectory helper = beginTrajectory;
    while (helper != null) {
      if ( (u > helper.evolutionEnd ) && (helper.afterTrajectory != null)) {
        u = u - helper.evolutionEnd;
        helper = helper.afterTrajectory;
      } else { return helper.getTrajectPoint(u); }
    }
    return null;
  }
  /**
   * Calculates the speed at a certain distance from the beginning of this trajectory.
   * This method will call the <code>getProfileSpeed</code> method on the concerned
   * sub-trajectory.
   * <p>
   * This method needs to be overloaded in an extension of this <code>FuTrajectory</code> object.
   * Otherwise a Not-A-Number (<code>Double.NaN</code>) will be returned.
   * @param   u   evolution value measured from the beginning of this trajectory
   * @return  speed at a distance <code>u</code> on this trajectory
   */
  public double getProfileSpeed(double u) {
    if ( u < 0) { return Double.NaN; }
    FuTrajectory helper = beginTrajectory;
    while (helper != null) {
      if ( (u > helper.evolutionEnd ) && (helper.afterTrajectory != null)) {
        u = u - helper.evolutionEnd;
        helper = helper.afterTrajectory;
      } else { return helper.getProfileSpeed(u); }
    }
    return Double.NaN;
  }
  /**
   * Returns the maximal evolution of this trajectory. If the trajectory is
   * composed the total evolution of all sub-trajectories will be returned.
   * @see     #evolutionEnd
   * @return  the maximal evolution of this trajectory
   */
  public double getEvolutionEnd() {  return evolutionEnd; }
  /**
   * Returns parameter used to calculate the pilot.
   * @see    #pilotAlpha
   * @return the pilotAlpha-parameter
   */
  public double getPilotAlpha(double u) {  return getSubTrajectory(u).pilotAlpha; }
  /**
   * Returns parameter used to calculate the pilot.
   * @see    #pilotBeta
   * @return the pilotBeta-parameter
   */
  public double getPilotBeta(double u) {  return getSubTrajectory(u).pilotBeta; }
  /**
   * Returns parameter used to calculate the current speed.
   * @see    #speedGamma
   * @return the speedGamma-parameter
   */
  public double getSpeedGamma(double u) {  return getSubTrajectory(u).speedGamma; }
  /**
   * Returns parameter used to calculate the pilot speed.
   * @see    #speedMu
   * @return the speedMu-parameter
   */
  public double getSpeedMu(double u) {  return getSubTrajectory(u).speedMu; }
  /**
   * Creates an information object of this trajectory.
   * If this trajectory is composed, the created String will contain the structure of the
   * composed trajectory.
   * @return a string containing information of this trajectory.
   */
  public String toString() {  return "\n" + this.toString(""); }
  private String toString(String indent) {
    String string = indent + "Trajectory = "+super.toString()+"; evolutionEnd = "+evolutionEnd;
    if (transform != null) { string += "; transform = "+this.transform.toString()+"\n"; } else { string +="; transform = null\n"; }
    if (beginTrajectory != null) {string += beginTrajectory.toString(indent + "     "); }
    if (afterTrajectory != null) {string += afterTrajectory.toString(indent); }
    return string;
  }
  /**
   * Creates a copy of this <code>FuTrajectory</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @return  an object that is a copy of this <code>FuTrajectory</code> object.
   */
  public Object clone() throws CloneNotSupportedException {
    FuTrajectory clone = null;
    clone = (FuTrajectory)super.clone();
    if (beginTrajectory != null) { clone.beginTrajectory = (FuTrajectory)beginTrajectory.clone(); }
    if (afterTrajectory != null) { clone.afterTrajectory = (FuTrajectory)afterTrajectory.clone(); }
    if (transform != null) { clone.transform = (FuTransform)transform.clone(); }
    if (extension != null) { clone.extension = (FuSpace.FuPath)extension.clone(); }
    return clone;
  }
}
