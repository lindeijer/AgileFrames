package net.agileframes.traces.ticket;
import java.rmi.*;
import net.jini.core.transaction.Transaction;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;
/**
 * <b>The SelectTicket makes it possible to reserve on ticket out of a group of tickets.</b>
 * <p>
 * The API-documentation is not optimal.
 * @see     SetTicket
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class SelectTicket extends SetTicket {
  //value is -1 iff state != ASSIGNED.
  //Otherwise its value is the subTicket array-index of the selected ticket.
  Integer assigned_ticket = new Integer(-1);
  /**
   * Parameter to be used to debug this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;

  private SelectTicket(Ticket[] tickets) { // test all kinds of extras.
    this("AnonymousST",null,tickets);
  }

  private SelectTicket(String name,Ticket[] tickets) {
    this(name,null,tickets);
  }
  /**
   * Constructor for anonymous SelectTicket consisting of two tickets.<p>
   * Calls other constructor.
   * @see   #SelectTicket(String,SceneAction,Ticket[])
   * @param scene_action  the scene action to which this ticket belongs
   * @param t1            the first sub-ticket of this select-ticket
   * @param t2            the second sub-ticket of this select-ticket
   */
  public SelectTicket(SceneAction scene_action,
                      Ticket t1,Ticket t2) {
    this("anonymous",scene_action,null);
    Ticket[] tickets = new Ticket[2];
    tickets[0] = t1;
    tickets[1] = t2;
    setSubTickets(tickets);
  }
  /**
   * Constructor for SelectTicket consisting of multiple tickets.<p>
   * Only calls super.
   * @see   SetTicket#SetTicket(String,SceneAction,Ticket[])
   * @param name          the name of this ticket
   * @param scene_action  the scene action to which this ticket belongs
   * @param tickets       the (sub-)tickets of this select-ticket
   */
  public SelectTicket(String name,
                      SceneAction scene_action,
                      Ticket[] tickets) {
    super(name,scene_action,tickets);
  }

  // implementation of abstract methods inherited from Ticket_ImplBase //////
  public boolean _attempt() throws BlockException {
    try {
      for (int i=0;i<sub_tickets.length;i++) {
        if (sub_tickets[i].attempt()) {
          assigned_ticket = new Integer(i);
          return true;
        }
      }
    }
    catch (RemoteException e) {
      System.out.println(toString() + " RemoteException in _attempt()");
      System.exit(1);
    }
    return false;
  }

  // state == INTIAL
  public synchronized boolean _reserve() throws BlockException {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* SelectTicket  "+toString()+"._reserve()");

    // do the atomic reserve or fail or BlockException.
    reserve(sub_tickets);
    // state is RESERVING.
    // create the callback structure.
    try {
//      int i=0;//watch out with these i's !!
      int nr = -1;
      for(int i=0;i<sub_tickets.length;i++) {
        this.assigned_sub_tickets[i] = sub_tickets[i].reserve(this,i);
        // goes to TicketIB.reserve(t,i)
        // transaction is set to null
        // if ticket can be reserved, true is returned
        // if not, the callback structure is formed
        if (assigned_sub_tickets[i] == true) {
          // assigned_ticket initialized with -1
          assigned_ticket = new Integer(i);
          nr = i;
          break;
        }
      }
      if (DEBUG) System.out.println("*D* SelectTicket._reserve(): nr = "+nr);
      // next line changed HW 25/06/01: only free tickets if a free sem is found
      // freeing tickets will clear its semaphore's reserved_list, thats why
      if (assigned_ticket.intValue() != -1) {// changed by HW
//        int j = 0;//watch out with this
        // if a free sem is found: free all others
        // if no free sem is found: free all
        for(int j = 0; j < nr; j++) {
          sub_tickets[j].free();// will call semaphore.free
        }
        for(int j = nr + 1; j < sub_tickets.length; j++) {// and all others
          //OLD//sub_tickets[j].reserve(this,i);  // txn must become null !!
          sub_tickets[j].reserve(this,j);  // txn must become null !! => changed by HW 27/6/01
          sub_tickets[j].free();//will call semaphore.free
        }
      }
    }
    catch (RemoteException re) {
      System.out.println(toString() +
        "could not create callback structure: RemoteException " +
        "(Am I no longer connected?)" );
      re.printStackTrace();
      System.exit(1);
    }
    if (DEBUG) System.out.println("*D* SelectTicket._reserve(): assigned_ticket (in th end) = "+assigned_ticket.toString());
    if (assigned_ticket.intValue() != -1) return true;
    // OLD CODE WAS if (assigned_ticket != null) return true;
    if (DEBUG) System.out.println("*D* SelectTicket._reserve(): return FALSE");
    return false;
  }

  //state == BLOCKING.
  //wait for callback
  public void _insist() {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* SelectTicket  "+toString()+"._insist(),  assigned_ticket = "+assigned_ticket.toString());
    while (assigned_ticket.intValue() == -1) {
      //OLD//synchronized (assigned_ticket) {
      synchronized (this) {//changed by hw 26/06/01(this is new line)
        if (DEBUG) System.out.println("### WAITING FOR NOTIFY (SELECT_INSIST)");
        try { wait(5000); }//changed by HW 21JUN2001(this is old line)
        //OLD//try { assigned_ticket.wait(5000); }
        catch (InterruptedException e) {}
      }
    }
    if (DEBUG) System.out.println("### WE GOT OUT OF SELECT_INSIST!!!");
    if (getState() != ASSIGNED) {
      System.out.println("A ticket called back but state != ASSIGNED");
      System.exit(1);
    }
  }

/*  public void run() {
    for(;;) {
      try { this.wait(5000); }
      catch (InterruptedException e) {}
      if (assigned_ticket.intValue() != -1) {
        if (getState() != ASSIGNED) {
          int i = -1;
          try {
            for(i=0;i<assigned_ticket.intValue()-1;i++) {
              sub_tickets[i].free();
            }
            for(i=assigned_ticket.intValue()+1;i<sub_tickets.length;i++) {
              sub_tickets[i].free();
            }
          }
          catch (RemoteException e) {
            System.out.println("fjdhdjdjffj");
            System.exit(1);
          }
          catch (BlockException e) {
            System.out.println(toString() + " BlockException in run()." +
            " This implies you insisted on a sub-ticket you idiot.");
            System.exit(1);
          }
          setState(ASSIGNED);
        }
        else { /* I apparently allready did this / }
      }
      assigned_ticket.notify();
    }
  }*/

  public void _free() throws BlockException {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* SelectTicket  "+toString()+"._free(),  assigned_ticket = "+assigned_ticket.toString());
    if (assigned_ticket.intValue() != -1) {
      try {
        /// inserted new lines by HW 25/06/01, because we need to take all tickets out of reserved list
        for (int i = 0; i < sub_tickets.length; i++) {//added by hw
          sub_tickets[i].free();//added by hw
        }//added by hw
        //OLD//sub_tickets[assigned_ticket.intValue()].free();
        assigned_ticket = new Integer(-1);
      }
      catch (BlockException block_exception) {
        System.out.println(toString() +
        " is bogying on sub-ticket " + assigned_ticket.intValue() +
        " during _free().");
        throw block_exception;
      }
      catch (RemoteException e) {
        System.out.println(toString() + " RemoteException in _free()");
        System.exit(1);
      }
    }
  }

  public int _snip() {
    if (assigned_ticket.intValue() != -1) {
      return assigned_ticket.intValue() + 1;
    }
    else return 0;
  }

  ///////////////////////////////////////////////////////////

  /**
   * Reserves under transaction.<p>
   * Identical to collect-ticket.reserve(Transaction)
   * @see   CollectTicket#reserve(Transaction)
   */
  public void _reserve(Transaction tx)
    throws ReserveDeniedException,BlockException {
    if (DEBUG) System.out.println("SelectTicket  "+toString()+"._reserve( "+tx.toString()+" )");
    // this is in some way a sub-ticket of a collect ticket.
//    int i = -1;//watch out with these i's in unsynchronized methods!!
    try {
      for (int i=0;i<sub_tickets.length;i++) {
        sub_tickets[i].reserve(tx);
      }
    }
    catch (RemoteException re) {
      System.out.println(toString() +
      " got a RemoteException on sub-ticket " +"i"+ " during _reserve(). ");
      re.printStackTrace();
      throw new ReserveDeniedException("RemoteException");
    }
  }

  /*
  RemoteExceptions must be deals with. RE-RE-RE TRY;
  Identical to collect-ticket.reserve(Ticket,int);
  parameters are irrelevant.
  */
  protected boolean _reserve(net.agileframes.core.traces.Ticket super_ticket,int index)
      throws BlockException {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* SelectTicket  "+toString()+"._reserve( "+super_ticket.toString()+", "+index+" )");
    // parameters are irrelevant.
    int i=0;
    boolean isassigned = true;
    boolean reserved = false;
    for(i=0;i<sub_tickets.length;i++) {
      while (!reserved) {
        try {
          reserved = sub_tickets[i].reserve(this,i);
        }
        catch (BlockException block_exception) {
          System.out.println(toString() +
          " is bogying on sub-ticket " + i +
          " during _reserve(Ticket,int).");
          throw block_exception;
        }
        catch (RemoteException re) {
          System.out.println(toString() +
          " got a RemoteException on sub-ticket " + i +
          " during _reserve(Ticket,int).");
          reserved = false;
          Object o = new Object();//hw
          try { o.wait(2000); }//hw: was : this.wait
          catch (InterruptedException e) {}
        }
      };
      isassigned = isassigned && reserved;
    }
    return isassigned;
  }

//i modified next line -hw 21/06/01
  public synchronized void setAssigned(net.agileframes.core.traces.Ticket sub_ticket,int index) {
    if (DEBUG) System.out.println("### CALLBACK SUCCEEDED");
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* SelectTicket: setAssigned on selectTicket called, this = "+ this.toString());
    if (!isState(BLOCKING)) {
      if (DEBUG) System.out.println("*D* setAssigned: State = "+getState()+", but we need state BLOCKING");
      return;
    }

    if (!sub_tickets[index].equals(sub_ticket)) {
      System.out.println(toString() +
      " got a callback by sub-ticket " + index + " who is spoofing");
      System.exit(1);
    }
/*    if (assigned_ticket.intValue() == index) {
      if (DEBUG) System.out.println("%%%%% multiple CALLBACKs received from same ticket, we dont do a thing.");
      return;
    }*/
//    if (assigned_ticket.intValue() == -1) {//otherwise you are to late: other tickets were freed before
      assigned_sub_tickets[index] = true;
      // all others must be false.
      // code added by HW 25/06/01
      for (int j = 0 ; j < sub_tickets.length; j++) {
        if (j != index) {
          try {
            if (DEBUG) System.out.println("FREEING SUB_TICKET "+j);
            sub_tickets[j].free();
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println("We will try again to free ticket "+j);
            j--;
          }
        }
      }//end added code
      if (DEBUG) System.out.println("ASSIGNED_TICKET (in setAssigned): "+index);
      assigned_ticket = new Integer(index);
      setState(ASSIGNED);
      synchronized(assigned_ticket) { assigned_ticket.notifyAll(); }
//    }
/*    else {
      // should not be possible!!!!!!!!!
      // added by HW 21/6/01
      if (DEBUG) System.out.println("%%%%% CALLBACK received I think, but, alas, the setticket was given to somebody else already. I think semaphore.free should be called but i am not sure.");
      try { sub_ticket.free(); } catch (Exception e) {e.printStackTrace(); }
    }*/
  }

  ////////////////////////////////////////////////////////////////////////

  /*
  protected TicketPainter _getTicketPainter() {
    return new TicketPainter(this);
  }
  */


}