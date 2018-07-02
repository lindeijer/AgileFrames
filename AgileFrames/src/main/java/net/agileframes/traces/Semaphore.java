package net.agileframes.traces;

import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.traces.SceneImplBase;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.TicketImplBase;
// import net.agileframes.TRACES.VIEW.*;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.TransactionManager;
import java.rmi.RemoteException;
import com.objectspace.jgl.Array;
import java.rmi.server.UnicastRemoteObject;

import com.agileways.traces.scene.SemaphoreViewer;

/**
The semaphore possessess a thread that assignes the prime-tickets
All methods are synchronized.
Construction procedure is the same as for basic actions.
*/

public class Semaphore extends UnicastRemoteObject
    implements TransactionParticipant {

  /**
   * creates a semaphore with a maximum capacity of 1.
   *
   */
  public Semaphore(String name) throws RemoteException {
    this(name,1,new Discipline());
  }

  public Semaphore(String name,int max_capacity) throws RemoteException {
    this(name,max_capacity,new Discipline());
  }

  public Semaphore(String name,int max_capacity,Discipline discipline) throws RemoteException {
    this.name = name;
    this.max_capacity = max_capacity;
    this.capacity = max_capacity;
    this.setDiscipline(discipline);
  }

  Object object = new Object();

  /**
  Transaction State: ACTIVE(1),VOTING,PREPARED,NOTCHANGED,COMMITTED,ABORTED(6)
  @see TransactionConstants
  */
  private int state = COMMITTED;
  private void setState(int s) {
    if (s!=state) {
      state = s;
      model_changed();
    }
  }
  public int getState() { return state; }
  private boolean isState(int s) { return (s==state); }

  ////////////////////////////////////////////////////////////////////////

  private ServerTransaction server_transaction = null;

  private void setServerTransaction(ServerTransaction stx)
      throws ReserveDeniedException {
    if (stx == null) { server_transaction = null; return; }
    if (server_transaction == null) {
      server_transaction = stx;
      try {
        server_transaction.join(this,0); // we don't crash.
        setState(ACTIVE);
        //  System.out.println(getName() +
        //  " joined, is active under " + server_transaction.id);
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
        // System.out.println(getName() + " already reserving under transaction: " + server_transaction.id);
        throw new ReserveDeniedException("denied: " + stx.id);
      }
    }
  }

  /**
  The semaphores name within the scene.
  */
  private String name = null;

  public String getName() { return this.name; }

  public String toString() {
    return this.getClass().toString() + " " + this.getName() + " in scene " +
           this.scene.toString();
  }


  /**
  The scene the semaphore belongs to.
  */
  private SceneImplBase scene = null;
  public SceneImplBase getScene() { return scene; }

  /**
  The semaphores maximum capacity.
  */
  private int max_capacity = 0;
  public int getMaxCapacity() { return max_capacity; }

  /**
  The semaphores current available capacity.
  This value must be less or equal to the maximum capacity.
  */
  private int capacity = 0;
  public int getCapacity() { return capacity; }
  private void setCapacity(int c) {
    capacity = c;
    model_changed();
    // System.out.println(getIdentity() + " capacity is: " + capacity);
  }

  /**
  The semaphores discipline decides which reserving prime-ticket to honor
  its capacity when capacity is repossessed.
  */
  private Discipline discipline = null;
  public Discipline getDiscipline() { return discipline; }
  public void setDiscipline(Discipline discipline) {
    if (discipline == null) { this.discipline = new Discipline(); }
    else { this.discipline = discipline; }
    this.discipline.semaphore = this;
    this.discipline.reserved_list = this.reserved_list;
  }

  /**
  Prime-tickets are appended to this list when their claim is honored.
  */
  private Array assigned_list = new Array();
  public int getAssignedListLength() {
    return assigned_list.size();
  }

  /**
  Prime-tickets are appended to this list when they start reserving.
  There order in the list corresponds with their arrival time.
  The reserve-pending-pending list is added to this list upon commit().
  */
  private Array reserved_list = new Array();
  public int getReservedListLength() {
    return reserved_list.size();
  }

  /**
  Prime-tickets are moved to this list from the reserve-pending-pending list
  when the semaphore votes PREPARED. At that time their time-stamp is set.
  */
  private Array reserve_pending_list = new Array();
  public int getReservePendingListLength() {
    return reserve_pending_list.size();
  }

////////////// ATTEMPT //////////////////////////////////////////////////////

  /**
  The prime-tickets claim is honored iff there is enough available capacity.
  It ignores possible transaction that are going on.
  @return true iff the claim is honored.
  */
  public synchronized boolean attempt(PrimeTicket prime_ticket) {
    if (assigned_list.contains(prime_ticket)) {
      //System.out.println("Semaphore " + getName() +
      //  " multiple attempt by assigned ticket " + prime_ticket.toString());
      return true;
    }
    if (reserved_list.contains(prime_ticket)) {
      //System.out.println("Semaphore " + getName() +
      //  " multiple attempt by impaitent ticket " + prime_ticket.toString());
      return false;
    }
    if (prime_ticket.claim <= capacity) {
      setCapacity(capacity - prime_ticket.claim);
      assigned_list.add(prime_ticket);
      //System.out.println("Semaphore " + getName() +
      //  " honored " + prime_ticket.toString() + "'s claim immediately");
      return true;
    }
    else return false;
  }

////////////// FREE ////////////////////////////////////////////////////////

  /**
  The prime-tickets claim is repossessed.
  */
  public synchronized void free(PrimeTicket prime_ticket) {
    int i = assigned_list.count(prime_ticket);
    if (i > 1) {
      //System.out.println("Semaphore " + getName() +
      //  " crashed due to multiple occurrence of " + prime_ticket.toString() +
      //  " in the assigned list.");
      System.exit(1);
    }
    if (i == 0) {
      //System.out.println("Semaphore " + getName() +
      //  " unwarrented free by " + prime_ticket.toString());
      return;
    }
    //System.out.println("Semaphore " + getName() +
    //  " repossessed claim of " + prime_ticket.toString());
    assigned_list.remove(prime_ticket);
    setCapacity(capacity + prime_ticket.claim);
    assign();
  }

////////////// SNIP /////////////////////////////////////////////////////////

  public synchronized int snip(PrimeTicket prime_ticket) {
    if (assigned_list.contains(prime_ticket)) {
      return TicketImplBase.ASSIGNED;
    }
    if (reserved_list.contains(prime_ticket)) {
      return TicketImplBase.RESERVING;
    }
    return TicketImplBase.INITIAL;
  }

////////////// RESERVE ///////////////////////////////////////////////////////

  /**
  The prime-tickets claim is honored when there is enough available capacity.
  If the semaphore is "under transaction" the reserve may continue because
  the call is assumed to have been called by a prime-ticket on its own right.
  by the actor-thread, it not a danger for deadlocking! The point is that
  parallel reserves may deadlock. Note that with this implementation collect
  tickets suddenly becones very relevant when claiming sequences. If not
  everybody follows the same sequence then ........
  @return true iff the claim is honored during this all.
  */
  public synchronized boolean reserve(PrimeTicket prime_ticket) {
    if (server_transaction != null) {
      //System.out.println("Semaphore " + getName() +
      //  " reserved ticket " + prime_ticket.toString() +
      //  " ignoring the danger of deadlock.");
    }
    if (assigned_list.contains(prime_ticket)) {
      //System.out.println("Semaphore " + getName() +
      //" multiple reserve by assigned ticket " + prime_ticket.toString());
      return true;
    }
    if (reserved_list.contains(prime_ticket)) {
      //System.out.println("Semaphore " + getName() +
      //" multiple reserve by reserving ticket " + prime_ticket.toString());
      return false;
    }
    if (prime_ticket.claim <= capacity) {
      setCapacity(capacity - prime_ticket.claim);
      assigned_list.add(prime_ticket);
      //System.out.println("Semaphore " + getName() +
      //  " honored claim by " + prime_ticket.toString());
      return true;
    }
    else {
      reserved_list.add(prime_ticket);
      //System.out.println("Semaphore " + getName() +
      //  " reserved claim by " + prime_ticket.toString());
      model_changed();
      return false;
    }
  }

///////////////////// RESERVE(TRANSACTION) ////////////////////////////////////

  /**
  After this a normal reserve is called also iff not aborted,
  unless the primeticket is assigned in the mean time.
  @exception ReserveDeniedException
  */
  public synchronized void reserve(Transaction tx,PrimeTicket prime_ticket)
      throws ReserveDeniedException {
    switch (state) {
      case NOTCHANGED: { /* a new transaction */ }
      case COMMITTED:  { /* a new transaction */ }
      case ABORTED:    { setServerTransaction(null); }
      case ACTIVE: {
        setServerTransaction((ServerTransaction)tx);
        if (reserve_pending_list.count(prime_ticket) == 0) {
          reserve_pending_list.add(prime_ticket);
        }
        //System.out.println("Semaphore " + getName() +
        //" reserve-pended under transaction " + server_transaction.id);
        break;
      }
      case VOTING: { throw new ReserveDeniedException(toString() +
        " was VOTING for another transaction: " + server_transaction.id);
      }
      case PREPARED: { throw new ReserveDeniedException(toString() +
        " was PREPARED for another transaction: " + server_transaction.id);
      }
    }
  }

/////////// IMPLEMENTATION OF TransactionParticipant /////////////////////

  public synchronized int prepare(TransactionManager tx,long id)
      throws UnknownTransactionException {
    if (server_transaction.id != id) {
      System.out.println(toString() +
        " not prepared: UnknownTransactionException");
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE: { setState(VOTING);
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
    return state;
  }

  public synchronized void commit(TransactionManager tx,long id)
      throws UnknownTransactionException {
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
      case PREPARED: {
        // System.out.println(getIdentity() + " committed. " + id);
        break;
      }
      case NOTCHANGED: {
        //System.out.println(toString() + " committed again to NOTCHANGED " + id);
        System.exit(1);
        break;
      }
      case COMMITTED: {
        //System.out.println(toString() + " committed again to COMMITTED " + id);
        System.exit(1);
        break;
      }
      case ABORTED: {
        //System.out.println(toString() + " committed again to ABORTED " + id);
        System.exit(1);
        break;
      }
    }
    while(!reserve_pending_list.isEmpty()) {
      reserved_list.pushBack(reserve_pending_list.popFront());
    }
    setState(COMMITTED);
    // System.out.println("resevrved list: " + reserved_list.toString());
    assign();
  }

  public synchronized void abort(TransactionManager tx,long id)
      throws UnknownTransactionException, RemoteException {
    if (server_transaction.id != id) {
      System.out.println(
        "semaphore not aborted: UnknownTransactionException");
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE:    {
        // System.out.println(toString() +
        //" aborted (before prepare) " + id);
        break;
      }
      case VOTING:    {
        System.out.println(toString() +
        " aborted (during prepare) " + id);
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
  }

  public synchronized int prepareAndCommit(TransactionManager tx,long id)
      throws UnknownTransactionException, RemoteException {
    if (server_transaction.id != id) {
      System.out.println("semaphore not prepareAndCommit" + id +
      " " + server_transaction.id);
      throw new UnknownTransactionException();
    }
    switch (state) {
      case ACTIVE: { System.out.println(toString() +
        " prepareAndCommit, now voting." + id);
        setState(VOTING);
      }
      case VOTING: { System.out.println(toString() +
        " prepareAndCommit, now prepared." + id);
        setState(PREPARED);
      }
      case PREPARED: { System.out.println(toString() +
        " prepareAndCommit, now committing." + id);
        break;
      }
      case COMMITTED: { System.out.println(toString() +
        " prepareAndCommit after commit." + id);
        break;
      }
      case NOTCHANGED: { System.out.println(toString() +
        " prepareAndCommit after NOTCHANGED voted" + id);
        break;
      }
      case ABORTED: { System.out.println(toString() +
        " prepareAndCommit after ABORTED" + id);
        break;
      }
    }
    while(!reserve_pending_list.isEmpty()) {
      reserved_list.pushBack(reserve_pending_list.popFront());
    }
    setState(COMMITTED);
    assign();
    return state;
  }


//////// implementation of prime-ticket assignment ///////////////////////////

  /**
  Calls the discipline to do some assigning. Always called by free().
  If so then commit or abort does the call
  */
  private synchronized void assign() {
    switch (state) {
      case ACTIVE: {}
      case VOTING: {}
      case PREPARED: {
        //System.out.println("assign() postponed due to transaction");
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
    // System.out.println("DISCIPLIUNE ASSIGNED ::::::: " + counter);
  }

  /*
  Does the actual assigning
  */
  private synchronized void assign(PrimeTicket pt) {
    int i = reserved_list.count(pt);
    if (i > 1) {
      System.out.println(pt.toString() + " multiply in reserved list");
      System.exit(1);
    }
    if (i == 0) {
      System.out.println(pt.toString() + " not in reserved list");
      System.exit(1);
    }
    if (capacity >= pt.threshold) {
      setCapacity(capacity - pt.claim);
      reserved_list.popFront();
      assigned_list.add(pt);
      pt.setAssigned(null,-1);
      // System.out.println(pt.getIdentity() + " claim honored");
    }
    else {
      System.out.println(pt.toString() + " not assigned: too large threshold");
    }
  }

  ////////////////////////////////////////////
  private SemaphoreViewer semaphoreViewer = null;
  public void setViewer (SemaphoreViewer semaphoreViewer) {
    this.semaphoreViewer = semaphoreViewer;
  }


  private void model_changed() {
    if (semaphoreViewer!=null) {this.semaphoreViewer.modelChanged();}
  }

}

////////////////////////////////////////////////////////////////////////////
///////////// VIEW RELATED METHODS /////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

  /**
  The semaphores name + scenes package name construct the class name of the painer.
  So: painterClassName = scenePackageName + ".VIEW.SP_" + semaphoreName.
  The painter is an instance of this class, it is this class that defines
  how the semaphore must be painted in a Graphics2D (3D) context.

  private SemaphorePainter sem_painter = null;

  public SemaphorePainter getSemaphorePainter() {
    if (sem_painter == null) { sem_painter = _getSemaphorePainter(); }
    return sem_painter;
  }

  /**
  returns the custom semaphore painter or the default

  protected SemaphorePainter _getSemaphorePainter() {
    return new SemaphorePainterNull();
  }

  public void setSemaphorePainter(SemaphorePainter painter) {
    if (sem_painter != painter) { sem_painter = painter; }
  }

  private void model_changed() {
    if (sem_painter != null) sem_painter.modelChanged();
  }

}

  //////////////////////////////////////////////////////////////////////

  public Semaphore root0 = null;
  public Semaphore[] root1 = null;
  public Semaphore[][] root2 = null;
  public Semaphore[][][] root3 = null;
  public Semaphore[][][][] root4 = null;

  /////////////////////////////////////////////////////////////////////
  /////////////// CONSTRUCTION ////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////

  /**
  Called for cloning. Initialization determines if this is a clone
  or an original-clone.

  public Semaphore() throws RemoteException {}

  private Semaphore(int capacity) throws RemoteException {
    this(null,null,capacity,new Discipline());
  }

  private Semaphore(int capacity,Discipline discipline)
      throws RemoteException {
    this(null,null,capacity,discipline);
  }

  private Semaphore(String name,Scene scene,int capacity)
      throws RemoteException {
    this(name,scene,capacity,new Discipline());
  }

  /**
  Caled by the scene during its own construction. This is the original,
  and there is only one semaphore like this in the scene.

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline)
      throws RemoteException {
    initialize(name,scene,cap,discipline);
    root0 = this;
  }

  /**
  Caled by the scene during its own construction. This is the original.
  @param code is a subset of code and defines the structure of the original-clones.

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline,
        int[] code)
      throws RemoteException {
    this(name,scene,cap,discipline);
    this.code = code;
    switch (code.length) {
      case 1: createClones(code[0]);
      case 2: createClones(code[0],code[1]);
      case 3: createClones(code[0],code[1],code[2]);
      case 4: createClones(code[0],code[1],code[2],code[3]);
    }
  }

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline,
        int c1)
      throws RemoteException {
    this(name,scene,cap,discipline);
    this.code = new int[] { c1 };
    createClones(c1);
  }

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline,
        int c1,int c2)
      throws RemoteException {
    this(name,scene,cap,discipline);
    this.code = new int[] { c1,c2 };
    createClones(c1,c2);
  }

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline,
        int c1,int c2,int c3)
      throws RemoteException {
    this(name,scene,cap,discipline);
    this.code = new int[] { c1,c2,c3 };
    createClones(c1,c2,c3);
  }

  public Semaphore(
        String name,Scene scene,int cap,Discipline discipline,
        int c1,int c2,int c3,int c4)
      throws RemoteException {
    this(name,scene,cap,discipline);
    this.code = new int[] { c1,c2,c3,c4 };
    createClones(c1,c2,c3,c4);
  }


  private void initialize(
      String name,Scene_ImplBase scene,int cap,Discipline discipline) {
    this.name = name;
    this.scene = scene;
    this.max_capacity = cap;
    setCapacity(cap);
    setDiscipline(discipline);
    // System.out.println(
    //   "Semaphore " + name + " created in scene " + scene.getName());
  }

  //////////////////////////////////////////////////////////////////////////

  private Semaphore this_clone() {
    Semaphore semaphore = null;
    try { semaphore = new Semaphore(); }
    catch (RemoteException e) {}
    return semaphore;
  }

  /**
  Called for the scene by the original to create original-clones.

  private void createClones(int c0) {
    root1 = new Semaphore[c0];
    for(int i=0;i<c0;i++) {
      root1[i] = this_clone();
      root1[i].initializeClone(this,i);
    }
  }

  private void createClones(int c0,int c1) {
    root2 = new Semaphore[c0][c1];
    for(int i=0;i<c0;i++) {
      for (int j=0;j<c1;j++) {
        root2[i][j] = this_clone();
        root2[i][j].initializeClone(this,i,j);
      }
    }
  }

  private void createClones(int c0,int c1,int c2) {
    root3 = new Semaphore[c0][c1][c2];
    for(int i=0;i<c0;i++) {
      for (int j=0;j<c0;j++) {
        for (int k=0;k<c2;k++) {
          root3[i][j][k] = this_clone();
          root3[i][j][k].initializeClone(this,i,j,k);
        }
      }
    }
  }

  private void createClones(int c0,int c1,int c2,int c3) {
    root4 = new Semaphore[c0][c1][c2][c3];
    for(int i=0;i<c0;i++) {
      for (int j=0;j<c0;j++) {
        for (int k=0;k<c2;k++) {
          for (int l=0;l<c2;l++) {
            root4[i][j][k][l] = this_clone();
            root4[i][j][k][l].initializeClone(this,i,j,k,l);
          }
        }
      }
    }
  }

  /////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////

  /**
  subset of scene code, defines the sub-semaphre structure & indexing.

  protected int[] code = null;
  /**
  the index of this sub-semaphore in the scene-structure.

  protected int[] CODE = null;
  /**
  the original semaphore, the root of the structure.

  private Semaphore original = null;

    String this_name = name;
    if (code != null) {
      for (int i=0;i<code.length;i++) {
        this_name = this_name + "#" + code[i] + "#";
      }
    }
    if (CODE != null) {
      for (int i=0;i<CODE.length;i++) {
        this_name = this_name + "[" + CODE[i] + "]";
      }
    }
    return this_name;
  }


  private Discipline discipline_clone(Discipline d) {
    Discipline discipline = null;
    try {
      discipline = (Discipline)d.getClass().newInstance();
      // System.out.println("Discipline created! ");
    }
    catch (InstantiationException e) {
      System.out.println("InstantiationException :" + e.getMessage());
      System.exit(2);
    }
    catch (IllegalAccessException e) {
      System.out.println("IllegalAccessException :" + e.getMessage());
      System.exit(2);
    }
    return discipline;
  }

  private void initializeClone(Semaphore o) {
    this.original = o;
    Discipline discipline = discipline_clone(o.getDiscipline());
    initialize(o.getName(),o.getScene(),o.getMaxCapacity(),discipline);
  }

  /**
  Called after cloning by the original. This is for the scene.

  private void initializeClone(Semaphore o,int c0) {
    this.CODE = new int[] { c0 };
    initializeClone(o);
  }

  public void initializeClone(Semaphore o,int c0,int c1) {
    this.CODE = new int[] { c0,c1 };
    initializeClone(o);
  }

  public void initializeClone(Semaphore o,int c0,int c1,int c2) {
    this.CODE = new int[] { c0,c1,c2 };
    initializeClone(o);
  }

  public void initializeClone(Semaphore o,int c0,int c1,int c2,int c3) {
    this.CODE = new int[] { c0,c1,c2,c3 };
    initializeClone(o);
  }


 */


