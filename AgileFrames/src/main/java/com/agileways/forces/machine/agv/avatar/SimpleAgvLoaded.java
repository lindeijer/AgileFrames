package com.agileways.forces.machine.agv.avatar;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;

import net.agileframes.vr.Color3D;

public class SimpleAgvLoaded extends SimpleAgvEmpty{

  private ColoringAttributes containerCA;

  private final float containerX=40*12*0.0254f;
  private final float containerY=8*12*0.0254f;
  private final float containerZ=8*12*0.0254f;

  public SimpleAgvLoaded(float scale, Color3f colorcar){

  }

  public SimpleAgvLoaded(float scale) {
    super(scale);
    this.containerCA = new ColoringAttributes();
    Appearance containerApp = new Appearance();
    containerCA.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    containerApp.setColoringAttributes(containerCA);

    containerCA.setColor(0.0f, 1.0f, 0.0f);

    this.vectrans.set(0.0, 0.0, containerZ/2);
    this.vectrans.scale(scale);
    this.t3d.set(vectrans);
    TransformGroup tgContainer = new TransformGroup(t3d);

    Box container = new Box(scale*containerX/2, scale*containerY/2, scale*containerZ/2, containerApp);
    tgContainer.addChild(container);
    this.tgTop.addChild(tgContainer);
  }

  public SimpleAgvLoaded(float scale, Color3f carcolor, Color3f containercolor) {
    super(scale, carcolor);
    this.containerCA = new ColoringAttributes();
    Appearance containerApp = new Appearance();
    containerCA.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    containerApp.setColoringAttributes(containerCA);

    containerCA.setColor(containercolor);

    this.vectrans.set(0.0, 0.0, containerZ/2);
    this.vectrans.scale(scale);
    this.t3d.set(vectrans);
    TransformGroup tgContainer = new TransformGroup(t3d);

    Box container = new Box(scale*containerX/2, scale*containerY/2, scale*containerZ/2, containerApp);
    tgContainer.addChild(container);
    this.tgTop.addChild(tgContainer);

  }


  public SimpleAgvLoaded() {
    this(1.0f, Color3D.red, Color3D.blue);
  }


  public void setColor( Color3f c) {
    this.containerCA.setColor(c);
  }

}

