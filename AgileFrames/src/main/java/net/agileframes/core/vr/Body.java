package net.agileframes.core.vr;
import java.rmi.*;
import net.agileframes.core.forces.State;
import net.agileframes.forces.space.POS;


/**
 * Created: Wed Jan 12 14:56:39 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

 A machine must implement interface body if it has a 3D avatar which exists in
 virtuality. Via the interface body the avatar can request its position and orientation.
 Furthermore, the avatar may possess several 3D representations, and the avatar can request
 which representation it must assume.

 An avatar may also register with the body an thus allow the body to update the avatar.
*/

public interface Body extends java.rmi.Remote {

  class StateAndAvatar {
    public State state;
    public Avatar avatar;
    // public StateAndAvatar() {}
  }

  /**
  An avatar will represent the body's state.
  */
  public void addAvatar(Avatar avatar) throws RemoteException;


  /**
  An avatar stops to represent the body's state.
  */
  public void removeAvatar(Avatar avatar) throws RemoteException;

  /**
  returns the body's current staqe. The call is probably made by an avatar in order to update its presentation.
  */
  public State getState() throws RemoteException;

  public int getGeometryID() throws RemoteException;
  public int getAppearanceID() throws RemoteException;

  /**
  This body is the parent of (a) child-body, the child-body is removed and this
  body is no longer its parent. Assuming this body passesses an avatar, the corresponding child-avatar
  is also removed. (All this should happen under a transaction!!).
  */
  public State removeChild(Body child) throws RemoteException;

  /**
  This body is the new parent of child-body. The avatar ...
  */
  public StateAndAvatar addChild(Body child, State state) throws RemoteException;

  /**
  This body is a new child of the parent. The avatar ...
  */
  public void setParent(Body parent) throws RemoteException;

}
