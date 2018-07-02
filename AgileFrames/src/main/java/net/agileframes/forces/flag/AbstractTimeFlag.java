package net.agileframes.forces.flag;
import net.agileframes.core.forces.Machine.TimeFlag;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Flag;

/**
Defines a time-function wrt the machine evolution. If the
flag is associated with a trajectory the velocity-function is
defined wrt the domain of the trajectory.
<p>
Assuming u(t) is the realized evolution of the machine
then u(t) == getTime(u).
The time flag may assumes the following form:
<ul>
<li>begin beginOperator u endOperator end AND
<li>u(t) timeOperator getTime(u).
<li>begin may be null, this in to be interpreted and Float.NEGATIVE_INFINITY.
<li>end may be null, this in to be interpreted and Float.POSITIVE_INFINITY.
</ul>

<p>For example:
<ul>
<li>begin==34, end==34, begin == end in [0,trajectory.domain]
<li>beginOperator is <=, endOperator is <=, TimeOperator is ==
<li>getTime(u) == arrivalTime
<li>for (u == 34) { u(t)==arrivalTime }
<li>the above is true if the machine will arrive at u==34
    at the specified arrivalTime.
</ul>

*/

public abstract class AbstractTimeFlag extends AbstractFlag
    implements TimeFlag {

  public long time;
  public int timeOperator;

  public long getTime() { return time; }
  public long getTime(float evolution) { return -1; }
  public int getTimeOperator() { return timeOperator; }

}