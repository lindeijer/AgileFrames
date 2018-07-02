package net.agileframes.forces.rule;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.flag.AbstractFlag;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.State;

public abstract class TerminatingRule extends AbstractFlag implements Rule {

  public TerminatingRule(Trajectory trajectory) {
    super(trajectory,trajectory.domain,Flag.EQUALS);
  }

  public boolean evaluate(State state) {
    System.out.println("TerminatingRule should be evaluated by the machines evaluator, now it is always false");
    return false;
  }

  public abstract void execute(boolean val);


} 