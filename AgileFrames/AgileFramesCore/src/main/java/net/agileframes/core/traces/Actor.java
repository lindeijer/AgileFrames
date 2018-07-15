package net.agileframes.core.traces;

import java.rmi.RemoteException;

import net.agileframes.core.forces.MachineRemote;
import net.agileframes.core.server.Server;
import net.agileframes.core.services.Job;
import net.jini.core.lookup.ServiceID;

/**
 * <b>Interface for the (remote) Actor.</b>
 * <p>
 * An actor-service should implement this interface.
 * The remote functions are used to provide an actor with a <code>Job</code>
 * and to get general information from the actor. This interface should also be
 * used if the actor is not used in a remote context.<br>
 * The <code>Actor</code> represents a real-world (handling) machine in the
 * context of traffic control.
 * <p>
 * <b>Properties:</b><br>
 * The specific actor-properties must be available in a specific
 * {@link Properties Properties} inner-class.
 * <p>
 * <b>Inheritance:</b><br>
 * <code>Actor</code> inherits methods from
 * {@link net.agileframes.core.server.Server Server}.
 * The <code>Server</code>-interface is used to let the machine function in a Remote context.
 * <p>
 * <b>Stubs:</b><br>
 * Because this interface is a Remote interface, any class implementing this
 * interface should create its own Stub.
 * <p>
 * <b>Relation with <code>MachineRemote</code>:</b><br>
 * In AgileFrames, the permanent base for executing is the machine, or, more
 * abstractly, the machine function driver (mfd). <br>
 * When a machine is started, an actor should be made available simultaneously.
 * Normally, the actor is located on the same computer-platform as the machine,
 * but this is not necessary as the actor and the machine can communicate remotely.
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.traces.ActorIB Actor Implementation Base}
 * (<code>ActorIB</code>). This class has all the (minimal) functionality
 * needed for an actor. If another implementation is needed,
 * it is advised to extend <code>ActorIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * A reason for another implementation could be to create a (more) intelligent
 * actor that can drive more independently.
 * @see net.agileframes.core.services.Job
 * @see net.agileframes.forces.mfd
 * @see net.agileframes.core.forces.MachineRemote
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface Actor extends Server {
  /**
   * Accepts to perform a job.<p>
   * The actor must translate the job into a sequence of actions that the
   * machine can execute. The assignment of the job is done in the
   * <i>SERVICES</i> part of AgileFrames.<br>
   * When the <code>Job</code> is translated, a command will be given to the
   * machine, either directly or in a <code>SceneAction</code> or <code>Move</code>
   * @see SceneAction
   * @see net.agileframes.core.forces.Move
   * @see net.agileframes.core.services.Job
   * @see net.agileframes.core.services
   * @param   serverID   the serviceID of the the server that gives the assignment
   * @param   job        the job that has to be performed by this actor
   * @return  <b><code>true</code></b>   if and only if the job is accepted<br>
   *          <b><code>false</code></b>  if the job is rejected, for example because
   *                                     actor does not understand it, or the job
   *                                     is physically impossible to drive
   * @throws  NotTrustedException   if and only if the serviceID or the job is not trusted
   */
  public boolean acceptJob(ServiceID serverID,Job job) throws NotTrustedException, RemoteException;
  /**
   * Returns a (remote) reference to the machine that this actor belongs to.
   * @return a (remote) reference to the machine that this actor belongs to.
   */
  public MachineRemote getMachine() throws RemoteException;
  /**
   * Disposes this actor.<p>
   * Resets the Scene to the state as if this Actor has never existed.
   */
  public void dispose() throws RemoteException;
  /**
   * Returns the specific properties of this actor.<p>
   * The specific properties are stored in a data-object that implements
   * {@link Properties Properties}. To read the properties, the result
   * of this method should be type-casted to the right Properties-class.
   * @return the specific properties of this actor
   */
  public Properties getProperties() throws RemoteException;
  /**
   * <b>Interface to be implemented by specific Properties classes.</b><p>
   * It is advised to extend
   * {@link net.agileframes.traces.ActorIB.ActorProperties ActorIB.ActorProperties}
   * rather than creating a brand new class that implements this interface.
   * <p>
   * Specific properties could be values like actor-origin, actor-destination,
   * actor-capacity, etc.
   * @see #getProperties()
   * @author  D.G. Lindeijer, H.J. Wierenga
   * @version 0.1
   */
  public interface Properties {}
}
