package net.agileframes.core.traces;
import net.agileframes.core.forces.FuSpace;
/**
 * <b>Data-object containing information about a position.</b>
 * <p>
 * The use of this data-object is to have a generic format of (logistic)
 * positions. Every Scene has its own set of logistic positions where an
 * actor can drive to (i.e. parking places). In every Scene, these positions
 * are indexed in a way that is specific for that scene.<br>
 * This object can be used to exchange information about positions without
 * knowing the format of that position.<br>
 * A Logistic Position is identified using its Scene, name and parameters.
 * <p>
 * @see Scene
 * @see Scene#whereAmI(FuSpace)
 * @see Scene#getClosestLogisticPosition(FuSpace)
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
// data-object
public class LogisticPosition implements java.io.Serializable {
  //-- Attributes --
  /** Name of the position. Must be unique in the context of its Scene, may be generic in the context of the Scene-tree. */
  public String name;
  /** Absolute location in the function space of its Scene. */
  public FuSpace location;
  /** The Scene in which this Logistic Position is located. */
  public Scene scene;
  /** The semaphore that controls the acces to this position. */
  public SemaphoreRemote semaphore;
  /** The indexation of this position in its Scene. */
  public int[] params;

  //-- Constructor --
  /**
   * Basic Constructor with all necessary information in the context of SceneActions.
   * @see SceneAction
   * @param name    the name of the position
   * @param scene   the scene in which this position is located
   * @param params  the parameters that index this position
   */
  public LogisticPosition(String name, Scene scene, int[] params) { this(name, null, scene, null, params); }
  /**
   * Detailed Constructor with all information in the context of the Join-SceneAction, for example.
   * @param name      the name of the position
   * @param location  location in the function space
   * @param scene     the scene in which this position is located
   * @param semaphore the semaphore that controls this position
   * @param params    the parameters that index this position
   */
  public LogisticPosition(String name, FuSpace location, Scene scene, SemaphoreRemote semaphore, int[] params) {
    this.name = name;
    this.location = location;
    this.scene = scene;
    this.semaphore = semaphore;
    if (params != null) {
      this.params = new int[params.length];
      for (int i = 0; i < params.length; i++) {  this.params[i] = params[i]; }
    }
  }
  /**
   * Returns information about this Logistic Position.<p>
   * The information contains the name, scene, semaphore and location of
   * this position (if available).
   * @return a string with information about this Logistic Position.
   */
  public String toString() {
    String s = "LogisticPosition: "+name;
    try {
      if (scene != null) s += " in "+scene.getName();
      if (semaphore != null) s += " (semaphore="+semaphore.getName()+")";
      if (location != null) s += "\nLocation = "+location.toString();
    } catch (Exception e) { e.printStackTrace(); }
    return s;
  }
  /**
   * Returns name (including parameters) of this Logistic Position.
   * The parameters will be added to the {@link #name name} using
   * dots (".") (for example: park.0.1).
   * @return a string containing the name of this Logistic Position
   */
  public String getName() {
    String s = this.name;
    if (params != null) {
      for (int i = 0; i < params.length; i++) { s += "."+params[i]; }
    }
    return s;
  }
  /**
   * Checks if two Logistic Positions are the same.
   * Two positions are the same if and only if:<ul>
   * <li>their {@link #name names} are the same
   * <li>their {@link #scene scenes} are the same
   * <li>their {@link #params params} are the same
   * </ul>
   * @param obj the <code>Logistic Position</code> object that should be compared with this position.
   * @return <code><b>true</b></code> if and only if the two positions are the same <br>
   *         <code><b>false</b></code> otherwise
   */
  public boolean equals(Object obj) {
    if (obj == null) { return false; }
    LogisticPosition lp = (LogisticPosition) obj;
    if (!name.equals(lp.name)) { return false; }
    if (!scene.equals(lp.scene)) { return false; }//changed 11 July 2001 (HW)
    if (params != null) {
      if (lp.params == null) { return false; }
      if (lp.params.length != params.length) { return false; }
      for (int i = 0; i < params.length; i++) { if (params[i] != lp.params[i]) { return false; } }
    }
    if (lp.params != null) { return false; }
    return true;
  }
}