package net.agileframes.traces.ticket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.agileframes.core.traces.Ticket;
/**
 * <b>The interface for the remote setAssigned-method on PrimeTicket.</b>
 * <p>
 * @see     PrimeTicket
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface PrimeTicketRemote extends Remote {
  /**
   * Tells this ticket that it is assigned by its Semaphore.<p>
   * Will call setAssignedByPTR on PrimeTicket. This construction is necessary,
   * because PrimeTicket is not a remote-object.<br>
   * See PrimeTicket.setAssigned for a detailed description of the construction.
   * @see PrimeTicket#setAssigned()
   * @see PrimeTicket#setAssignedByPTR()
   */
  public void setAssigned() throws RemoteException;
}
