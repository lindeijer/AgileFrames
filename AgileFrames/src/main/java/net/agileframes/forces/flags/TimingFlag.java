package net.agileframes.forces.flags;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Timing Flag. </b>
 * <p>
 * The Timing-Flag will be raised if and only if the time to complete this manoeuvre
 * is larger than or as large as the prescribed completion time.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class TimingFlag extends Flag {
  private long completionTime;
  private double deceleration;
  /**
   * Default Constructor.<p>
   * Only sets these three parameters.
   * @param manoeuvre       the manoeuvre on which this Flag is defined
   * @param completionTime  the prescribed completion time
   * @param deceleration    the prescribed maximal deceleration
   */
  public TimingFlag(Manoeuvre manoeuvre, long completionTime, double deceleration) {
    this.completionTime = completionTime;
    this.deceleration = deceleration;
    this.manoeuvre = manoeuvre;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * time to complete this manoeuvre  >=  prescribed completion time
   * </blockquote></code>
   * The time to complete this manoeuvre is calculated by dividing the current
   * speed with the prescribed maximal deceleration.
   */
  public void evaluate() {// wrong calculation of timeToEnd!!
    long timeToEnd = (long)(1000 * (manoeuvre.getCalcSpeed() / deceleration));
    if (timeToEnd >= completionTime) { raise(); }
  }
}

