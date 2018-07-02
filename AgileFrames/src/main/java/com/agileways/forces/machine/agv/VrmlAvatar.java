package com.agileways.forces.machine.agv;

import javax.media.j3d.*;
import javax.vecmath.*;

import net.agileframes.vr.VrmlAvatarFactory;
import net.agileframes.vr.BaseGeometry;

public class VrmlAvatar extends BaseGeometry {

  BranchGroup rootBG = null;

  public VrmlAvatar(String location) {
    // this.rootBG = VrmlAvatarFactory.getAvatar(location);
  }

  public void setColor(Color3f c) {
    // do nothing
  }

  public BranchGroup getBG() {
    return this.rootBG;
  }
}