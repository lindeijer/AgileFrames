

package net.agileframes.traces.ticket;
//import net.agileframes.TRACES.VIEW.PrimeTicketPainter;
//import net.agileframes.TRACES.VIEW.TicketPainter;
import net.jini.core.transaction.Transaction;
import net.agileframes.traces.Semaphore;
import net.agileframes.traces.SceneAction;
import net.agileframes.core.traces.ReserveDeniedException;

import java.rmi.*;

public class PrimeTicket extends TicketImplBase {
  public Semaphore semaphore;
  public int claim;
  public int threshold;

  // convention NAME , PARENT , character parameters.

  private PrimeTicket(Semaphore s) { this(s,1,1); }

  private PrimeTicket(Semaphore s,int claim) { this(s,claim,1); }

  private PrimeTicket(Semaphore s,int claim,int threshold) {
    this("anonymous&alone",null,s,claim,threshold);
  }

  public PrimeTicket(SceneAction scene_action,Semaphore s) {
    this("anonymous",scene_action,s,1,1);
  }

  public PrimeTicket(String name,SceneAction scene_action,
                     Semaphore s,int claim,int threshold) {
    super(name,scene_action);
    this.semaphore = s;
    this.semName=semaphore.getName();
    this.claim = claim;
    this.threshold = threshold;
    if (scene_action != null) {
      scene_action.addTicket(this);  // ImplBase gets a painter.
    }
  }

  /// implementation of Ticket //////////////////////////////////////////

  public synchronized boolean _attempt()  {
    // state == INITIAL
    return semaphore.attempt(this);
  }

  public synchronized boolean _reserve() {
    // state == INITIAL
    return semaphore.reserve(this);
  }

  public synchronized void _insist() {
    // state == BLOCKING
    while (getState() != ASSIGNED) {              // notify in assign.
      try  { this.wait(10000);
         //System.out.println(getIdentity() + "down . ");
      } // ten seconds
      catch (InterruptedException e) {}
      //System.out.println(getIdentity() + "up . ");
    }
  }

  public synchronized void _free() {
    // state == ASSIGNED
    semaphore.free(this);
  }

  /*
  public static final int INITIAL   = 0;
  public static final int RESERVING = 1;
  public static final int ASSIGNED  = 2;
  */
  public synchronized int _snip() {
    switch (getState()) {
      case INITIAL   : {
      switch (semaphore.snip(this)) {
        case INITIAL :   { return 0; }
        case RESERVING : { setState(RESERVING); return 0; }
        case ASSIGNED :  { System.out.println(toString() + "huh?"); System.exit(1); }
      }
      }
      case RESERVING : {
      switch (semaphore.snip(this)) {
        case INITIAL :   { System.out.println(toString() + "huh2?"); System.exit(1); }
        case RESERVING : { return 0; }
        case ASSIGNED :  { System.out.println(toString() + "huh3?"); System.exit(1); }
      }
      }
      case ASSIGNED  : return 1;
    }
    return 0;
  }

  public void _reserve(Transaction tx)
      throws ReserveDeniedException {
    // state == INITIAL
    semaphore.reserve(tx,this);
    // state remains INITIAL until discovered otherwise.
  }

  /**
  remember happends if the return is false.
  */
  public synchronized boolean _reserve(net.agileframes.core.traces.Ticket super_ticket,int i) {
    return isState(ASSIGNED);  // this is false !!
  }

  ////////////////////////////////////////////////////////////////////////

  public synchronized void setAssigned(net.agileframes.core.traces.Ticket t,int i) { // t = null, i = -1.
    if (t!=null) {
       System.out.println(toString() + "assign callback by a sub-ticket??");
       System.exit(1);
    }
    if (i!=-1) {
       System.out.println(toString() + "assign callback by a null with an index??");
       System.exit(1);
    }
    if (isState(ASSIGNED)) {
       System.out.println(toString() + "assign by semaphore again?");
       System.exit(1);
    }
    setState(ASSIGNED);  // does callback, run is short!!
    this.notify();
  }

  /////// VIEW RELATED METHODS ////////////////////////////////////////

  /*
  protected TicketPainter _getTicketPainter() {
    return new PrimeTicketPainter(this);
  }
  */

}