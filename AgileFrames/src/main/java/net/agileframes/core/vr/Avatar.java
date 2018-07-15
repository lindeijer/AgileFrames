package net.agileframes.core.vr;

import net.agileframes.forces.space.Orientation;
import net.agileframes.forces.space.Position;
import net.agileframes.forces.space.POS;
import java.rmi.RemoteException;
import java.rmi.Remote;
import net.agileframes.core.forces.State;


/**
 * Created: Wed Jan 12 14:56:39 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

 An avatar presents the state of a body to an audience.
 */

public interface Avatar extends java.rmi.Remote {

  /**
  set the body this avatar belongs to
  */
  public void setBody(Body body) throws RemoteException;

  /**
  set the functional state, probably position and orientation
  */
  public void setState(State state) throws RemoteException;

  /**
  set a new geometry (partial state) for the presentation. hmmmmm
  */
  public void setGeometryID(int i) throws RemoteException;

  /**
  set a new appearance (partial state) for the presentation. hmmmmm
  */
  public void setAppearanceID(int i) throws RemoteException;

  /**
  set an new shape and its parameters, also update the view.
  @param geometryId, the geometry-id for this new shape.
  @param geometryId, the appearance-id for this new shape.
  @param shape must be a BranchGroup,
  */
  public void setGeometryAndAppearance(int geometryId,int appearanceID,
    java.rmi.MarshalledObject shape) throws RemoteException;

  /**
   * set an new shape with the specified geometry and apperance.
   */
  public void setGeometryAndAppearanceID(int geometryId,int appearanceID)
    throws RemoteException;

  /**
   * set a parent for the avatar. It may be more efficient for the avatar the obtain
   * state-data about its body from its parent-avatar, which is also the avatar
   * of the body's parent.
   */
  public void setParent(Avatar avatar) throws java.rmi.RemoteException;

}

