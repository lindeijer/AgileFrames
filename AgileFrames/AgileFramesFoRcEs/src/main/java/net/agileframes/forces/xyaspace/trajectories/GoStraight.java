package net.agileframes.forces.xyaspace.trajectories;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyaspace.*;
/**
 * <b>A straight trajectory to the right with parameterized length.</b>
 * <p>
 * <img SRC="doc-files/GoStraight-1.gif" height=300 width=400> <br>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class GoStraight extends FuTrajectory {
  private double length = Double.NaN;
  /**
   * Default Constructor.<p>
   * All generic constants will have default values:<br>
   * pilotAlpha=2.0<br>
   * pilotBeta=2.0<br>
   * speedGamma=0.75<br>
   * speedMu=0.25<br>
   * @see   #GoStraight(double,XYATransform,double,double,double,double)
   */
  public GoStraight(double length, XYATransform transform) {
    this(length, transform, /*0.5*/2.0, /*2.5*//*0.1*/2.0, /*0.25*/0.75, 0.25);
  }
  /**
   * Specific Constructor.<p>
   * All constants will be set. The evolution-end will be calculated.
   * The extension will be calculated.
   * @param length      the length of this trajectory (in meters)
   * @param transform   the xya-transform associated with this trajectory
   * @param pilotAlpha  a parameter used to calculate the pilot
   * @param pilotBeta   a parameter used to calculate the pilot
   * @param speedGamma  a parameter used to calculate the speed
   * @param speedMu     a parameter used to calculate the pilot-speed
   */
  public GoStraight(double length, XYATransform transform, double pilotAlpha,
                    double pilotBeta, double speedGamma, double speedMu) {
    this.length = length;
    this.transform = transform;
    this.pilotAlpha = pilotAlpha;
    this.pilotBeta = pilotBeta;
    this.speedGamma = speedGamma;
    this.speedMu = speedMu;

    this.evolutionEnd = length;
    FuSpace endPoint = new XYASpace(length, 0, 0);
    extension = endPoint.createPath(new XYASpace(length + 1, 0, 0));
  }

  public FuSpace getTrajectPoint(double u) {
    if (u > evolutionEnd) { return transform.transform( extension.getConnectionPoint(u - evolutionEnd) ); }
    return transform.transform(new XYASpace(u, 0, 0));
  }

  public double getProfileSpeed(double u) {
    if (u > evolutionEnd) { return 0; }
    return 1;
  }
}
