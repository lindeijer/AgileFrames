package com.agileways.forces.machine.agv.avatar;

import net.agileframes.vr.AvatarImplBase;
import net.agileframes.core.vr.Body;
import net.agileframes.forces.space.POS;
import net.agileframes.vr.BaseGeometry;

import com.agileways.forces.machine.agv.sim.SimAGV;
import com.agileways.forces.machine.agv.AGV;

import javax.media.j3d.*;
import javax.vecmath.*;

public class AvatarAGV extends AvatarImplBase {

  public AvatarAGV() {
    this(null, 0);
  }

  public AvatarAGV(Body agv) {
    this(agv,0);
  }

  public AvatarAGV(Body agv, int frames) {
    super(agv,frames);
    //addGeometry(new VrmlAvatar("/export/home/david/sourcebase/com/agileways/forces/machine/agv/ols1.wrl"));
    addGeometry(new SimpleAgvEmpty());
    addGeometry(new SimpleAgvLoaded());

    //addAppearance(new Color3f(0.0f, 0.0f, 0.0f));//2
    addAppearance(new Color3f(0.0f, 0.0f, 1.0f));//1
    addAppearance(new Color3f(0.0f, 1.0f, 0.0f));//4
    addAppearance(new Color3f(0.0f, 1.0f, 1.0f));//3
    addAppearance(new Color3f(1.0f, 0.0f, 0.0f));//6
    addAppearance(new Color3f(1.0f, 0.0f, 1.0f));//5
    addAppearance(new Color3f(1.0f, 1.0f, 0.0f));//8
    addAppearance(new Color3f(1.0f, 1.0f, 1.0f));//7

    this.setGeometryAndAppearanceID(0, 0); //default
  }

}

