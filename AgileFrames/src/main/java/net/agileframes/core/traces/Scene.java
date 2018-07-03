package net.agileframes.core.traces;

import net.agileframes.core.server.Server;
import net.jini.core.transaction.Transaction;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.vr.Avatar;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.traces.RemoteSceneListener;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.vr.BodyRemote;

/**
 * <b>Interface for the traffic infrastructure.</b>
 * <p>
 * A scene offers a script infrastructure, where actors perform their
 * activities as executors of scripts. Scenes may comprise sub-scenes.
 * They are equipped with optional creation parameters to specify the
 * composition structure and semaphore capacities.
 * <p>
 * Typical elements of a scene are moves, semaphores, scene-actions and
 * sub-scenes.
 * <p>
 * <b>Inheritance:</b><br>
 * <code>Scene</code> inherits methods from two other interfaces:
 * {@link net.agileframes.core.server.Server Server} and {@link net.agileframes.core.vr.BodyRemote}.
 * The first one is used to let the scene function in a remote context,
 * the second to be able to visualize the scene.
 * <p>
 * <b>Stubs:</b><br>
 * Because this interface is a Remote interface, any class implementing this
 * interface should create its own Stub.
 * <p>
 * <b>Nested Scenes:</b><br>
 * As stated above, Scenes may consist of various sub-scenes, which may be
 * nested themselves. A tree of Scenes can be imagined. Scenes that are in the
 * same tree, are not necessary running on the same computer. Actually, one of
 * the reasons to create nested scenes is to run parts on different machines.<br>
 * The distributed Scene-structure must be functioning, even if some Scenes in the
 * Scene-tree are not available (i.e. because they are under construction or
 * not developed yet). To achieve this, all Scenes may be started independently,
 * but all Scenes that contain sub-Scenes should know the name and position of
 * its sub-Scenes.<br>
 * When a sub-Scene becomes available, <code>setSubScene</code> will be called
 * on the (super-)Scene. When a super-Scene has found one of its sub-Scenes, it
 * calls <code>setSuperScene</code> on that sub-Scene.<br>
 * These methods function in a remote context.
 * <p>
 * <b>Implementation:</b><br>
 * For a basic implementation of this class, see the
 * {@link net.agileframes.traces.SceneIB Scene Implementation Base}
 * (<code>SceneIB</code>). This class has all the functionality
 * needed for a scene. If a specific Scene is created,
 * it is advised to extend <code>SceneIB</code>
 * rather than create a brand new class implementing this interface.<br>
 * <code>SceneIB</code> also provides all the code to program nested Scenes.
 * @see SceneAction
 * @see Semaphore
 * @see net.agileframes.core.forces.Move
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public interface Scene extends Server, BodyRemote {
  /**
   * Returns a <code>Transaction</code> object for some tickets.<p>
   * A <code>Transaction</code> object is needed to do an atomic reserve operation
   * on tickets.
   * See the Jini-specifications for more details about transactions.
   * @see net.agileframes.traces.ticket.SetTicket
   * @param   tickets array of tickets for which the <code>Transaction</code> object is needed
   * @return  the <code>Transaction</code> object
   */
  public Transaction getTransaction(Ticket[] tickets) throws java.rmi.RemoteException;
  /**
   * Returns a specific <code>SceneAction</code>.
   * @param   name  the name or description of the <code>SceneAction</code>
   * @param   actor the actor that asks for the <code>SceneAction</code>
   * @return  the requested <code>SceneAction</code>
   */
  public SceneAction getSceneAction(String name, Actor actor) throws java.rmi.RemoteException;
  /**
   * Destroys a specific <code>SceneAction</code>.<p>
   * Normally this method is called when the actor-process died or is stopped
   * manually. This method should take care that all SceneActions that are
   * currently being performed by the specified actor should be destroyed.
   * The Semaphores claimed by the specified actor in these SceneActions
   * need to be freed again.
   * @see     Actor
   * @param   actor the actor that asks for the <code>SceneAction</code>
   * @return  the requested <code>SceneAction</code>
   */
  public void destroySceneAction(Actor actor) throws java.rmi.RemoteException;
  /**
   * Checks if the Scene is still available.<p>
   * This method always returns TRUE. It is used by the actor to
   * see if the scene is still reachable. If not, it will receive a
   * RemoteException, otherwise a TRUE.
   * @see     Actor
   * @return  <code><b>true</code></b> if this method is reached
   */
  public boolean getLifeSign() throws java.rmi.RemoteException;
  /**
   * Checks if the Scene is changed.<p>
   * Changing the Scene, means changing one or more elements in the Scene,
   * adding elements to the Scene or removing elements from the Scene.<br>
   * This method is needed to keep visualization up-to-date.
   * @see net.agileframes.core.vr.Virtuality
   * @return  <code><b>true</code></b>  if the Scene has been changed<br>
   *          <code><b>false</code></b> if the Scene has stayed the same
   */
  public boolean isChanged() throws java.rmi.RemoteException;
  /**
   * Returns all trajectories in this Scene.<p>
   * This method is needed to be able to visualize the trajectories of the Scene.
   * The trajectories are indirectly known (via Move and Manoeuvre).
   * @see net.agileframes.core.forces.Move
   * @see net.agileframes.core.forces.Manoeuvre
   * @return  an array of all trajectories defined in this scene
   */
  public FuTrajectory[] getSceneTrajects()  throws java.rmi.RemoteException;
  /**
   * Returns all semaphores in this Scene.<p>
   * This method is needed to be able to monitor the state of all Semaphores
   * in a particular Scene. It should not be used by SceneActions.
   * @return  an array of all semaphores defined in this scene
   */
  public SemaphoreRemote[] getSemaphores() throws java.rmi.RemoteException;
  /**
   * Sets the sub-scene of this Scene.
   * Needed if nested Scenes are being used. This method will be called by
   * a <code>RemoteSceneListener</code> when a subScene of this Scene has become
   * available.<br>
   * If the subScene is not available anymore, this method will be called
   * with parameter <code>null</code>.
   * @param subScene  the sub-Scene that has become available (<code>null</code> if the subScene is gone)
   * @param index     the index that the subScene got at initialization of this Scene
   * @param listener  the <code>RemoteSceneListener</code> that calls this method.
   * @see net.agileframes.traces.RemoteSceneListener
   */
  public void setSubScene(Scene subScene, int index, RemoteSceneListener listener) throws java.rmi.RemoteException;
  /**
   * Sets the super-scene of this Scene.
   * Needed if nested Scenes are being used. This method will be called by
   * the super-Scene of this Scene.<br>
   * @param superScene  the super-Scene that has found this Scene
   * @see #setSubScene(Scene,int,RemoteSceneListener)
   */
  public void setSuperScene(Scene superScene) throws java.rmi.RemoteException;
  /**
   * Returns the top-scene in the entire Scene-tree.<p>
   * If this Scene is not part of a nested structure, a reference to this
   * Scene must be returned.
   * @return  the top scene in the nested structure
   */
  public Scene getTopScene() throws java.rmi.RemoteException;
  /**
   * Returns the Scene that contains the specified SceneAction.<p>
   * Uses the links to super- and sub-Scenes to find a Scene with the specified
   * SceneAction.
   * @param sceneActionName the name or description of the inquired SceneAction
   * @return a reference to the inquired Scene. If the SceneAction was not found,
   * <code>null</code> must be returned.
   */
  public Scene find(String sceneActionName) throws java.rmi.RemoteException;
  /**
   * Answers which logistic position in the entire Scene-tree is closest to a
   * certain point in the function space.<p>
   * The position that is requested is NOT necessary in this scene. Links to
   * super- and sub-Scenes can be used to find the closest position.
   * @see     #getClosestLogisticPosition(FuSpace)
   * @param   p point in the function space which should be traced
   * @return  the position, somewhere in the entire Scene-tree, that is closest to point p
   */
  public LogisticPosition whereAmI(FuSpace p) throws java.rmi.RemoteException;
  /**
   * Tells which position in this scene is closest to a certain point in the function space.<p>
   * The position that is requested must be either in this Scene or in one of
   * its sub-Scenes(if available).
   * @see     #whereAmI(FuSpace)
   * @param   p point in the function space which should be traced
   * @return  the position that is closest to point p
   */
  public LogisticPosition getClosestLogisticPosition(FuSpace p) throws java.rmi.RemoteException;
  /**
   * Creates a Join-SceneAction that makes the actor enter this Scene.<p>
   * To be used when an actor wants to join a Scene. The Join-SceneAction
   * makes sure that the right Semaphores will be claimed.<br>
   * This method probably will be called during starting up.
   * @see #whereAmI(FuSpace)
   * @see Semaphore
   * @param actor   the actor that requests the Join-SceneAction
   * @param lp      the position to which the actor wants to go
   * @return  the requested Join-SceneAction
   */
  public SceneAction join(Actor actor, LogisticPosition lp) throws java.rmi.RemoteException;
}
