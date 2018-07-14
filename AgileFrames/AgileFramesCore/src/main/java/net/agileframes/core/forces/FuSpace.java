package net.agileframes.core.forces;
import java.io.Serializable;
/**
 * <b>The Function Space.</b>
 * <p>
 * The FuSpace models the function-space. Note that
 * this object should be extended to implement the methods in a certain space
 * (for example: 2DSpace, 3DSpace, XYSpace, XYASpace, etc.). Some methods
 * in this object have more than one signature for programming-flexibility.
 *  <p>
 * Abstract class, including inner-class FuSpace.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public abstract class FuSpace implements Serializable {
  /**
   * Calculates the state-distance between two points. <p>
   * The state-distance is used
   * as a measure for state-deviations with respect to the prescribed trajectory.
   * <p>
   * Overload this method in your specific implementation and define a static method.
   * @param   point1  first state
   * @param   point2  second state
   * @return  state-distance between point1 and point2
   * @see     #stateDistance(FuSpace)
   * @see     #evolutionDistance(FuSpace, FuSpace)
   */
  public abstract double stateDistance (FuSpace point1, FuSpace point2);
  /**
   * Calculates the state-distance between this object and another point
   * @param   point   state to be compared with this object
   * @return  state-distance between this object and point
   * @see     #stateDistance(FuSpace,FuSpace)
   */
  public double stateDistance (FuSpace point) { return stateDistance(this, point); }

  /**
   * Calculates the evolution-distance between two points. <p>
   * The evolution-distance is used
   * as a measure for evolutionary progress. In some implementations, the evolution-distance
   * can be the same as the {@link #stateDistance(FuSpace,FuSpace) state-distance}
   * <p>
   * Overload this method in your specific implementation and define a static method.
   * @param   point1  first state
   * @param   point2  second state
   * @return  evolution-distance between point1 and point2
   * @see     #evolutionDistance(FuSpace)
   * @see     #stateDistance(FuSpace, FuSpace)
   */
  public abstract double evolutionDistance (FuSpace point1, FuSpace point2);
  /**
   * Calculates the evolution-distance between this object and another point
   * @param   point   state to be compared with this object
   * @return  evolution-distance between this object and point
   * @see     #evolutionDistance(FuSpace,FuSpace)
   */
  public double evolutionDistance (FuSpace point) { return evolutionDistance(this, point); }

  /**
   * Creates a path between two points. <p>
   * The shape of the path is defined in the specific implementation.
   * Use {@link FuPath#getConnectionPoint(double) FuPath.getConnectionPoint} to calculate a point on the path.
   * <p>
   * Overload this method in your specific implementation.
   * @param   point1  start-state of the path
   * @param   point1  end-state of the path
   * @return  path between point1 and point2
   * @see     #createPath(FuSpace)
   * @see     FuPath
   * @see     FuPath#getConnectionPoint(double)
   */
  public abstract FuPath createPath (FuSpace point1, FuSpace point2);
  /**
   * Creates a path between this object and another point.
   * @param   point   end-state of the path
   * @return  path between this object and point
   * @see     #createPath(FuSpace, FuSpace)
   */
  public FuPath createPath (FuSpace point) { return createPath(this, point); }


  /**
   * Path between two states.
   * <p>
   * This object should be created with {@link FuSpace#createPath(FuSpace,FuSpace) FuSpace.createPath}.
   * Use {@link #clone() clone} to create a copy of this object.
   * @author  H.J. Wierenga, D.G. Lindeijer
   * @version 0.1
   */
  public abstract class FuPath implements Cloneable, Serializable {
    /**
     * Constructor not used.
     * This object should be created with {@link FuSpace#createPath(FuSpace,FuSpace) FuSpace.createPath}.
     */
    public FuPath() {}
    /**
     * Calculates a point on the path. <p>
     * The point at a distance d from the start-point
     * will be calculated. The specific implementation of this method will
     * prescribe where this point is located, but it is not necessary linear.
     * @param   d   distance on the path, measured from the start-state
     * @return  the state located at a distance d on the path
     * @see     FuSpace#createPath(FuSpace, FuSpace)
     */
    public abstract FuSpace getConnectionPoint(double d);
    /**
     * Creates a copy of this object.<p>
     * Use this method to create a copy of the object if you want to make sure
     * all values and references will stay intact.
     * @return an object that is a copy of this <code>FuSpace</code> object.
     */
    public Object clone() throws CloneNotSupportedException { return super.clone(); }
  }
}

