package com.agileways.forces.maneuver;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;


/**
 * A crab to the left with parameterized crab-distance.
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1
*/

public class CrabLeft extends Trajectory{
  float distance;

  public CrabLeft() {}

  /**
   * @param distance float that contains the distance between the two parallel lanes between which the crab will be performed.
   */
  public CrabLeft(float distance) {
    this.distance = distance;
    this.domain = (float)3.5142*distance;
    this.ownTransform = new POSTransform((float)3.3168*distance,distance,0);
  }

 /**
  * Computes position.
  * @param u float being the evolution parameters on this trajectory.
  */
  public State compute(float u) {
    u = u - initialEvolution;
    if (u < 0)      { return null; }
    if (u > domain) { return null; }

    float x, y, orientation;
    if (u <= domain/2) {
      x = 3*distance * (float)Math.sin(u/(3*distance));
      y = 3*distance * (1 - (float)Math.cos(u/(3*distance)));
      orientation = 0;
    }
    else {
      x = (float)(3.3176*distance - 3*distance * Math.sin(1.1714-u/(3*distance)));
      y = (float)(distance        - 3*distance * (1 - Math.cos(1.1714-u/(3*distance))));
      orientation = 0;
    }
    if (currentValue == null) {currentValue = new POS();}
    ((POS)currentValue).x = x;
    ((POS)currentValue).y = y;
    ((POS)currentValue).alpha = orientation;

    return currentValue;

  }

}
