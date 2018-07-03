package net.agileframes.forces.xyaspace.trajectories;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyaspace.*;
/**
 * <b>A circular bend to the left with parameterized radius and endAngle.</b>
 * <p>
 * <img SRC="doc-files/CircularBendLeft-1.gif" height=400 width=400> <br>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class CircularBendLeft extends FuTrajectory {
  //--- Attributes ---
  private double radius, endAngle;
  //--- Constructors ---
  /**
   * Default Constructor.<p>
   * All generic constants will have default values:<br>
   * pilotAlpha=2.5<br>
   * pilotBeta=1.5<br>
   * speedGamma=0.45<br>
   * speedMu=0.25<br>
   * @see   #CircularBendLeft(double,double,XYATransform,double,double,double,double)
   */
  public CircularBendLeft(double radius, double endAngle, XYATransform transform){
    this(radius,
         endAngle,
         transform,
         /*radius/5*/2.5,
         /*2.5*/1.5,
         /*0.25*/0.45,
         0.25);
  }
  /**
   * Specific Constructor.<p>
   * All constants will be set. The evolution-end will be calculated.
   * The extension will be calculated.
   * @param radius      the radius of this circular-bend (in meters)
   * @param endAngle    the end-angle of this circular-bend (in radians)
   * @param transform   the xya-transform associated with this bend
   * @param pilotAlpha  a parameter used to calculate the pilot
   * @param pilotBeta   a parameter used to calculate the pilot
   * @param speedGamma  a parameter used to calculate the speed
   * @param speedMu     a parameter used to calculate the pilot-speed
   */
  public CircularBendLeft(
    double radius,
    double endAngle,
    XYATransform transform,
    double pilotAlpha,
    double pilotBeta,
    double speedGamma,
    double speedMu) {

    this.radius = radius;
    this.endAngle = endAngle;
    this.transform = transform;
    this.pilotAlpha = pilotAlpha;
    this.pilotBeta = pilotBeta;
    this.speedGamma = speedGamma;
    this.speedMu = speedMu;
    this.evolutionEnd = radius * endAngle;

    double endX = radius * Math.sin(endAngle);
    double endY = radius * (1 - Math.cos(endAngle));
    double endA = endAngle;
    FuSpace endPoint = new XYASpace(endX, endY, endA);

    double extX = endX + Math.cos(endAngle);
    double extY = endY + Math.sin(endAngle);
    double extA = endA;
    extension = endPoint.createPath(new XYASpace(extX, extY, extA));
  }

  //--- Methods ---
  public FuSpace getTrajectPoint(double u) {
    if (u > evolutionEnd) { return transform.transform( extension.getConnectionPoint(u - evolutionEnd) ); }
    double x = radius * Math.sin(u / radius);
    double y = radius - radius * Math.cos(u / radius);
    double alpha = u / radius;

    return transform.transform(new XYASpace(x, y, alpha));
  }

  public double getProfileSpeed(double u) {
    if (u > evolutionEnd) { return 0; }
    //if (u < 0.7*evolutionEnd) { return 0.8; }
    return 1;
  }
}