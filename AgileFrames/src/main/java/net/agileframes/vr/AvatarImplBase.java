package net.agileframes.vr;

import javax.media.j3d.*;
import javax.vecmath.*;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.vr.Body;

import net.agileframes.vr.BaseGeometry;
import com.objectspace.jgl.Array;
import net.agileframes.core.vr.Avatar;
import net.agileframes.vr.BodyImplBase;

/**
  @author van Dijk, Lindeijer, Evers
  @version 0.0.1
*/

public class AvatarImplBase implements Avatar {

  private Body body = null;
  private Avatar parentAvatar = null;

  private int currentGeometryID;
  public int currentAppearanceID;

  private BaseGeometry currentGeometry;
  private Color3f currentAppearance;

  private BranchGroup topBG;
  private BranchGroup bottomBG;
  private BranchGroup behaviorBG=null;
  public TransformGroup translateTG;// public caus used at AvatarQC (herman)
  private TransformGroup rotateTG;
  private Switch geometrySwitchNode; // the different shapes are addes to this BranchGroup
  protected Transform3D t3dTranslation;
  protected Transform3D t3dRotation;
  private RefreshBehavior refreshBehavior;

  private Array geometryArray;
  private Array appearanceArray;

  private  POS pos;
  protected Vector3d vectorTranslation;
  protected Vector3d vectorRotation;
  protected Matrix3d matrixRotation;



  public AvatarImplBase() {
    this(null, -1);
  }

  /**
   * Constructor. hhhahshhshshshshshs
   */
  public AvatarImplBase(Body body, int frames) {

    //this.needRefresh = frames >= 0 ? true : false;

    this.topBG = new BranchGroup();
    this.topBG.setCapability(BranchGroup.ALLOW_DETACH);
    this.topBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    this.topBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    this.topBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

    this.geometrySwitchNode = new Switch();
    this.geometrySwitchNode.setCapability(Switch.ALLOW_SWITCH_WRITE);
    this.geometrySwitchNode.setCapability(Switch.ALLOW_CHILDREN_EXTEND);

    this.bottomBG = new BranchGroup();
    this.bottomBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    this.bottomBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    this.bottomBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);


    this.translateTG = new TransformGroup();
    this.translateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.translateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    this.rotateTG = new TransformGroup();
    this.rotateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    this.rotateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    this.rotateTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    this.rotateTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

    this.behaviorBG = new BranchGroup();
    this.behaviorBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    this.behaviorBG.setCapability(BranchGroup.ALLOW_DETACH);

    this.topBG.addChild(this.translateTG);
    this.translateTG.addChild(this.rotateTG);
    this.rotateTG.addChild(this.geometrySwitchNode);
    this.rotateTG.addChild(this.bottomBG);
    //this.rotateTG.addChild(this.behaviorBG);

    this.geometryArray = new Array();
    this.appearanceArray = new Array();

    this.t3dTranslation = new Transform3D();
    this.t3dRotation    = new Transform3D();

    this.vectorTranslation = new Vector3d();
    this.vectorRotation = new Vector3d();
    this.matrixRotation = new Matrix3d();

    this.currentAppearanceID = 0;
    this.currentGeometryID  = 0;

    setBody(body);
    if (frames >= 0)
      addRefreshBehavior(frames);
  }


  /**
   *  Set the appearance corresponding to appearanceID as the new appearance
   *  @param appearanceID the new appearanceID
   */
  public void setAppearanceID(int appearanceID) {

    if (this.appearanceArray.size() > appearanceID) {
      this.currentAppearanceID = appearanceID;
      setShape();
    }
    else {
      System.err.println("AvatarImplBase.setAppearanceID(int) :-> appearanceID too large !!!");
    }
  }

  /**
   *  Set the geometry corresponding to geometryID as the new geometry
   *  @param geometryID ID corresponding to the new geometry
   */
  public void setGeometryID(int geometryID)  {
    if (this.geometryArray.size() > geometryID) {
      this.currentGeometryID = geometryID; // wat is nieuwe geometryID
      setShape();
    }
    else {
      System.err.println("AvatarImplBase.setGeometryID(int) :-> geometryID too large !!!");
    }
  }

  /**
   * preferably do this in the constructor
   */
  public void addGeometry(BaseGeometry geometry) {
    this.geometryArray.add(geometry);
    this.geometrySwitchNode.addChild(geometry.getBG() ); // voeg geometry toe aan array
  }

  /**
   *  Add a geometry to the array of geometry's
   *  @param node the newly to be added geometry
   */
  public void addGeometry(Node node) {
    if (this.geometrySwitchNode.numChildren() == 0) {
      this.geometrySwitchNode.addChild(node);
      this.geometrySwitchNode.setWhichChild(Switch.CHILD_ALL);
    }
    else
      System.err.println("DEBUG method, only one geometry !!!!");
  }

  /**
   * Add a new appearance to the array of appearances
   *  @param color the newly to be added appearance
   */
  public void addAppearance(Color3f color) {
    this.appearanceArray.add(color);
  }

  private void setShape() {
    this.currentAppearance = (Color3f)this.appearanceArray.at(this.currentAppearanceID); // wat is de nieuwe kleur
    this.currentGeometry   = (BaseGeometry)this.geometryArray.at(this.currentGeometryID); // wat is nieuwe geometry
    this.currentGeometry.setColor(this.currentAppearance); // geef geometry nieuwe appearance
    this.geometrySwitchNode.setWhichChild(this.currentGeometryID); // maak nieuwe geometry actief
  }

  /**
   * Set the geometry and appearance corresponding to resp. geometryID and appearanceID
   * @param geometryID the ID corresponding to the new geometry
   * @param appearanceID the ID corresponding to the new appearance
   */
  public void setGeometryAndAppearanceID(int geometryID, int appearanceID) {
    if ((this.geometryArray.size() > geometryID) && (this.appearanceArray.size() > appearanceID)) {
      this.currentGeometryID = geometryID;
      this.currentAppearanceID = appearanceID;
      setShape();
    }
    else {
      System.err.println("AvatarImplBase.setGeometryAndAppearanceID(int, int) :-> one or more ID's too large !!!");
    }
  }


  public void refresh() {
    if (this.body != null) {

    try {
      this.setState(this.body.getState());
    }
    catch (java.rmi.RemoteException e) {
      System.err.println("RemoteException in AvatarImplBase.refresh="+e.getMessage() );
      setBody(null); // causes the refresh to be removed
    }
    catch (java.lang.Exception e) {
      System.err.println("Exception in AvatarImplBase.refresh="+e.getMessage() );

    }

    }
  }

  /**
   * Link this avatar with a body
   * @param body the body this avatar is to be linked with
   * @param frames the amount of frames between each 'trigger'
   */
  public void setBody(Body body, int frames) {
    this.body = body;
    addRefreshBehavior(frames);
  }

  /**
   * Link this avatar with a body
   * @param body the body this avatar is to be linked with
   */
  public void setBody(Body body) {
    this.body = body;
  }

  /**
   * Remove a behavior from the scene, causing it no longer to call refresh
   */
  private void removeRefreshBehavior() {
    this.behaviorBG.detach();
    System.out.println("Just removed RefreshBehavior");
  }

  /**
   * Add refreshbehavior for this object to the scene
   * @param frames the number of frames between each 'trigger'
   */
  private void addRefreshBehavior(int frames) {
    BranchGroup bBG=null;
    boolean islive = this.behaviorBG.isLive();
    if (!islive) {
      System.out.println("Inside addRefreshBehavior");
      this.behaviorBG = new BranchGroup();
      this.behaviorBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
      this.behaviorBG.setCapability(BranchGroup.ALLOW_DETACH);
      this.bottomBG.addChild(this.behaviorBG);
      System.out.println("Just added behaviorBG");
    }
    this.refreshBehavior = new RefreshBehavior(this, frames);
    this.refreshBehavior.setSchedulingBounds( new BoundingSphere( new Point3d(), 2000.0) );
    this.behaviorBG.addChild(this.refreshBehavior);
    System.out.println("Just added RefreshBehavior");
  }



  private void copy(POS state) {
    this.pos.x     = state.x;
    this.pos.y     = state.y;
    this.pos.z     = state.z;
    this.pos.alpha = state.alpha;
    this.pos.beta  = state.beta;
    this.pos.gamma = state.gamma;
  }


  // see interface Avatar
  public void setState(State state) {
    POS newState = (POS)state;

    this.vectorTranslation.set(newState.x, newState.y, newState.z);
    this.t3dTranslation.set(this.vectorTranslation);
    this.translateTG.setTransform(this.t3dTranslation);


    this.vectorRotation.set(newState.gamma, newState.beta, newState.alpha);
    this.t3dRotation.setEuler(this.vectorRotation);
    this.rotateTG.setTransform(this.t3dRotation);
  }


 // see interface Avatar
  public int getGeometryArraySize() {
    return this.geometryArray.size();
  }

  // see interface Avatar
  public int getAppearanceArraySize() {
    return this.appearanceArray.size();
  }



  /**
   * set an new shape and its parameters, also update the view.
   * @param geometryId, the geometry-id for this new shape.
   * @param geometryId, the appearance-id for this new shape.
   * @param shape must be a BranchGroup,
   */
  public void setGeometryAndAppearance(int geometryId,int appearanceID, java.rmi.MarshalledObject shape)  {
  }


  /**
   * Remove a child from its parent
   * @param childAvatar the avatar of the child to be orphaned
   */
  public void removeChild(Avatar childAvatar) {
    AvatarImplBase child = (AvatarImplBase)childAvatar;
    child.getTopBG().detach();
    System.out.println("Avatar.removeChild...just succeeded");
  }

  /**
   * Associate an avatar with this avatar.
   * The associated avatar will be a child of this avatar.
   * Therefore its position and orientation will be updated accordingly
   * @param avatar the avatar to be associated with
   */
  public void addChild(Avatar childAvatar) {
    AvatarImplBase child = (AvatarImplBase)childAvatar;
    this.bottomBG.addChild(child.getTopBG());
    System.out.println("Avatar.addChild...just succeeded");
  }

  /*

  public void debugPrint() {

    if (this.bottomBG.isLive()) {

      Transform3D debugT3D = new Transform3D();
      Vector3d    debugVec = new Vector3d();
      this.bottomBG.getLocalToVworld(debugT3D); // t3d bevat 3Dpositie
      debugT3D.get(debugVec);
      System.out.println("AvatarImplbase.debugPrint...Child abs. 3D position = "+debugVec.toString());
    } //
    else
      System.out.println("AvatarImplBase.debugPrint...Child.BottomBG is NOT Live");
  }

  */

  /**
   * Set a parent for this child
   * @param parentAvatar the avatar to be the new parent for this avatar
   */
  public void setParent(Avatar parentAvatar) {
    //if (parentAvatar != null)
    this.topBG.detach();
    AvatarImplBase parent = (AvatarImplBase)parentAvatar;
    parent.getBottomBG().addChild(this.topBG); // ook meegeven de rel pos van child !!!
  }

  /**
   * Get the top BranchGroup of this avatar
   * @return the top BranchGroup of this avatar
   */
  public BranchGroup getBG() {
    return this.topBG;
  }

  /**
   * Get the top BranchGroup of this avatar
   * @return the top BranchGroup of this avatar
   */
  public BranchGroup getTopBG() {
    return this.topBG;
  }

  /**
   * get the bootom BranchGroup of this avatar
   * @return the bottom most BarnchGroup of this avatar
   */
  public BranchGroup getBottomBG() {
    return this.bottomBG;
  }

/*
  public POS getAbsoluteAvatarPOS() {
    Transform3D xxx = new Transform3D();
    POS newPOS = new POS();
    Matrix3d rotation = new Matrix3d();
    Vector3f trans    = new Vector3f();
    if (this.testBG.isLive()) {
      this.testBG.getLocalToVworld(xxx);
      xxx.get(rotation);
      xxx.get(trans);
      newPOS.x = trans.x;
      newPOS.y = trans.y;
      newPOS.z = trans.z;
//      System.out.println("Avatar.getAbsoluteAvatarPOS= "+newPOS.getPosition().toString());
    }
    else {
//      System.out.println("Avatar.getAbsoluteAvatarPOS....bottomBG not Live");
    }
    return newPOS;
  }
*/

}

