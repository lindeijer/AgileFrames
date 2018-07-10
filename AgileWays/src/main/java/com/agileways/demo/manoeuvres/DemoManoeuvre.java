package com.agileways.demo.manoeuvres;
// com
import com.agileways.demo.DemoParameters;
// net
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.xyaspace.XYATransform;

/**
 * Class used as general class for all Manoeuvres in the DemoScene.
 * Provides its children with useful parameters.
 *
 * To load all parameters, call super(direct, lane) in constructor of descendents.
 */
public class DemoManoeuvre extends Manoeuvre {
  //-- Attributes --
  protected double agv = DemoParameters.AGV_LENGTH;
  protected double l = DemoParameters.DIST_BETW_LANES;
  protected double r = DemoParameters.TURN_RADIUS;
  protected double x, y, a;
  //-- Constructor --
  public DemoManoeuvre(FuTransform transform, int direct, int lane) {
    super(transform, DemoParameters.MAX_SPEED, DemoParameters.MAX_ACCEL, DemoParameters.MAX_DECEL, DemoParameters.MAX_DEVI);
    //-- Position Parameters --
    x = DemoParameters.getX(direct, lane);
    y = DemoParameters.getY(direct, lane);
    a = DemoParameters.getAlpha(direct);
  }
  public DemoManoeuvre(FuTransform transform, int parkNr, int turnDirection, boolean anyValue) {
    super(transform, DemoParameters.MAX_SPEED, DemoParameters.MAX_ACCEL, DemoParameters.MAX_DECEL, DemoParameters.MAX_DEVI);
    //-- Position Parameters --
    double pi = Math.PI;
    if (parkNr==0) {
      x = 1.5 * l;
      y = 2 * l;
      if (turnDirection == 0) { a = 0; } else { a = pi; }
    } else {
      if (parkNr == 1) {
        x = 2 * l;
        y = 1.5 * l;
        if (turnDirection == 0) { a = -pi/2; } else { a = pi/2; }
      } else {
        if (parkNr == 2) {
          x = 1.5 * l;
          y = l;
          if (turnDirection == 0) { a = pi; } else { a = 0; }
        } else {
          x = l;
          y = 1.5 * l;
          if (turnDirection == 0) { a = pi/2; } else { a = -pi/2; }
        }
      }
    }
  }
}