package com.agileways.forces.infrastructure.jumbo.avatar;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.geometry.*;
import net.agileframes.vr.*;

public class Pillar {
  private BranchGroup rootBG = null;
  private TransformGroup posorTG = null;
  private Box box = null;
  private Appearance appearance = null;
  private ColoringAttributes coloringattributes = null;
  private Transform3D t3d = null;

  private static final float XDIM = 1.0f;
  private static final float YDIM = 6.0f;
  private static final float ZDIM = 5.0f;

  /**
   * Makes a 'Pillar'
   * @param x x coordinate
   * @param y y coordinate
   * @param alpha angle with x-axis
   */
  public Pillar(float x, float y, float alpha, Color3f c, float scale) {
    this.rootBG = new BranchGroup();
    this.posorTG = new TransformGroup();
    this.appearance = new Appearance();
    this.box = new Box(scale*XDIM/2.0f, scale*YDIM/2.0f, scale*ZDIM/2.0f, this.appearance);
    this.coloringattributes = new ColoringAttributes(c, ColoringAttributes.FASTEST);
    this.t3d = new Transform3D();

    Matrix3f matrix = new Matrix3f(); // rotation matrix
    Vector3f vector = new Vector3f(); // translatie vector
    matrix.rotZ(alpha);
    vector.set(x, y, scale*ZDIM/2.0f);
    this.t3d.set(matrix, vector, 1.0f);

    this.rootBG.addChild(this.posorTG);
    this.posorTG.addChild(this.box);
    this.posorTG.setTransform(this.t3d);
    this.appearance.setColoringAttributes(this.coloringattributes);
    this.box.setAppearance(this.appearance);
  }

  public BranchGroup getBG() {
    return this.rootBG;
  }
}