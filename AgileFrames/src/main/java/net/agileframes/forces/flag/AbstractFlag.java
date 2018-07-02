package net.agileframes.forces.flag;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Flag;

/**
trajectory, U, evolutionOperator.
U = trajectory.initialEvolution + U.
u >= U is the default : the flag is raised when U is passed.

*/


public abstract class AbstractFlag implements Flag {

  public AbstractFlag() {}

  /**
  @param trajectory the flag is associated with
  @param evolution relative to the trajectory (not the absolute evolution at run-time).
  @param evolution operator indicating beyond, before or at the evolution point.
  */
  public AbstractFlag(Trajectory trajectory,float evolution,int evolutionOperator) {
    this.evolution = evolution;
    this.evolutionOperator = evolutionOperator;
    this.setTrajectory(trajectory);
  }

  public AbstractFlag(Trajectory trajectory,float evolution) {
    this(trajectory,evolution,Flag.GREATER_EQUAL);
  }



  protected boolean isRaised = false;
  public boolean isRaised() { return isRaised; }
  public void setRaised(boolean evaluationValue) { isRaised = evaluationValue; }

  ///////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////

  public Trajectory trajectory = null; // set upon arrival at the machine
  private float evolution = Float.NaN;
  public float absoluteEvolution = Float.NaN;
  public int evolutionOperator = -1;

  public Trajectory getTrajectory() { return trajectory; }  // may be null

  public void setTrajectory(Trajectory trajectory) { // also called by the machine after acceptance
    this.trajectory = trajectory;
    if (trajectory != null) {
      this.absoluteEvolution = trajectory.initialEvolution + this.evolution;
    }
  }

  public float getEvolution() { return absoluteEvolution; }
  public int getEvolutionOperator() { return evolutionOperator; }

  protected boolean isActive = true;
  public boolean isActive() { return isActive; }
  public void setActive(boolean active) { isActive = active; }


}