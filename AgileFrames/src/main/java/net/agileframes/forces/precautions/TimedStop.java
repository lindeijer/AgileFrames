package net.agileframes.forces.precautions;
import net.agileframes.core.forces.*;
/**
 * <b>The Timed Stop Precaution.</b>
 * <p>
 * Precaution that has two objectives:<br>
 * 1. Make sure that the SlowStoppingTime(SST - The time needed to come to a standstil
 *    if the regular deceleration is used) will not be larger than the parametric time.
 *    [this is to control the time needed for stopping]<br>
 * 2. Make sure that the end of the manoeuvre will not be passed.<p>
 *
 * If the two objectives are being kept with the current deceleration, the return-value
 * of getDeceleration() is Double.NaN, else it returns the prescribed deceleration.
 * If a emergency stop is needed, 0.0 is returned.
 *
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class TimedStop extends Precaution {
  //-- Attributes --
  private double time;
  /** Tool for debugging, default=FALSE */
  public final boolean DEBUG = false;
  //-- Constructor --
  // be sure to not give an param deceleration which is higher than max deceleration
  // of the manoeuvre!!
  /**
   * Default Constructor.<p>
   * Only sets these three parameters.
   * @param manoeuvre     the manoeuvre on which this Precaution is defined
   * @param time_sec      the prescribed time (in seconds) in which it always
   *                      must be possible to come to a stand-still
   * @param deceleration  the prescribed deceleration to be used to stop slowly.
   *                      Deceleration must always be > 0
   */
  public TimedStop(Manoeuvre manoeuvre, double time_sec, double deceleration) {
    this.manoeuvre = manoeuvre;
    this.time = time_sec;
    this.deceleration = deceleration;
  }
  //-- Methods --
  /**
   * Calculates the deceleration to keep this precaution.<p>
   * Objective 1:
   * <blockquote>
   *      <code>v</code> = current speed
   *      <code>d</code> = prescribed deceleration
   *      <code>t</code> = prescribed time in seconds
   *      slow-stopping-time: <code>sst = v / d</code>
   *      deceleration: <code>dec1 = v / t</code>
   * </blockquote>
   * Objective 2:
   * <blockquote>
   *      stopping-distance: <code>sd = v * sst - 0.5 * d * sst<sup>2</sup> </code>
   *      deceleration: <code>dec2 = v<sup>2</sup> / (2*d) </code>
   * </blockquote>
   */
  public double getDeceleration() {
    if (!active) { return Double.NaN; }
    double cycleT = this.manoeuvre.cycleTime;
    if (Double.isNaN(cycleT)) { cycleT = 1.0; }//to be sure

    double dec = Double.NaN;// always positive
    double v = manoeuvre.getCalcSpeed();// always positive
    double d = manoeuvre.getTrajectory().getEvolutionEnd() - manoeuvre.getCalcEvolution();

    if (d < 0) { return 0; }//complete stand-still

    if (DEBUG) { System.out.println("*D* TimedStop(1): dec(0) = "+dec+"  curr speed = "+v+"  curr dist to end = "+ d); }
    //-- Objective 1 --
    double sst =  v / deceleration;// slow-stopping-time

    sst = cycleT * Math.ceil(sst / cycleT); // because we cannot end the traject in the middle of a cycle...

    if (sst > time) {
      //objective 1 is valid
      dec = v / time;
    }
    if (DEBUG) { System.out.println("*D* TimedStop(2): dec(1) = "+dec+"  slow-stopping-time = "+sst); }


    //-- Objective 2 --
    double cycleD = cycleT * v;//distance that we will drive within next cycle if we dont decelerate
    double sd = v * sst - 0.5 * deceleration * sst * sst;// stopping-distance: xt = x0 +v0t + 1/2 at2
    double dec2 = v*v / (2*d);//deceleration to slow down in before d meters are gone.
    if (sd + cycleD > d) {
      //objective 2 is valid
      if (Double.isNaN(dec)) { dec = 0; }
      dec = Math.max(dec, dec2);
    }

      //if (dec < 1.0) { dec = 0; }// complete stand-still

    if (DEBUG) { System.out.println("*D* TimedStop(3): dec(2) = "+dec+"  stopping-distance = "+sd); }
    return dec;
  }

  public String toString() {
    return "TimedStop(time="+time+", dec="+this.deceleration+") active="+isActive()+", current speed = "+manoeuvre.getCalcSpeed()+"  current distance to go = "+(manoeuvre.getTrajectory().getEvolutionEnd() - manoeuvre.getCalcEvolution());
  }

}