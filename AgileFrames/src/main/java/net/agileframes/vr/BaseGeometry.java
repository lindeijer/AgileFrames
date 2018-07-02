package net.agileframes.vr;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;

/**
 * This class serves as a base class for all possible geometries used with (descendants of) AvatarImplBase
 * Because all geometries must show the same behavior.....
 * @author van Dijk
 * @version 0.0.1
 */

public abstract class BaseGeometry {

  /**
   * Get the top most BranchGroup of this geometry
   * This branchgroup is the actual 'add' point of the geometry
   */
  public abstract BranchGroup getBG();

  /**
   * Set the color of the geometry
   * @param c the new color of the geometry
   */
  public abstract void setColor(Color3f c);
}
