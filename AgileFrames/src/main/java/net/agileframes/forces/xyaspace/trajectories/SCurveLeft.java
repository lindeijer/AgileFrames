package net.agileframes.forces.xyaspace.trajectories;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyaspace.*;
/**
 * <b>A S-curve to the left with parameterized length and width.</b>
 * <p>
 * <img SRC="doc-files/SCurveLeft-1.gif" height=200 width=400> <br>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class SCurveLeft extends FuTrajectory {
  //--- Attributes ---
  private double length;     // length of S-Curve
  private double width;      // width of S-Curve
  private double radius;     // radius of S-Curve
  private double max_radians;// radians for a half S-Curve
  //--- Constructors ---
  /**
   * Default Constructor.<p>
   * All generic constants will have default values:<br>
   * pilotAlpha=2.5<br>
   * pilotBeta=1.5<br>
   * speedGamma=0.45<br>
   * speedMu=0.25<br>
   * @see   #SCurveLeft(double,double,XYATransform,double,double,double,double)
   */
  public SCurveLeft(double length, double width, XYATransform transform){
    this(length,
         width,
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
   * @param length      the length of this S-curve (in meters)
   * @param width       the width of this S-curve (in radians)
   * @param transform   the xya-transform associated with this trajectory
   * @param pilotAlpha  a parameter used to calculate the pilot
   * @param pilotBeta   a parameter used to calculate the pilot
   * @param speedGamma  a parameter used to calculate the speed
   * @param speedMu     a parameter used to calculate the pilot-speed
   */
  public SCurveLeft(
    double length,
    double width,
    XYATransform transform,
    double pilotAlpha,
    double pilotBeta,
    double speedGamma,
    double speedMu) {

    this.length = length;
    this.width = width;
    this.transform = transform;
    this.pilotAlpha = pilotAlpha;
    this.pilotBeta = pilotBeta;
    this.speedGamma = speedGamma;
    this.speedMu = speedMu;

    this.radius = ( width + length*length/width) / 4;
    this.max_radians = Math.asin(length/(2*radius));
    this.evolutionEnd = max_radians * radius * 2;

    FuSpace endPoint = new XYASpace(length, width, 0);
    extension = endPoint.createPath(new XYASpace(length + 1, width, 0));
  }

  //--- Methods ---
  public FuSpace getTrajectPoint(double u) {
    if (u > evolutionEnd) { return transform.transform( extension.getConnectionPoint(u - evolutionEnd) ); }

    double x, y, alpha;
    if (u < (evolutionEnd / 2)) {
      alpha = u / radius;
      x = radius * Math.sin(alpha);
      y = radius * (1 - Math.cos(alpha));
    } else {
      alpha = max_radians - (u - evolutionEnd / 2) / radius;
      x = length - radius * Math.sin(alpha);
      y = width - radius * (1 - Math.cos(alpha));
    }

    return transform.transform(new XYASpace(x, y, alpha));
  }

  public double getProfileSpeed(double u) {
    if (u > evolutionEnd) { return 0; }
    //if ((u > 0.35 * evolutionEnd) && (u < 0.6 * evolutionEnd)) { return 1; }
    //if (u > 0.8 * evolutionEnd) { return 1; }
    //return 0.8;
    return 1.0;
  }
}