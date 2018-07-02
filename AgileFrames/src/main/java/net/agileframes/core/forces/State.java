package net.agileframes.core.forces;
import net.agileframes.core.forces.Machine;

/**

 The functional state of a machine.
 A state is associated with an machine (entity), the time the state occured,
 and optionally with the evolution-level upon which it occurred.

 * Created: Wed Jan 12 12:59:43 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

 */


public class State implements java.io.Serializable,Cloneable {

  public State() {}

  /** the machine this is the state of */
  public Machine machine;

  /** the time when this state occurred */
  public long t;

  /** the evolution level when this state occurred, set together with t */
  public float u;

  ////////////////////////////////////////////////////////////////////

  /** the state computed before this one (shortcut to the state.t==t-dt) */
  public State before;

  /** the state computed after this one (shortcut to the state.t==t+dt) */
  public State after;

  ///////////////////////////////////////////////////////////////////

  /**
  Distance between two states within this state-space.
  Overload this method in your state definition, make a static method.
  @param state 1
  @param state 2
  @return distance between state 1 and 2
  */
  public float distance(State state) { return Float.NaN; }

  /**
  Adds this state to another state, makes sense iff distance does.
  @param state to be added to this state.
  @return this state plus state.
  */
  public State add(State state) { return state; }

  public State subtract(State state) { return state; }

  public Object clone() {
    State clone = new State();
    clone.machine = machine;
    clone.t = t;
    clone.u = u;
    return clone;
  }

  public boolean equals(State state) {
    if (this == state) {return true;}
    else {return false;}
  }

  ////////////////////////////////////////////////////////////////////////

  public float uVelocity = Float.NaN;
  public float uAccelleration = Float.NaN;

}


