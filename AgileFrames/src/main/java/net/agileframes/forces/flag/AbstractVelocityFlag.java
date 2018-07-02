package net.agileframes.forces.flag;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Machine.VelocityFlag;
import net.agileframes.core.forces.Flag;

/**
Defines a velocity-function wrt the machine evolution. If the
flag is associated with a trajectory the velocity-function is
defined wrt the domain of the trajectory.
<p>
Assuming u(t) is the realized evolution of the machine
then v(u)==du(t)/dt and a(u)==ddu(t)/ddt, evolution-velocity and
evolution-accelleration repectively.
The velocity flag may assumes the following form:
<ul>
<li>begin beginOperator u endOperator end AND
<li>v(u) velocityOperator getVelocity(u)
<li>begin may be null, this in to be interpreted and Float.NEGATIVE_INFINITY.
<li>end may be null, this in to be interpreted and Float.POSITIVE_INFINITY.
</ul>

<p>For example:
<ul>
<li>begin==14, end==52, begin && end in [0,trajectory.domain]
<li>beginOperator is <=, endOperator is <=, velocityOperator is >=
<li>getAccelleration(u) == 2.5
<li>for (14 <= u <= 52) { v(u)>=2.5 }
<li>the above is true if the machine velocity is greater than 2.5
    within the specified range within the trajectory.
</ul>

*/



public abstract class AbstractVelocityFlag extends AbstractFlag
    implements VelocityFlag {

  public AbstractVelocityFlag() {}

  public AbstractVelocityFlag(
      Trajectory trajectory,
      float evolution, int evolutionOperator,
      float velocity,  int velocityOperator){
    super(trajectory,evolution,evolutionOperator);
    this.velocity = velocity;
    this.velocityOperator = velocityOperator;
  }

  /////////////////////////////////////////////////////////////////////////////

  public float velocity = Float.NaN;
  public int velocityOperator = -1;

  public float getVelocity() { return velocity; }
  public float getVelocity(float evolution) { return Float.NaN; }
  public int getVelocityOperator() { return velocityOperator; }



} 