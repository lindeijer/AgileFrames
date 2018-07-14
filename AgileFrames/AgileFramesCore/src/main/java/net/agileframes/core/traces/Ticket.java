package net.agileframes.core.traces;
import java.rmi.*;
import net.jini.core.transaction.Transaction;

/**
 * <b>Interface used to perform operations on Semaphores.</b>
 * <p>
 * Scene-actions (event-processes) make claims upon semaphores via tickets. This
 * interface defines the methods the actor-thread may call on Tickets.
 * <p>
 * The Ticket is used to control synchronization between the physical traffic
 * and the model-representation and to introduce possibilities of anticipation.
 * <p>
 * <b>Ticket-operations:</b><br>
 * Tickets are created with a reference to a semaphore. More than one ticket can
 * refer to the same semaphore. The following operations are possible on a Ticket:
 * <ul>
 * <li> insist()    ask for capacity, wait until capacity is available and get capacity
 * <li> free()	    give back allocated capacity to semaphore
 * <li> reserve()   get capacity as soon as capacity is available but do not wait
 * <li> attempt()   get capacity only if capacity is available
 * <li> snip()      get ticket-value
 * </ul><br>
 * All Semaphores need to be claimed and freed using tickets.
 * Standard Tickets are called PrimeTickets, two special tickets are the CollectTicket
 * and the SelectTicket.
 * @see net.agileframes.traces.ticket.PrimeTicket
 * @see net.agileframes.traces.ticket.SelectTicket
 * @see net.agileframes.traces.ticket.CollectTicket
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */

public interface Ticket extends java.io.Serializable, java.lang.Cloneable {//extends Remote {

  /**
   * The ticket attempts to get its claim honored "immediately" with the
   * semaphores involved. <p>
   * This ticket operation may be considered "atomic"
   * and "instantaneous".
   * @return is true iff the tickets claim is honored "immediately".
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the thread is a bogey.
   */
  public boolean attempt() throws RemoteException,BlockException;

  /////////////////////////////////////////////////////////////////////////

  /**
   * The ticket reserves its claim with the semaphores involved.<p>
   * This operation should preceed the operation insist().
   * This ticket operation may be considered "atomic" and "instantaneous".
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the thread is a bogey.
   */
  public void reserve() throws RemoteException,BlockException;

  /**
   * The ticket insists its claim be honored with the semaphores involved.<p>
   * If this operation has not been preceded by reserve() then that operation is called first.
   * This ticket operation lasts until the claim is honored.
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the thread is a bogey.
   */
  public void insist() throws RemoteException,BlockException;

  /**
   * The ticket releases its claim, the semaphores involved repossess their capacity.<p>
   * This ticket operation can be considered "atomic" and "instantaneous".
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the thread is a bogey.
   */
  public void free() throws RemoteException,BlockException;

  /**
   * Reveals if the tickets claim is honored or not.<p>
   * This ticket operation can be considered "atomic" and "instantaneous".
   * @return -1 iff the tickets claim is honored, > -1 otherwise.
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the thread is a bogey.
   */
  public int snip() throws RemoteException,BlockException;

  ////////////////////////////////////////////////////////////////
  ////////////////////// ATOMIC //////////////////////////////////
  ////////////////////////////////////////////////////////////////

  /**
   * Reserves ticket under transaction.<p>
   * As reserve() but now governed by a transaction in order to guarentee that it
   * is "atomic", all reservations with semaphores will occur at the same time.
   * This ticket operation can be considered "instantaneous".
   * @see #reserve()
   * @exception ReserveDeniedException this call involves the danger of deadlock,
   *            if a possible deadlock is signalled then the exception is thrown.
   * @exception RemoteException the ticket could not be reached over the network,
   * @exception BlockException the ticket was blocking.
   */
  public void reserve(Transaction txn)
    throws RemoteException,ReserveDeniedException,BlockException;


  public boolean reserve(Ticket super_ticket,int i)
    throws RemoteException,BlockException;

  /**
   * Assigns a ticket.<p>
   * Upon assignment this ticket calls t.assign(i).
   * This ticket operation can be considered "instantaneous".
   * @see #reserve(Ticket,int)
   * @param sub_ticket  the ticket that is calling back
   * @param i           the index of the sub-ticket calling back after assignment.
   * @return <code><b>false</b></code> iff the super-select-ticket rejects the assignment.
   * collect-tickets always return true.
   */
  public void setAssigned(Ticket super_ticket,int i) throws RemoteException;
  /**
   * Creates a copy of this <code>Ticket</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @return  an object that is a copy of this <code>Ticket</code> object.
   */
  public Object clone(Actor actor) throws java.lang.CloneNotSupportedException;
}