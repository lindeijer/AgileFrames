package com.agileways.forces.machine.crane.jumboqc.avatar;

import net.agileframes.vr.BaseGeometry;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;

import net.agileframes.vr.*;

public class SimpleCrane extends BaseGeometry{

//********************Dimensions****************************
  private static final float QC_X=28.0f;
  private static final float CARRIAGE_X=QC_X;
  private static final float CARRIAGE_Y=1.0f;
  private static final float CARRIAGE_Z=1.0f;
  private static final float DISTANCE_QUAY_PILLAR=67.0f;

  private static final float OVERHANG=70.0f;

  private static final float PILLAR_Z=5.0f;
  private static final float PILLARTOP_Z=0.25f;

  private static final float QUAYLEG_X=1.5f;
  private static final float QUAYLEG_Y=1.5f;
  private static final float QUAYLEG_Z=35.0f;

  private static final float PILLARLEG_X=1.5f;
  private static final float PILLARLEG_Y=1.5f;
  private static final float PILLARLEG_Z=QUAYLEG_Z - PILLAR_Z - PILLARTOP_Z;

  private static final float MIDDLEBEAM_X=1.5f;
  private static final float MIDDLEBEAM_Y=DISTANCE_QUAY_PILLAR;
  private static final float MIDDLEBEAM_Z=1.5f;

  private static final float TOPBEAM_X=1.5f;
  private static final float TOPBEAM_Y=DISTANCE_QUAY_PILLAR+OVERHANG;
  private static final float TOPBEAM_Z=1.5f;

  private static final float SPAR_Y=1.5f;
  private static final float SPAR_Z=1.5f;

  private static final float RAIL_X=QC_X;
  private static final float RAIL_Y=1.5f;
  private static final float RAIL_Z=1.5f;

  private static final float SLIDER_X=40*12*0.0254f;
  private static final float SLIDER_Y=8*12*0.0254f;
  private static final float SLIDER_Z=1.0f;

  private static final float CAT_X=40*12*0.0254f;
  private static final float CAT_Y=8*12*0.0254f;
  private static final float CAT_Z=1.0f;

  private static final float TURNTABLE_X=28.0f;
  private static final float TURNTABLE_Y=14.0f;
  private static final float TURNTABLE_Z=0.1f;

  private static final float TURNCIRCLE_R=7.0f;
  private static final float TURNCIRCLE_Z=1.0f;


//***********************3D objects*********************************
  private BranchGroup rootBG=null;

  private Box geomPillarLeftLeg=null;
  private Box geomPillarRightLeg=null;
  private Box geomQuayLeftLeg=null;
  private Box geomQuayRightLeg=null;

  private Box geomQuayCarriage=null;
  private Box geomPillarCarriage=null;

  private Box geomTopLeftBeam=null;
  private Box geomTopRightBeam=null;
  private Box geomTopBackBeam=null;
  private Box geomTopCenterBeam=null;
  private Box geomTopFrontBeam=null;

  private Box geomMiddleLeftBeam=null;
  private Box geomMiddleRightBeam=null;

  private Box geomCatRail=null;
  private Box geomPillarRail=null;
  private Box geomQuayRail=null;

  private Box geomTopBackSpar=null;
  private Box geomTopFrontSpar=null;
  private Box geomTopMiddleSpar=null;

  private Box geomSlider=null;
  private Box geomCat=null;

  private Box geomTurnTable=null;
  private Cylinder geomTurnCircle=null;

  private Appearance appearanceLeg=null;
  private Appearance appearanceRail=null;
  private Appearance appearanceSpar=null;
  private Appearance appearanceSlider=null;
  private Appearance appearanceCat=null;
  private Appearance appearanceTurnTable=null;
  private Appearance appearanceTurnCircle=null;

  private ColoringAttributes caRail=null;
  private ColoringAttributes caLeg=null;
  private ColoringAttributes caSpar=null;
  private ColoringAttributes caSlider=null;
  private ColoringAttributes caCat=null;
  private ColoringAttributes caTurnTable=null;
  private ColoringAttributes caTurnCircle=null;

  private TransformGroup tgRotate=null;
//  private TransformGroup tgRotate=null

  private TransformGroup tgBASE=null;

  private TransformGroup tgPillarLeftLeg=null;
  private TransformGroup tgPillarRightLeg=null;
  private TransformGroup tgPillarCarriage=null;
  private TransformGroup tgQuayCarriage=null;

  private TransformGroup tgQuayLeftLeg=null;
  private TransformGroup tgQuayRightLeg=null;


  private TransformGroup tgTopBackSpar=null;
  private TransformGroup tgTopFrontSpar=null;
  private TransformGroup tgTopMiddleSpar=null;

  private TransformGroup tgTopCenterBeam=null;
  private TransformGroup tgTopBeamFront=null;

  private TransformGroup tgMiddleLeftBeam=null;
  private TransformGroup tgMiddleRightBeam=null;

  private TransformGroup tgTopLeftBeam=null;
  private TransformGroup tgTopRightBeam=null;

  private TransformGroup tgPillarRail=null;
  private TransformGroup tgQuayRail=null;

  private TransformGroup tgSlider=null;
  private TransformGroup tgCat=null;

  private TransformGroup tgTurnTable=null;
  private TransformGroup tgTurnCircle=null;

  private Transform3D t3d=null;
  private Vector3f vector3f=null;


  public SimpleCrane(float distance_between_rails, float pillar_z, boolean complete) {

    this.rootBG = new BranchGroup();

    this.tgRotate = new TransformGroup();
    this.tgBASE = new TransformGroup();

    this.tgPillarLeftLeg = new TransformGroup();
    this.tgPillarRightLeg = new TransformGroup();
    this.tgQuayLeftLeg = new TransformGroup();
    this.tgQuayRightLeg = new TransformGroup();

    this.tgMiddleLeftBeam = new TransformGroup();
    this.tgMiddleRightBeam = new TransformGroup();

    this.tgTopLeftBeam   = new TransformGroup();
    this.tgTopRightBeam  = new TransformGroup();
    this.tgTopCenterBeam  = new TransformGroup();

    this.tgTopBackSpar =  new TransformGroup();
    this.tgTopFrontSpar = new TransformGroup();
    this.tgTopMiddleSpar = new TransformGroup();

    this.tgPillarCarriage    = new TransformGroup();
    this.tgQuayCarriage      = new TransformGroup();

    this.tgSlider = new TransformGroup();
    this.tgCat = new TransformGroup();

    this.tgTurnTable = new TransformGroup();
    this.tgTurnCircle = new TransformGroup();

    this.t3d  = new Transform3D();
    this.vector3f = new Vector3f();

//-----------------------Capabilities--------------------------------------------------
    this.tgSlider.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.tgCat.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.tgTurnCircle.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

//-----------------------Appearance-----------------------------------------------------
    this.caLeg = new ColoringAttributes(Color3D.red, ColoringAttributes.FASTEST);
    this.caRail = new ColoringAttributes(Color3D.yellow, ColoringAttributes.FASTEST);
    this.caSpar = new ColoringAttributes(Color3D.gray, ColoringAttributes.FASTEST);
    this.caSlider = new ColoringAttributes(Color3D.orange, ColoringAttributes.FASTEST);
    this.caCat = new ColoringAttributes(Color3D.green, ColoringAttributes.FASTEST);
    this.caTurnTable = new ColoringAttributes(Color3D.blue, ColoringAttributes.FASTEST);
    this.caTurnCircle = new ColoringAttributes(Color3D.gray, ColoringAttributes.FASTEST);

    this.appearanceLeg = new Appearance();
    this.appearanceRail = new Appearance();
    this.appearanceSpar = new Appearance();
    this.appearanceSlider = new Appearance();
    this.appearanceCat = new Appearance();
    this.appearanceTurnTable = new Appearance();
    this.appearanceTurnCircle = new Appearance();

    this.appearanceLeg.setColoringAttributes(this.caLeg);
    this.appearanceRail.setColoringAttributes(this.caRail);
    this.appearanceSpar.setColoringAttributes(this.caSpar);
    this.appearanceSlider.setColoringAttributes(this.caSlider);
    this.appearanceCat.setColoringAttributes(this.caCat);
    this.appearanceTurnTable.setColoringAttributes(this.caTurnTable);
    this.appearanceTurnCircle.setColoringAttributes(this.caTurnCircle);

//------------------------Geometry------------------------------------------------------
    this.geomPillarLeftLeg   = new Box(PILLARLEG_X/2, PILLARLEG_Y/2, PILLARLEG_Z/2, appearanceLeg);
    this.geomPillarRightLeg  = new Box(PILLARLEG_X/2, PILLARLEG_X/2, PILLARLEG_Z/2, appearanceLeg);
    this.geomQuayLeftLeg  = new Box(QUAYLEG_X/2, QUAYLEG_X/2, QUAYLEG_Z/2, appearanceLeg);
    this.geomQuayRightLeg = new Box(QUAYLEG_X/2, QUAYLEG_X/2, QUAYLEG_Z/2, appearanceLeg);

    this.geomTopBackBeam   = new Box(MIDDLEBEAM_X/2, MIDDLEBEAM_Y/2, MIDDLEBEAM_Z/2, appearanceLeg);

    this.geomTopFrontBeam  = new Box(MIDDLEBEAM_X/2, MIDDLEBEAM_Y/2, MIDDLEBEAM_Z/2, appearanceLeg);

    this.geomTopBackSpar   = new Box(QC_X/2, SPAR_Y/2, SPAR_Z/2, appearanceSpar);
    this.geomTopFrontSpar  = new Box(QC_X/2, SPAR_Y/2, SPAR_Z/2, appearanceSpar);
    this.geomTopMiddleSpar = new Box(QC_X/2, SPAR_Y/2, SPAR_Z/2, appearanceSpar);

    this.geomPillarCarriage    = new Box(CARRIAGE_X/2, CARRIAGE_Y/2, CARRIAGE_Z/2, appearanceRail);
    this.geomQuayCarriage      = new Box(CARRIAGE_X/2, CARRIAGE_Y/2, CARRIAGE_Z/2, appearanceRail);

    this.geomMiddleLeftBeam = new Box(MIDDLEBEAM_X/2, MIDDLEBEAM_Y/2, MIDDLEBEAM_Z/2, appearanceLeg);
    this.geomMiddleRightBeam = new Box(MIDDLEBEAM_X/2, MIDDLEBEAM_Y/2, MIDDLEBEAM_Z/2, appearanceLeg);

    this.geomTopLeftBeam = new Box(TOPBEAM_X/2, TOPBEAM_Y/2, TOPBEAM_Z/2 , appearanceLeg);
    this.geomTopCenterBeam = new Box(TOPBEAM_X*2, TOPBEAM_Y/2, TOPBEAM_Z/2, appearanceLeg);
    this.geomTopRightBeam = new Box(TOPBEAM_X/2, TOPBEAM_Y/2, TOPBEAM_Z/2, appearanceLeg);

    this.geomSlider = new Box(SLIDER_X/2, SLIDER_Y/2, SLIDER_Z/2, this.appearanceSlider);
    this.geomCat = new Box(CAT_X/2, CAT_Y/2, CAT_Z/2, this.appearanceCat);

    this.geomTurnTable = new Box(this.TURNTABLE_X/2, this.TURNTABLE_Y/2, this.TURNTABLE_Z/2, this.appearanceTurnTable);
    this.geomTurnCircle = new Cylinder(this.TURNCIRCLE_R, this.TURNCIRCLE_Z/2, this.appearanceTurnCircle);


//---------Positioning pillarcarriage---------------------------------------------------------------------
    this.vector3f.set(0.0f, 0.0f, PILLAR_Z+PILLARTOP_Z);
    this.t3d.setTranslation(vector3f);
    this.tgPillarCarriage.setTransform(t3d);
    this.tgPillarCarriage.addChild(this.geomPillarCarriage);
    if (complete)
      this.tgBASE.addChild(this.tgPillarCarriage);


//---------Positioning quaycarriage-----------------
    this.vector3f.set(0.0f, this.DISTANCE_QUAY_PILLAR, 0.0f);
    this.t3d.setTranslation(vector3f);
    this.tgQuayCarriage.setTransform(t3d);
    this.tgQuayCarriage.addChild(this.geomQuayCarriage);
    if (complete)
      this.tgBASE.addChild(this.tgQuayCarriage);

//---------Positioning PillarLeftleg--------
    this.vector3f.set(-QC_X/2, 0.0f, PILLARLEG_Z/2 + PILLAR_Z + PILLARTOP_Z);
    this.t3d.setTranslation(vector3f);
    this.tgPillarLeftLeg.setTransform(t3d);
    this.tgBASE.addChild(this.tgPillarLeftLeg);
    if (complete)
      this.tgPillarLeftLeg.addChild(this.geomPillarLeftLeg);

//---------Positioning PillarRightleg--------
    this.vector3f.set(QC_X/2, 0.0f, PILLARLEG_Z/2 + PILLAR_Z + PILLARTOP_Z);
    this.t3d.setTranslation(vector3f);
    this.tgPillarRightLeg.setTransform(t3d);
    this.tgPillarRightLeg.addChild(this.geomPillarRightLeg);
    if (complete)
      this.tgBASE.addChild(this.tgPillarRightLeg);


//-------Positioning QuayLeftLeg--------------------------
    this.vector3f.set(-QC_X/2, this.DISTANCE_QUAY_PILLAR, QUAYLEG_Z/2);
    this.t3d.setTranslation(vector3f);
    this.tgQuayLeftLeg.setTransform(t3d);
    this.tgQuayLeftLeg.addChild(this.geomQuayLeftLeg);
    if (complete)
      this.tgBASE.addChild(this.tgQuayLeftLeg);

//-------Positioning QuayRightLeg--------------------------
    this.vector3f.set(QC_X/2, this.DISTANCE_QUAY_PILLAR, QUAYLEG_Z/2);
    this.t3d.setTranslation(vector3f);
    this.tgQuayRightLeg.setTransform(t3d);
    this.tgQuayRightLeg.addChild(this.geomQuayRightLeg);
    if (complete)
      this.tgBASE.addChild(this.tgQuayRightLeg);

//--------Positioning Middle Beam Left
    this.vector3f.set(-QC_X/2, this.DISTANCE_QUAY_PILLAR - MIDDLEBEAM_Y/2, QUAYLEG_Z/2);
    this.t3d.setTranslation(vector3f);
    this.tgMiddleLeftBeam.setTransform(t3d);
    this.tgMiddleLeftBeam.addChild(this.geomMiddleLeftBeam);
    if (complete)
      this.tgBASE.addChild(this.tgMiddleLeftBeam);

//--------Positioning Middle Beam Right
    this.vector3f.set(QC_X/2, this.DISTANCE_QUAY_PILLAR - MIDDLEBEAM_Y/2, QUAYLEG_Z/2);
    this.t3d.setTranslation(vector3f);
    this.tgMiddleRightBeam.setTransform(t3d);
    this.tgMiddleRightBeam.addChild(this.geomMiddleRightBeam);
    if (complete)
      this.tgBASE.addChild(this.tgMiddleRightBeam);

//-----------Positioning Top  Left  Beam
    this.vector3f.set(-QC_X/2, TOPBEAM_Y/2, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopLeftBeam.setTransform(t3d);
    this.tgTopLeftBeam.addChild(this.geomTopLeftBeam);
    //this.tgBASE.addChild(this.tgTopLeftBeam);

//-----------Positioning Top  Right  Beam
    this.vector3f.set(QC_X/2, TOPBEAM_Y/2, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopRightBeam.setTransform(t3d);
    this.tgTopRightBeam.addChild(this.geomTopRightBeam);
    //this.tgBASE.addChild(this.tgTopRightBeam);

//-------Positioning topbeam middle
    this.vector3f.set(0.0f, TOPBEAM_Y/2, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopCenterBeam.setTransform(t3d);
    this.tgTopCenterBeam.addChild(this.geomTopCenterBeam);
    if (complete)
      this.tgBASE.addChild(this.tgTopCenterBeam);


//-------Positioning topback spar
    this.vector3f.set(0.0f, 0.0f, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopBackSpar.setTransform(t3d);
    this.tgTopBackSpar.addChild(this.geomTopBackSpar);
    if (complete)
      this.tgBASE.addChild(this.tgTopBackSpar);

//-------Positioning topfront spar
    this.vector3f.set(0.0f, TOPBEAM_Y, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopFrontSpar.setTransform(t3d);
    this.tgTopFrontSpar.addChild(this.geomTopFrontSpar);
    //this.tgBASE.addChild(this.tgTopFrontSpar);

//-------Positioning topmiddle spar
    this.vector3f.set(0.0f, TOPBEAM_Y/2, QUAYLEG_Z);
    this.t3d.setTranslation(vector3f);
    this.tgTopMiddleSpar.setTransform(t3d);
    this.tgTopMiddleSpar.addChild(this.geomTopMiddleSpar);
    if (complete)
      this.tgBASE.addChild(this.tgTopMiddleSpar);

// Positioning Slider (child of topmiddle beam......
    this.vector3f.set(0.0f, 0.0f, -1.0f);
    this.t3d.setTranslation(vector3f);
    this.tgSlider.setTransform(t3d);
    this.tgSlider.addChild(this.geomSlider); // avatar
    if (complete)
      this.tgTopMiddleSpar.addChild(this.tgSlider);

//Positioning Cat
    this.vector3f.set(0.0f, 0.0f, -5.0f);
    this.t3d.setTranslation(vector3f);
    this.tgCat.setTransform(t3d);
    this.tgCat.addChild(this.geomCat);
    if (complete)
      this.tgSlider.addChild(this.tgCat);

//Positioning TurnTable
    this.vector3f.set(0.0f, this.TURNTABLE_Y/2 + 20.0f, 0.0f);
    this.t3d.setTranslation(vector3f);
    this.tgTurnTable.setTransform(t3d);
    this.tgTurnTable.addChild(this.geomTurnTable);
    this.tgBASE.addChild(this.tgTurnTable);

//Positioning TurnCircle
   this.t3d.rotX(Math.PI/2);
   this.tgTurnCircle.setTransform(t3d);
   this.tgTurnCircle.addChild(this.geomTurnCircle);
   this.tgTurnTable.addChild(this.tgTurnCircle);

   t3d.rotZ(Math.PI);
   this.tgRotate.setTransform(t3d);
   this.tgRotate.addChild(this.tgBASE);
   this.rootBG.addChild(this.tgRotate);
  }

  private void construct(){
  }

  public BranchGroup getBG(){
    return this.rootBG;
  }

  public void setColor(Color3f color){
    // not yet implemented.....  
  }

  public void setSliderPosition(float y) {
    this.vector3f.set(0.0f, -TOPBEAM_Y/2+y, -1.0f);
    this.t3d.setTranslation(this.vector3f);
    this.tgSlider.setTransform(this.t3d);
  }

  private void setTurnCircle(float angle) {

  }

  public void setCatPosition(float z) {
    this.vector3f.set(0.0f, 0.0f, z);
    this.t3d.setTranslation(this.vector3f);
    this.tgCat.setTransform(this.t3d);
  }

  public void setColors(Color3f cLeg, Color3f cRail, Color3f cSpar, Color3f cSlider, Color3f cCat, Color3f cTT, Color3f cTC) {
    this.caLeg.setColor(cLeg);
    this.caRail.setColor(cRail);
    this.caSpar.setColor(cSpar);
    this.caSlider.setColor(cSlider);
    this.caCat.setColor(cCat);
    this.caTurnTable.setColor(cTT);
    this.caTurnCircle.setColor(cTC);
  }

}
