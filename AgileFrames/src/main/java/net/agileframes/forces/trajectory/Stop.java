package net.agileframes.forces.trajectory;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.State;

/**
The Mover must return to the Guff. If the Mover has a body and
an avatar the avatar should disapear from universe.
*/


public class Stop extends Trajectory {

  State state = new State();

  public Stop() {
    domain = 0;
  }

  public State compute(float u) { return state; }
}