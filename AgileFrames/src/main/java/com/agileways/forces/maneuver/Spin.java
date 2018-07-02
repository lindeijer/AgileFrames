package com.agileways.forces.maneuver;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;

/**
 * A spin around axis with x,y staying constant. Only to be used for visualization purposes in simulated context.
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1
*/

public class Spin extends Trajectory {

  public Spin() {
  }

  private double angle;

  /**
   * @param angle is angle around which to spin (in radians)
   */
  public Spin(double angle) {
    this.angle=angle;
    this.domain=(float)Math.abs(angle*10);
    this.ownTransform = new POSTransform(0,0,angle);
  }

 /**
  * Computes position.
  * @param u float being the evolution parameters on this trajectory.
  */
  public State compute(float u) {
    u = u - initialEvolution;
    if (u < 0)      { System.out.println("Spin returns null u="+u+"  domain="+domain+"  initialEvolution="+initialEvolution);return null; }
    if (u > domain) { System.out.println("Spin returns null u="+u+"  domain="+domain+"  initialEvolution="+initialEvolution);return null; }
    //float x = 0;
    //float y = 0;
    //double orientation = angle;//(float)((angle*10/domain)*u/10);
    if (currentValue == null) {currentValue = new POS();}
    ((POS)currentValue).x = 0;
    ((POS)currentValue).y = 0;
    ((POS)currentValue).alpha = (angle*10/domain)*u/10;

    return currentValue;

  }
}

