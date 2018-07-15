package net.agileframes.core.traces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

import net.agileframes.core.traces.Ticket;
import net.jini.core.transaction.Transaction;
/**
 * <b>Interface with the remote methods for Semaphore.</b><p>
 * For descriptions of the methods and the functioning of this object, see Semaphore.
 * @see Semaphore
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface SemaphoreRemote extends Remote {
  /** @see Semaphore#attempt(PrimeTicket) */
  public boolean attempt(Ticket pt) throws RemoteException;
  /** @see Semaphore#reserve(PrimeTicket) */
  public boolean reserve(Ticket pt) throws RemoteException;
  /** @see Semaphore#reserve(Transaction, PrimeTicket) */
  public void reserve(Transaction txn, Ticket pt) throws RemoteException, ReserveDeniedException;
  /** @see Semaphore#free(PrimeTicket) */
  public void free(Ticket pt) throws RemoteException;

  /** @see Semaphore#getName() */
  public String getName() throws RemoteException;
  /** @see Semaphore#setViewer(SemaphoreViewerProxy) */
  // public void setViewer (SemaphoreViewerProxy semaphoreViewer) throws java.rmi.RemoteException;
  /** @see Semaphore#getCapacity() */
  public int getCapacity() throws RemoteException;

  /** @see Semaphore#abort(PrimeTicket) */
  public void abort(Ticket prime_ticket) throws RemoteException;
  /** @see Semaphore#snip(PrimeTicket) */
  public int snip(Ticket prime_ticket) throws RemoteException;
  
}
