


package net.agileframes.traces.ticket;
import net.agileframes.traces.SceneAction;

import net.agileframes.core.traces.Ticket;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import java.rmi.RemoteException;
import net.jini.core.transaction.Transaction;
import com.objectspace.jgl.Array;
/**temp**/import net.agileframes.forces.mfd.ActorProxy;


/**
Provides basic functionality common to all types of tickets.
@see AgileFrames.TRACES.Ticket
@see AgileFrames.TRACES.PrimeTicket
@see AgileFrames.TRACES.CollectTicket
@see AgileFrames.TRACES.SelectTicket

@author D.G.Lindeijer
@author J.J.M.Evers
@version 1.0.0 beta
*/

public abstract class TicketImplBase implements Ticket {
  /**temp**/public String  semName="";

  /**
  State indicating that the ticket is not making any claim.
  */
  public static final int INITIAL   = 0;
  /**
  State indicating that the ticket is waiting for its claim to be honored.
  */
  public static final int RESERVING = 1;
  /**
  State indicating that the tickets claim has been honored.
  */
  public static final int ASSIGNED  = 2;
  /**
  State indicating that the ticket and the event-process is waiting
  for its claim to be honored.
  */
  public static final int BLOCKING  = 3;

  /////////////////////////////////////////////////////////////////////

  protected Transaction transaction = null;

  ///////////////////////////////////////////////////////////////////////

  /**
  The tickets name may be arbitrary.
  */
  public String name = null;

  public String getName() {
    if (name != null) return name;
    else return "anonymous";
  }

  public String toString() {
    String identity =
      this.getName() + "@" + this.getClass().toString() + " in " +
      this.scene_action.getClass().toString() + " belonging to " +
      this.scene_action.actor.toString();
    return identity;
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  The tickets state must be one of the constants.
  @see AgileFrames#TRACES#Ticket
  */
  private int state = 0;
  protected void setState(int i) {
    if (state != i) {
      switch (i) {
      case INITIAL: {
        state = INITIAL;
        //System.out.println(toString() + " has become INITIAL");
        break;
        }
      case RESERVING: {
        state = RESERVING;
        //System.out.println(toString() + " has become RESERVING");
        break;
        }
      case ASSIGNED: {
        state = ASSIGNED;
        //System.out.println(toString() + " has become ASSIGNED");
        this.callback();
        break;
        }
      case BLOCKING: {
        state = BLOCKING;
        //System.out.println(toString() + " has become BLOCKING");
        break;
        }
      } // end of switch.
      modelChanged();
    }
  }
  public int getState() { return state; }
  public boolean isState(int i) {
    return (state == i);
  }

  ////////////////////////////////////////////////////////////////////////

  /**
  The ticket is always created in the context of a scene-action.
  */
  public SceneAction scene_action = null;

  /**
  Upon creation a tickets state is INITIAL.
  */
  public TicketImplBase(String name,SceneAction scene_action) {
    if (name == null) { this.name = "anonymous"; } else { this.name = name; }
    this.scene_action = scene_action;
    this.state = INITIAL;
  }


  ////////////////////////////////////////////////////////////////////
  /// implementation of Ticket //////////////////////////////////////
  ///////////////////////////////////////////////////////////////////

  /**
  The ticket attempts to get its claim honored "immediately" with the
  semaphores involved. This call should last only milliseconds.
  @see AgileFrames#Ticket#attempt()
  @see AgileFrames#PrimeTicket#_attempt()
  @see AgileFrames#CollectTicket#_attempt()
  @see AgileFrames#SelectTicket#_attempt()
  @return true: state is ASSIGNED, or false: state is INITIAL.
  @exception BlockException this call was made by a second thread.
  */
  public synchronized boolean attempt() throws BlockException {
    if (transaction != null) { throw new BlockException(
      "Second thread in " + toString() +  " attempt() during transaction");
    }
    switch (state) {
      case INITIAL : {
        if ( _attempt() ) {
          setState(ASSIGNED);
          return true;
        }
        else { return false; }
      }
      case RESERVING : { return false; }
      case ASSIGNED  : { return true; }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling attempt()");
      }
    }
    return false;
  }

  /**
  The ticket reserves its claim with the semaphores involved.
  This call should last only milliseconds.
  @see AgileFrames#Ticket#reserve()
  @see AgileFrames#PrimeTicket#_reserve()
  @see AgileFrames#CollectTicket#_reserve()
  @see AgileFrames#SelectTicket#_reserve()
  @return state is ASSIGNED or RESERVING
  @exception BlockException this call was made by a second thread.
  */
  public synchronized void reserve() throws BlockException {
    if (transaction != null) { throw new BlockException(
      "Second thread in " + toString() +  " reserve() during transaction");
    }
    switch (state) {
      case INITIAL :   {
        if ( _reserve() ) setState(ASSIGNED);
        else setState(RESERVING);
        break;
      }
      case RESERVING : { break; }
      case ASSIGNED  : { break; }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling reserve()");
      }
    }
  }

  /**
  The ticket insists its claim be honored with the semaphores involved.
  This call lasts as long as it takes to get the claim honored. Event-process
  atomic.
  @see AgileFrames#Ticket#insist()
  @see AgileFrames#PrimeTicket#_insist()
  @see AgileFrames#CollectTicket#_insist()
  @see AgileFrames#SelectTicket#_insist()
  @return state is ASSIGNED
  @exception BlockException this call was made by a second thread.
  */
  public synchronized void insist() throws BlockException {
    //((ActorProxy)scene_action.actor).claimFrame.addText("Ticket -> insisted on "+semName+"\n");
    //((ActorProxy)scene_action.actor).claimFrame.addText("          in sceneAction "+scene_action.toString()+"\n");

    if (transaction != null) { throw new BlockException(
      "Second thread in " + toString() +  " insist() during transaction");
    }
    switch (state) {
      case INITIAL : {
        if ( _reserve() ) {
          setState(ASSIGNED);
          break;
        }
        else setState(RESERVING);
      }
      case RESERVING : {
        setState(BLOCKING);
        _insist();
        setState(ASSIGNED);
        break;
      }
      case ASSIGNED  : { break; }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling insist()");
      }
    }
  }

  /**
  The ticket releases its claim, the semaphores involved repossess their
  capacity. This call should last only milliseconds. Event-process atomic.
  @see AgileFrames#Ticket#free()
  @see AgileFrames#PrimeTicket#_free()
  @see AgileFrames#CollectTicket#_free()
  @see AgileFrames#SelectTicket#_free()
  @return state is INITIAL
  @exception BlockException this call was made by a second thread.
  */
  public synchronized void free() throws BlockException {
    if (transaction != null) { throw new BlockException(
      "Second thread in " + toString() +  " free() during transaction");
    }
    switch (state) {
      case INITIAL : { break; }
      case RESERVING : {
        _free();
        setState(INITIAL);
        break;
      }
      case ASSIGNED  : {
        _free();
        setState(INITIAL);
        break;
      }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling free()");
      }
    }
  }

  /**
  Reveals if the tickets claim is honored or not.
  This call should last only milliseconds.
  @see AgileFrames#Ticket#free()
  @see AgileFrames#PrimeTicket#_free()
  @see AgileFrames#CollectTicket#_free()
  @see AgileFrames#SelectTicket#_free()
  @return state is INITIAL
  @exception BlockException this call was made by a second thread.
  */
  public synchronized int snip() throws BlockException {
    if (transaction != null) { throw new BlockException(
      "Second thread in " + toString() +  " snip() during transaction");
    }
    switch (state) {
      case INITIAL   : { return _snip(); }
      case RESERVING : { return _snip(); }
      case ASSIGNED  : { return _snip(); }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling snip()");
      }
    }
    return 0; // not assigned.
  }

  /////////////////////////////////////////////////////////////////////////


  /**
  followed by reserve(Ticket,int). Any problem causes a ReserveDeniedException.
  @see AgileFrames#TRACES#Ticket#reserve(Ticket,int,Transaction)
  @see AgileFrames#TRACES#Ticket#reserve(Ticket,int);
  @see AgileFrames#TRACES#Ticket#reserve();
  @return state is unchanged.
  */
  public synchronized void reserve(Transaction txn)
      throws ReserveDeniedException,BlockException {
    if (transaction != null) {
      if (txn.equals(transaction)) { return; }
    }
    else { transaction = txn; }
    // Note that a new transaction is simply accepted.
    // It is assumed that the previous one aborted and the new one is a retry.
    switch (state) {
      case INITIAL   : {
        _reserve(txn);
        // state does not become reserving because there may come an abort!
        break;
      }
      case RESERVING : { break; }
      case ASSIGNED  : { break; }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling reserve(Transaction)");
      }
    }
    //System.out.println(getIdentity() + " completed reserve(Transaction)");
  }

  /**
  Preceded by reserve(Transaction), creates the callback structure.
  Last transaction committed. This method keeps re-trying upon RemoteExceptions.
  */
  public synchronized boolean reserve(
          net.agileframes.core.traces.Ticket super_ticket,int i)
      throws BlockException {
    transaction = null;
    boolean isassigned;
    switch (state) {
      case INITIAL   : { setState(RESERVING); }  // fall through.
      case RESERVING : {
        if (_reserve(super_ticket,i)) {
          setState(ASSIGNED);
          return true;
        }
        else {
          remember(super_ticket,i);
          return false;
        }
      }
      case ASSIGNED  : { return true; }
      case BLOCKING  : { throw new BlockException(
        "Second thread in " + toString() +  " calling reserve(Transaction)");
      }
    };
    // System.out.println(getIdentity() + " completed reserve(Ticket,int)");
    return false;
  }

////////////////// callback ///////////////////////////////

  Array callback_list = new Array();

  private class ZZZ {
    net.agileframes.core.traces.Ticket super_ticket;
    int index;
    public ZZZ(net.agileframes.core.traces.Ticket super_ticket,int i) {
      this.super_ticket = super_ticket;
      this.index = i;
    }
  }

  private void remember(net.agileframes.core.traces.Ticket super_ticket,int i) {
    ZZZ zzz = new ZZZ(super_ticket,i);
    callback_list.pushBack(zzz);
  }

  private void callback() {
    ZZZ zzz = null;
    while (!callback_list.isEmpty()) {
      zzz = (ZZZ) callback_list.popFront();
      try {
        zzz.super_ticket.setAssigned(this,zzz.index);
      }
      catch (RemoteException re) {
        System.out.println(toString() +
        " got a RemoteException on a super-ticket during assign(). ");
        re.printStackTrace();
        System.exit(1);
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////

  /** called when state == INITIAL */
  protected abstract boolean _attempt() throws BlockException;

  /** called when state == INITIAL, note the boolean return */
  protected abstract boolean _reserve() throws BlockException;

  /** called when state == RESERVING */
  protected abstract void    _insist()  throws BlockException;

  /** called when state == ASSIGNED */
  protected abstract void    _free()    throws BlockException;

  /** called when state == ASSIGNED,RESERVING or INITIAL */
  protected abstract int     _snip()    throws BlockException;

  /** called when state == INITIAL */
  protected abstract void     _reserve(Transaction tx)
    throws ReserveDeniedException,BlockException;

  /** called when state ==  */
  protected abstract boolean  _reserve(net.agileframes.core.traces.Ticket super_ticket,int i)
    throws BlockException;

  /////////// View Related Methods ////////////////////////////////////////

  /**
  The tickets painter colors the tickets accorning to the tickets state and
  The tickets delegates the painting of the shape to the semaphores painter.

  public Painter ticketPainter = null;
  protected void modelChanged(int property_index) {}
  protected void modelNew() {}
  */

  protected void modelChanged() {
//    ((ActorProxy)scene_action.actor).claimFrame.addText("Ticket -> "+semName+" has become "+state+"\n");
//    ((ActorProxy)scene_action.actor).claimFrame.addText("          in sceneAction "+scene_action.toString()+"\n");
  /*
    if (ticket_painter != null) { ticket_painter.modelChanged(); }
    else { System.out.println("DAMN, PRIME TICKET PAINTER IS NULL"); }
  */
  }

} // end of Ticket



