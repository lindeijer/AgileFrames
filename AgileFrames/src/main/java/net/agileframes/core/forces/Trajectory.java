package net.agileframes.core.forces;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Transform;

/**
Class defining a trajectory through a state-space.


Extend this class in order to define a function from
an evolution parameter u in R to State.
Such a function could describe how a machines (functional) state should evolve.

A Trajectory is "basic" iff it is described as a function or as
a set of FunctionalStates with their associated evolutions (intermediate values must be interpolated)
The Trajectory is "composed" iff it is a sequential composition
of basic and composed trajectories. But what does this matter?

 * Created: Wed Jan 12 13:02:18 2000
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1

*/

public class Trajectory implements java.io.Serializable, Cloneable { // a function from [0..U] to State

  /**
  The first trajectory before this trajectory.
  This trajectory may be an element of a sequence of trajectories composing a super-trajectory,
  beforeTrajectory refers to the trajectory preceding this one in the sequence.
  The beforeTrajectory will be executed before this trajectory.
  */
  public Trajectory beforeTrajectory = null;

  /**
  The first trajectory after this trajectory.
  This trajectory may be an element of a sequence of trajectories composing a super-trajectory,
  afterTrajectory refers to the trajectory following this one in the sequence.
  The afterTrajectory will be executed after this trajectory.
  */
  public Trajectory afterTrajectory = null;

  /**
  The first sub-trajectory within this trajectory. Is null if this is a basic trajectory.
  If this is a composed trajectory then beginTrajectory refers to the first trajectory in the composing sequence,
  the beginTrajectory fill be executed first when this composed trajectory must be executed.
  */
  public Trajectory beginTrajectory = null;

  /**
  The last sub-trajectory within this trajectory. Is null if this is a basic trajectory.
  If this is a composed trajectory then endTrajectory refers to the last trajectory in the composing sequence,
  the endTrajectory fill be executed last when this composed trajectory must be executed.
  */
  public Trajectory endTrajectory = null;

  /**
  The domain over which this trajectory is defined. If this is a composed trajectory,
  the domain is the sum of all the sub-stajectory domains.
  */
  public float domain = 0; //Float.NaN;


  /**
   * A boolean parameter that can be switched to false in order to let the machine
   * evoluate till the end of the trajectory without imagining an obstacle at the end
   * of which it should keep a distance
   */
  public boolean obstacleAtEnd = true;


  public Trajectory() {
  }

  /**
  Constructs a composed trajectory.
  @param trajectories are compatible in the order they are presented.
  */
  public Trajectory(Trajectory[] trajectories) {
    this.beginTrajectory = trajectories[0];
    this.endTrajectory = trajectories[trajectories.length-1];
    for (int i=0; i<trajectories.length; i++) {
      // first get rid of too much decimals...
      trajectories[i].domain=((float)((int)(trajectories[i].domain*10000)))/10000;
      this.domain = this.domain + trajectories[i].domain;
      if (i>0) {
        trajectories[i].initialEvolution = trajectories[i-1].domain + trajectories[i-1].initialEvolution;
        trajectories[i].beforeTrajectory = trajectories[i-1];
      }
      if (i<trajectories.length-1) {
        trajectories[i].afterTrajectory = trajectories[i+1];
      }
    }
    this.initialEvolution = 0;
  }

  ///////////////////////////////////////////////////////////////

  /**
  Computes a state f on this trajectory that has a minimal distance to another (non-trajectory) state g.
  If there are multiple states on the trajectory that satisfy this condition then state
  corresponding with the least evolution is selected.
  @param state to be projected onto the trajectory.
  @return the evolution corresponding with the states projection.
  */
  public float project(State state) {
    System.out.println("Trajectory.project is not implemented");
    return Float.NaN;
  }

  /**
  Computes the distance between the trajectory and a state
  @param state estimated by the statefinder
  @param evolution estimated by the statefinder
  */
  public float distance(State state,float evolution) {return Float.NaN;}

  ///////////////////////////////////////////////////////////////

  public State currentValue = null;

  /**
  Computes the trajectory-state with respect to initialEvolution and initialTransform
  which are set by the machine during execution of the trajectory.
  <ol>
  <li>computes localEvolution=u-initialEvolution, 0<=localEvolution<=domain.
  <li>computes the localState with respect to localEvolution
  <li>computes absoluteState=initialTransform.transform(localState)
  <ol>
  You must overload this method to define a basic-trajectory,
  the default implementation works for composed trajectories.
  @param u the absolute evolution of the machine itself, initialEvolution<=u<=initialEvolution+domain
  @return the computed absolute trajectory-state.
  */
  public State compute(float u, State container) {
    if (u- initialEvolution<0)           { container.u = Float.NaN; return container; }
    if (u- initialEvolution>this.domain) { container.u = Float.NaN; return container; }
    container = null;
    Trajectory helper = beginTrajectory;
    do {
      if (u - helper.initialEvolution > helper.domain) {
        helper = helper.afterTrajectory;
      }
      else {
        container = helper.compute(u);
        if (helper.initialTransform!=null) {container = helper.initialTransform.transform(container);}
        helper = null;
      }
    } while (helper!=null);
    if (container==null) {
      // if may never have been true due to float-roundings
      // u may still be a fraction greater than zero
      // a fraction greater than the last trajectories[i].domain
      container = getEnd();
    }
    container.u = u;
    return container;
  }

  // this method is the best implementation of the two compute()s
  public State compute(float u) {
    if (u - initialEvolution < 0)      { return null; }
    if (u - initialEvolution > (domain+0.001) ) { return null; }
    currentValue = null;
    Trajectory helper = beginTrajectory;
    do {
      if ((u - helper.initialEvolution > helper.domain )&& (helper.afterTrajectory==null)){
        u = helper.initialEvolution + helper.domain - 0.001f;
      }
      if (u - helper.initialEvolution > helper.domain ) {
        helper = helper.afterTrajectory;
      }
      else {
        // this is to prevent errors due to rounding:
        if (u<helper.initialEvolution) {u=helper.initialEvolution;}
        if (u>helper.initialEvolution + helper.domain) {u = helper.initialEvolution + helper.domain;}

        currentValue = helper.compute(u);
        if (helper.initialTransform!=null) {
          currentValue = helper.initialTransform.transform(currentValue);
        }
        helper = null;
      }
    } while (helper!=null);

    if (currentValue==null) {
        currentValue = getEnd();
      // if may never have been true due to float-roundings
      // u may still be a fraction greater than zero
      // a fraction greater than the last trajectories[i].domain
    }
    //currentValue.u = u;
    return currentValue;
  }


  /////////////////////////////////////////////////////////////////////////
  // depricated method
  /** Evoloution is a simply a float, this should be deprecated */
  public String getDomainClass() {
    if (beginTrajectory == null) { return "float"; }
    return beginTrajectory.getDomainClass();
  }

  /**
  Evoloution is a float, by default is is associated with meters.
  You may overload this method to indecate that you associate evolution with another unit.
  @return the unit evolution is associated with.
  */
  // depricated method
  public String getDomainUnit() {
    if (beginTrajectory == null) { return "meter"; }
    return beginTrajectory.getDomainUnit();
  }

  /**
  Trajectories are defined with respect to a state-space,
  the state-space spawned by a pre-defined class. F:float=->class.
  @return the name of the class that spawns the state-space.
  */
  // depricated method
  public String getRangeClass()  {
    if (beginTrajectory == null) {
      return "net.agileframes.core.State";
    }
    return beginTrajectory.getRangeClass();
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  Appends a trajectory to this trajectory.
  This implementation assumes a composed trajectory.
  @param trajectory is assumed to be coherent wrt the end of this trajectory.
  This implies ...
  */
  public void append(Trajectory trajectory) {
    synchronized(this){
      trajectory.domain=((float)((int)(trajectory.domain*10000)))/10000;

      float initialU = endTrajectory.initialEvolution + endTrajectory.domain;
      initialU=((float)((int)(initialU*10000)))/10000;//get rid of too much decimals
      trajectory.setComposedEvolution(initialU);

      endTrajectory.afterTrajectory = trajectory;
      trajectory.beforeTrajectory = endTrajectory;
      endTrajectory = trajectory;
      domain = domain + trajectory.domain;
    }
  }

  /**
   * Adds certain value to this trajectory including all sub-trajectories. Evolution
   * can be nagative in order to substract a value. This method is needed when a
   * composedTrajectory is added to another (composed)Trajectory.
   *
   * @param evolution evolution to be added to this trajectory
   */
  public synchronized void setComposedEvolution(float evolution) {
    this.initialEvolution += evolution;
    Trajectory helper = beginTrajectory;
    while (helper!=null){
      if (helper.beginTrajectory!=null) {
        helper.setComposedEvolution(evolution);
      } else {
        helper.initialEvolution += evolution;
      }
      helper = helper.afterTrajectory;
    }
  }

  /**
   * Rips this trajectory out of its context. Sets initialEvolution
   * back to zero and removes links to after- and beforeTrajectories.
   */
  public synchronized void reset() {
    this.afterTrajectory = null;
    this.beforeTrajectory = null;
    this.setComposedEvolution(-this.initialEvolution);
  }

  /**
   * Sets initialTransform of this trajectory in such a way that this trajectory
   * is glued to the selected trajectory. With the parameter transform, the intialTransform
   * can be set relatively to the end of trajectory.
   *
   * @param trajectory      the Trajectory at the end of which the initialTransform of
   *                        this trajectory has to point.
   * @param transform       the Transform that represents the difference between the end of
   *                        trajectory and the initialTransform.
   *                        transform is null if initialTransform need to be set at the end of
   *                        trajectory.
   */
  public synchronized void append(Trajectory trajectory, Transform transform) {
    if (transform == null) {
      setTransform(trajectory.initialTransform.add(trajectory.ownTransform));
    }
    else {
      setTransform(trajectory.initialTransform.add(trajectory.ownTransform).add(transform));
    }
    this.initialEvolution = trajectory.initialEvolution + trajectory.domain;
  }

  /** delete trajectories[0] and shift-left the references */
  // depricated method
  public synchronized void decapitate() {
    domain = domain - beginTrajectory.domain;
    beginTrajectory = beginTrajectory.afterTrajectory;
    beginTrajectory.beforeTrajectory = null;
  }

  /////////////////////////////////////////////////////////////////

  /**
  get dF(u(t))/dt, the first derivative of u wtr t,
  you could interpret this as velocity
  */
  // depricated method
//  public float get_du(float u) {
  /*
    if (u<0)           { return Float.NEGATIVE_INFINITY; }
    if (u>this.domain) { return Float.POSITIVE_INFINITY; }
    float _du = Float.NaN;
    for (int i=0;i<trajectories.length;i++) {
      float trajectory_i_domain = trajectories[i].domain;
      if (u <= trajectory_i_domain) {
        _du=trajectories[i].get_du(u);
        break;
      }
      u = u - trajectories[i].domain;
    }
    return _du;
    */
//    return Float.NaN;
//  }

  /**
  get ddF(u(t)/dt, the second derivative of u wtr t,
  you could interpret this as accelleration
  */
//  public float get_ddu(float u) {
    /*
    if (u<0)           { return Float.NEGATIVE_INFINITY; }
    if (u>this.domain) { return Float.POSITIVE_INFINITY; }
    float _ddu = Float.NaN;
    for (int i=0;i<trajectories.length;i++) {
      float trajectory_i_domain = trajectories[i].domain;
      if (u <= trajectory_i_domain) {
        _ddu=trajectories[i].get_du(u);
        break;
      }
      u = u - trajectories[i].domain;
    }
    return _ddu;
      */
//    return Float.NaN;
//  }

  ///////////////////////////////////////////////////////////////////////

  /**
  The transform of the trajectory itself, this is the transform you would experience if
  you followed this trajectory from the beginning to the end.
  */
  public Transform ownTransform = null;

  /**
  The transform at the beginning of this trajectory in an operational environment.
  The initialTransform corresponds with the state of the machine has when it starts the trajectory.
  */
  public Transform initialTransform = null;

  /**
  The evolution at the beginning of this trajectory in an operational environment.
  The initialEvolution is the (cumulative) evolution of the machine has when it starts the trajectory.
  */
  public float initialEvolution = 0;

  /**
  Sets the initial transform
  */
  public void setTransform(Transform transform) { initialTransform = transform; }

  /**
  Sets the initial evolution
  */
  public void setEvolution(float u) { initialEvolution = u;}

  /**
  Gets the state at the beginning of the trajectory wrt the initialTransform.
  */
  public State getBegin() {
    State f = null;
    if (beginTrajectory != null) { f = beginTrajectory.getBegin(); }
    else { f = compute(0+initialEvolution); }
    if (initialTransform!=null) {return initialTransform.transform(f);}
    else {return f;}
  }

  /**
  Gets the state at the end of the trajectory wrt the initialTransform.
  */
  public State getEnd() {
    State f = null;
    if (endTrajectory != null) { f = endTrajectory.getEnd(); }
    else { f = compute(domain+initialEvolution-0.001f); }
    if ((initialTransform!=null)&&(f!=null)) {return initialTransform.transform(f);}
    else {return f;}
  }

  /**
   * Prints this trajectory including all sub-trajectories on the screen, showing connections,
   * with specified initialEvolution and domain.
   */
  public void toOutput(String indent){
    System.out.println(indent+"Trajectory="+this.toString()+"  initialEvolution="+this.initialEvolution+"  domain="+this.domain);
    Trajectory helper = beginTrajectory;
    while (helper!=null){
      if (helper.beginTrajectory!=null) {
        helper.toOutput(indent+"     ");
      } else {
        System.out.println(indent+"     Trajectory="+helper.toString()+"  initialEvolution="+helper.initialEvolution+"  domain="+helper.domain);
      }
      helper = helper.afterTrajectory;
    }
  }

  /**
   * Gets the clone of this Trajectory-object.
   *
   * @return clone
   */
  public Trajectory getClone(){
    Trajectory clone = null;
    try{
      clone = (Trajectory) this.clone();
    } catch(Exception e) {
      System.out.println("Error in Trajectory.getClone():"+e.getMessage());e.printStackTrace();
    }
    Trajectory lastHelper = null;
    Trajectory lastHelperClone = null;
    Trajectory helper = beginTrajectory;
    Trajectory helperClone = null;
    if (helper!=null) {helperClone = helper.getClone();}
    clone.beginTrajectory = helperClone;

    while (helper!=null){
      if (lastHelperClone!=null) {
        lastHelperClone.afterTrajectory = helperClone;
      }
      helperClone.beforeTrajectory = lastHelperClone;

      if (helper.afterTrajectory == null) {
        clone.endTrajectory = helperClone;
      }

      lastHelper = helper;
      lastHelperClone = helperClone;
      helper = helper.afterTrajectory;
      if (helper!=null) {helperClone = helper.getClone();} else {helperClone=null;}
    }
    return clone;
  }
}
