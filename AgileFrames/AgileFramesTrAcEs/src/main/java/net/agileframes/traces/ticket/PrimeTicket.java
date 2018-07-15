package net.agileframes.traces.ticket;
import net.jini.core.transaction.Transaction;
import net.agileframes.core.traces.SemaphoreRemote;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;
import java.rmi.*;
/**
 * <b>The PrimeTicket is the -basic- ticket that interacts with the Semaphore.</b>
 * <p>
 * All final nodes of a SelectTicket/CollectTicket structure are built with PrimeTickets.
 * The API-documentation is not optimal.
 * @see     net.agileframes.core.traces.Semaphore
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class PrimeTicket extends TicketIB {
  /**
   * Parameter to be used to debug this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;
  /** Reference to the Semaphore for which this ticket was created */
  public SemaphoreRemote semaphore;
  /** Number of units that are requested by this ticket */
  public int claim;
  /** Threshold of this ticket */
  public int threshold;
  /**
   * A reference to the remote instance of this ticket.<p>
   * Is needed to perform callback in the context of SetTickets.
   */
  public PrimeTicketRemote primeTicketRemote = null;
  //--- Constrcutors ---
  // convention NAME , PARENT , character parameters.
  private PrimeTicket(SemaphoreRemote s) { this(s,1,1); }
  private PrimeTicket(SemaphoreRemote s,int claim) { this(s,claim,1); }
  private PrimeTicket(SemaphoreRemote s,int claim,int threshold) {
    this("anonymous&alone",null,s,claim,threshold);
  }
  /**
   * Constructor for anonymous PrimeTicket with claim and threshold of 1 unit.<p>
   * Calls other constructor.
   * @see                 #PrimeTicket(String,SceneAction,SemaphoreRemote,int,int)
   * @param scene_action  the scene action that this ticket belongs to
   * @param s             the semaphore for which this ticket is created
   */
  public PrimeTicket(SceneAction scene_action,SemaphoreRemote s) {
    this("anonymous",scene_action,s,1,1);
  }
  /**
   * Constructor for PrimeTicket with specified parameters.<p>
   * Sets parameters and calls super.
   * @see   TicketIB#TicketIB(String,SceneAction)
   * @param name          the name of this ticket
   * @param scene_action  the scene action that this ticket belongs to
   * @param s             the semaphore for which this ticket is created
   * @param claim         the number of units to be claimed by this ticket
   * @param threshold     the threshold of this ticket
   */
  public PrimeTicket(String name,SceneAction scene_action,
                     SemaphoreRemote s,int claim,int threshold) {
    super(name,scene_action);
    this.semaphore = s;
    //// moved next statement to _reserve 31MAY2001
    try {
		primeTicketRemote = new PrimeTicketRemoteImpl(this); //its back again
	} catch (RemoteException e) {
		System.out.println("RemoteException in PrimeTicket (init) : "+e.getMessage());
	}
    //this.semName=semaphore.getName();
    this.claim = claim;
    this.threshold = threshold;
  }

  /// implementation of Ticket //////////////////////////////////////////

  public synchronized boolean _attempt() {
    if (DEBUG) System.out.println("*D* PrimeTicket._attempt: "+toString());
    // state == INITIAL
    boolean result = false;
    try { result = semaphore.attempt(this); }
    catch (java.rmi.RemoteException e) {
      System.out.println("RemoteException in PrimeTicket (attempt) : "+e.getMessage());
      e.printStackTrace();
      System.out.println("The system will NOT quit");
      //System.exit(0);
    }
    return result;
  }

  public synchronized boolean _reserve() {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* PrimeTicket ##1 _reserve() called in "+toString());
    //// primeTicketRemote should be created here and not for example in the constructor
    //// because ptr should have a reference to the (eventually cloned & serialized, on the side of the agv)
    //// primeticket that performed the reserve-operation, rather than to the original
    //// primeticket, which is at the side of the scene-server.
    try {
		primeTicketRemote = new PrimeTicketRemoteImpl(this); //its back again
	} catch (RemoteException e) {
		System.out.println("RemoteException in PrimeTicket (_reserve) : "+e.getMessage());
	}
    // state == INITIAL
    boolean result = false;
    try { result = semaphore.reserve(this); }
    catch (java.rmi.RemoteException e) {
      System.out.println("RemoteException in PrimeTicket (reserve) : "+e.getMessage());
      e.printStackTrace();
      System.out.println("The system will NOT quit");
      //System.exit(0);
    }
    return result;
  }

  public synchronized void _insist() {
    // state == BLOCKING
    while (getState() != ASSIGNED) {              // notify in assign.
      try  {
        this.wait(10000);
      } // ten seconds
      catch (InterruptedException e) {// happens when finalize() is called...actor died
        try { semaphore.abort(this); }
        catch (java.rmi.RemoteException re) {
          System.out.println("RemoteException in PrimeTicket (insist) : "+re.getMessage());
          re.printStackTrace();
          System.out.println("The system will NOT quit");
          //System.exit(0);
        }
        break;
      }
    }
    if (DEBUG) System.out.println("### WE BROKE OUT OF PT_INSIST!!");
  }

  public synchronized void _free() {//HW 25/06/01
    // state == ASSIGNED
    try { semaphore.free(this); }
    catch (java.rmi.RemoteException e) {
      System.out.println("RemoteException in PrimeTicket (free) : "+e.getMessage());
      e.printStackTrace();
      System.out.println("The system will NOT quit");
      //System.exit(0);
    }
  }

  /*
  public static final int INITIAL   = 0;
  public static final int RESERVING = 1;
  public static final int ASSIGNED  = 2;
  */
  public synchronized int _snip() {
    int snip = 0;
    try { snip = semaphore.snip(this); }
    catch (java.rmi.RemoteException e) {
      System.out.println("RemoteException in PrimeTicket (snip) : "+e.getMessage());
      e.printStackTrace();
      System.out.println("The system will NOT quit");
      //System.exit(0);
    }

    switch (getState()) {
      case INITIAL   : {
      switch (snip) {
        case INITIAL :   { return 0; }
        case RESERVING : { setState(RESERVING); return 0; }
        case ASSIGNED :  { System.out.println(toString() + "huh?"); System.exit(1); }
      }
      }
      case RESERVING : {
      switch (snip) {
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
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* PrimeTicket ##3 _reserve() called in "+toString());

    try {
		primeTicketRemote = new PrimeTicketRemoteImpl(this); //its back again
	} catch (RemoteException e) {
		System.out.println("RemoteException in PrimeTicket (_reserve(txn)) : "+e.getMessage());
	}
    // state == INITIAL
    try { semaphore.reserve(tx,this); }
    catch (java.rmi.RemoteException e) {
      System.out.println("RemoteException in PrimeTicket (reserve txn) : "+e.getMessage());
      e.printStackTrace();
      System.out.println("The system will NOT quit");
      //System.exit(0);
    }
    // state remains INITIAL until discovered otherwise.
  }

  /*
  remember happends if the return is false.
  */
  public synchronized boolean _reserve(Ticket super_ticket,int index) {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* PrimeTicket ##2 _reserve(t,index) called in "+toString());
    return isState(ASSIGNED);  // this is false !!
  }

  ////////////////////////////////////////////////////////////////////////

  /**
   * Not used. System will exit if this method is called by mistake.
   */
  public void setAssigned(Ticket super_ticket,int index) {
    System.out.println("### WARNING: ABOUT TO EXIT");
    System.exit(0);
  }
  /**
   * Tells this ticket that it is assigned by its Semaphore.<p>
   * Because the Ticket probably can exist remotely with respect to the Semaphore,
   * a special construction is used here.<br>
   * If this Ticket exists remotely, this ticket has two instances: one at the side
   * of the Semaphore(local) and one at the side of the actor(remote). This situation
   * occurs when the tickets are defined in a SceneAction in a Scene (thus, at the same
   * side of the Semaphore) and then are cloned, together with the entire SceneAction,
   * and brought to the side of the Actor.
   * <p>
   * The Semaphore calls this method when it has assigned this ticket. If the ticket
   * exists locally, then this state will be set to assigned and the the ticket
   * will stop being blocking.<br>
   * However, if this ticket exists in a remote context, this ticket will have
   * a reference to PrimeTicketRemote, which is available on both sides. The
   * PrimeTicketRemote is the connection between this two instances of PrimeTicket.<br>
   * This method will be called by the Semaphore, not on the instance of this ticket
   * that is available on the side of the actor, but on the instance that is
   * available locally, next to the Semaphore. <br>
   * The local instance of this ticket will call the method setAssigned on its
   * PrimeTicketRemote, that will call setAssignedByPTR on the remote instance of
   * this ticket. In this way this Serializable ticket is used remotely.
   * @see #setAssignedByPTR()
   * @see PrimeTicketRemote#setAssigned()
   * @see TicketIB#clone(Actor)
   * @see net.agileframes.core.traces.Semaphore
   */
  public void setAssigned() {
    if (primeTicketRemote != null) {
      if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* PrimeTicket SETASSIGNED CALLED  "+toString());
      try { primeTicketRemote.setAssigned(); }
      catch (Exception e) { e.printStackTrace(); }
    } else { System.out.println("setAssigned called while primeTicketRemote = null!"); }
  }
  /**
   * This method is called by PrimeTicketRemote.<p>
   * If this method is made synchronized, problems occur.
   * @see #setAssigned()
   * @see PrimeTicketRemote#setAssigned()
   */
  public void setAssignedByPTR() {
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* PrimeTicket SETASSIGNEDBYPTR CALLED  "+toString());
    if (DEBUG) System.out.println(this.getName()+ " setAssignedByPTR called, tostring = "+ toString());
    if (isState(ASSIGNED)) {
       System.out.println(toString() + "assign by semaphore again?");
       System.exit(1);
    }
    setState(ASSIGNED);  // does callback, run is short!!
    if (DEBUG)  System.out.println("### ABOUT to notify in setAssignedByPTR");
    synchronized (this){
      this.notify();
      if (DEBUG) System.out.println("### notify in setAssignedByPTR DONE!!");
    }
  }

  public boolean equals(Object obj) {
    if (super.equals(obj)) {
      return true;
    }
    PrimeTicket pt = (PrimeTicket)obj;
    boolean result = false;
    if (pt.actorID == null) {
      try { pt.actorID = pt.scene_action.actor.getServiceID(); } catch (Exception e) { e.printStackTrace();}
    }
    if (actorID == null) {
      try { actorID = scene_action.actor.getServiceID(); } catch (Exception e) { e.printStackTrace();}
    }
    if ( (pt.actorID != null) && (this.actorID != null) ) {
      result = (actorID.equals(pt.actorID));
      result = (result && (pt.semaphore.equals(semaphore)));
    } else {
      System.out.println("actorID == null, probably we have local tickets");
      System.out.println("pt="+pt.toString()+"  this="+this.toString());
      System.out.println("result=false anyway");
    }
    if (DEBUG) System.out.println("*D* PrimeTicket.equals = "+result+":  "+toString()+" equals called:  obj="+obj.toString());
    return result;
  }

  public String toString() {
    String s = super.toString();
    try {
      if (semaphore != null) { s += "; semaphore="+semaphore.getName(); } else {s += "; semaphore=null";}
      if ( actorID != null) { s += "; actorID=" + actorID.toString(); } else { s += "; actorID = null";}
    } catch (Exception e) { e.printStackTrace(); }
    return s;
  }

  /////// VIEW RELATED METHODS ////////////////////////////////////////

  /*
  protected TicketPainter _getTicketPainter() {
    return new PrimeTicketPainter(this);
  }
  */

}


