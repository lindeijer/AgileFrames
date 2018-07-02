package net.agileframes.forces.rule;
import net.agileframes.core.forces.State;

// u >= trajectory.initialEvolution + domain 

public abstract class FinishedRule {

  public FinishedRule() {
  }

  public abstract boolean evaluate(State state);
  public abstract void execute(boolean val);
  public abstract void accepted(boolean firstVal);

}


