package net.agileframes.forces.rule;

//import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.flag.AbstractFlag;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.State;

/**
 * Created: Mod Feb 7 13:02:18 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

*/

public abstract class EvolutionRule extends AbstractFlag implements Rule {

  public EvolutionRule() {}

  /**
  The rules boolean expression is true beyond the evolution.
  @param trajectory
  @param evolution <= u and 0 <= evolution <= trajectory.domain.
  */
  public EvolutionRule(Trajectory trajectory,float evolution) {
    super(trajectory,evolution,Flag.GREATER_EQUAL);
  }

  public boolean evaluate(State state) {
    System.out.println("EvolutionRules should be evaluated by the machines evaluator, now it is always false");
    return false;
  }

  public abstract void execute(boolean val);
 

}

