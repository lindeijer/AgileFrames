package com.agileways.vr.agv;

import javax.media.j3d.*;
import javax.vecmath.*;
import net.agileframes.vr.BaseGeometry;

import com.sun.j3d.utils.geometry.*;

public class SimpleAgv extends BaseGeometry{
  public static final int BODY = 0;
  public static final int WHEELS = 1;
  //public static final int PAYLOAD = 2;

  private BranchGroup AgvBG;
  private ColoringAttributes[] ca;
  private final float agvX=17.5f;
  private final float agvY=2.5f;
  private final float agvZ=1.0f;
  private final float clearing=0.2f;

  protected TransformGroup tgTop = null;
  protected Transform3D t3d = new Transform3D();
  protected Vector3d vectrans = new Vector3d();


  public SimpleAgv(float scale) {
    this.AgvBG = new BranchGroup();
    this.AgvBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    int a = 2;
    this.ca = new ColoringAttributes[a];
    Appearance[] appearance = new Appearance[a];
    for (int i = 0; i < a; i++) {
      appearance[i] = new Appearance();
      this.ca[i] = new ColoringAttributes();
      this.ca[i].setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
      appearance[i].setColoringAttributes(ca[i]);
      //appearance[i].setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
    }

    ca[BODY].setColor(1.0f, 0.0f, 0.0f);
    ca[WHEELS].setColor(0.0f, 0.0f, 1.0f);

    Box top          = new Box(scale*agvX/2,  scale*agvY/2, scale*agvZ/2, appearance[BODY]);

    Cylinder wheel_front_left  = new Cylinder(scale*agvZ/2, scale*agvZ/6, appearance[WHEELS]);
    Cylinder wheel_front_right = new Cylinder(scale*agvZ/2, scale*agvZ/6, appearance[WHEELS]);
    Cylinder wheel_rear_left   = new Cylinder(scale*agvZ/2, scale*agvZ/6, appearance[WHEELS]);
    Cylinder wheel_rear_right  = new Cylinder(scale*agvZ/2, scale*agvZ/6, appearance[WHEELS]);


    vectrans.set(0.0f, 0.0f, clearing + agvZ/2);
    vectrans.scale(scale);
    t3d.set(vectrans);
    this.tgTop = new TransformGroup(t3d);

    vectrans.set(0.75f*agvX/2, agvY/2, -clearing);
    vectrans.scale(scale);
    t3d.set(vectrans);
    TransformGroup tgWheelFrontLeft = new TransformGroup(t3d);

    vectrans.set(0.75f*agvX/2, -agvY/2, -clearing);
    vectrans.scale(scale);
    t3d.set(vectrans);
    TransformGroup tgWheelFrontRight = new TransformGroup(t3d);

    vectrans.set(-0.75f*agvX/2, agvY/2, -clearing);
    vectrans.scale(scale);
    t3d.set(vectrans);
    TransformGroup tgWheelRearLeft = new TransformGroup(t3d);

    vectrans.set(-0.75f*agvX/2, -agvY/2, -clearing);
    vectrans.scale(scale);
    t3d.set(vectrans);
    TransformGroup tgWheelRearRight = new TransformGroup(t3d);

    tgTop.addChild(top);
    tgWheelFrontLeft.addChild(wheel_front_left);
    tgWheelFrontRight.addChild(wheel_front_right);
    tgWheelRearLeft.addChild(wheel_rear_left);
    tgWheelRearRight.addChild(wheel_rear_right);

    tgTop.addChild(tgWheelFrontLeft);
    tgTop.addChild(tgWheelFrontRight);
    tgTop.addChild(tgWheelRearLeft);
    tgTop.addChild(tgWheelRearRight);

    this.AgvBG.addChild(tgTop);

  }


  public SimpleAgv() {
    this(1.0f);
  }

  public BranchGroup getBG() {
    return this.AgvBG;
  }

  public void setColor( Color3f c) {
    this.ca[BODY].setColor(c);
  }

  public void setText(String t) {}

}

