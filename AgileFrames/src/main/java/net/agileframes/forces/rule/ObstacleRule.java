package net.agileframes.forces.rule;

import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;

// introduced by inforcement, should change to the obstacle in package forces.

public class ObstacleRule // implements Rule
{
        public double horizon = 0.0;

  public void accepted(boolean firstVal)
  {
  }

  public void execute(boolean val)
  {
  }

  public boolean isActive()
  {
  return true;
  }

  public boolean evaluate(State state)
  {
  return true;
  }

  public Trajectory getTrajectory() {return null;}
  public void setTrajectory() {}
  public float getBeginEvolution() {return 0.0f;}
  public int getBeginOperator() {return 0;}
  public float getEndEvolution() {return 0.0f;}
  public int getEndOperator()    {return 0;}

}
