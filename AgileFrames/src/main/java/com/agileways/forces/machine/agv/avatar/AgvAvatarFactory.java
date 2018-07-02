package com.agileways.forces.machine.agv.avatar;
import net.agileframes.core.vr.*;

public class AgvAvatarFactory extends AvatarFactory {

  public AgvAvatarFactory() {}

  /**
  Create an avatar for this type of agv-body,
  dont forget to setBodyLater

  */
  public Avatar getAvatar(Body body) {
    return new AvatarAGV();
  }

} 