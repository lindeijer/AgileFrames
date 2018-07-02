package net.agileframes.server;


import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import net.jini.space.JavaSpace;

//import java.util.*;
//import com.sun.jini.lookup.ServiceIDListener;
//import java.rmi.activation.Activatable;
//import java.rmi.activation.ActivationID;
//import java.rmi.MarshalledObject;
//import com.sun.jini.lookup.JoinManager;
//import net.jini.core.lookup.ServiceRegistration;


/**
Implementation-base class interface Server, it
<ul>
<li>acquires a unique serviceID if not provided in the constructor.
<li>does not export this object to the rmi-daemon, it is not persistent.
<li>exports this object to the rmi-broker/network
<li>registers with AgileSystem under its unique serviceID.
<li>provides a unique serviceID for back-calling services.
<li>provides a space for communication with services.
<li>does not automatically export an serviceProxy, the extension must do this itself
</ul>
@see net.agileframes.core.server
*/

public class ServerImplBase extends UnicastRemoteObject implements net.agileframes.core.server.Server {

  /**
  The name of this server, its full name is name@class.toString().
  @see getName()
  */
  public String name = null;

  /**
  The serviceID of this server (not of any of its provided services!)
  */
  protected ServiceID serviceID = null;

  /////////////////////////////////////////////////////////////////////////

  public ServerImplBase(String name) throws RemoteException  {
    this(name,null);
  }

  public ServerImplBase(String name,ServiceID serviceID) throws RemoteException {
    this.name = name;
    if (serviceID == null) {
      this.serviceID = AgileSystem.registerServer(this);
    } else {
      this.serviceID = serviceID;
      AgileSystem.registerServer(this,serviceID);
    }
    /* this class extends unicastremoteobject now
    try {
      UnicastRemoteObject.exportObject(this);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in AgileServer() of " + this.getName() +  " = " + e.getMessage());
      System.exit(1);
    }
    */
    System.out.println(getName() + " serviceID=" +
                       this.serviceID.toString() + " (exported)");
  }

  //////////////////////////////////////////////////////////////////////

  /**
  Unregisters the server with the AgileSystem. Call this method if you
  overload it, and you should. This methods is called by the AgileSystem
  (on all registered servers) when the AgileSystem itself must dispose.
  */
  public void dispose() {
    AgileSystem.unregisterServer(this);
  }

  /////// implementation of net.agileframes.core.server.Server /////////////////

  public String getName() {
    return name + "@" + this.getClass().getName();
  }

  public String getLoginbaseName() {
    return AgileSystem.getLoginbaseName();
  }

  public ServiceID getServiceID() {
    return serviceID;
  }

  /**
  @param serialVersionUID of an uploaded service.
  @return serviceID for the downloaded service.
  */
  public ServiceID getServiceID(long serialVersionUID) {
    return null;
  }

  /**
  @param serviceID of a downloaded service
  @return space for communication between this server and the downloaded service.
  */
  public JavaSpace getSpace(ServiceID proxyID) {
    return null;
  }

}