package net.agileframes.forces.space;
import net.agileframes.core.forces.Trajectory;


public class Obstacle { // obstactle in space    \

  public Obstacle(){}

  public Obstacle(float offset) {
    this.offset = offset;
  }

  public Obstacle(POS position,Orientation orientation,float velocity,float acceleration) {
    this.position = position;
    this.orientation = orientation;
    this.velocity = velocity;
    this.acceleration = acceleration;
  }

  public Obstacle(Trajectory trajectory,float horizon) {
    this.trajectory = trajectory;
    this.horizon = horizon;
  }

  public Obstacle(Trajectory trajectory) {
    this.trajectory = trajectory;
    this.horizon = trajectory.initialEvolution + trajectory.domain;
  }

  public POS position = null;
  public Orientation orientation = null;
  public float velocity = Float.NaN;
  public float acceleration = Float.NaN;
  public float offset = Float.NaN;
  //
  public Trajectory trajectory = null;
  public float horizon = Float.NaN;

}





