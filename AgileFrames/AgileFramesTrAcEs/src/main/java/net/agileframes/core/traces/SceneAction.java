package net.agileframes.core.traces;
import java.io.Serializable;

import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Sign;
import net.agileframes.traces.ActionIB;

/**
 * <b>A SceneAction is a description of activities in the form of a script on basis of scene elements.</b><p>
 * A SceneAction imports the necessary parts from the Scene (moves, semaphores)
 * and contains a list of instructions involving these parts (the script). The
 * script will be executed in order to get an actor from a certain known state
 * to another planned state.<br>
 * <p>
 * <b>Relation with Scene:</b><br>
 * SceneActions belongs to a Scene. It is advised to not use one SceneAction
 * to drive in more than one Scene.
 * <p>
 * <b>Extensions:</b><br>
 * This SceneAction class provides the methods to be used by any scene-action
 * The specific scene-actions for specific environments should extend this class and
 * override the constructor, the sceneActionScript and the initialize-method.<br>
 * <p>
 * <b>Creation and Running:</b><br>
 * In the constructor of a scene-action, the tickets, moves and signs of the
 * SceneAction should be defined, as well as some specific parameters if needed.<br>
 * When the SceneAction is requested from the Scene by an Actor, the SceneAction
 * needs to be cloned. Cloning the SceneAction, will make an exact copy of the
 * entire SceneAction. The clone then is sent to the Actor. Note that user-defined
 * parameters will not be cloned automatically! Only the ticket- sign- and
 * move-array will be cloned.<br>
 * After receiving the cloned SceneAction, the Actor has to call the method
 * <code>setActor(Actor)</code> to make itself known to the SceneAction.<br>
 * When the Actor feels it's time, it runs the SceneAction by calling <code>run</code>.
 * Before running the script, this method will first call <code>initialize</code>.
 * In this method user-defined parameters can be created. For example: in the
 * constructor is stated:<br><code>
 *   tickets[0] = new PrimeTicket(this, semaphore_parking_place_1);</code><br>
 * Then the initialize can contain:<br><code>
 *   ticParkingPlace1 = tickets[0];</code><br>
 * Which will make programming the script easier, especially when very much tickets
 * are involved.
 * <p>
 * <b>Communication:</b><br>
 * Signs, defined on the moves and other scene-action, are used to communicate
 * with this SceneAction.
 * <p>
 * @see Scene
 * @see Actor
 * @see Semaphore
 * @see Ticket
 * @see net.agileframes.core.forces.Move
 * @see net.agileframes.core.forces.Sign
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public abstract class SceneAction extends ActionIB implements Cloneable, Serializable {
  //------------------ Attributes ---------------------
  /**
   * Moves to be driven in this SceneAction.<p>
   * These moves will be cloned when the SceneAction will be cloned.<br>
   * Need to be defined in the constructor. Specific user-defined names
   * can be given in {@link #initialize() initialize}.
   */
  protected Move[] moves;
  /**
   * Tickets that need to be claimed while driving this SceneAction.<p>
   * These tickets will be cloned when the SceneAction will be cloned.
   * Need to be defined in the constructor. Specific user-defined names
   * can be given in {@link #initialize() initialize}.<p>
   * Tickets need to be defined both locally and on the remote scene-server
   * in order to be able to clear tickets (semaphores) on the remote-scene-server
   * in case of lost of contact with the machine or a deadlock.
   */
  protected Ticket[] tickets;
  /**
   * Tickets that need to be claimed or freed while driving this SceneAction.<p>
   * To be defined outside this SceneAction: reference is given in run or execute.<br>
   * These tickets may include references to semaphore located outside the Scene
   * which this action belongs to.
   */
  protected Ticket[] externalTickets;
  /** The Scene to which this SceneAction belongs. */
  public Scene scene = null;
  /** The super-action of this SceneAction. */
  public Action superSceneAction = null;
  /** The Actor that owns this SceneAction. */
  public Actor actor = null;
  private Ticket exitTicket = null;// exit-ticket of this sa, specified in finish(ticket)
  /**
   * Signs to be broadcasted in this SceneAction.<p>
   * These signs will be cloned when the SceneAction will be cloned.<br>
   * Need to be defined in the constructor. Specific user-defined names
   * can be given in {@link #initialize() initialize}.
   */
  protected Sign[] signs;
  private static boolean DEBUG = false;
  /** The name of this SceneAction. */
  protected String name;
  /** The end position of this SceneAction: only used with variable SceneActions. */
  protected LogisticPosition endPosition;
  /** The begin position of this SceneAction: only used with variable SceneActions. */
  protected LogisticPosition beginPosition;

  //------------------ Constructor --------------------
  /** Empty Constructor. Not used. */
  public SceneAction() {}
  /** Example Constructor. Overload for specific SceneActions. */
  public SceneAction(Scene scene, Action superSceneAction){
    this.scene = scene;
    this.superSceneAction = superSceneAction;
  }

  //------------------ Methods ------------------------
  
  public Scene getScene() {
	  return scene;
  }
  /**
   * The script of the SceneAction.<p>
   * Interaction of tickets, signs and moves.
   * List of commands that need to be executed.<br>
   * Needs to be implemented in the specific SceneActions.
   * @throws BlockException if there are problems with an operation on a ticket
   */
  protected abstract void sceneActionScript() throws BlockException;

  /**
   * Executes the SceneAction.<p>
   * Will not start a new Thread. Will not free any exitTickets.
   * Will not claim any external tickets.
   * @see #execute(Ticket[])
   */
  public void execute() { execute(null); }
  /**
   * Executes the SceneAction with external-tickets.<p>
   * Will not start a new Thread. <br>
   * Will use external tickets to claim or to free. These external
   * tickets can be the exit-tickets of the previous SceneAction.
   * @param externalTickets the external tickets that will be used in the script of this SceneAction
   * @see #execute()
   * @see #run(Ticket[])
   */
  public void execute(Ticket[] externalTickets) {
    this.externalTickets = externalTickets;
    setState(EXECUTING);
    initialize();
    try { sceneActionScript(); }
    catch ( Exception e) { e.printStackTrace(); }
    setState(POST_EXEC);
  }
  /**
   * Runs the SceneAction.<p>
   * Will start a new Thread. Will not free any exitTickets.
   * Will not claim any external tickets.
   * @see #run(Ticket[])
   */
  public void run() { run(null); }
  /**
   * Runs the SceneAction with external-tickets.<p>
   * Will start a new Thread. <br>
   * Will use external tickets to claim or to free. These external
   * tickets can be the exit-tickets of the previous SceneAction.
   * @param externalTickets the external tickets that will be used in the script of this SceneAction
   * @see   #run()
   * @see   #execute(Ticket[])
   */
  public void run(Ticket[] externalTickets) {
    this.externalTickets = externalTickets;
    setState(EXECUTING);// should be RUNNING
    Thread scriptThread = new Thread("SceneActionScriptThread@"+this.toString()){
      public void run() {
        try { sceneActionScript(); }
        catch (Exception e) { System.out.println(e.getMessage()); e.printStackTrace(); System.exit(2); }
      }
    };
    initialize();
    scriptThread.start();
    setState(POST_EXEC);
  }
  /**
   * Blocks until specified sign is raised.
   * <p>
   * This method registers this <code>SceneAction</code> as a listener with the
   * specified sign and will be notified when the specified sign is raised.
   * @see   #watch(Sign[])
   * @param sign  the sign to be watched
   */
  public void watch(Sign sign){ watch(new Sign[] {sign}); }
    /**
   * Blocks until specified signs are raised.
   * <p>
   * This method registers this <code>SceneAction</code> as a listener with the
   * specified signs and will be notified when one of the specified signs is raised.
   * @see   #watch(Sign)
   * @see   net.agileframes.core.forces.Sign#addListener(Object)
   * @see   net.agileframes.core.forces.Sign#isBroadcasted()
   * @param signs   the array of signs to be watched
   */
  public void watch(Sign[] signs) {
    boolean broadcasted = false;
    for (int i = 0; i < signs.length; i++) {
      signs[i].addListener(this);
      if (!broadcasted) { broadcasted = signs[i].isBroadcasted();}
    }
    while (!broadcasted) {
      try{ synchronized(this) { this.wait();  }  }
      catch (Exception e) {
        System.out.println("Exception in SceneAction.watch():"+e.getMessage());
        e.printStackTrace();
      }
      for (int i = 0; i < signs.length; i++) { if (!broadcasted) { broadcasted = signs[i].isBroadcasted();} }
    }
  }

  /**
   * Initializes this SceneAction.<p>
   * Should be used to create specific tickets and moves.
   */
  protected void initialize() {
    System.out.println("SceneAction.initialize() not implemented.");
  }
  /**
   * Creates a copy of this <code>SceneAction</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact. Will clone all the moves
   * belonging to this SceneAction.<br>
   * <b><i>Make sure that after cloning you use setActor(Actor)!</b></i>
   * @see     #setActor(Actor)
   * @see     net.agileframes.core.forces.Move#clone()
   * @param   actor   the actor that requests the clone.
   * @return  an object that is a copy of this <code>SceneAction</code> object.
   */
  public Object clone(Actor actor) throws CloneNotSupportedException{
    System.out.println("Cloning SceneAction "+this.toString());
    SceneAction clone = (SceneAction)clone();
    this.actor = actor;
    if (moves != null) {
      clone.moves = new Move[moves.length];
      for (int i = 0; i <   moves.length; i++ ) {  if (  moves[i] != null)   clone.moves[i] =     (Move)moves[i].clone();   }
    }
    if (tickets != null) {
      clone.tickets = new Ticket[tickets.length];
      for (int i = 0; i < tickets.length; i++ ) {  if (tickets[i] != null) clone.tickets[i] = (Ticket)tickets[i].clone(actor);   }
    }
    clone.setActor(actor);

    return clone;
  }

  /**
   * Returns exitTicket of this scene-action.<p>
   * The exit-ticket can be specified in every SceneAction by using finish.
   * The exit-ticket can be used to free the last ticket claimed in this
   * SceneAction in the next SceneAction (at the moment we leave the resource
   * controlled by that ticket's semaphore).
   * @see     #finish(Ticket)
   * @return  exitTicket of this scene-action
   */
  public Ticket getExitTicket() { return exitTicket; }
  /**
   * Sets exit-ticket of this Scene-Action.<p>
   * May be overloaded to give more functionality.
   * @see   #getExitTicket()
   * @param exitTicket  the exit-ticket of this SceneAction
   */
  protected void finish(Ticket exitTicket) {
    this.exitTicket = exitTicket;
  }

  /**
   * Sets the actor that owns this SceneAction.<p>
   * <b><i>Should always be called after cloning a SceneAction!.</b></i>
   * @see   #clone()
   * @param actor   the actor of this SceneAction
   */
  public void setActor(Actor actor) {
    if (DEBUG) System.out.println("setActor called on "+this.toString());
    this.actor = actor;
    if (moves != null) {
      for (int i = 0; i < moves.length; i++ ) {  if (moves[i] != null) moves[i].setActor(actor);  }
    }
  }
  /**
   * Kills this SceneAction.<p>
   * Is called on side of the Scene, not on side of the Actor. Is necessary in
   * order to be able to kill a SceneAction of an Actor that does not respond
   * any more (or for any other reason). <br>
   * Frees all tickets in this SceneAction and calls superSceneAction.dispose(),
   * if available.
   * @see Scene
   * @see Actor
   */
  public void dispose() {
    System.out.println("DISPOSING  "+this.toString());

    if (tickets != null) { for (int i = 0; i < tickets.length; i++ ) {
      try {
        if (DEBUG) if (tickets[i] != null) System.out.println("*D* SceneAction.dispose(): freeing ticket "+i+": "+tickets[i].toString());
        if (tickets[i] != null) tickets[i].free();
      }
      catch (Exception e) {
        System.out.println("Exception in SceneAction.dispose() while freeing tickets: "+ e.getMessage());
        e.printStackTrace();
        System.out.println("Exception ignored. ");
      }
    }}
    if ((superSceneAction != null) && (superSceneAction instanceof SceneAction)) {
      ((SceneAction)superSceneAction).dispose();
    }
  }

  /**
   * Returns end-position of this SceneAction, iff relevant.<p>
   * This method only is relevant if the SceneAction can decide itself what
   * will be the end position. For example, if there are 10 parking places,
   * the SceneAction might want to park in one of the empty places, no matter
   * which place that is.
   * @return the end-position of this SceneAction
   */
  public LogisticPosition getEndPosition() { return this.endPosition; }
  /**
   * Sets begin-position of this SceneAction.<p>
   * This method only is relevant if the SceneAction is constructed in such
   * a way that it can leave from more than one begin position.
   * @param the begin-position of this SceneAction
   */
  public void setBeginPosition(LogisticPosition beginPosition) { this.beginPosition = beginPosition; }
  /**
   * Returns the name of this SceneAction.<p>
   * @return the name of this SceneAction.
   */
  public String getName() { return name; }
  /**
   * Returns the specified sign belonging to this SceneAction.<p>
   * Necessary to be able to watch on one of this SceneAction's Signs.<p>
   * If this method is called with an index which is out of bounds, a <code>java.lang.NullPointerException</code>
   * will be thrown.
   * @see     #watch(Sign)
   * @return  a reference to the specified sign.
   */
  public Sign getSign(int i) { return signs[i]; }
  /**
   * Returns the specified move belonging to this SceneAction.<p>
   * If this method is called with an index which is out of bounds, a <code>java.lang.NullPointerException</code>
   * will be thrown.
   * @return  a reference to the specified move.
   */
  public Move getMove(int i) { return moves[i]; }
}
