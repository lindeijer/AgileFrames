

package net.agileframes.core.traces;
import java.rmi.*;
import net.jini.core.transaction.Transaction;

/**
Scene-actions (event-processes) make claims upon semaphores via tickets. This
interface defines the methods the actor-thread may make on Tickets.



@see AgileFraqes#TRACES#Ticket_ImplBase
@see AgileFraqes#TRACES#PrimeTicket
@see AgileFraqes#TRACES#SelectTicket
@see AgileFraqes#TRACES#CollectTicket
*/

public interface Ticket {

  /**
  The ticket attempts to get its claim honored "immediately" with the
  semaphores involved. This ticket operation may be considered "atomic"
  and "instantaneous".
  @return is true iff the tickets claim is honored "immediately".
  @see AgileFrames#TRACES#PrimeTicket#_attempt()
  @see AgileFrames#TRACES#CollectTicket#_attempt()
  @see AgileFrames#TRACES#SelectTicket#_attempt().
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the thread is a bogey.
  */
  public boolean attempt() throws RemoteException,BlockException ;

  /////////////////////////////////////////////////////////////////////////

  /**
  The ticket reserves its claim with the semaphores involved.
  This operation should preceed the operation insist().
  This ticket operation may be considered "atomic" and "instantaneous".
  @see AgileFrames#TRACES#PrimeTicket#_reserve()
  @see AgileFrames#TRACES#CollectTicket#_reserve()
  @see AgileFrames#TRACES#SelectTicket#_reserve()
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the thread is a bogey.
  */
  public void reserve() throws RemoteException,BlockException ;

  /**
  The ticket insists its claim be honored with the semaphores involved.
  If this operation has not been preceded by reserve() then that operation is called first.
  This ticket operation lasts until the claim is honored.
  @see AgileFrames#TRACES#PrimeTicket#reserve()
  @see AgileFrames#TRACES#PrimeTicket#_insist()
  @see AgileFrames#TRACES#CollectTicket#_insist()
  @see AgileFrames#TRACES#SelectTicket#_insist()
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the thread is a bogey.
  */
  public void insist()  throws RemoteException,BlockException ;

  /**
  The ticket releases its claim, the semaphores involved repossess their capacity.
  This ticket operation can be considered "atomic" and "instantaneous".
  @see AgileFrames#TRACES#PrimeTicket#_free()
  @see AgileFrames#TRACES#CollectTicket#_free()
  @see AgileFrames#TRACES#SelectTicket#_free()
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the thread is a bogey.
  */
  public void free()  throws RemoteException,BlockException ;

  /**
  Reveals if the tickets claim is honored or not.
  This ticket operation can be considered "atomic" and "instantaneous".
  @return -1 iff the tickets claim is honored, > -1 otherwise.
  @see AgileFrames#TRACES#PrimeTicket#_snip()
  @see AgileFrames#TRACES#CollectTicket#_snip()
  @see AgileFrames#TRACES#SelectTicket#_snip()
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the thread is a bogey.
  */
  public int snip()  throws RemoteException,BlockException ;

  ////////////////////////////////////////////////////////////////
  ////////////////////// ATOMIC //////////////////////////////////
  ////////////////////////////////////////////////////////////////

  /**
  As reserve() but now governed by a transaction in order to guarentee that it
  is "atomic", all reservations with semaphores will occur at the same time.
  This ticket operation can be considered "instantaneous".
  @see AgileFrames#TRACES#Ticket#reserve();
  @see AgileFrames#TRACES#Ticket#assign(Ticket,int)
  @exception ReserveDeniedException this call involves the danger of deadlock,
    if a possible deadlock is signalled then the exception is thrown.
  @exception RemoteException the ticket could not be reached over the network,
  @exception BlockException the ticket was blocking.
  */
  public void reserve(Transaction txn)  throws RemoteException,BlockException,ReserveDeniedException ;


  public boolean reserve(Ticket super_ticket,int i)  throws RemoteException,BlockException ;

  /**
  Upon assignment this ticket calls t.assign(i).
  This ticket operation can be considered "instantaneous".
  @see AgileFrames#TRACES#Ticket#reserve(Transaction,Ticket,int)
  @param sub_ticket is calling back
  @param i the index of sub-ticket calling back after assignment.
  @return false iff the super-select-ticket rejects the assignment.
    collect-tickets always return true.
  */
  public void setAssigned(Ticket super_ticket,int i)  throws RemoteException ;



}