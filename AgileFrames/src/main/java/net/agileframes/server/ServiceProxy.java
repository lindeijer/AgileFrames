package net.agileframes.server;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.server.Server; // Remote interface
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;

import java.rmi.RemoteException;


/**
 * <b>The Service-Proxy.</b>
 * <p>
 * Uploaded by an AgileServer with the serviceID of the AgileServer
 * downloaded from lookup services by clients.
 * acquires its own serviceID from AgileSystem
 * Provides basic functionality implied by the services.Service interface
 * @author  D.G. Lindeijer
 * @version 0.1
 */

public class ServiceProxy implements net.agileframes.core.server.Service {
  private Server server;
  private String name;
  private ServiceID serviceID;
  private Entry[] attributeSet;
  /**
   * Constructor without attributes.<p>
   * @see   #ServiceProxy(Server,String,ServiceID,Entry[])
   */
  public ServiceProxy(Server server, String name, ServiceID serviceID) {
    this(server, name, serviceID, new Entry[] {});
  }
  /**
   * Constructor with one attribute.<p>
   * @see   #ServiceProxy(Server,String,ServiceID,Entry[])
   */
  public ServiceProxy(Server server, String name, ServiceID serviceID, Entry attribute) {
    this(server, name, serviceID, new Entry[] {attribute});
  }
  /**
   * Constructor with attributes.<p>
   * Sets the parameters.
   * Creates an attributeSet that contains the specified attributes and the name
   * of this ServiceProxy.
   * @param server      the server of this proxy
   * @param name        the name of this proxy
   * @param serviceID   the unique service id of this proxy
   * @param attributes  the attributes of this proxy, may be null
   */
  public ServiceProxy(Server server, String name, ServiceID serviceID, Entry[] attributes) {
    this.server = server;
    this.name = name;
    this.serviceID = serviceID;
    if (attributes == null) { attributes = new Entry[] {}; }
    this.attributeSet = new Entry[attributes.length + 1];
    this.attributeSet[0] = new Name(this.name);
    for (int i = 1; i < (attributes.length + 1); i++) {
      this.attributeSet[i] = attributes[i-1]; // this is indeed the avatarfactory, i tested it 08MRT2001
    }
  }
  /** Empty Constructor. Not used. */
  public ServiceProxy() {}
  /**
   * Uploads this ServiceProxy.<p>
   * @see   AgileSystem#registerService(Server,ServiceID,Object,Entry[])
   */
  public void uploadProxy() {
    ServiceID resultID = AgileSystem.registerService(server, serviceID, this, attributeSet);
    if (resultID != null) { System.out.println(name + " uploaded Proxy_Stub with ServiceID "+resultID.toString()); }
    else { System.out.println(name + " did not upload Proxy_Stub"); }
  }
  public net.agileframes.core.server.Server getServer() { return server; }
  /**
   * Returns the name of this ServiceProxy.<p>
   * @return the name of this ServiceProxy.
   */
  public String getName() { return name; }
  public void setClient(Object client, ServiceID clientID) { }
  public void setClient(Object client) {  }

}




