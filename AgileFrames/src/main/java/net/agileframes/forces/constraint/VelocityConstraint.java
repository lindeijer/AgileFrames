package net.agileframes.forces.constraint;
import net.agileframes.forces.flag.AbstractVelocityFlag;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Constraint;

public class VelocityConstraint extends AbstractVelocityFlag implements Constraint {

  public VelocityConstraint() {
  }

  public VelocityConstraint(
      Trajectory trajectory,
      float evolution, int evolutionOperator,
      float velocity,  int velocityOperator){
    super(trajectory,evolution,evolutionOperator,velocity,velocityOperator);
  }

} 