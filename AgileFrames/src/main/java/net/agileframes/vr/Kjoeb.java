package net.agileframes.vr;

import javax.vecmath.*;
import javax.media.j3d.*;

import com.sun.j3d.utils.geometry.ColorCube;

public class Kjoeb extends BaseGeometry{

  public BranchGroup rootBG=null;

  public Kjoeb(float scale) {
    this.rootBG = new BranchGroup();
    this.rootBG.addChild(new ColorCube(scale));
  }

  public void setColor(Color3f color) {

  }

  public BranchGroup getBG() {
    return this.rootBG;
  }
} 