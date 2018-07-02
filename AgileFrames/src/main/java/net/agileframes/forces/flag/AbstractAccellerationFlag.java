package net.agileframes.forces.flag;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Machine.AccellerationFlag;
import net.agileframes.core.forces.Flag;

/**
Defines an accelleration-function wrt the machine evolution. If the
flag is associated with a trajectory the accelleration-function is
defined wrt the domain of the trajectory.
<p>
Assuming u(t) is the realized evolution of the machine
then v(u)==du(t)/dt and a(u)==ddu(t)/ddt, evolution-velocity and
evolution-accelleration repectively.
The accelleration flag may assumes the following form:
<ul>
<li>begin begin_operator u end_operator end AND
<li>a(u) accellerationOperator getAccelleration(u).
<li>begin may be null, this in to be interpreted and Float.NEGATIVE_INFINITY.
<li>end may be null, this in to be interpreted and Float.POSITIVE_INFINITY.
</ul>

<p>For example:
<ul>
<li>begin==2, end==56, begin && end in [0,trajectory.domain]
<li>beginOperator is <=, endOperator is <=, accellerationOperator is <=
<li>getAccelleration(u) == 0.2
<li>for (2 <= u <= 56) { a(u)<=0.2 }
<li>the above is true if the machine accelleration is less than 0.2
    within the specified range within the trajectory.
</ul>

*/

public abstract class AbstractAccellerationFlag extends AbstractFlag
    implements AccellerationFlag {

 public float accelleration = Float.NaN;
 public int accellerationOperator  = -1;

  public float getAccelleration(float evolution) { return Float.NaN; }
  public float getAccelleration() { return  accelleration; }
  public int getAccellerationOperator() { return accellerationOperator; }


}
