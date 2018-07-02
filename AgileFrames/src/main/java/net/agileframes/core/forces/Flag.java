package net.agileframes.core.forces;
import java.io.Serializable;

/**
A flag is a boolean expression to be evaluated in the context of an
operational machine. The flag interface should be implemented by a class
defining a boolean conditions evaluatable by the machine.

A flag may be associated with a trajectory.
*/

public interface Flag extends Serializable {

  public static final int EQUALS        = 0;
  public static final int GREATER       = 1;
  public static final int GREATER_EQUAL = 2;
  public static final int LESS          = 3;
  public static final int LESS_EQUAL    = 4;

  ////////////////////////////////////////////////////////

  public Trajectory getTrajectory();
  public float getEvolution();
  public int getEvolutionOperator();

}