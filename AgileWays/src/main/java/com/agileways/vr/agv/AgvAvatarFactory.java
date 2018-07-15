package com.agileways.vr.agv;

import com.agileways.vr.agv.AvatarAGV;
import net.agileframes.core.vr.Body;
import net.agileframes.vr.space3d.AvatarFactory3D;
import net.agileframes.core.vr.Avatar;
/**
 * Entry for an AGV
 */

public class AgvAvatarFactory extends AvatarFactory3D {
  // only the public fields are available of an entry
  // and they must have an empty constructor
  // is this true? i wrote this after 3-4 months..
  public String text;
  public Integer appearance;

  public AgvAvatarFactory(){}

  public AgvAvatarFactory(int agvNr) {
    this.appearance = Integer.getInteger("", Math.abs(agvNr));
    this.text = String.valueOf(agvNr);
  }

  protected Avatar getAvatar2D(Body body) { return null; }
  protected Avatar getAvatar3D(Body body) {
    AvatarAGV avatar = new AvatarAGV(body, 0);
    avatar.setGeometryAndAppearanceID(1, appearance.intValue());
    avatar.setText(text);
    return avatar;
  }

	@Override
	public Avatar getAvatar(Class virtualityType, Body body) {
		return getAvatar3D(body);
	}
}
