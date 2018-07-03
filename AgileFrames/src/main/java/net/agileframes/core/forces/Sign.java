package net.agileframes.core.forces;
import java.io.Serializable;
/**
 * <b>A Sign is used for communication between Moves and SceneActions.</b>
 * <p>
 * Signs can be used in the context of Moves and SceneActions only. It provides
 * an uncomplicated tool to model event-oriented interaction.
 * <p>
 * <b>How does it work?</b><br>
 * In the context of a {@link Move#moveScript() moveScript} or a
 * {@link net.agileframes.core.traces.SceneAction#sceneActionScript() sceneActionScript}
 * a specific Sign can be broadcasted. Via the method
 * {@link net.agileframes.core.traces.SceneAction#watch(Sign) SceneAction.watch}
 * the actor can wait for the Sign to be broadcasted.
 * <p>
 * <b>Using Signs:</b><br>
 * Signs do not monitor a certain expression as {@link Flag flags} and
 * {@link Precaution precautions} do and thus are all the same. Creating
 * user-defined extensions is not necessary.
 * @see net.agileframes.core.traces.SceneAction
 * @see Move
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class Sign implements Serializable {
  //------------------------- Attributes -------------------------------------
  private boolean broadcasted = false;
  private Object[] listenerList = new Object[10];
  private int listenerCounter = 0;
  //-------------------------- Methods ----------------------------------------
  /**
   * Checks if this Sign is broadcasted.
   * @see #broadcast()
   * @return <code><b>true</b></code> if and only if this Sign is broadcasted
   */
  public boolean isBroadcasted() { return broadcasted; }
  /**
   * Broadcasts this Sign.<p>
   * This method will be called in {@link Move#moveScript() moveScript} or in
   * {@link net.agileframes.core.traces.SceneAction#sceneActionScript() sceneActionScript}
   * <p>
   * Notifies listeners.
   * @see #addListener(Object)
   */
  public void broadcast() {
    broadcasted = true;
    for (int i = 0; i < listenerCounter; i++) {
      synchronized(listenerList[i]) { listenerList[i].notify(); }
    }
  }
  /**
   * Adds a listener to this Sign.
   * <p>
   * The listeners will be notified when the Sign is broadcasted.
   * @see #broadcast()
   * @see net.agileframes.core.traces.SceneAction#watch(Sign)
   * @param addListener the listener-object that will be notified when the Sign is raised.
   */
  public void addListener(Object listenerObject) {
    listenerList[listenerCounter] = listenerObject;
    listenerCounter++;
  }
}
