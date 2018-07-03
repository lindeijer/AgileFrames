package net.agileframes.core.vr;

import net.agileframes.core.vr.AvatarFactory;
import com.agileways.vr.agv.AvatarAGV;


public class AgvAvatarFactory extends AvatarFactory {
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
}
