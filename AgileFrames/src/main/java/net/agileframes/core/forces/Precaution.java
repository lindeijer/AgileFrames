package net.agileframes.core.forces;
import java.io.Serializable;
/**
 * <b>A precaution controls the speed using a condition that must be kept.</b>
 * <p>
 * A precaution is created and only visible in the context of a manoeuvre.
 * Its purpose is to control speed. A precaution may use the current state of
 * the machine and its creation parameters.
 * <p>
 * <b>How does it work?</b><br>
 * In the context of execution of a manoeuvre the condition of the Precaution
 * must be kept. If necessary, the precaution must describe a deceleration
 * in order to keep its condition.
 * <p>
 * When more than one precaution is active at the same time, the highest value of the deceleration
 * is applied. Observe that, in operational state, a precaution generates no direct interaction
 * between move-script and manoeuvre.
 * <p>
 * If the Precaution is not necessary anymore, use {@link #remove() remove} to remove it.
 * <p>
 * <b>Using Precautions:</b><br>
 * Some standard precautions exist, but the user may program his own precautions as well.
 * To make an extension of this class (creating a new user-defined Precaution), be sure
 * to define a constructor that sets the {@link #manoeuvre manoeuvre}, the
 * {@link #deceleration deceleration} and the method {@link #getDeceleration() getDeceleration}.
 *
 * @see Manoeuvre
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public abstract class Precaution implements Cloneable, Serializable {
  /** Indicates if this Precaution is still active. */
  protected boolean active = true;
  /** Maximal deceleration of this Precaution. Must be set in Constructor. */
  protected double deceleration = Double.NaN;
  /** Manoeuvre that owns this Precaution. Must be set in Constructor.*/
  protected Manoeuvre manoeuvre = null;
  /** Empty Constructor is not used */
  public Precaution() {}
  /** Deactivates this Precaution. */
  public void remove() { active = false; }
  /**
   * Calculates number between [0..{@link #deceleration deceleration}] to indicate
   * the prescribed deceleration.
   * <p>
   * If no deceleration is needed, <code>Double.NaN</code> (Not a Number) is returned.
   * If a safety-stop is required, 0 (zero) is returned.
   * <p>
   * This method is called by {@link Manoeuvre#updateCalculatedState(FuSpace,double,Manoeuvre) Manoeuvre.updateCalculatedState}.
   * <p>
   * The condition of a specific Precaution must be specified in this method.
   * <p>
   * Implementation is in the specific Precautions. <br>
   * For user-defined Precautions, be sure to overload this method.
   *
   * @return  prescribed deceleration between [0..{@link #deceleration deceleration}]<br>
   *                     0 if safety stop is required<br>
   *                     <code>Double.NaN</code> if no deceleration is needed.
   * @see Manoeuvre
   */
  public abstract double getDeceleration();
  /**
   * Checks if this Precaution is still active.
   * @see #remove()
   * @return <code><b>true</b></code> if this Precaution is active<br>
   *         <code><b>false</b></code> if this Precaution is removed
   */
  public boolean isActive() { return active; }
  /**
   * Creates a copy of this <code>Precaution</code> object.
   * Use this method to create a copy of the object if you want to make sure
   * all values and references will stay intact.
   * @param   manoeuvre   the manoeuvre that will own the clone.
   * @return  an object that is a copy of this <code>Precaution</code> object.
   * @see java.lang.Cloneable
   * @see java.lang.Object#clone()
   */
  public Object clone(Manoeuvre manoeuvre) throws CloneNotSupportedException {
    Precaution clone = (Precaution)super.clone();
    clone.manoeuvre = manoeuvre;
    return clone;
  }
}

