package net.agileframes.forces.rule;
import net.agileframes.core.forces.Flag;
import net.agileframes.forces.TrajectoryState;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.core.forces.Rule;
import net.agileframes.forces.Evaluator;

public class EvaluatorImplBase implements Evaluator {

  MachineImplBase machine=null;

  public EvaluatorImplBase(MachineImplBase machine) {
    this.machine=machine;
  }

  public void evaluateRules(Flag[] rules,TrajectoryState trajectoryState) {

    for (int i=0;i<rules.length;i++) {
      if (rules[i] != null) {
        //Rule rule_i = (Rule)rules[i];
        if (((Rule)rules[i]).isActive() == true) {//rule_i
          evaluateRule((Rule)rules[i],trajectoryState);//(Rule)
          //System.out.println("EvaluatorImplBase.evaluateRule evaluated " + rules[i].toString());
        }
        else {
          machine.remove((Rule)rules[i]);//rule_i
        }
      } else {
        break;
      }
    }
  }

  public void evaluateRule(Rule rule,TrajectoryState trajectoryState) {
    if (rule instanceof EvolutionRule) {
      evaluateEvolutionRule((EvolutionRule)rule,trajectoryState);
      return;
    }
    if (rule instanceof TerminatingRule) {
      evaluateTerminatingRule((TerminatingRule)rule,trajectoryState);
      return;
    }
    System.out.println("EvaluatorImplBase.evaluateRule can only evaluate evolutionrules and terminatingrules, sorry");
  }

  public void evaluateEvolutionRule(EvolutionRule evolutionRule,TrajectoryState trajectoryState) {
    float u = trajectoryState.state.u;
    float U = evolutionRule.getEvolution();
    //System.out.println("     evaluating EvolutionRule, trajectoryState.state.u=" + u + " and U=" + U);
    //System.out.println("     still evaluating EvolutionRule, cu");
    boolean evaluationValue = false;
    switch (evolutionRule.evolutionOperator) {
      case Flag.GREATER_EQUAL : { if(u>=U) { evaluationValue = true; } break; }
      case Flag.GREATER :       { if(u> U) { evaluationValue = true; } break; }
      case Flag.LESS :          { if(u< U) { evaluationValue = true; } break; }
      case Flag.LESS_EQUAL :    { if(u<=U) { evaluationValue = true; } break; }
      case Flag.EQUALS :        { if(u==U) { evaluationValue = true; } break; }
    }
    if (evolutionRule.isRaised() != evaluationValue) {
      evolutionRule.setRaised(evaluationValue);
      evolutionRule.execute(evaluationValue);
    }
  }

  private float terminationMargin = 0.5f;

  public void evaluateTerminatingRule(TerminatingRule terminatingRule,TrajectoryState trajectoryState) {
    float u = trajectoryState.state.u;
    float U = terminatingRule.getEvolution();
    float velocity = trajectoryState.velocity;
    float d = U - u - terminationMargin;
    float a = machine.responseModel[1];
    float maxVelocity = (float)Math.sqrt(2*a*d);
    boolean evaluationValue = false;
    if (velocity >= maxVelocity) { evaluationValue = true; }
    if (terminatingRule.isRaised() != evaluationValue) {
      terminatingRule.setRaised(evaluationValue);
      terminatingRule.execute(evaluationValue);
    }
  }

}


