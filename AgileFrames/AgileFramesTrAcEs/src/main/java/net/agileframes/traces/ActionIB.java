package net.agileframes.traces;

import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.Scene;

import java.io.Serializable;

/**
 * <b>Implementation of Action</b>
 * <p>
 * To be Implemented in this Class rather then in SceneAction and Move.
 *
 * protected void execute(Ticket[]) {}
 * protected void run(Ticket[]) {}
 * protected void script() {}
 * protected void setActor(Actor actor) {}
 * protected void watch(interface Watchable) {}
 * (Interface Watchable should be created in net.agileframes.forces
 * and implemented by Signs and Flags, the only method of Watchable
 * should be addListener())
 * @author  D.G. Lindeijer
 * @version 0.1
 */

public abstract class ActionIB implements Action, Serializable {
  //--------------------------- Attributes ----------------------------------
  /** The Actor that owns this action. */
  protected Actor actor = null;
  /** The Scene in which this action takes place. */
  protected Scene scene = null;
  /** Action-Constant. State after Creation. */
  public static final int CREATED     = 0;
  /** Action-Constant. State after Initialization. */
  public static final int INITIALIZED = 1;
  /** Action-Constant. State when action is active. */
  public static final int ACTIVE      = 2;
  /** Action-Constant. State when action is executing. */
  public static final int EXECUTING   = 3;
  /** Action-Constant. State after action-script is completed. */
  public static final int POST_EXEC   = 4;
  /** Action-Constant. State after action has been active. */
  public static final int POST_ACTIVE = 5;

  //--------------------------- Constructor ---------------------------------
  /** Empty Constructor. Not used. */
  public ActionIB() {}

  //--------------------------- Methods -------------------------------------
  private int state = 0;
  /**
   * Sets state of this action. <p>
   * @see #CREATED
   * @see #INITIALIZED
   * @see #ACTIVE
   * @see #EXECUTING
   * @see #POST_EXEC
   * @see #POST_ACTIVE
   * @param s state to be set
   */
  protected void setState(int s) { state = s; }
  /**
   * Returns current state of this action. <p>
   * @see #CREATED
   * @see #INITIALIZED
   * @see #ACTIVE
   * @see #EXECUTING
   * @see #POST_EXEC
   * @see #POST_ACTIVE
   * @return  the state of this action
   */
  protected int getState() { return state; }
  /**
   * Checks if this action is created.
   * @see #CREATED
   * @return  <code><b>true</b></code  iff this action is created<br>
   *          <code><b>false</b></code iff this action is not created yet
   */
  protected boolean isCreated() { return (state == CREATED); }
  /**
   * Checks if this action is initialized.
   * @see #INITIALIZED
   * @return  <code><b>true</b></code  iff this action is initialized<br>
   *          <code><b>false</b></code iff this action is not initialized yet
   */
  protected boolean isInitialized() {
    System.out.println(" state="+state);
    return (state == INITIALIZED);
  }
  /**
   * Checks if this action is active.
   * @see #ACTIVE
   * @return  <code><b>true</b></code  iff this action is active<br>
   *          <code><b>false</b></code iff this action is not active yet
   */
  protected boolean isActive() { return (state == ACTIVE); }
  /**
   * Checks if this action is executing.
   * @see #EXECUTING
   * @return  <code><b>true</b></code  iff this action is executing<br>
   *          <code><b>false</b></code iff this action is not executing yet
   */
  protected boolean isExecuting() { return (state == EXECUTING); }
  /**
   * Checks if this action has finished executing.
   * @see #POST_EXEC
   * @return  <code><b>true</b></code  iff this action has finished executing<br>
   *          <code><b>false</b></code iff this action has not finished executing yet
   */
  protected boolean isPostExec() { return (state == POST_EXEC); }
  /**
   * Checks if this action has finished being active.
   * @see #POST_ACTIVE
   * @return  <code><b>true</b></code  iff this action has finished being active<br>
   *          <code><b>false</b></code iff this action has not finished being active yet
   */
  protected boolean isPostActive() { return (state == POST_ACTIVE); }
}
