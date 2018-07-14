package net.agileframes.traces;
import java.rmi.RemoteException;
import net.agileframes.server.ServiceProxy;
import net.agileframes.core.server.Server;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.core.services.Job;
import net.jini.core.lookup.ServiceID;
import net.agileframes.forces.MachineProxy;
import net.jini.core.lookup.ServiceTemplate;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.forces.MachineRemote;
/**
 * <b>The Proxy of the Actor-Object</b>
 * <p>
 * This proxy is an example of how intelligent code to a Actor-Stub can be
 * added that decreases network-traffic. The proxy 'remembers' answers from its
 * Actor-Stub and will decide whether or not it needs to make a remote call.<p>
 * Currently no intelligent code is added yet.
 * @see     net.agileframes.server.ServiceProxy
 * @see     ActorIB
 * @see     net.agileframes.core.traces.Actor
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */

public class ActorProxy extends ServiceProxy implements Actor {
  //------------------------ Attributes ----------------
  private Actor actor;
  private ServiceID actorID;
  //------------------------ Constructor ---------------
  /**
   * Constructor.<p>
   * Calls super, sets actor and actorID and uploads proxy.
   * @see   net.agileframes.server.ServiceProxy#ServiceProxy(Server,String,ServiceID,Entry)
   * @see   net.agileframes.server.ServiceProxy#uploadProxy()
   * @param actor     the actor of which this is the proxy
   * @param machineID the serviceID of the actor
   */
  public ActorProxy(Actor actor, ServiceID actorID) throws RemoteException {
    super((Server)actor, actor.getName(), actor.getServiceID());
    this.actor = actor;
    this.actorID = actorID;
    this.getServiceID();//only to load the serviceID in our memory
    this.uploadProxy();
  }
  //------------------------ Methods -------------------
  /**
   * Returns the actor-properties.
   * @see     Actor#getProperties()
   * @return  the actor-properties
   */
  public Actor.Properties getProperties() throws RemoteException {
    return actor.getProperties();
  }
  /**
   * Accepts a job.<p>
   * Currently not used.
   * @see   Actor#acceptJob(ServiceID,Job)
   * @param basicServerID the service-id of the actor's server
   * @param job           the job to accept
   */
  public boolean acceptJob(ServiceID basicServerID, Job job) throws NotTrustedException, RemoteException {
    return actor.acceptJob(basicServerID, job);
  }
  /**
   * Returns the service-id of the Actor.<p>
   * @return  the service-id of the actor
   */
  public ServiceID getActorID() throws RemoteException {
    return actorID;
  }
  private MachineProxy machineProxy = null;
  /**
   * Sets the machine-proxy belonging to this actor.<p>
   * This method is meant to be used at the remote side of the machine and actor.
   * When a pair of machine-actor is discovered, they can be linked here.
   * @param machineProxy  the machineProxy to set
   */
  public void setMachineProxy(MachineProxy machineProxy) {this.machineProxy = machineProxy; }
  /**
   * Returns the machine-proxy belonging to this actor.<p>
   * @see     #setMachineProxy(MachineProxy)
   * @return  the machine-proxy belonging to this actor.
   */
  public MachineProxy getMachineProxy() { return machineProxy; }
  /**
   * Disposes this actor.<p>
   * If the Actor-stub is not available anymore, the proxy will clean up
   * the Scene itself.<br>
   * @see   net.agileframes.traces.ActorIB#dispose()
   * @see   net.agileframes.core.traces.Scene#getTopScene()
   * @see   net.agileframes.core.traces.Scene#destroySceneAction(Actor)
   */
  public void dispose() {
    System.out.println("##ActorProxy.dispose()");
    try {
      actor.getMachine().dispose();
      actor.dispose();
      return;
    } catch (RemoteException e) {
      System.out.println("Actor-stub is not available anymore. Proxy will clean Scene");
    }
    //download a scene
    Class[] sceneClass = { net.agileframes.core.traces.Scene.class };
    ServiceTemplate sceneTemplate = new ServiceTemplate(null, sceneClass, null);
    Scene scene = (Scene)AgileSystem.lookup(sceneTemplate);// any scene
    if (scene == null) {
      System.out.println("ActorProxy cannot find any Scene. ");
      System.out.println("Nothing was disposed.");
      return;
    }
    System.out.println("Scene has been found.");
    try {
      scene.getTopScene().destroySceneAction(this);
    } catch (RemoteException e) {
      System.out.println("Connection to scene lost while destroying scene-action.");
      System.out.println("Exception ignored.");
    }
    System.out.println(System.currentTimeMillis()+": Actor disposed via its Proxy");
  }

  //inherited from Actor
  private MachineRemote machine = null;
  private boolean connectionLost = false;
  public MachineRemote getMachine() {
    if (connectionLost) { System.out.println("Connection lost in ActorProxy.getMachine()"); return null; }
    if (machine != null) { return machine; }
    try {
      machine = actor.getMachine();
    } catch (RemoteException e) {
      System.out.println("RemoteException while getting machine in ActorProxy.");
      System.out.println("Exception ignored");
      connectionLost = true;
    }
    return machine;
  }
  //inherited from Actor
  private ServiceID serviceID = null;
  public ServiceID getServiceID() {
    if (connectionLost) { System.out.println("Connection lost in ActorProxy.getServiceID()"); return null; }
    if (serviceID != null) { return serviceID; }
    try {
      serviceID = actor.getServiceID();
    } catch (RemoteException e) {
      System.out.println("RemoteException while getting serviceID in ActorProxy.");
      System.out.println("Exception ignored");
      connectionLost = true;
    }
    return serviceID;
  }
  public ServiceID getServiceID(long serialVersionUID) { return getServiceID(); }
  private String loginBaseName;
  public String getLoginbaseName() {
    if (connectionLost) { System.out.println("Connection lost in ActorProxy.getServiceID()"); return "NotFound"; }
    if (loginBaseName != null) { return loginBaseName; }
    try {
      loginBaseName = actor.getLoginbaseName();
    } catch (RemoteException e) {
      System.out.println("RemoteException while getting loginBaseName in ActorProxy.");
      System.out.println("Exception ignored");
      connectionLost = true;
    }
    return loginBaseName;
  }

  /**
   * Checks if the two instances are equal.<p>
   * @param   obj the object to be checked
   * @return  <code><b>true </code></b>  iff the two objects are equal<br>
   *          <code><b>false</code></b>  iff the two objects are not equal
   */
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof Actor)) return false;
    Actor actor = (Actor)obj;
    try { return this.serviceID.equals(actor.getServiceID()); }
    catch (RemoteException e) { e.printStackTrace(); }
    return false;
  }

}