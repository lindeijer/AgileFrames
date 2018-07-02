package net.agileframes.forces.trajectory;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.State;

/**
The Mover is in the Guff. Send a Mover this move and it will be born,
it will be delivered into the world. If the Mover has a body and
an avatar the avatar should visualize in the universe.
*/

public class Start extends Trajectory {

  State state = null;

  public Start(State state) {
    this.state = state;
    domain = 0;
  }

  public State compute(float u) { return state; }

  class StartState extends State {
    public float distance(State state) { return 0; }
  }
}