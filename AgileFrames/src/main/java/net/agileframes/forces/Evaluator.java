package net.agileframes.forces;
import net.agileframes.core.forces.Flag;

public interface Evaluator {

  public void evaluateRules(Flag[] rules,TrajectoryState trajectoryState);

} 