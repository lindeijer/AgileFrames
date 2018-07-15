package net.agileframes.vr.space3d;

import net.agileframes.core.vr.Avatar;
import net.agileframes.core.vr.AvatarFactory;
import net.agileframes.core.vr.Body;

public class AvatarFactory3D extends AvatarFactory {

	public Avatar getAvatar(Class virtualityType, Body body) {
		System.out.println("AvatarFactory creates Avatar");
		if (virtualityType.equals(net.agileframes.vr.space3d.Virtuality3D.class)) {
			return getAvatar3D(body);
		}
		// else if (virtualityType.equals(net.agileframes.vr.space2d.Virtuality2D.class)
		// ) { return getAvatar2D(body); }
		else {
			throw new ClassCastException("AvatarFactory could not find right ...");
		}
	}

	/**
	 * Returns an Avatar for a 2D virtuality.
	 * 
	 * @param body
	 *            the body for which the avatar is needed
	 * @return the (2d-)avatar
	 */
	protected Avatar getAvatar2D(Body body) {
		return null;
	}

	/**
	 * Returns an Avatar for a 3D virtuality.
	 * 
	 * @param body
	 *            the body for which the avatar is needed
	 * @return the (3d-)avatar
	 */
	protected Avatar getAvatar3D(Body body) {
		return null;
	}

}
