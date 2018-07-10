package com.agileways.miniworld.lps;
import net.agileframes.core.server.Server;
import net.agileframes.core.forces.FuTransform;

public interface RemoteDistributor extends Server, java.rmi.Remote {
  public void registerStateListener(int machineNr, RemoteStateListener listener) throws java.rmi.RemoteException;
  public void unregisterStateListener(int machineNr) throws java.rmi.RemoteException;
  /**
   * Sets transform
   * @param t     transform with x,y in meters, alpha in rad
   * @param scale value=1 is normal
   */
  public void setTransformAndScale(FuTransform t, double scale) throws java.rmi.RemoteException;
}
