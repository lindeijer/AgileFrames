package net.agileframes.core.vr;
import net.jini.entry.AbstractEntry;
import java.lang.ClassCastException;


/**
 * <b>An entry-object necessary if Avatar is needed in a remote Virtuality. </b>
 * <p>
 * An implementation of the Entry interface that
 * knows to cast the service object to type Avatar
 * and instantiates and returns a new Avatar object
 * <p>
 * This class currently is only equiped with methods for obtaining space2d
 * and space3d avatars.
 * @see Avatar
 * @see Virtuality
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class AvatarFactory extends AbstractEntry {
  /** Empty Constructor. Needed for every Entry. */
  public AvatarFactory() {}
  /**
   * Retrieves the right Avatar-type coresponding to the uploaded Body
   * @param Body The machine-descendant object which has been uploaded
   * @return Avatar corresponding to the Body uploaded
   */
  public Avatar getAvatar(Class virtualityType, Body body) {
    System.out.println("AvatarFactory creates Avatar");
    if (virtualityType.equals(net.agileframes.vr.space3d.Virtuality3D.class) ) {  return getAvatar3D(body); }
//    else if (virtualityType.equals(net.agileframes.vr.space2d.Virtuality2D.class) ) {  return getAvatar2D(body); }
    else throw new ClassCastException("AvatarFactory could not find right ");
  }
  /**
   * Returns an Avatar for a 2D virtuality.
   * @param   body  the body for which the avatar is needed
   * @return  the (2d-)avatar
   */
  protected Avatar getAvatar2D(Body body) { return null; }
  /**
   * Returns an Avatar for a 3D virtuality.
   * @param   body  the body for which the avatar is needed
   * @return  the (3d-)avatar
   */
  protected Avatar getAvatar3D(Body body) { return null; }
}
