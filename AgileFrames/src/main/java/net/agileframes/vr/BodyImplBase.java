package net.agileframes.vr;

import net.agileframes.core.vr.*;
import net.agileframes.core.forces.State;
import net.agileframes.forces.space.POS;
import net.agileframes.core.vr.Body.StateAndAvatar;

import net.agileframes.vr.AvatarImplBase;

import javax.vecmath.*;
import javax.media.j3d.*;

public class BodyImplBase implements Body{

  public POS state;
  public Body parent;
  public Avatar avatar;
  public Transform3D t3d;
  public String name;
  public boolean smart;
  private EulerXYZ eulerParentAbsolute = null;
  private EulerXYZ eulerChildAbsolute = null;
  private EulerXYZ eulerChildRelative = null;
  private EulerXYZ eulerGeneral = null;
  private Matrix4d matrix4d = null;

  private Vector3f vectorOld = null;
  private Vector3f vectorNew = null;
  private Vector3f vectorGeneral = null;
  private Vector3d vectorAngles = null;
  private float angleX, angleY, angleZ;


  public BodyImplBase() {
    this.state = new POS();
    this.parent = null;
    this.avatar = null;
    this.t3d = new Transform3D();

    this.eulerChildAbsolute  = new EulerXYZ();
    this.eulerChildRelative  = new EulerXYZ();
    this.eulerParentAbsolute = new EulerXYZ();

    this.matrix4d = new Matrix4d();
    this.eulerGeneral = new EulerXYZ();

    this.vectorOld = new Vector3f();
    this.vectorNew = new Vector3f();
    this.vectorGeneral = new Vector3f();

    this.vectorAngles = new Vector3d();
  }


  /**
   * Add an avatar to this body
   * @param avatar the avatar to be added to this body
   */
  public void addAvatar(Avatar avatar) {
    this.avatar = avatar;
  }

  /**
   * Remove an avatar from this body
   * @param avatar the avatar to be removed from this body
   */
  public void removeAvatar(Avatar avatar) {

  }

  private void copy(POS state) {
    this.state.x     = state.x;
    this.state.y     = state.y;
    this.state.z     = state.z;
    this.state.alpha = state.alpha;
    this.state.beta  = state.beta;
    this.state.gamma = state.gamma;
  }

  public void setState(POS state) {
    this.state.x     = state.x;
    this.state.y     = state.y;
    this.state.z     = state.z;
    this.state.alpha = state.alpha;
    this.state.beta  = state.beta;
    this.state.gamma = state.gamma;
  }

  /**
   * Get this object's state variables
   * @return This classes state object
   */
  public State getInternalState() {
    return new POS(state.x, state.y, state.z, state.alpha, state.beta, state.gamma);
  }

  /**
   * Get the absolute state
   * @return The absolute state of the object
   */
  public State getState() {
    float x, y, z;
    double alpha, beta, gamma;

    POS thisState   = this.state;
    POS parentState = null;
    POS returnState = null;

    if (this.parent == null) {
    // bovenste object in ketting bereikt
      returnState = new POS(this.state.x, this.state.y, this.state.z, this.state.alpha, this.state.beta, this.state.gamma);
    }
    else {
      try {
        parentState = (POS)this.parent.getState();
      }
      catch (java.rmi.RemoteException e) {
        System.err.println( e.getMessage() );
        System.exit(0);
      }
      returnState = this.calcAbsoluteState(parentState, thisState);
    }  // parent NOT null

    return returnState;
  }


  /**
   * Get the ID of the current geometry
   * @return ID belonging to the current geometry
   */
  public int getGeometryID() {
    return 0;
  }

  /**
   * Get the ID of the current appearance
   * @return ID belonging to the current appearance
   */
  public int getAppearanceID() {
    return 0;
  }

  /**
   * calculate the absolute state of this object
   * @param parentState the state of this.parent
   * @param childStateRelativeToParent my state relative to my parent
   * @return my absolute state
   */
  public POS calcAbsoluteState(POS parentState, POS childStateRelativeToParent) {
    //With the old parents state calculate my absolute state
    //t3d.setEuler(new Vector3d(parentState.gamma, parentState.beta, parentState.alpha));
    //Vector3f oldVec = new Vector3f(childStateRelativeToParent.x, childStateRelativeToParent.y, childStateRelativeToParent.z);
    //Vector3f newVec = new Vector3f();
    this.vectorOld.set(childStateRelativeToParent.x, childStateRelativeToParent.y, childStateRelativeToParent.z);


    //t3d.transform(oldVec, newVec);

    //EulerXYZ eulerXYZ = new EulerXYZ();
    this.eulerGeneral.set(parentState.gamma, parentState.beta, parentState.alpha);
    //eulerXYZ.set(parentState.gamma, parentState.beta, parentState.alpha);
    //t3d.set(eulerXYZ.getRotationMatrix());
    this.t3d.set(this.eulerGeneral.getRotationMatrix());
    //t3d.transform(oldVec, newVec);
    this.t3d.transform(this.vectorOld, this.vectorNew);

    //float x = newVec.x + parentState.x;
    //float y = newVec.y + parentState.y;
    //float z = newVec.z + parentState.z;
    //this.vectorGeneral
    this.vectorGeneral.x = this.vectorNew.x + parentState.x;
    this.vectorGeneral.y = this.vectorNew.y + parentState.y;
    this.vectorGeneral.z = this.vectorNew.z + parentState.z;
    //this.vectorGeneral.add(this.vectorNew, parentState.);
    //EulerXYZ absoluteChildEulerXYZ  = new EulerXYZ();
    //EulerXYZ relativeChildEulerXYZ  = new EulerXYZ(childStateRelativeToParent.gamma, childStateRelativeToParent.beta, childStateRelativeToParent.alpha);
    this.eulerChildRelative.set(childStateRelativeToParent.gamma, childStateRelativeToParent.beta, childStateRelativeToParent.alpha);
    //EulerXYZ absoluteParentEulerXYZ = new EulerXYZ(parentState.gamma, parentState.beta, parentState.alpha);
    this.eulerParentAbsolute.set(parentState.gamma, parentState.beta, parentState.alpha);
    //Matrix4d m = new Matrix4d();
    this.matrix4d.mul(this.eulerParentAbsolute.getRotationMatrix(), this.eulerChildRelative.getRotationMatrix());
    //m.mul(absoluteParentEulerXYZ.getRotationMatrix(), relativeChildEulerXYZ.getRotationMatrix());
    //m.mul(this.eulerParentAbsolute.getRotationMatrix(), this.eulerChildRelative.getRotationMatrix());
    //absoluteChildEulerXYZ.set(m);
    this.eulerChildAbsolute.set(this.matrix4d);
    //double[] angles = absoluteChildEulerXYZ.calculateEulerAngles();
    Vector3d angles = this.eulerChildAbsolute.calculateEulerAngles();

    return new POS(this.vectorGeneral.x, this.vectorGeneral.y, this.vectorGeneral.z, angles.z, angles.y, angles.x);
  }

  /**
   * Set the new parent of this body
   * @param the body of the new parent
   */
  public void setParent(Body parentBody) {
    POS oldParentState  = null;
    POS myRelativeState = this.state;
    POS myAbsoluteState = null;
    StateAndAvatar stateAndAvatar = null;

    if (this.parent != null) {
      //Remove (this)child from its current parent, and get the parents absolute state
      //System.out.println("DEBUG:: parent NOT null");
      try {
        oldParentState = (POS)this.parent.removeChild(this);
      }
      catch (java.rmi.RemoteException e) {
        System.out.println( e.getMessage() );
        System.exit(0);
      }
      //With the old parents state calculate my absolute state
      myAbsoluteState = this.calcAbsoluteState(oldParentState, myRelativeState);
    }
    else { // parent == null
      myAbsoluteState = new POS(state.x, state.y, state.z, state.alpha, state.beta, state.gamma);
    }
    try {
      //Add me to my new parent, it will return my new relative state and a reference to my new parents avatar
      stateAndAvatar = parentBody.addChild(this, myAbsoluteState);
    }
    catch (java.rmi.RemoteException e) {
      System.out.println( e.getMessage() );
      System.exit(0);
    }
    //Set the relative State in my internal structure
    setState((POS)stateAndAvatar.state);
    //System.out.println("DEBUG:: StateAndAvatar in Body.setParent = "+debugState.toString());
    try {
      //Tell the avatar its new state. Do not change the order !!!!
      //because then during debugging wrong values will be read
      this.avatar.setState(this.state);
      //Tell the avatar to change its parent also
      this.avatar.setParent(stateAndAvatar.avatar);
    }
    catch (java.rmi.RemoteException e) {
      System.out.println( e.getMessage() );
      System.exit(0);
    }
    //Finally, link me to my new parent
    this.parent = parentBody;
  }

  /**
   * Add a child to this parent
   * @param childBody the child(body) to be added
   * @param state the absolute state of the child
   * @return the relative postion of the child with respect to its parent, and a reference to the this.avatar
   */
  public StateAndAvatar addChild(Body childBody, State state) {
    StateAndAvatar sa = new StateAndAvatar();
    //What is my state
    POS parentState = (POS)this.getState();
    //System.out.println("DEBUG:: AbsparentState in Body.addChild"+parentState.toString() );
    //What is the childs state
    POS childState =  (POS)state;
    //System.out.println("DEBUG:: AbsChildState in Body.addChild"+childState.toString() );
    //What is the childs relative position with respect to its new parent(this)
    POS relativeChildState = this.getStateRelativeTo(parentState, childState);
    //Put new found values in return object StateAndAvatar
    sa.state = relativeChildState;
    sa.avatar = this.avatar;
    return sa;
  }

  /**
   * Removes a child from its parent (this=parent)
   * @param childBody the child to be removed
   * @return returns the absolute state of the parent
   */
  public State removeChild(Body childBody) {
    return this.getState();
  }

  private boolean parentOf(Body childBody) {
    BodyImplBase child = (BodyImplBase)childBody;
    return (child.parent == this);
  }

  public Body getParent() {
    return this.parent;
  }

  /**
   * Determines the relative position of this with respect to an other Body
   * @param parentBody the other (parent)body
   */
  public State getStateRelativeTo(Body parentBody) {
    POS absChildState = null;
    POS absParentState = null;
    float x;
    float y;
    float z;
    double alpha;
    double beta;
    double gamma;

    // Get the childs absolute state
    absChildState  = (POS)this.getState();
    try {
      // Get the parents absolute state
      absParentState = (POS)parentBody.getState();
    }
    catch (java.rmi.RemoteException e) {
      System.out.println(e.getMessage());
      System.exit(0);
    }
    // Get the relative state
    x = absChildState.x - absParentState.x;
    y = absChildState.y - absParentState.y;
    z = absChildState.z - absParentState.z;

    alpha = absChildState.alpha - absParentState.alpha;
    beta  = absChildState.beta  - absParentState.beta;
    gamma = absChildState.gamma - absParentState.gamma;

    return new POS(x, y, z, alpha, beta, gamma);
  }

  /**
   * calculate the child's state relative to the (new) parent state
   * @param parentState the state of the (new) parent
   * @param childState the absolute state of th child
   */
  public POS getStateRelativeTo(POS parentState, POS childState) {
    // calculate the relative position in absolute axis
    float x = childState.x - parentState.x;
    float y = childState.y - parentState.y;
    float z = childState.z - parentState.z;
    // now calculate the relative orientation.....
    // Put the child's state in an euler Rotation matrix
    //EulerXYZ childEulerXYZ = new EulerXYZ(childState.gamma, childState.beta, childState.alpha);
    this.eulerChildAbsolute.set(childState.gamma, childState.beta, childState.alpha);
    // Put the parent's state also in an euler Rotation matrix
    //EulerXYZ parentEulerXYZ = new EulerXYZ(parentState.gamma, parentState.beta, parentState.alpha);
    this.eulerParentAbsolute.set(parentState.gamma, parentState.beta, parentState.alpha);
    // Now get the child's rotation matrix
    //Matrix4d childMatrix = childEulerXYZ.getRotationMatrix();

    //..and the parent's rotation matrix
    //Matrix4d parentMatrix = parentEulerXYZ.getRotationMatrix();
    // Formula is Parent(abs) * Child(rel) = Child(abs) => Child(rel) = Parent(abs)(inv) * Child(abs)
    // Inverting the parent

    //parentMatrix.invert();
    // ... and multiplying with the child's absloute matrix
    //Matrix4d childRelative = new Matrix4d();
    //this.matrix4d.mul();
    this.matrix4d.set(this.eulerParentAbsolute.getRotationMatrix());
    this.matrix4d.invert();
    this.matrix4d.mul(this.eulerChildAbsolute.getRotationMatrix());

    //this.matrix4d.mul(this.eulerParentAbsolute.getRotationMatrix().invert(), this.eulerChildAbsolute.getRotationMatrix());
    //childRelative.mul(parentMatrix, childMatrix);
//    this.eulerChildRelative
    // Now put the matrix into an EulerXYZ object to determine alpha, beta, gamma
    //childEulerXYZ.set(childRelative);
    this.eulerChildRelative.set(this.matrix4d);
    //double[] triplet = childEulerXYZ.calculateEulerAngles();
    //Vector3d triplet = this.eulerChildRelative.calculateEulerAngles();
    //this.vectorAngles.set(this.eulerChildRelative.calculateEulerAngles());
    Vector3d anglesChildRelative = this.eulerChildRelative.calculateEulerAngles();
    // now transform the absolute vector to the new-parents axis
    //Transform3D t3d = new Transform3D();


    //Vector3d angles = new Vector3d(parentState.gamma, parentState.beta, parentState.alpha);
    //Vector3f angles = this.vectorGeneral;
    this.vectorAngles.set(parentState.gamma, parentState.beta, parentState.alpha);
    //angles.set(parentState.gamma, parentState.beta, parentState.alpha);
    this.t3d.setEuler(this.vectorAngles);
    this.t3d.invert();
    //Vector3f oldVec = new Vector3f(x, y, z);
    this.vectorOld.set(x, y, z);
    //Vector3f newVec = new Vector3f();
    this.t3d.transform(this.vectorOld, this.vectorNew);

    //return new POS(newVec.x, newVec.y, newVec.z, alpha, beta, gamma);
    return new POS(this.vectorNew.x, this.vectorNew.y, this.vectorNew.z, anglesChildRelative.z, anglesChildRelative.y, anglesChildRelative.x);
  }
  /*
  public POS getAvatarState() {
    AvatarImplBase ding = (AvatarImplBase)this.avatar;
    POS returnPOS = ding.getAbsoluteAvatarPOS();
    return returnPOS;
  }
  */
}