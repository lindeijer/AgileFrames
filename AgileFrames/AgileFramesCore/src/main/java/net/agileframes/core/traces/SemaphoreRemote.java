package net.agileframes.core.traces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.jini.core.transaction.Transaction;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.viewer.SemaphoreViewerProxy;
/**
 * <b>Interface with the remote methods for Semaphore.</b><p>
 * For descriptions of the methods and the functioning of this object, see Semaphore.
 * @see Semaphore
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface SemaphoreRemote extends Remote {
  /** @see Semaphore#attempt(PrimeTicket) */
  public boolean attempt(PrimeTicket pt) throws RemoteException;
  /** @see Semaphore#reserve(PrimeTicket) */
  public boolean reserve(PrimeTicket pt) throws RemoteException;
  /** @see Semaphore#reserve(Transaction, PrimeTicket) */
  public void reserve(Transaction txn, PrimeTicket pt) throws RemoteException, ReserveDeniedException;
  /** @see Semaphore#free(PrimeTicket) */
  public void free(PrimeTicket pt) throws RemoteException;

  /** @see Semaphore#getName() */
  public String getName() throws RemoteException;
  /** @see Semaphore#setViewer(SemaphoreViewerProxy) */
  public void setViewer (SemaphoreViewerProxy semaphoreViewer) throws java.rmi.RemoteException;
  /** @see Semaphore#getCapacity() */
  public int getCapacity() throws RemoteException;

  /** @see Semaphore#abort(PrimeTicket) */
  public void abort(PrimeTicket prime_ticket) throws RemoteException;
  /** @see Semaphore#snip(PrimeTicket) */
  public int snip(PrimeTicket prime_ticket) throws RemoteException;
}
