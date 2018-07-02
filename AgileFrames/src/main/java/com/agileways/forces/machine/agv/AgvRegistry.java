package com.agileways.forces.machine.agv;
import java.rmi.RemoteException;
import java.rmi.Remote;


public interface AgvRegistry extends Remote {

  public void rebind(String name,AGV agv) throws RemoteException;
  public AGV lookup(String name) throws RemoteException;

} 