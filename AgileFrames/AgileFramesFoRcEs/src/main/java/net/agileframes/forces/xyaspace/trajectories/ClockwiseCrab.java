package net.agileframes.forces.xyaspace.trajectories;

import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;

public class ClockwiseCrab extends FuTrajectory {
  private double distance = Double.NaN;

  public ClockwiseCrab(double distance, XYATransform transform) {
    this(distance, transform, Math.max(0.5, distance/5), 2.5, 0.25, 0.25);
  }
  public ClockwiseCrab(double distance, XYATransform transform, double pilotAlpha,
                       double pilotBeta, double speedGamma, double speedMu) {
    this.distance = distance;
    this.transform = transform;
    this.pilotAlpha = pilotAlpha;
    this.pilotBeta = pilotBeta;
    this.speedGamma = speedGamma;
    this.speedMu = speedMu;

    this.evolutionEnd = 3.514 * distance;
    FuSpace endPoint = new XYASpace(3.317 * distance, -distance, 0);
    extension = endPoint.createPath(new XYASpace(4 * distance, -distance, 0));
  }

  public FuSpace getTrajectPoint(double u) {
    if (u > evolutionEnd) { return transform.transform( extension.getConnectionPoint(u - evolutionEnd) );}
    double x, y;

    if (u <= evolutionEnd/2) {
      x =   3 * distance * Math.sin( u / (3*distance) );
      y = - 3 * distance * (1 - Math.cos( u / (3*distance) ));
    } else {
      x = 3.317 * distance - 3 * distance * Math.sin( 1.171 - u / (3*distance) );
      y =       - distance + 3 * distance * (1 - Math.cos( 1.171 - u / (3*distance) ));
    }
    double alpha = 0;
    return transform.transform(new XYASpace(x, y, alpha));
  }

  public double getProfileSpeed(double u) {
    if (u > evolutionEnd) { return 0; }
    if (u < 0.7*evolutionEnd) { return 0.60; }
    if (u < 0.8*evolutionEnd) { return 0.75; }
    if (u < 0.9*evolutionEnd) { return 0.90; }
    return 1;
  }
}
