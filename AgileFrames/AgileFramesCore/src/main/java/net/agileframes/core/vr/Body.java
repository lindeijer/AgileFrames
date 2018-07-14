package net.agileframes.core.vr;
import net.agileframes.core.forces.FuSpace;
import java.rmi.RemoteException;


/**
 * <b>A machine must implement interface body if it has an avatar which exists in virtuality.</b>
 * <p>
 * Via the interface body the avatar can request its position and orientation.
 * Furthermore, the avatar may possess several representations, and the avatar
 * can request which representation it must assume.
 * An avatar may also register with the body an thus allow the body to update the avatar.
 * Created: Wed Jan 12 14:56:39 2000
 * @author  D.G. Lindeijer, F.A. van Dijk
 * @version 0.1
 */

public interface Body extends java.io.Serializable {

  /**
   * An avatar will represent the body's state.
   * @param avatar the avatar that will represent the body's state
   */
  public void addAvatar(Avatar avatar) throws RemoteException;


  /**
   * An avatar stops to represent the body's state.
   * @param avatar the avatar to be removed
   */
  public void removeAvatar(Avatar avatar) throws RemoteException;

  /**
   * Get the body's current state.
   * The call is probably made by an avatar in order to update its presentation.
   * If a body is a child of some parentbody it does not contain its absolute state, but only its state relative to its parent.
   * This method 'chains' all the way back to the topmost body and recursively calculates the absolute state
   * @return a copy of the absolute state
   */
  public FuSpace getState() throws RemoteException;

  /**
   * Get the ID of the current geometry
   * @return the current ID
   */
  public int getGeometryID() throws RemoteException;


  /**
   * Get the ID of the current appearance
   * @return int the current appearanceID
   */
  public int getAppearanceID() throws RemoteException;

  /**
   * Remove the a child from its parent (this).
   * This body is the parent of (a) child-body.
   * The child-body is removed and this body is no longer its parent.
   * Assuming this body passesses an avatar, the corresponding child-avatar is also removed.
   * (All this should happen under a transaction!!).
   * @param child the child to be removed
   * @return the absolute state of the 'old' parent-body
   */
  public FuSpace removeChild(BodyRemote child) throws RemoteException;

  /**
   * Add a child to this body
   * This body is the new parent of child-body.
   * The parent-avatar performs a similar method on the child-avatar
   * @param child the child's body to be added
   * @param state this body's absolute state
   * @return the relative postion of the childbody with respect to its parent, and a reference to the this.avatar
   */
  public Body.StateAndAvatar addChild(BodyRemote child, FuSpace state) throws RemoteException;


  /**
   * Make a certain body the new parent of this body
   * This body will be the child of parent(body).
   * The avatar's are also connected in a similar fashion.
   * If this body already has a parent then the old parent and avatar are removed and the new body and avatar are linked
   * @param parent the new parent of this body
   */
  public void setParent(BodyRemote parent) throws RemoteException;

  /**
   * <b>Internal class used to bundle the state of a Body and a reference to its Avatar.</b>
   * <p>
   * This class for created to prevent multiple Remote Method Calls.
   */
  class StateAndAvatar {
    // The state of a Body
	public FuSpace state;
    // A reference to the corresponding Avatar
    public Avatar avatar;
    public StateAndAvatar() {}
  }

}
