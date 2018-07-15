package net.agileframes.vr.space3d;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.vr.Body;
import net.agileframes.vr.RefreshBehavior;
import net.agileframes.vr.BaseGeometry;
import com.objectspace.jgl.Array;//should be solved in another way
import net.agileframes.core.vr.Avatar;
import net.jini.core.lookup.ServiceID;
/**
 * <b>Implementation of an Avatar in a 3d environment.</b>
 * <p>
 * This class can be used as an example to create avatars for other environments.
 * In order to view this Avatar in a remote virtuality, use an AvatarFactory.
 * @see     net.agileframes.core.vr.AvatarFactory
 * @author  F.A. van Dijk, D.G. Lindeijer, H.J. wierenga
 * @version 0.1
 */
public class Avatar3D implements Avatar {
  private String name=null;
  private long counter=0;
  /** The body that belongs to this Avatar */
  protected Body body = null;
  private Avatar parentAvatar = null;
  /** The current geometry-id of this Avatar */
  public int currentGeometryID;
  /** The curent appearance-id of this Avatar */
  public int currentAppearanceID;
  /** The current geometry of this Avatar */
  protected BaseGeometry currentGeometry;
  /** The curent appearance of this Avatar */
  protected Color3f currentAppearance;

  private BranchGroup topBG;
  private BranchGroup bottomBG;
  private BranchGroup behaviorBG=null;

  /** Translate Transform Group */
  protected TransformGroup translateTG;// is used by AvatarQC-> needs to be public (herman)
  /** Rotate Transform Group */
  protected TransformGroup rotateTG;
  /** The different shapes are added to this BranchGroup */
  protected Switch geometrySwitchNode;
  /** Transform 3d translation */
  protected Transform3D t3dTranslation;
  /** Transform 3d rotation*/
  protected Transform3D t3dRotation;
  /** The refresh-behavior of this Avatar */
  protected RefreshBehavior refreshBehavior=null;
  /** Array containing all possible geometries of this Avatar. */
  protected Array geometryArray;
  /** Array containing all possible appearances of this Avatar. */
  protected Array appearanceArray;
  /** Vector Translation */
  protected Vector3d vectorTranslation;
  /** Vector Rotation */
  protected Vector3d vectorRotation;
  /** Matrix Rotation */
  protected Matrix3d matrixRotation;

  /**
   * Creates a new instance of Avatar3D without any reference to a body, and therefore no refreshbehavior<p>
   * Calls default constructor.
   * @see #Avatar3D(Body,int)
   */
  public Avatar3D() {
    this(null, -1);
  }
  /**
   * Creates a new instance of Avatar3D without refreshbehavior<p>
   * Calls default constructor.
   * @see #Avatar3D(Body,int)
   */
  public Avatar3D(Body body) {
    this(body, -1);
  }
  /**
   * Default Constructor, creates a new instance of Avatar3D<p>
   * Calls default constructor.
   * @see   #setBody(Body,int)
   * @param body    the body to which this avatar belongs
   * @param frames  the number of frames specifies after how many frames the avatar will be refreshed<br>
   *                (0=every frame)
   */
  public Avatar3D(Body body, int frames) {
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

    this.geometryArray = new Array();
    this.appearanceArray = new Array();

    this.t3dTranslation = new Transform3D();
    this.t3dRotation    = new Transform3D();

    this.vectorTranslation = new Vector3d();
    this.vectorRotation = new Vector3d();
    this.matrixRotation = new Matrix3d();

    this.currentAppearanceID = 0;
    this.currentGeometryID  = 0;

    if ((frames >= 0) && (body!=null)){
      setBody(body, frames);
    } else {
      setBody(body);
    }
  }


  /**
   *  Sets the appearance corresponding to appearanceID as the new appearance.<p>
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
   *  Sets the geometry corresponding to geometryID as the new geometry.<p>
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
   * Adds a geometry to the list of geometries this avatar can shape.<p>
   * @see   #addGeometry(Node)
   * @param geometry the new geometry
   */
  public void addGeometry(BaseGeometry geometry) {
    this.geometryArray.add(geometry);
    this.geometrySwitchNode.addChild(geometry.getBG() ); // voeg geometry toe aan array
  }

  /**
   * Adds a geometry to the array of geometries.<p>
   * @see   #addGeometry(BaseGeometry)
   * @param node the newly to be added geometry
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
   * Adds a new appearance to the array of appearances.<p>
   * @param color the newly to be added appearance
   */
  public void addAppearance(Color3f color) {
    this.appearanceArray.add(color);
  }
  /**
   * Sets and shows the new geometry and appearance.<p>
   */
  protected void setShape() {
    this.currentAppearance = (Color3f)this.appearanceArray.at(this.currentAppearanceID); // wat is de nieuwe kleur
    this.currentGeometry   = (BaseGeometry)this.geometryArray.at(this.currentGeometryID); // wat is nieuwe geometry
    this.currentGeometry.setColor(this.currentAppearance); // geef geometry nieuwe appearance

    this.geometrySwitchNode.setWhichChild(this.currentGeometryID); // maak nieuwe geometry actief
  }

  /**
   * Sets the geometry and appearance corresponding to resp. geometryID and appearanceID.<p>
   * @param geometryID    the ID corresponding to the new geometry
   * @param appearanceID  the ID corresponding to the new appearance
   */
  public void setGeometryAndAppearanceID(int geometryID, int appearanceID) {
    // make sure that chosen IDs are always within range
    geometryID = geometryID % geometryArray.size();
    appearanceID = appearanceID % appearanceArray.size();
//    if ((this.geometryArray.size() > geometryID) && (this.appearanceArray.size() > appearanceID)) {
      this.currentGeometryID = geometryID;
      this.currentAppearanceID = appearanceID;
      setShape();
//    }
//    else {
//      System.err.println("AvatarImplBase.setGeometryAndAppearanceID(int, int) :-> one or more ID's too large !!!");
//    }
  }


  private boolean isRefreshCalled = false;
  /**
   * Gets the new state from the body and sets it in the avatar.<p>
   * This method should preferably not be called by the user.
   * @see net.agileframes.vr.RefreshBehavior#processStimulus(Enumeration)
   */
  public synchronized void refresh() {
    isRefreshCalled = true;
    synchronized (refreshBehavior) {
      try { refreshBehavior.notify();  }
      catch (Exception e) { e.printStackTrace();  }
    }
  }

  /** Empty method. */
  public void refresh(int i) {
  }

  /**
   * Links this avatar with a body.<p>
   * Adds a refresh-behavior that starts a refresh-thread in this object that refreshes the Avatar.
   * @see   #runForRefreshThread()
   * @param body the body this avatar is to be linked with
   * @param frames the number of frames between each 'refreshtrigger'
   */
  public void setBody(Body body, int frames) {
    this.body = body;
    addRefreshBehavior(frames);
  }

  /**
   * Links this avatar with a body.<p>
   * @see   #setBody(Body)
   * @param body the body this avatar is to be linked with
   */
  public void setBody(Body body) {
    this.body = body;
    System.out.println("Avatar3D: No RefreshBehavior was added!");
  }

  /**
   * Removes a behavior from the scene, causing it no longer to call refresh.<p>
   */
  private void removeRefreshBehavior() {
    this.behaviorBG.detach();
    System.out.println("Just removed RefreshBehavior");
  }

  /**
   * Adds refreshbehavior for this avatar to the scene.<p>
   * Starts a refresh-thread in this object that refreshes the Avatar.
   * @param frames the number of frames between each 'trigger'
   */
  private void addRefreshBehavior(int frames) {
    if (!this.behaviorBG.isLive()) {
      if (this.refreshBehavior == null) {
        this.refreshBehavior = new RefreshBehavior(this, frames);
        this.refreshBehavior.setSchedulingBounds( new BoundingSphere( new Point3d(), 2000.0) );
        this.behaviorBG.addChild(this.refreshBehavior);
      } // refreshbehavior == null
      this.topBG.addChild(this.behaviorBG);
      //
      Thread refreshThread = new Thread() {
        public void run() {
          runForRefreshThread();
        }
      };
      System.out.println("START refreshbehavior");
      refreshThread.start();
    } // !isLive
    else {
      //System.out.println("DEBUG -- Avatar already has RefreshBehavior!");
    }
  }
  /**
   * Run method in which the Refresh-Thread lives.<p>
   * Started in setBody via the constructor.
   * @see #setBody(Body, int)
   */
  public void runForRefreshThread() {//must this really be public??
    for (;;) {
      if (isRefreshCalled) {
        isRefreshCalled = false;

        FuSpace newState = null;
        if (body != null) {
          try { newState = body.getState(); }
          catch (java.rmi.RemoteException e) {
            System.out.println("Exception in Avatar3D.runForRefreshThread="+e.getMessage() );
          }
          if (newState != null) { setState(newState); }
        }
      } else {
        synchronized (refreshBehavior) {
          try { refreshBehavior.wait();  }
          catch (Exception e) { e.printStackTrace();  }
        }
      }
    }
  }
  //inherited from Avatar
  public void setState(FuSpace state) {
    XYASpace newState=null;
    try{newState = (XYASpace)state;} catch (Exception e) {/*System.out.println("error in setState");*/ return;}

    this.vectorTranslation.set(newState.getX(), newState.getY(), 0);
    this.t3dTranslation.set(this.vectorTranslation);
    this.translateTG.setTransform(this.t3dTranslation);

    this.vectorRotation.set(0, 0, newState.getAlpha());
    this.t3dRotation.setEuler(this.vectorRotation);
    this.rotateTG.setTransform(this.t3dRotation);
  }


  /**
   * Sets a new shape and its parameters, also updates the view.<p>
   * Currently not implemented.
   * @param geometryId, the geometry-id for this new shape.
   * @param geometryId, the appearance-id for this new shape.
   * @param shape must be a BranchGroup,
   */
  public void setGeometryAndAppearance(int geometryId,int appearanceID, java.rmi.MarshalledObject shape)  {
  }


  /**
   * Removes a child from its parent.<p>
   * @param childAvatar the avatar of the child to be orphaned
   */
  public void removeChild(Avatar childAvatar) {
    Avatar3D child = (Avatar3D)childAvatar;
    child.getTopBG().detach();
    System.out.println("Avatar.removeChild...just succeeded");
  }

  /**
   * Adds another avatar to this avatar.<p>
   * The associated avatar will be a child of this avatar.
   * Therefore its position and orientation will be updated accordingly
   * @param avatar the avatar to be associated with
   */
  public void addChild(Avatar childAvatar) {
    Avatar3D child = (Avatar3D)childAvatar;
    this.bottomBG.addChild(child.getTopBG());
    System.out.println("Avatar.addChild...just succeeded");
  }


  /**
   * Sets a parent for this child.<p>
   * @param parentAvatar the avatar to be the new parent for this avatar
   */
  public void setParent(Avatar parentAvatar) {
    //if (parentAvatar != null)
    this.topBG.detach();
    Avatar3D parent = (Avatar3D)parentAvatar;
    parent.getBottomBG().addChild(this.topBG); // ook meegeven de rel pos van child !!!
  }

  /**
   * Returns the top BranchGroup of this avatar.
   * Same implementation as getTopBG
   * @see     #getTopBG()
   * @return  the top BranchGroup of this avatar
   */
  public BranchGroup getBG() {
    return this.topBG;
  }

  /**
   * Returns the top BranchGroup of this avatar
   * @return the top BranchGroup of this avatar
   */
  public BranchGroup getTopBG() {
    return this.topBG;
  }

  /**
   * Returns the bottom BranchGroup of this avatar.<p>
   * @return the bottom most BarnchGroup of this avatar
   */
  public BranchGroup getBottomBG() {
    return this.bottomBG;
  }
  /**
   * Sets the name of this Avatar.<p>
   * @param name  the name of this Avatar
   */
  public void setName(String name) {
    this.name = name;
  }
  //inherited from Avatar
  public void setText(String text) {
    this.currentGeometry.setText(text);
  }
  //inherited from Avatar
  private ServiceID serviceID = null;
  public void setServiceID(ServiceID serviceID) { this.serviceID = serviceID; }
  public ServiceID getServiceID() { return serviceID; }

}
