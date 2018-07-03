package net.agileframes.core.vr;

import java.rmi.RemoteException;
import java.rmi.Remote;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.FuSpace;


/**
 * <b>An avatar presents the state of a body to an audience.</b>
 * <p>
 * @see Body
 * @see BodyRemote
 * @see Virtuality
 * Created: Wed Jan 12 14:56:39 2000
 * @author  D.G. Lindeijer, F.A. van Dijk, H.J. Wierenga
 * @version 0.1
 */

public interface Avatar extends Remote {

  /**
   * Set the body to which this avatar belongs.<p>
   * @param body  the body to set
   */
  public void setBody(Body body) throws RemoteException;

  /**
   * Set the functional state, probably position and orientation. <p>
   * @param state the functional state to set
   */
  public void setState(FuSpace state) throws RemoteException;

  /**
   * Sets a new geometry (partial state) for the presentation.
   * @param   id  the specified geometry
   */
  public void setGeometryID(int id) throws RemoteException;

  /**
   * Sets a new appearance (partial state) for the presentation.
   * @param   id  the specified appearance
   */
  public void setAppearanceID(int id) throws RemoteException;

 /**
  * Sets an new shape and its parameters, also update the view.<p>
  * @param geometryID     the geometry-id for this new shape.
  * @param appearanceID   the appearance-id for this new shape.
  * @param shape          must be a BranchGroup,
  */
  public void setGeometryAndAppearance(int geometryID,int appearanceID,
    java.rmi.MarshalledObject shape) throws RemoteException;

  /**
   * Sets an new shape with the specified geometry and apperance.<p>
   * Geometry has effect on the shape of the avatar. Appearance on
   * the outlook, for example the color.
   * @param   geometryID    the specified geometry
   * @param   appearanceID  the specified appearance
   */
  public void setGeometryAndAppearanceID(int geometryID,int appearanceID) throws RemoteException;

  /**
   * Sets a parent for the avatar. <p>
   * It may be more efficient for the avatar to obtain
   * state-data about its body from its parent-avatar, which is also the avatar
   * of the body's parent.
   * @param avatar  the parent-avatar
   */
  public void setParent(Avatar avatar) throws RemoteException;

  /**
   * Refreshes the avatar.
   * @see net.agileframes.vr.RefreshBehavior#processStimulus(Enumeration)
   */
  public void refresh();

  /**
   * Provides the avatar with a text to display.<p>
   * @param   text    the text to display on the avatar
   */
  public void setText(String text) throws RemoteException;

  /**
   * Sets ServiceID on the avatar.<p>
   * @param serviceID the serviceID to set
   */
  public void setServiceID(ServiceID serviceID);
  /**
   * Gets the ServiceID of this avatar.<p>
   * @return the serviceID
   */
  public ServiceID getServiceID();
}

