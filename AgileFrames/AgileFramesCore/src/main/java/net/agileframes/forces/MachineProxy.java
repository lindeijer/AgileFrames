package net.agileframes.forces;
import java.rmi.RemoteException;
import net.agileframes.server.AgileSystem;
import net.agileframes.server.ServiceProxy;
import net.agileframes.core.forces.MachineRemote;
import net.agileframes.core.server.Server;
import net.agileframes.core.vr.BodyRemote;
import net.agileframes.core.vr.Body;
import net.agileframes.core.vr.Avatar;
import net.agileframes.core.forces.FuSpace;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.vr.AvatarFactory;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Proxy of the Machine-Object</b>
 * <p>
 * This proxy is an example of how intelligent code to a Machine-Stub can be
 * added that decreases network-traffic. The proxy 'remembers' answers from its
 * Machine-Stub and will decide whether or not it needs to make a remote call.<p>
 * The proxy will be uploaded together with its Avatar, using the AvatarFactory
 * Entry object. AvatarFactory takes care that the Avatar belonging to the Machine's
 * Body can be viewed in a remote virtuality.
 * @see     net.agileframes.server.ServiceProxy
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class MachineProxy extends ServiceProxy implements Body /*, MachineRemote*/ {
  //------------------------ Attributes ----------------
  /** The Stub of MachineRemote. MachineRemote (probably) resides on another computer.*/
  private MachineRemote machine;
  //------------------------ Constructor ---------------
  /**
   * Constructor.<p>
   * Calls super, sets machine and uploads proxy.
   * @see   net.agileframes.server.ServiceProxy#ServiceProxy(Server,String,ServiceID,Entry)
   * @see   net.agileframes.server.ServiceProxy#uploadProxy()
   * @see   net.agileframes.core.vr.Body
   * @param machine the machine of which this is the proxy
   * @param factory the factory containing the avatar belonging to this Machine's Body
   */
  public MachineProxy(MachineRemote machine, AvatarFactory factory) throws RemoteException {
    super((Server)machine, machine.getName(), machine.getServiceID(), factory);
    this.machine = machine;
    this.getServiceID();//to load in memory
    this.uploadProxy();
  }

  //------------------------ Methods -------------------
  /**
   * Returns the specific Machine Properties.<p>
   * @see     net.agileframes.core.forces.MachineRemote#getProperties()
   * @return  the machine properties
   */
  public MachineRemote.Properties getProperties() {
    MachineRemote.Properties props = null;
    try { props = machine.getProperties(); }
    catch (RemoteException e) { System.out.println("RemoteException while reading props from Machine"); }
    return props;
  }
  private int machineNr = -1;
  /**
   * Returns the machine number.<p>
   * If the number is asked before, the number will be replied from memory (without
   * a remote call).
   * @see     net.agileframes.core.forces.MachineRemote#getMachineNumber()
   * @return  the number of this machine
   */
  public int getMachineNumber() {
    if (machineNr != -1) { return machineNr; }
    try { machineNr = machine.getMachineNumber(); }
    catch (RemoteException e) { System.out.println("RemoteException while reading nr from Machine"); }
    return machineNr;
  }
  private ServiceID id = null;
  /**
   * Returns the machine's service-id.<p>
   * If the id is asked before, the id will be replied from memory (without
   * a remote call).
   * @see     net.agileframes.core.forces.MachineRemote#getServiceID()
   * @return  the service-id of this machine
   */
  public ServiceID getServiceID() {
    if (id != null) { return id; }
    try { id = machine.getServiceID(); }
    catch (RemoteException e) { System.out.println("RemoteException while reading serviceID from Machine"); }
    return id;
  }
  /**
   * Checks if the two instances are equal.<p>
   * @param   obj the object to be checked
   * @return  <code><b>true </code></b>  iff the two objects are equal<br>
   *          <code><b>false</code></b>  iff the two objects are not equal
   */
  public boolean equals(Object obj) {
    //System.out.println("EQUALS: this="+this.toString()+"  obj="+obj.toString());
    if ((obj == null) || !(obj instanceof MachineProxy)) return false;
    MachineProxy machine = (MachineProxy)obj;
    try { return this.id.equals(machine.getServiceID()); }
    catch (Exception e) { e.printStackTrace(); }
    return false;
  }
  // Methods from MachineRemote
  public void dispose() {
    try { machine.dispose(); }
    catch (Exception e) { e.printStackTrace(); }
  }
  public void prepare(Manoeuvre m) throws RemoteException {
    /**@todo: Implement this net.agileframes.core.forces.MachineRemote method*/
    throw new java.lang.UnsupportedOperationException("Method prepare() not yet implemented.");
  }
  public void begin(Manoeuvre m) throws RemoteException {
    /**@todo: Implement this net.agileframes.core.forces.MachineRemote method*/
    throw new java.lang.UnsupportedOperationException("Method begin() not yet implemented.");
  }
  public String getLoginbaseName() throws RemoteException {
    /**@todo: Implement this net.agileframes.core.server.Server method*/
    throw new java.lang.UnsupportedOperationException("Method getLoginbaseName() not yet implemented.");
  }
  public ServiceID getServiceID(long serialVersionUID) throws RemoteException {
    /**@todo: Implement this net.agileframes.core.server.Server method*/
    throw new java.lang.UnsupportedOperationException("Method getServiceID() not yet implemented.");
  }


  //------------------------------ Methods inherited from Body ---------------------------------
  public void addAvatar(Avatar avatar) {
    try { machine.addAvatar(avatar); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to addAvatar in MachineProxy"); }
  }
  public void removeAvatar(Avatar avatar) {
    try { machine.removeAvatar(avatar); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to removeAvatar in MachineProxy"); }
  }
  public FuSpace getState() {
    //System.out.println("MachineProxy.getState");
    FuSpace p = null;
    try { p = machine.getState(); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to getState in MachineProxy"); }
    return p;
  }
  public int getGeometryID() {
    int id = -1;
    try { id = machine.getGeometryID(); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to getGeometryID in MachineProxy"); }
    return id;
  }
  public int getAppearanceID() {
    int id = -1;
    try { id = machine.getAppearanceID(); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to getAppearanceID in MachineProxy"); }
    return id;
  }
  public FuSpace removeChild(BodyRemote child) {
    FuSpace p = null;
    try { p = machine.removeChild(child); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to removeChild in MachineProxy"); }
    return p;
  }
  public Body.StateAndAvatar addChild(BodyRemote child, FuSpace state) {
    Body.StateAndAvatar sa = null;
    try { sa = machine.addChild((BodyRemote)child, state); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to addChild in MachineProxy"); }
    return sa;
  }
  public void setParent(BodyRemote parent) {
    try { machine.setParent(parent); }
    catch (RemoteException e) { System.out.println("RemoteException: didnt succeed to setParent in MachineProxy"); }
  }
}
