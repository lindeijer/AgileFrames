package net.agileframes.server;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.server.Server; // Remote interface

import java.rmi.RemoteException;

/**
Uploaded by an AgileServer with the serviceID of the AgileServer
downloaded from lookup services by clients.
acquires its own serviceID from AgileSystem
Provides basic functionality implied by the services.Service interface
*/

public class ServiceProxy implements net.agileframes.core.server.Service {

  Server server = null;

  public ServiceProxy(Server server) {
    this.server = server;
  }

  public ServiceProxy() {}

  public net.agileframes.core.server.Server getServer() { return server; }
  public void setClient(Object client,ServiceID clientID) { }
  public void setClient(Object client) {  }

}




