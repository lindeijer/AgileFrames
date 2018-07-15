package net.agileframes.core.forces;
import net.agileframes.core.server.Server;
import net.agileframes.core.vr.BodyRemote;
import java.rmi.RemoteException;

/**
 * <b>Interface for the (remote) Machine.</b>
 * <p>
 * A machine-server should implement this interface in order to provide remote
 * access to the some functions of the machine. The remote functions are used
 * to provide a machine with a <code>Manoeuvre</code> and to get general
 * information from the machine. This interface should also be used if the
 * machine is not used in a remote context.<br>
 * The <code>MachineRemote</code> interface represents a real-world (handling)
 * machine in the context of physical execution.
 * <p>
 * <b>Properties:</b><br>
 * There is one generic property which must be availble on every machine: the
 * machine-number. The specific machine-properties must be available in
 * a specific {@link Properties Properties} inner-class.
 * <p>
 * <b>Inheritance:</b><br>
 * <code>MachineRemote</code> inherits methods from two other interfaces:
 * {@link net.agileframes.core.server.Server Server} and {@link net.agileframes.core.vr.BodyRemote}.
 * The first one is used to let the machine function in a Remote context,
 * the second to be able to visualize the machine.
 * <p>
 * <b>Stubs:</b><br>
 * Because this interface is a Remote interface, any class implementing this
 * interface should create its own Stub.
 * <p>
 * <b>Relation with <code>Actor</code>:</b><br>
 * In AgileFrames, the permanent base for executing is the machine, or, more
 * abstractly, the machine function driver (mfd). <br>
 * When a machine is started, an actor should be made available simultaneously.
 * Normally, the actor is located on the same computer-platform as the machine,
 * but this is not necessary as the actor and the machine can communicate remotely.
 * <p>
 * <b>Implementation:</b><br>
 * For a very basic implementation of this class, see the
 * {@link net.agileframes.forces.MachineIB Machine Implementation Base}
 * (<code>MachineIB</code>). It is advised to extend <code>MachineIB</code>
 * rather than create a brand new class implementing this interface.
 * @see Manoeuvre
 * @see net.agileframes.forces.mfd
 * @see net.agileframes.core.traces.Actor
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface MachineRemote extends Server, BodyRemote {
  /**
   * Returns the specific properties of this machine.<p>
   * The specific properties are stored in a data-object that implements
   * {@link Properties Properties}. To read the properties, the result
   * of this method should be type-casted to the right Properties-class.
   * @return the specific properties of this machine
   */
  public Properties getProperties() throws RemoteException;
  /**
   * Let the system anticipate on the succeeding manoeuvre.<p>
   * This command most likely will be called by the actor.
   * @see net.agileframes.core.traces.Actor
   * @see net.agileframes.forces.mfd.ManoeuvreDriver#prepare(Manoeuvre)
   * @param m the manoeuvre to anticipate on
   */
  public void prepare(Manoeuvre m) throws RemoteException;
  /**
   * Let the system start a manoeuvre.<p>
   * This command most likely will be called by the actor.
   * @see net.agileframes.core.traces.Actor
   * @see net.agileframes.forces.mfd.ManoeuvreDriver#begin(Manoeuvre)
   * @param m the manoeuvre to start with
   */
  public void begin(Manoeuvre m) throws RemoteException;
  /**
   * To be called when the Machine and MFD-Thread are to die. <p>
   * @see   ManoeuvreDriverIB#dispose()
   */
  public void dispose() throws RemoteException;
  /**
   * Returns the number of this machine.<p>
   * The machine-number is the only generic property of the machine.
   * Note that the machine-number is not an unique number with which it is
   * possible to identify a machine. It should be used as a handy tool for the
   * end-user only, to let him quickly recognize a specific machine.<br>
   * To identify a machine, every (remote) machine has its unique <code>ServiceID</code>.
   * @see #getProperties()
   * @see net.agileframes.core.server.Server#getServiceID()
   * @return the number of this machine
   */
  public int getMachineNumber() throws RemoteException;
  //--------------------------- Inner-interface ---------------------
  /**
   * <b>Interface to be implemented by specific Properties classes.</b><p>
   * It is advised to extend
   * {@link net.agileframes.forces.MachineIB.MachineProperties MachineIB.MachineProperties}
   * rather than creating a brand new class that implements this interface.
   * <p>
   * Specific properties could be values like machine-speed, machine-location,
   * machine-acceleration, etc.
   * @see #getProperties()
   * @author  D.G. Lindeijer, H.J. Wierenga
   * @version 0.1
   */
  public interface Properties {}
}
