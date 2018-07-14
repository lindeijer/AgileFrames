package net.agileframes.forces.precautions;
import net.agileframes.core.forces.*;
public class PassedStop extends Precaution {
  //-- Attributes --
  private double evolution;
  //-- Constructor --
  // be sure to not give an param deceleration which is higher than max deceleration
  // of the manoeuvre!!
  public PassedStop(Manoeuvre manoeuvre, double evolution, double deceleration) {
    this.manoeuvre = manoeuvre;
    this.evolution = evolution;
    this.deceleration = deceleration;
  }
  //-- Methods --
  public double getDeceleration() {
    //System.out.println("checking precaution "+this.toString()+"  evolution="+evolution);
    if (!active) {
      //System.out.println("..not-active.. checking precaution "+this.toString()+"  evolution="+evolution);
      return Double.NaN;
    }
    //System.out.println("precaution is active!");
    double cycleT = this.manoeuvre.cycleTime;
    if (Double.isNaN(cycleT)) { cycleT = 1.0; }//to be sure

    double dec = Double.NaN;// always positive
    double v = manoeuvre.getCalcSpeed();// always positive
    double d = evolution - manoeuvre.getCalcEvolution();
    //System.out.println("v= "+v+"  d="+d);
//    if (d < 0) { System.out.println("## d < 0 ## checking precaution "+this.toString()+"  evolution="+evolution); }
        //else { System.out.println("........... checking precaution "+this.toString()+"  evolution="+evolution); }
    if (d < 0) { return 0; }//complete stand-still

    double sst =  v / deceleration;// slow-stopping-time
    sst = cycleT * Math.ceil(sst / cycleT); // because we cannot end the traject in the middle of a cycle...

    double cycleD = cycleT * v;//distance that we will drive within next cycle if we dont decelerate
    double sd = v * sst - 0.5 * deceleration * sst * sst;// stopping-distance: xt = x0 +v0t + 1/2 at2
    double dec2 = v*v / (2*d);//deceleration to slow down in before d meters are gone.
    if (sd + cycleD > d) {  dec = dec2; }

    //System.out.println("returned dec = "+dec);

    return dec;
  }

  public String toString() {
    return "PassedStop(evolution="+evolution+", dec="+this.deceleration+") active="+isActive()+", current speed = "+manoeuvre.getCalcSpeed()+"  current distance to go = "+(manoeuvre.getTrajectory().getEvolutionEnd() - manoeuvre.getCalcEvolution());
  }

}