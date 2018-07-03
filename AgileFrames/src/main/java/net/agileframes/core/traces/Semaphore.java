package net.agileframes.core.traces;

import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.traces.SceneIB;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.TicketIB;
import net.agileframes.traces.Discipline;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.TransactionManager;
import java.rmi.RemoteException;
import com.objectspace.jgl.Array;
// actually, code should be re-written wrt arrays; it is better to make home-made arrays
// instead of these jgl-objects, because we don't want to import & install this object.
// maybe it will cost money too if we use it...?
import java.rmi.server.UnicastRemoteObject;
import net.agileframes.traces.viewer.SemaphoreViewerProxy;

/**
 * <b>Capacity controller for shared resources.</b>
 * <p>
 * A Semaphore is a non-negative integer variable, representing available
 * capacity on a controlled resource.<br>
 * <p>
 * <b>Operations on Semaphores:</b><br>
 * When an actor wants to use the resource controlled by this Semaphore (wants
 * to drive on this controlled area of the Scene), the operation <i>insist</i>
 * is called. If the Semaphore has enough free capacity, the needed capacity is
 * allocated to the actor and the remaining capacity of the Semaphore is
 * diminished with this value (For an example, see Figure).<br>
 * <img SRC="doc-files/Semaphore-1.gif" height=190 width=578> <br>
 * If the Semaphore does not have enough free capacity (another actor is driving
 * in the controlled area for example) the resource is blocked and the claiming
 * actor will have to wait.<br>
 * When the actor has finished using the resource (driven completely out of the
 * controlled area), the operation <i>free</i> is called, allocating the
 * capacity to another vehicle if one is waiting or increasing the available
 * capacity of the Semaphore with the allocated capacity.<br>
 * Only prim-tickets are used to perform operations on Semaphores.
 * <p>
 * <b>Deadlocks:</b><br>
 * A deadlock is a situation in which two or more actors are waiting for each
 * other to free a resource.<br>
 * <img SRC="doc-files/Semaphore-2.gif" height=274 width=342> <br>
 * In the Figure above an example of a possible deadlock-situation is drawn. <br>
 * Deadlocks are hard to detect and the possibility of deadlocks should be
 * avoided. Semaphores could be protected from being deadlocked in different
 * ways. For example, the procedure for claiming a semaphores could be made in
 * such a way that the semaphores always are claimed in the same order.<br>
 * AgileFrames provides two special kind of Tickets that perform atomic
 * operations on Semaphores, the {@link net.agileframes.traces.ticket.CollectTicket CollectTicket}
 * and the {@link net.agileframes.traces.ticket.SelectTicket SelectTicket}.<br>
 * <p>
 * When vehicles always drive the moves in the same direction, deadlocks can
 * only occur if the following conditions are true:<br>
 * 1) there is more than one actor in the scene that is claiming semaphores<br>
 * 2) the semaphores are claimed using different scripts<br>
 * 3) more than one semaphore is claimed at the same time<br>
 * <p>
 * <b>Relation with Scene:</b><br>
 * All Semaphores should be part of exactly one Scene. In the case of a multiple
 * Scene environment (Scene-tree), Semaphores that control the access of the
 * connection between two Scenes, should be placed in that two Scene's super-Scene.<br>
 * Semaphores that control a resource within one Scene should be placed in that Scene. <br>
 * Semaphores that control the resource of an entire (sub-)Scene (for example
 * to make sure that not more than a certain number of actors are in the same
 * Scene at the same time), should be placed in the Scene's super-Scene.<br>
 * <p>
 * <b>Building a Scene with Semaphores:</b><br>
 * The resources controlled by semaphores often can be considered as physical areas in
 * a model. It should be kept in mind, however, that semaphores do not necessarily
 * guard a physical part of the scene.<br>
 * In the design process the following objectives are formed:<ul>
 * <li> use as few semaphores as possible
 * <li> the semaphores should be fully parameterizable, even when dimensions or constants change
 * <li> avoid possibility of conflicting situations when demanding capacity on a resource
 * <li> semaphores can overlap and do not necessarily represent geographical areas
 * </ul>
 * To create semaphores in a model that represent geographical areas, locations
 * where two or more moves cross should be examined. The following procedure is executed:<ul>
 * <li> for every move without any intersecting moves, a semaphore is created
 * <li> for areas with overlapping moves, semaphores for the moves are combined
 * </ul>
 * The design process should be executed with the intention to first create a rough
 * grid of controlled resources that can be worked out in greater detail later on.
 * In the design process the areas controlled by the semaphores that represent
 * geographical areas are considered to not be overlapping each other.<br>
 * <p>
 * <b>Remote Use of Semaphores:</b><br>
 * Semaphores often will be used in a remote context. In that case, the Scene will
 * be on one computer and the actor on another one. For remote use, Semaphore
 * implements the remote interface SemaphoreRemote. All methods defined in this
 * interface can be called remotely and are available in the Semaphore_Stub.<br>
 *
 * @see SemaphoreRemote
 * @see Actor
 * @see Ticket
 * @see net.agileframes.traces.ticket.PrimeTicket
 * @see Scene
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class Semaphore extends UnicastRemoteObject
    implements TransactionParticipant, SemaphoreRemote {
  /**
   * Parameter to be used to debug this class. <p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;
  /**
   * Parameter to be used to debug the synchronization of this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * If switched on, a print statement will appear on entering and existing each
   * synchronized method in this class, including the Thread that performs the
   * action.<br>
   * Will result in a huge amount of print-statements.
   * Default is <b><code>false</b></code>.
   */
  public static boolean SYNCH = false;
  /**
   * Constructor with name.<p>
   * Creates a semaphore with a maximum capacity of 1.
   * The discipline will be {@link net.agileframes.traces.Discipline Discipline}.
   * @see #Semaphore(String, int, Discipline)
   * @param name  a string specifying the name of the semaphore
   */
  public Semaphore(String name) throws RemoteException {
    this(name,1,new Discipline());
  }
  /**
   * Constructor with name and capacity.<p>
   * The discipline will be {@link net.agileframes.traces.Discipline Discipline}.
   * @see #Semaphore(String, int, Discipline)
   * @param name          a string specifying the name of the semaphore
   * @param max_capacity  the capacity of this semaphore
   */
  public Semaphore(String name,int max_capacity) throws RemoteException {
    this(name,max_capacity,new Discipline());
  }
  /**
   * Constructor with name, capacity and discipline.<p>
   * Starts a Thread that takes care of the assignment of tickets.
   * @param name          a string specifying the name of the semaphore
   * @param max_capacity  the capacity of this semaphore
   * @param discipline    the discipline of this semaphore.
   */
  public Semaphore(String name,int max_capacity,Discipline discipline) throws RemoteException {
    this.name = name;
    this.max_capacity = max_capacity;
    this.capacity = max_capacity;
    this.setDiscipline(discipline);

    Thread assignThread = new Thread("assignThread") {
      public void run() { runForAssignThread(); }
    };
    assignThread.start();
  }

  private int state = COMMITTED;
  private void setState(int s) {
    if (s!=state) {
      state = s;
      model_changed();
    }
  }
  /**
   * Returns Transaction State.<p>
   * Transaction State:
   * <ul>
   * <li>1 - ACTIVE
   * <li>2 - VOTING
   * <li>3 - PREPARED
   * <li>4 - NOTCHANGED
   * <li>5 - COMMITTED
   * <li>6 - ABORTED
   * </ul>
   * See also: <code>net.jini.core.transaction.server.TransactionConstants</code>
   * @return  the Transaction state
   */
  public int getState() { return state; }
  private boolean isState(int s) { return (s==state); }

  ////////////////////////////////////////////////////////////////////////

  private ServerTransaction server_transaction = null;

  private void setServerTransaction(ServerTransaction stx)
      throws ReserveDeniedException {
    if (DEBUG) {
      if (stx != null) System.out.println("*D* Semaphore.setServerTransaction("+stx.toString()+")");
      else System.out.println("*D* Semaphore.setServerTransaction(null)");
    }

    if (stx == null) { server_transaction = null; return; }
    if (server_transaction == null) {
      server_transaction = stx;
      try {
        server_transaction.join(this,0); // we don't crash.
        setState(ACTIVE);
        if (DEBUG) System.out.println(getName()+" joined, is active under "+server_transaction.id+" toString="+server_transaction.toString());
      }
      catch (UnknownTransactionException e) {
        System.out.println(toString() +
        " join failed: " + e.toString() + " " + server_transaction.id);
        System.exit(1);
        throw new ReserveDeniedException("UnknownTransactionException");

      }
      catch (CannotJoinException e) {
        System.out.println(toString() +
        " join failed: " + e.toString() + " " + server_transaction.id);
        System.exit(1);
        throw new ReserveDeniedException("CannotJoinException");
      }
      catch (CrashCountException e) {
        System.out.println(toString() +
        " join failed: " + e.toString() + " " + server_transaction.id);
        System.exit(1);
        throw new ReserveDeniedException("CrashCountException");
      }
      catch (RemoteException e) {
        System.out.println(toString() +
        " join failed: " + e.toString() + " " + server_transaction.id);
        System.exit(1);
        throw new ReserveDeniedException("RemoteException");
      }
    }
    else {
      if (stx.id != server_transaction.id) {
        System.out.println(getName() + " already reserving under transaction: " + server_transaction.id);
        throw new ReserveDeniedException("denied: " + stx.id);
      }
    }
  }

  private String name = null;
  /**
   * Returns the name of this Semaphore.<p>
   * The name should be unique in its Scene.
   * @return the name of this Semaphore.
   */
  public String getName() { return this.name; }
  /**
   * Returns a string with information about this semaphore.<p>
   * The string contains the semaphore's class, name and scene.
   * @return a string with information about this semaphore.
   */
  public String toString() {
    String string = getClass().toString() + " " + getName();
    if (scene != null) { string += " in scene " + scene.toString(); }
    return string;
  }

  private SceneIB scene = null;
  /**
   * Returns a reference to the Scene to which this semaphore belongs.
   * @return the scene the semaphore belongs to.
   */
  public SceneIB getScene() { return scene; }

  private int max_capacity = 0;
  /**
   * Returns the total capacity of this semaphore, when no units are claimed.
   * @return the semaphore's maximum capacity.
   */
  public int getMaxCapacity() { return max_capacity; }

  private int capacity = 0;
  /**
   * Returns the current available capacity of this semaphore.<p>
   * This value must be less or equal to the maximum capacity.
   * @see #getMaxCapacity()
   * @return the semaphore's current available capacity.
   */
  public int getCapacity() { return capacity; }
  private void setCapacity(int c) {
    capacity = c;
    model_changed();
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore "+getName() + ": capacity is: " + capacity);
  }

  private Discipline discipline = null;
  /**
   * Returns the discipline of this semaphore.<p>
   * The semaphore's discipline decides which reserving prime-ticket to honor
   * its capacity when capacity is repossessed.<br>
   * For example: FIFO: First In First Out.
   * @see     #setDiscipline(Discipline)
   * @return  the discipline of this semaphore
   */
  public Discipline getDiscipline() { return discipline; }
  /**
   * Sets the discipline of this semaphore.<p>
   * The semaphore's discipline decides which reserving prime-ticket to honor
   * its capacity when capacity is repossessed.<br>
   * For example: FIFO: First In First Out.
   * @see     #getDiscipline()
   * @param   discipline  the discipline of this semaphore
   */
  public void setDiscipline(Discipline discipline) {
    if (discipline == null) { this.discipline = new Discipline(); }
    else { this.discipline = discipline; }
    this.discipline.semaphore = this;
    this.discipline.reserved_list = this.reserved_list;
  }

  private Array assigned_list = new Array();
  /**
   * Returns the number of prime-tickets that are currently assigned.<p>
   * Prime-tickets are appended to this list when their claim is honored.
   * @return the number of prime-tickets that are currently assigned
   */
  public int getAssignedListLength() {
    return assigned_list.size();
  }

  private Array reserved_list = new Array();
  /**
   * Returns the number of prime-tickets that are currently reserving.<p>
   * Prime-tickets are appended to this list when they start reserving.
   * Their order in the list corresponds with their arrival time.
   * The reserve-pending-pending list is added to this list upon commit.
   * @see #commit(TransactionManager,long)
   * @return the number of prime-tickets that are currently reserving
   */
  public int getReservedListLength() {
    return reserved_list.size();
  }

  private Array reserve_pending_list = new Array();
  /**
   * Returns the number of prime-tickets that are pending.<p>
   * Prime-tickets are moved to this list from the reserve-pending-pending list
   * when the semaphore votes PREPARED. At that time their time-stamp is set.
   * @return the number of prime-tickets that are pending
   */
  public int getReservePendingListLength() {
    return reserve_pending_list.size();
  }

////////////// ATTEMPT //////////////////////////////////////////////////////

  /**
   * Reserves a semaphore only if it is possible.<p>
   * The prime-tickets claim is honored iff there is enough available capacity.
   * It ignores possible transactions that are going on.
   * @param prime_ticket  the ticket that requests the attempt.
   * @return true iff the claim is honored.
  */
  public synchronized boolean attempt(PrimeTicket prime_ticket) throws RemoteException {
    if (DEBUG) System.out.println("*D* Semaphore:"+getName()+"   attempt("+prime_ticket.toString()+")");
    if (assigned_list.contains(prime_ticket)) {
      if (DEBUG) System.out.println("Semaphore " + getName() +
        " multiple attempt by assigned ticket " + prime_ticket.toString());
      return true;
    }
    if (reserved_list.contains(prime_ticket)) {
      if (DEBUG) System.out.println("Semaphore " + getName() +
        " multiple attempt by impatient ticket " + prime_ticket.toString());
      return false;
    }
    if (prime_ticket.claim <= capacity) {
      setCapacity(capacity - prime_ticket.claim);
      assigned_list.add(prime_ticket);
      if (DEBUG) System.out.println(System.currentTimeMillis()+"  Semaphore " + getName() +
        " honored " + prime_ticket.toString() + "'s claim immediately");
      return true;
    }
    else return false;
  }

////ABORT////////////////////////////

  /**
   * Cancels reserve-operation on this semaphore.<p>
   * For the time being NOT alid in context of transactions. Will only
   * cancel the operation, if the specified ticket started it.
   * @param prime_ticket  the ticket that requests the abort.
   */
  public synchronized void abort(PrimeTicket prime_ticket) throws RemoteException {
    if (SYNCH) System.out.println("*1* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println("*D* Semaphore:"+getName()+"   abort("+prime_ticket.toString()+")");
    int i = this.reserved_list.count(prime_ticket);
    if (i > 1) {
      if (DEBUG) System.out.println("Semaphore " + getName() +
        " crashed due to multiple occurrence of " + prime_ticket.toString() +
        " in the assigned list during abort.");
      System.exit(1);
    }
    if (i == 0) {
      if (DEBUG) System.out.println("Semaphore " + getName() +
        " unwarrented abort by " + prime_ticket.toString());
      if (SYNCH) System.out.println("*1* UNSYNCH "+Thread.currentThread().toString());
      return;
    }
    if (DEBUG) System.out.println("Semaphore " + getName() + " aborted");
    reserved_list.remove(prime_ticket);
    try { this.free(prime_ticket); }
    catch (RemoteException e) { System.out.println("RemoteException in Semaphore.abort --> we quit"); e.printStackTrace(); System.exit(0); }
    if (SYNCH) System.out.println("*1* UNSYNCH "+Thread.currentThread().toString());
  }

////////////// FREE ////////////////////////////////////////////////////////

  /**
   * Frees this semaphore.<p>
   * The prime-tickets claim is repossessed. Is only possible if the specified
   * ticket 'owns' capacity on this semaphore.
   * @param prime_ticket  the ticket that requests this free.
   */
  public synchronized void free(PrimeTicket prime_ticket) throws RemoteException {
    if (SYNCH) System.out.println(System.currentTimeMillis() + "  *2* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println("*D* Semaphore: "+getName()+"  free("+prime_ticket.toString()+")");
    if (reserved_list.contains(prime_ticket)) {
      reserved_list.remove(prime_ticket);
    }
    int i = assigned_list.count(prime_ticket);
    if (i > 1) {
      System.out.println("Semaphore " + getName() +
        " crashed due to multiple occurrence of " + prime_ticket.toString() +
        " in the assigned list.");
      System.exit(1);
    }
    if (i == 0) {
      if (DEBUG) System.out.println("*D* Semaphore " + getName() +
        " unwarrented free by " + prime_ticket.toString());
      if (SYNCH) System.out.println("*2* UNSYNCH "+Thread.currentThread().toString());
      return;
    }
    if (DEBUG) System.out.println("*D* Semaphore " + getName() +
      " repossessed claim of " + prime_ticket.toString());
    assigned_list.remove(prime_ticket);
    setCapacity(capacity + prime_ticket.claim);
    assign();
    if (SYNCH) System.out.println("*2* UNSYNCH "+Thread.currentThread().toString());
  }

////////////// SNIP /////////////////////////////////////////////////////////
  /**
   * Returns the state of the semaphore with respect to the specified prime-ticket.<p>
   * Can return the following values:<ul>
   * <li> {@link TicketIB#ASSIGNED TicketIB.ASSIGNED}
   * <li> {@link TicketIB#RESERVING TicketIB.RESERVING}
   * <li> {@link TicketIB#INITIAL TicketIB.INITIAL}
   * </ul>
   * @param   prime_ticket  the ticket of which the semaphore-state is requested
   * @return  the state of the semaphore
   */
  public synchronized int snip(PrimeTicket prime_ticket) throws RemoteException {
    if (SYNCH) System.out.println("*3* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println("*D* Semaphore:"+getName()+"   snip("+prime_ticket.toString()+")");
    if (assigned_list.contains(prime_ticket)) {
      if (SYNCH) System.out.println("*3* UNSYNCH "+Thread.currentThread().toString());
      return TicketIB.ASSIGNED;
    }
    if (reserved_list.contains(prime_ticket)) {
      if (SYNCH) System.out.println("*3* UNSYNCH "+Thread.currentThread().toString());
      return TicketIB.RESERVING;
    }
    if (SYNCH) System.out.println("*3* UNSYNCH "+Thread.currentThread().toString());
    return TicketIB.INITIAL;
  }

////////////// RESERVE ///////////////////////////////////////////////////////

  /**
   * Reserves this semaphore.<p>
   * The prime-tickets claim is honored if there is enough available capacity.
   * If the semaphore is "under transaction" the reserve may continue because
   * the call is assumed to have been called by a prime-ticket on its own right
   * by the actor-thread, it's not a danger for deadlocking! The point is that
   * parallel reserves may deadlock.<br>
   * Note that with this implementation collect tickets suddenly becones very
   * relevant when claiming sequences. If not everybody follows the same
   * sequence then ...Deadlock.<br>
   * @see     #reserve(Transaction,PrimeTicket)
   * @param   prime_ticket  the tickets that requests the reserve-operation
   * @return  true iff the claim is honored during this all.
   */
  public synchronized boolean reserve(PrimeTicket prime_ticket) throws RemoteException {
    if (SYNCH) System.out.println("*4* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore:"+getName()+"   reserve("+prime_ticket.toString()+", "+this.getName()+")");
    if (server_transaction != null) {
      if (DEBUG) System.out.println("WARNING Semaphore " + getName() +
        " reserved ticket " + prime_ticket.toString() +
        " ignoring the danger of deadlock.");
    }
    if (assigned_list.contains(prime_ticket)) {
      if (DEBUG) System.out.println("WARNING Semaphore " + getName() +
      " multiple reserve by assigned ticket " + prime_ticket.toString());
      if (SYNCH) System.out.println("*4* UNSYNCH "+Thread.currentThread().toString());
      return true;
    }
    if (reserved_list.contains(prime_ticket)) {
      if (DEBUG) System.out.println("WARNING Semaphore " + getName() +
      " multiple reserve by reserving ticket " + prime_ticket.toString());
      if (SYNCH) System.out.println("*4* UNSYNCH "+Thread.currentThread().toString());
      return false;
    }
    if (prime_ticket.claim <= capacity) {
      setCapacity(capacity - prime_ticket.claim);
      assigned_list.add(prime_ticket);
      if (DEBUG) System.out.println("*D* Semaphore " + getName() +
        " honored claim by " + prime_ticket.toString());
      if (SYNCH) System.out.println("*4* UNSYNCH "+Thread.currentThread().toString());
      return true;
    }
    else {
      reserved_list.add(prime_ticket);
      if (DEBUG) System.out.println("*D* Semaphore " + getName() +
        " reserved claim by " + prime_ticket.toString());
      model_changed();
      if (SYNCH) System.out.println("*4* UNSYNCH "+Thread.currentThread().toString());
      return false;
    }
  }

///////////////////// RESERVE(TRANSACTION) ////////////////////////////////////

  /**
   * Reserves this semaphore under transaction.<p>
   * After this a normal reserve is called also iff not aborted,
   * unless the primeticket is assigned in the mean time.<br>
   * See the Jini-specification for more details about transactions.
   * @see       #reserve(PrimeTicket)
   * @exception ReserveDeniedException  when semaphore is not in right state
   * @param     tx            the transaction under which this reserve should be performed
   * @param     prime_ticket  the ticket that requests the reserve
   */
  public synchronized void reserve(Transaction tx,PrimeTicket prime_ticket)
      throws ReserveDeniedException, RemoteException {
    if (SYNCH) System.out.println("*5* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore:"+getName()+"  reserve("+tx.toString()+", "+prime_ticket.toString()+")");
    switch (state) {
      case NOTCHANGED: { /* a new transaction */ }
        // the previous transaction left the semaphore behind in state NOTCHANGED
        // this reserve-transaction may proceed, do not break, follow through to case active
      case COMMITTED:  { /* a new transaction */ }
        // the previous transaction left the semaphore behind in state COMMITTED
        // this reserve-transaction may proceed, do not break, follow through to case active
      case ABORTED:    { setServerTransaction(null); }
        // this is not a new transaction ?????
        // the previous transaction left the semaphore behind in state ABORTED
        // this reserve-transaction may proceed, do not break, follow through to case active
      case ACTIVE: {
        //System.out.println("semaphore case is ACTIVE");
        // the previous transaction left the semaphore behind in state ACTIVE ???? ignore for now
        // I do not think this should be the case, but it does happen.
        // this reserve-transaction may proceed, do not break, follow through to case active
        //System.out.println("semaphore state before setServerTransaction =" + this.state + "1=ACTIVE;2=VOTING;3=PREPARED;4=NOTCHANGED;5=COMITTED;6=ABORTED");
        //if ((prime_ticket != null) && (prime_ticket.ticketRemote != null) ) System.out.println("reserve called by "+prime_ticket.toString()+"  ticketRemote="+prime_ticket.ticketRemote.toString());
        setServerTransaction((ServerTransaction)tx);
        // state has been set to active
        if (reserve_pending_list.count(prime_ticket) == 0) {
          reserve_pending_list.add(prime_ticket);
          if (DEBUG) System.out.println("*D* Semaphore:"+getName()+"  reserve_pending_list added "+ prime_ticket.toString());
        }
        if (DEBUG) System.out.println("*D* Semaphore " + getName() +
        " reserve-pended under transaction " + server_transaction.id);
        break;
      }
      case VOTING: { throw new ReserveDeniedException(toString() +
        " was VOTING for another transaction: " + server_transaction.id);
      }
      case PREPARED: { throw new ReserveDeniedException(toString() +
        " was PREPARED for another transaction: " + server_transaction.id);
      }
    }
    if (SYNCH) System.out.println("*5* UNSYNCH "+Thread.currentThread().toString());
  }

/////////// IMPLEMENTATION OF TransactionParticipant /////////////////////

  /**
   * Prepares this semaphore. Inherited from TransactionParticipant.<p>
   * See the Jini-specification for more details.<br>
   * Only relevant in the context of Select- and CollectTickets.
   * @see       net.agileframes.traces.ticket.SetTicket
   * @exception UnknownTransactionException if the transaction is not known
   * @param     tx  the tranaction-manager that performs this operation
   * @param     id  the identification-number of the transaction
   * @return    the state of this semaphore after prepare
   */
  public synchronized int prepare(TransactionManager tx,long id)
      throws UnknownTransactionException {
    if (SYNCH) System.out.println("*6* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore "+getName() + " is preparing transaction, state = "+state);
    if (server_transaction.id != id) {
      System.out.println(getName() +
        " not prepared: UnknownTransactionException");
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE: { setState(VOTING);      // no break: flowing through
      //System.out.println(getIdentity() + " voting");
      }
      case VOTING:{ setState(PREPARED);
        //System.out.println(getIdentity() + " prepared.");
        break;
      }
      case PREPARED: {
        //System.out.println(toString() + " prepared again: PREPARED");
        break;
      }
      case NOTCHANGED: {
        //System.out.println(toString() + " prepared again: NOTCHANGED ");
        break;
      }
      case COMMITTED: {
        //System.out.println(toString() + " prepared again: COMMITTED ");
        break;
      }
      case ABORTED: {
        //System.out.println(toString() + " prepared again: ABORTED ");
        break;
      }
    }
    if (SYNCH) System.out.println("*6* UNSYNCH "+Thread.currentThread().toString());
    return state;
  }

  /**
   * Commits this semaphore. Inherited from TransactionParticipant.<p>
   * See the Jini-specification for more details.<br>
   * Only relevant in the context of Select- and CollectTickets.
   * @see       net.agileframes.traces.ticket.SetTicket
   * @exception UnknownTransactionException if the transaction is not known
   * @param     tx  the tranaction-manager that performs this operation
   * @param     id  the identification-number of the transaction
   */
  public synchronized void commit(TransactionManager tx,long id)
      throws UnknownTransactionException {
    if (SYNCH) System.out.println("*7* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore "+getName() + " is commiting transaction, state = "+state);
    if (server_transaction.id != id) {
      System.out.println(toString() +
        " not committed: UnknownTransactionException");
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE: { System.out.println(toString() +
        " not committed: commit before prepare" + id);
        System.exit(1);
      }
      case VOTING:{ System.out.println(toString() +
        " not committed: commit during prepare" + id);
        System.exit(1);
      }
      case PREPARED: {//only state to go thru
        if (DEBUG) System.out.println("*D* Semaphore "+getName() + " committed. " + id);
        break;
      }
      case NOTCHANGED: {
        System.out.println("FATAL EXCEPTION: "+toString() + " committed again to NOTCHANGED " + id);
        System.exit(1);
        break;
      }
      case COMMITTED: {
        System.out.println("FATAL EXCEPTION: "+toString() + " committed again to COMMITTED " + id);
        System.exit(1);
        break;
      }
      case ABORTED: {
        System.out.println("FATAL EXCEPTION: "+toString() + " committed again to ABORTED " + id);
        System.exit(1);
        break;
      }
    }
    //state=PREPARED
    while(!reserve_pending_list.isEmpty()) {
      Object pt = reserve_pending_list.popFront();
      reserved_list.pushBack(pt);
    }
    setState(COMMITTED);
    assign();
    if (SYNCH) System.out.println("*7* UNSYNCH "+Thread.currentThread().toString());
  }

  /**
   * Aborts the reserve or commit of this semaphore. Inherited from TransactionParticipant.<p>
   * See the Jini-specification for more details.<br>
   * Only relevant in the context of Select- and CollectTickets.
   * @see       net.agileframes.traces.ticket.SetTicket
   * @exception UnknownTransactionException if the transaction is not known
   * @param     tx  the tranaction-manager that performs this operation
   * @param     id  the identification-number of the transaction
   */
  public synchronized void abort(TransactionManager tx,long id)
      throws UnknownTransactionException, RemoteException {
    if (SYNCH) System.out.println("*8* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println("*D* Semaphore "+getName() + " is aborting transaction, state = "+state);
    if (server_transaction.id != id) {
      //System.out.println(
      //  "semaphore not aborted: UnknownTransactionException");
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE:    {
        //System.out.println(toString() +" aborted (before prepare) " + id);
        break;
      }
      case VOTING:    {
        //System.out.println(toString() +
        //" aborted (during prepare) " + id);
        break;
      }
      case PREPARED:  {
        //System.out.println(toString() + " aborted " + id);
        break;
      }
      case NOTCHANGED:{ System.out.println(toString() +
        " aborted WARNING: abort whilst NOTCHANGED voted!! " + id);
        System.exit(1);
        break;
      }
      case COMMITTED: { System.out.println(toString() +
        " abort ignored: WARNING: abort after commit!! " + id);
        System.exit(1);
        break;
      }
      case ABORTED:   { System.out.println(toString() + " aborted again" + id);
        System.exit(1);
        break;
      }
    }
    while(!reserve_pending_list.isEmpty()) { reserve_pending_list.popFront(); }
    setState(ABORTED);
    assign();
    if (SYNCH) System.out.println("*8* UNSYNCH "+Thread.currentThread().toString());
  }

  /**
   * Prepares and commits this semaphore. Inherited from TransactionParticipant.<p>
   * See the Jini-specification for more details.<br>
   * Only relevant in the context of Select- and CollectTickets.
   * @see       #prepare(TransactionManager, long)
   * @see       #commit(TransactionManager, long)
   * @see       net.agileframes.traces.ticket.SetTicket
   * @exception UnknownTransactionException if the transaction is not known
   * @param     tx  the tranaction-manager that performs this operation
   * @param     id  the identification-number of the transaction
   * @return    the state of this semaphore after prepare and commit
   */
  public synchronized int prepareAndCommit(TransactionManager tx,long id)
      throws UnknownTransactionException, RemoteException {
    if (SYNCH) System.out.println("*9* SYNCH "+Thread.currentThread().toString());
    //System.out.println(getName() + " is prep&com transaction, state = "+state);
    if (server_transaction.id != id) {
      //System.out.println("semaphore not prepareAndCommit" + id +
      //" " + server_transaction.id);
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE: { //System.out.println(toString() +
        //" prepareAndCommit, now voting." + id);
        setState(VOTING);
      }
      case VOTING: { //System.out.println(toString() +
        //" prepareAndCommit, now prepared." + id);
        setState(PREPARED);
      }
      case PREPARED: { //System.out.println(toString() +
        //" prepareAndCommit, now committing." + id);
        break;
      }
      case COMMITTED: { //System.out.println(toString() +
        //" prepareAndCommit after commit." + id);
        break;
      }
      case NOTCHANGED: { //System.out.println(toString() +
        //" prepareAndCommit after NOTCHANGED voted" + id);
        break;
      }
      case ABORTED: { //System.out.println(toString() +
        //" prepareAndCommit after ABORTED" + id);
        break;
      }
    }
    while(!reserve_pending_list.isEmpty()) {
      Object pt = reserve_pending_list.popFront();
      reserved_list.pushBack(pt);
    }
    setState(COMMITTED);
    assign();
    if (SYNCH) System.out.println("*9* UNSYNCH "+Thread.currentThread().toString());
    return state;
  }


//////// implementation of prime-ticket assignment ///////////////////////////

  /**
   * Calls the discipline to do some assigning. <p>
   * This method is always called by {@link #free() free}.
   * If so then commit or abort does the call.
   */
  //changed by hw-26/06/01
  private synchronized void assign() {
    if (SYNCH) System.out.println("*10* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore "+getName()+" assign(), state = "+state);
    switch (state) {
      case ACTIVE: {}
      case VOTING: {}
      case PREPARED: {
        if (DEBUG) System.out.println("*D* Semaphore.assign() postponed due to transaction");
        if (SYNCH) System.out.println("*10* UNSYNCH "+Thread.currentThread().toString());
        return;
      }
    }
    PrimeTicket pt = discipline.select();
    int counter = 0;
    while (pt != null) {
      assign(pt);
      pt = discipline.select();
      counter++;
    }
    if (DEBUG) System.out.println("*D* Semaphore assign: number of tickets assigned (:::::)= " + counter);
    if (SYNCH) System.out.println("*10* UNSYNCH "+Thread.currentThread().toString());
  }

   /* OLD COMMENT: In here a dangerous - possible deadlock - situation may occur.
   The problem is that the thread of the last owner of the semaphore calls
   {@link #free() free} which calls {@link #assign() assign} which calls this method.
   This method calls {@link net.agileframes.traces.ticket.PrimeTicket#setAssigned()
   primeTicket.setAssigned}, which is a call to another prime-ticket of
   another actor. Thus, one actor calls a method on another actor.
   This is a dangerous situation and should be avoided.*/

  /**
   * Does the actual assigning.<p>
   * Will notify a special Thread that calls
   * {@link net.agileframes.traces.ticket.PrimeTicket#setAssigned() primeTicket.setAssigned}.
   * @param pt  the ticket that should be assigned to this Semaphore
   */
  //changed by hw - 26/06/01
  private synchronized void assign(PrimeTicket pt) {
    if (SYNCH) System.out.println("*11* SYNCH "+Thread.currentThread().toString());
    if (DEBUG) System.out.println(System.currentTimeMillis()+"  *D* Semaphore "+getName()+" assign(pt) called");
    int i = reserved_list.count(pt);
    if (i > 1) {
      System.out.println("Semaphore: "+pt.toString() + " multiply ("+i+" times) in reserved list. WE QUIT");
      System.exit(1);
    }
    if (i == 0) {
      System.out.println("Semaphore: "+pt.toString() + " not in reserved list. WE QUIT");
      System.exit(1);
    }
    if (capacity >= pt.threshold) {
      setCapacity(capacity - pt.claim);
      //OLD//      reserved_list.popFront();
      reserved_list.remove(pt); // changed by HW 21JUN01
      assigned_list.add(pt);
      if (DEBUG) System.out.println(System.currentTimeMillis()+"  ASSIGN: reserved_list = "+reserved_list.toString());
      if (DEBUG) System.out.println("ASSIGN: assigned_list = "+assigned_list.toString());
      ptToAssign_list.add(pt);//sets pt in runForAssignThread
      try { this.notify(); }//wakes up runForAssignThread
      //OLD//try { pt.setAssigned(); }
      catch(Exception e) { e.printStackTrace(); }
      if (DEBUG) System.out.println("*D* Semaphore "+getName()+" assign primeticket:"+pt.toString() + " claim honored");
    }
    else {
      System.out.println("WARNING: "+pt.toString() + " not assigned: too large threshold");
    }
    if (SYNCH) System.out.println("*11* UNSYNCH "+Thread.currentThread().toString());
  }

  ////////////////////////////////////////////
  private SemaphoreViewerProxy semaphoreViewer = null;
  /**
   * Sets a semaphore-viewer object for this semaphore that will monitor the state of this semaphore.<p>
   * Semaphore-viewers are used in a graphical user interface, to monitor the state
   * of a semaphore. Every time the state of this semaphore changes, the semaphore
   * viewer will receive a <code>modelChanged</code>-call from this semaphore.<br>
   * The reason that a viewer-proxy is used, is that otherwise the
   * <code>modelChanged</code>-call could result in a <code>RemoteException</code>
   * which would affect the performance of the semaphore.<br>
   * @see   net.agileframes.traces.viewer.SemaphoreViewer
   * @param semaphoreViewer the samphoreViewer (proxy) that monitors the state of this semaphore
   */
  public void setViewer (SemaphoreViewerProxy semaphoreViewer) throws java.rmi.RemoteException {
    this.semaphoreViewer = semaphoreViewer;
  }


  // assigns when the semaphore is notified
  // the semaphore will work without using this extra thread (by calling assign()
  // at places where now this.notify() is called), but in a remote context the
  // use of this extra thread is preferred.
  private Array ptToAssign_list = new Array();
  private void runForAssignThread() {
    for(;;) {
      while (ptToAssign_list.isEmpty()) {
        try {
          synchronized(this) {
            wait(10000);
          }
        } catch(Exception e) { e.printStackTrace(); }
      }
      while (!ptToAssign_list.isEmpty()) {
        PrimeTicket pt = (PrimeTicket)ptToAssign_list.popFront();
        if (DEBUG) System.out.println("SETASSIGNED CALLED ON PTTOASSIGN   --> pt="+pt.toString());
        pt.setAssigned();
      }
    }
  }

  private void model_changed() {
    if (semaphoreViewer != null) { semaphoreViewer.modelChanged(); }
  }

  /**
   * Creates a prime-ticket for this semaphore.<p>
   * This method is an alternative for using the constructor of prime-ticket.
   * Actually it is better to create prime-tickets in this way as it is
   * impossible to have a prime-ticket without a semaphore.
   * @see     net.agileframes.traces.ticket.PrimeTicket#PrimeTicket(String,SceneAction,SemaphoreRemote,int,int)
   * @param   sa    the scene action in which this prime-ticket is created
   * @param   claim the number of units to be claimed
   * @return  a new prime-ticket
   */
  public PrimeTicket createPrimeTicket(SceneAction sa, int claim) {
    return new PrimeTicket(getName()+"_PT", sa, this, claim, 0);
  }
}