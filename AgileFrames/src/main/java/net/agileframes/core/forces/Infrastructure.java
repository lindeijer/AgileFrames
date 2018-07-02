package net.agileframes.core.forces;
import net.agileframes.core.server.Service;
import net.jini.core.lookup.ServiceID;

/**
An infrastructure-service should extend and implement this interface. This is a
tagging interface indicating the service is provided by the infrastructure and thus facilitates
automatic boot-strapping of the machine in the infrastructure if desired.

The machine would interact with an infrastructure service as any
client would interact with a service. Indeed, I'm talking about calling setClient first
and making damn sure you do not get an unknowclientexception

 * @since AgileFrames 1.0.0
 * @author Lindeijer, Evers
 * @version 0.0.1

*/

public interface Infrastructure extends Service {  // serializable

  public void register(Machine machine,ServiceID serviceID) throws UnknownMachineException;
  public void unregister(Machine machine,ServiceID serviceID) throws UnknownMachineException;

  ////////////////////////////////////////////////////////

  public class UnknownMachineException extends Exception {}

} 