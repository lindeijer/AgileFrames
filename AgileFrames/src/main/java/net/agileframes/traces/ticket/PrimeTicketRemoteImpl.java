package net.agileframes.traces.ticket;
import net.agileframes.traces.ticket.PrimeTicket;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import com.objectspace.jgl.Array;
/**
 * <b>The implementation for the remote setAssigned-method on PrimeTicket.</b>
 * <p>
 * @see     PrimeTicket
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public final class PrimeTicketRemoteImpl extends UnicastRemoteObject implements PrimeTicketRemote {
  //-- Attributes --
  private PrimeTicket primeTicket = null;
  /**
   * Default Constructor.<p>
   * Exports this object using UnicastRemoteObject.exportObject.
   * @see   PrimeTicket
   * @param primeTicket the prime-ticket to which this primeticket-remote belongs
 * @throws RemoteException 
   */
  public PrimeTicketRemoteImpl(PrimeTicket primeTicket) throws RemoteException {
	super();
    this.primeTicket = primeTicket;
  }

  //-- Methods --
  // setAssigned is called by setAssigned() in the serialized version of PrimeTicket. The call to this
  // method is forwarded to the server. At the server this method calls the original version of
  // PrimeTicket with setAssignedByPTR()
  //comment available in interface
  public synchronized void setAssigned() throws java.rmi.RemoteException {
    primeTicket.setAssignedByPTR();
  }
}
