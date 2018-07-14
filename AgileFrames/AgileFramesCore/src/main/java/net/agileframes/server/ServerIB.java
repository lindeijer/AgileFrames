package net.agileframes.server;
import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import net.jini.space.JavaSpace;
import net.agileframes.core.server.Server;
/**
 * <b>Implementation of Server</b>
 * <p>
 * Takes of uploading etcetera. Very useful object.
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class ServerIB extends UnicastRemoteObject implements Server {
  /** Empty Constructor. Not used. */
  public ServerIB() throws RemoteException {}
  /** Name of this server. */
  public String name = null;
  /** ServiceID of this server. */
  protected ServiceID serviceID = null;
  /**
   * Constructor that will not register.<p>
   * Only sets name.
   * @param name  the name of this server
   */
  public ServerIB(String name) throws RemoteException  {
    this.name = name;
  }
  /**
   * Constructor that takes care of registering.<p>
   * @see   #registerServer(ServiceID)
   * @param name      the name of this server
   * @param serviceID the service-id of this server, iff null, a service-id will be created.
   */
  public ServerIB(String name,ServiceID serviceID) throws RemoteException {
    this.name = name;
    registerServer(serviceID);
  }
  /**
   * Registers this server.<p>
   * @see   #ServerIB(String,ServiceID)
   * @see   AgileSystem#registerServer(Server)
   * @see   AgileSystem#registerServer(Server,ServiceID)
   * @param serviceID the service-id of this server, iff null, a service-id will be created.
   */
  public void registerServer(ServiceID serviceID) {
    if (serviceID == null) {
      this.serviceID = AgileSystem.registerServer(this);
    } else {
      this.serviceID = serviceID;
      AgileSystem.registerServer(this,serviceID);
    }
    System.out.println(getName() + " serviceID=" + this.serviceID.toString() + " (exported)");
  }
  /**
   * Disposes this server.<p>
   * Will unregister this server.
   * @see AgileSystem#unregisterServer(Server)
   */
  public void dispose() {
    AgileSystem.unregisterServer(this);
  }
  /**
   * Returns name of this server.<p>
   * The return-value is the name+@+classname.
   * @return the name of this server
   */
  public String getName() {
    return name + "@" + this.getClass().getName();
  }
  public String getLoginbaseName() {
    return AgileSystem.getLoginbaseName();
  }
  public ServiceID getServiceID() {
    return serviceID;
  }
  public ServiceID getServiceID(long serialVersionUID) {
    return null;
  }
  /**
   * Returns space of this server. Not implemented.
   * @param   proxyID the serviceID of the proxy
   * @return  the java-space of this server
   */
  public JavaSpace getSpace(ServiceID proxyID) {
    return null;
  }

}
