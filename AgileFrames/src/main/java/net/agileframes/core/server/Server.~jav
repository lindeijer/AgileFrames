package net.agileframes.core.server;

import java.rmi.RemoteException;
import java.lang.String;
import net.jini.core.lookup.ServiceID;
import java.rmi.Remote;

/**
A server should extend and implement this remote interface. Such an interface
would identify the server as an agileframes-server and provide remote methods
service-proxies need to communicate the back-end protocol with their server.

@see net.agileframes.server.ServerImplBase

 * @since AgileFrames 1.0.0
 * @author Lindeijer, Evers
 * @version 0.0.1

*/

public interface Server extends Remote {

  /**
  Gets the name of the server.
  @return name + "@" + class.toSting().
  */
  String getName() throws RemoteException;

  /**
  Gets the name of the loginbase the server belongs to.
  @return name of the loginbase
  */
  String getLoginbaseName() throws RemoteException;

  /**
  Gets the serviceID of the server itself. The serviceID is guaranteed to be
  unique, even between different loginbases.
  @return the serviceID of the server
  */
  ServiceID getServiceID() throws RemoteException;

  /**
  Gets a new serviceID for a service to be provided to a client. 
  @param serialVersionUID of the class of the service now calling back.
  @return serviceID the service should use.
  */
  ServiceID getServiceID(long serialVersionUID) throws RemoteException;

}




