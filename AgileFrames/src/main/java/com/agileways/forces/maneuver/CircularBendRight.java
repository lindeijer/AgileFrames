package com.agileways.forces.maneuver;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;


/**
 * A circular bend to the right with parameterized radius, p and endAngle.
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1
*/

public class CircularBendRight extends Trajectory {
  float radius;
  float p;
  float endAngle = (float)(Math.PI/2);

  public CircularBendRight() {}

 /**
  * @param radius   float indicating the radius of the circular bend.
  * @param p  float indicating the distance between vehicle center and turning point of vehicle.
  * @param endAngle float contains the angle in radians that have to be made by this bend.
  */
  public CircularBendRight(float radius, float p, float endAngle) {
    this.endAngle = endAngle;
    this.p=p;
    this.radius=radius;
    this.domain = endAngle*radius;
    this.ownTransform = new POSTransform(radius*(float)Math.sin(endAngle),-radius+radius*(float)Math.cos(endAngle),-endAngle);
  }

  /**
   * EndAngle is set on a half times PI.
   */
  public CircularBendRight(float radius, float p) {
    this(radius,p,(float)(Math.PI/2));
  }



 /**
  * Computes position.
  * @param u float being the evolution parameters on this trajectory.
  */
  public State compute(float u) {
    u = u - initialEvolution;
    if (u < 0)      { return null; }
    if (u > domain) { return null; }
    float radian = (float)(u/(Math.sqrt(radius*radius + p*p)));
    float x = (float)(radius * Math.sin(radian) + p * Math.cos(radian));
    float y = (float)(-radius + radius * Math.cos(radian) - p * Math.sin(radian));
    float orientation = -radian;
    if (currentValue == null) {currentValue = new POS();}
    ((POS)currentValue).x = x;
    ((POS)currentValue).y = y;
    ((POS)currentValue).alpha = orientation;

    return currentValue;

  }

}
