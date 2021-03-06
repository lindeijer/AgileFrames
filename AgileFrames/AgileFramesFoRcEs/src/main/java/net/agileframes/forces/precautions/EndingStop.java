package net.agileframes.forces.precautions;
import net.agileframes.core.forces.Precaution;
/**
 * <b>The Ending Stop Precaution.</b>
 * <p>
 * Not Implemented yet.
 * @author  H.J. Wierenga
 * @version 0.1
 */
public abstract class EndingStop extends Precaution {

  public EndingStop() {
  }
}
/*
//----------------------- Predefined Classes ------------------------
class EndingStop extends Precaution {
  private double toDoLength = Double.NaN;
  private double evolutionEnd = Double.NaN;
  public EndingStop(double toDoLength, double deceleration, StateFinder stateFinder, FuTrajectory trajectory){
    this.toDoLength = toDoLength;
    this.deceleration = deceleration;// absolute [0..maxDeceleration]
    this.stateFinder = stateFinder;
    this.evolutionEnd = trajectory.getEvolutionEnd();
  }
  public double getDecFactor(){
    if (!isActive()) { return Double.NaN; }
    double distanceToEnd = evolutionEnd - stateFinder.getEstimatedEvolution();
    if (toDoLength > distanceToEnd) { return deceleration; }
    else { return Double.NaN; }
  }
}

class TimedStop extends Precaution {
  private long stopTime;
  private double evolutionEnd = Double.NaN;
  public TimedStop(long stopTime, double deceleration, StateFinder stateFinder, FuTrajectory trajectory){
    this.stopTime = stopTime;
    this.deceleration = deceleration;// absolute [0..maxDeceleration]
    this.stateFinder = stateFinder;
    this.evolutionEnd = trajectory.getEvolutionEnd();
  }
  public double getDecFactor(){
    if (!isActive()) { return Double.NaN; }
    // first make sure that we will (or: are able to) stop at the end of the trajectory.
    double distanceToEnd = evolutionEnd - stateFinder.getEstimatedEvolution();
    double decelerationToEnd = 0.5 * Math.pow(stateFinder.getEstimatedSpeed(),2) / distanceToEnd;
    if (decelerationToEnd > deceleration) { return decelerationToEnd; }
    // next make sure we can stop within stopTime.
    long slowStoppingTime = (long)(1000 * stateFinder.getEstimatedSpeed() / deceleration);
    if (stopTime < slowStoppingTime) { return deceleration; }
    else { return Double.NaN; }
  }
}

class DeviationSlowDown extends Precaution {
  private double maxDeviation = Double.NaN;
  public DeviationSlowDown (double maxDeviation, double deceleration, StateFinder stateFinder){
    this.maxDeviation = maxDeviation;
    this.deceleration = deceleration;// absolute [0..maxDeceleration]
    this.stateFinder = stateFinder;
  }
  public double getDecFactor(){
    if (!isActive()) { return Double.NaN;}
    double deviation = stateFinder.getEstimatedDeviation();
    if (deviation > maxDeviation) { return deceleration; }
    else { return Double.NaN; }
  }
}
*/