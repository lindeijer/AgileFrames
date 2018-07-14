package net.agileframes.core.vr;
import net.agileframes.vr.space3d.SceneAvatar3D;
/**
 * <b>An AvatarFactory for a Scene. </b>
 * <p>
 * An implementation of the Entry interface that
 * knows to cast the service object to type Avatar
 * and instantiates and returns a new Avatar object
 * <p>
 * This class currently is only equiped with methods for obtaining space2d
 * and space3d avatars.
 * @see net.agileframes.core.traces.Scene
 * @see BodyRemote
 * @see Avatar
 * @see Virtuality
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class SceneAvatarFactory extends AvatarFactory {
  /** Empty Constructor. Needed for every Entry. */
  public SceneAvatarFactory(){}
  /**
   * Returns an (Scene)Avatar for a 2D virtuality.
   * @param   body  the body for which the avatar is needed
   * @return  the (2d-)avatar
   */
  protected Avatar getAvatar2D(Body body) { return null; }
  /**
   * Returns an (Scene)Avatar for a 3D virtuality.
   * @param   body  the body for which the avatar is needed
   * @return  the (3d-)avatar
   */
  protected Avatar getAvatar3D(Body body) { return new SceneAvatar3D(body); }
}
