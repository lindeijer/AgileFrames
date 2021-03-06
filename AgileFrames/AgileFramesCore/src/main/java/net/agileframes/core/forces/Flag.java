package net.agileframes.core.forces;
import net.agileframes.core.forces.Manoeuvre;
import java.io.Serializable;
/**
 * <b>A Flag monitors the state of a certain boolean expression.</b>
 * <p>
 * Flags can be used in the context of Manoeuvres only.
 * The expression monitored by a Flag contains the creation parameters
 * of a specific Flag and the current state of the machine.
 * The condition must be described in {@link #evaluate() evaluate}.
 * <p>
 * <b>How does it work?</b><br>
 * When, in the context of execution of a manoeuvre the expression becomes true,
 * the flag will be raised. Via the method {@link Move#watch(Flag) Move.watch} the actor
 * can wait for the flag being raised.
 * <p>
 * <b>Using Flags:</b><br>
 * Some standard flags exist, but the user may program his own flags as well.
 * To make an extension of this class (creating a new user-defined Flag), be sure
 * to define a constructor that sets the {@link #manoeuvre manoeuvre} and the method
 * {@link #evaluate() evaluate}.
 *
 * @see Manoeuvre
 * @see Move
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public abstract class Flag implements Cloneable, Serializable {
  //---------------------- Attributes --------------------------------------
  private boolean raised = false;
  private Object[] listenerList = new Object[10];
  private int listenerCounter = 0;
  /** The manoeuvre which owns this flag, specified in constructor. */
  protected Manoeuvre manoeuvre = null;
  //---------------------- Methods -----------------------------------------
  /**
   * Evaluates boolean expression and raises the Flag if expression becomes true.
   * This method is called by {@link Manoeuvre#updateCalculatedState(FuSpace,double,Manoeuvre) Manoeuvre.updateCalculatedState}.
   * <p>
   * The condition of a specific Flag must be specified in this method.
   * <p>
   * Implementation is in the specific Flags. <br>
   * For user-defined Flags, be sure to overload this method.
   * <p>
   * Evaluate should call {@link #raise() raise} if the condition is <b><code>true</code></b>.
   */
  public abstract void evaluate();

  /**
   * Raises this Flag.
   * <p>
   * Notifies listeners.
   * @see #evaluate()
   * @see #addListener(Object)
   */
  protected void raise() {
	  if (this.raised) {
		  return;
	  }
    this.raised = true;
    for (int i = 0; i < listenerCounter; i++) {
      synchronized (listenerList[i]) { listenerList[i].notifyAll(); }
    }
  }
  /**
   * Adds a listener to this Flag.
   * <p>
   * The listeners will be notified when the flag is raised.
   * @see #raise()
   * @see Move#watch(Flag)
   * @param addListener the listener-object that will be notified when the flag is raised.
   */
  public void addListener(Object flagListener) {
    for (int i = 0; i < listenerCounter; i++) { if (listenerList[i].equals(flagListener)) { return; } }
    listenerList[listenerCounter] = flagListener;
    listenerCounter++;
  }
  /**
   * Checks if this Flag is raised.
   * @see #raise()
   * @return <code><b>true</b></code> if and only if this Flag is raised
   */
  public boolean isRaised() { return raised; }
  /**
   * Creates a copy of this <code>Flag</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @param   manoeuvre   the manoeuvre that will own the clone.
   * @return  an object that is a copy of this <code>Flag</code> object.
   * @see java.lang.Cloneable
   * @see java.lang.Object#clone()
   */
  public Object clone(Manoeuvre manoeuvre) throws CloneNotSupportedException {
    Flag clone = null;
    clone = (Flag)clone();
    clone.manoeuvre = manoeuvre;
    return clone;
  }
}

