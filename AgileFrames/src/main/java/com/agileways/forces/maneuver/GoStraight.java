package com.agileways.forces.maneuver;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;

/**
 * A straight maneuver with parameterized length.
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1
*/


public class GoStraight extends Trajectory {
  float length;

  public GoStraight() {
  }

  /**
   * @param length float contains the length of the maneuver.
   */
  public GoStraight(float length) {
    this.length = length;
    this.domain = length;
    this.ownTransform = new POSTransform(length,0,0);
  }

 /**
  * Computes position.
  * @param u float being the evolution parameters on this trajectory.
  */
  public State compute(float u) {
    u = u - initialEvolution;
    if (u < 0)      { return null; }
    if (u > domain) { return null; }
    float x = u;
    float y = 0;
    float orientation = 0;
    if (currentValue == null) {currentValue = new POS();}
    ((POS)currentValue).x = x;
    ((POS)currentValue).y = y;
    ((POS)currentValue).alpha = orientation;

    return currentValue;

  }
}

