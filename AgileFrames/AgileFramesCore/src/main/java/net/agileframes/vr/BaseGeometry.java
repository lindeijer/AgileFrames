package net.agileframes.vr;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
/**
 * <b>BaseGeometry is the abstract base class for all possible geometries used with (descendants of) Avatar.</b><p>
 * @author  F.A. van Dijk
 * @version 0.1
 */

public abstract class BaseGeometry {
  /**
   * Returns the top most BranchGroup of this geometry.<p>
   * This branchgroup is the actual 'add' point of the geometry
   * @return  the top branch-group
   */
  public abstract BranchGroup getBG();
  /**
   * Sets the color of the geometry.<p>
   * @param c the new color of the geometry
   */
  public abstract void setColor(Color3f color);
  /**
   * Sets the text on this geometry.<p>
   * @param text the String to be displayed
   */
  public abstract void setText(String text);
}
