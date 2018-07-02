package net.agileframes.core.server;

import java.io.Serializable;
import net.jini.core.lookup.ServiceID;

/**
A service should extend and implement this serializable interface. Such an interface
would identify such a service as an agileframes-service and provide methods that
clients need to communicate the service-protocol.

@see net.agileframes.server.ServiceProxy

 * @since AgileFrames 1.0.0
 * @author Lindeijer, Evers
 * @version 0.0.1

*/

public interface Service extends Serializable {

  /**
  Gets the remote interface to the server that uploaded the service.
  This allows the client to call remote methods directly on the server.
  @return server that uploaded the service
  */
  public Server getServer();

  /**
  Sets the client that desires service from the server, usually the first call in the service-protocol.
  @param client that desires service from the server. The client must be aware of the service protocol.
  @param serviceID of the client, may be null.
  @throws UnknownClientException if the client does not typecast to the interface required by the service-protocol
  */
  public void setClient(Object client,ServiceID clientID) throws UnknownClientException ;

  /**
  Sets the client that desires service from the server, usually the first call in the service-protocol.
  @param client that desires service from the server. The client must be aware of the service protocol.
  @throws UnknownClientException if the client does not typecast to the interface required by the service-protocol
  */
  public void setClient(Object client) throws UnknownClientException ;

  /**
  Exception indicating that the client does not implement the protocol associated with the requested service.
  A service can only be provided by a server when the client and itself respect the protocol
  associated with the service. This implies that both the client and the service-proxy
  implement the appropriate interfaces so that the two processes can invoke the desired methods
  upon each other.
  */
  public class UnknownClientException extends Exception {
    /**
    @param the name of the interface the client does not implement
    */
    public UnknownClientException(String missingInterface) { super(missingInterface); }
  }

}



