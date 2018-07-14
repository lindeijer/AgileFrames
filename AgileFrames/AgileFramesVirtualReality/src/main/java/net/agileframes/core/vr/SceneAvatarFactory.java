package net.agileframes.core.vr;

import net.agileframes.vr.space3d.SceneAvatar3D;

/**
 * <b>An AvatarFactory for a Scene. </b>
 * <p>
 * An implementation of the Entry interface that knows to cast the service
 * object to type Avatar and instantiates and returns a new Avatar object
 * <p>
 * This class currently is only equiped with methods for obtaining space2d and
 * space3d avatars.
 * 
 * @see net.agileframes.core.traces.Scene
 * @see BodyRemote
 * @see Avatar
 * @see Virtuality
 * @author H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class SceneAvatarFactory extends AvatarFactory {
	/** Empty Constructor. Needed for every Entry. */
	public SceneAvatarFactory() {
	}

	public Avatar getAvatar(Class virtualityType, Body body) { return new SceneAvatar3D(body); }
}
