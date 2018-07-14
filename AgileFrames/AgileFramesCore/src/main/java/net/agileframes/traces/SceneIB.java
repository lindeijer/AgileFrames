package net.agileframes.traces;

import net.jini.core.transaction.Transaction;
import net.agileframes.core.traces.Ticket;
import net.agileframes.server.ServerIB;
import net.agileframes.server.AgileSystem;
import net.jini.core.lookup.ServiceID;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.traces.SemaphoreRemote;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.MachineRemote;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.vr.Avatar;
import net.agileframes.core.vr.BodyRemote;
import net.agileframes.core.vr.Body;
import net.agileframes.core.vr.SceneAvatarFactory;
import net.agileframes.forces.TransformEntry;
import net.agileframes.traces.RemoteSceneListener;
import net.agileframes.traces.JoinSceneAction;

/**
 * <b>The basic implemenation of the Scene</b>
 * <p>
 * Extend this class when defining your own Scenes.
 * 
 * @see net.agileframes.core.traces.Scene
 * @author D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class SceneIB extends ServerIB implements Scene/* , Cloneable */ {
	// set in initialize of Scene-definition
	/** The moves belonging to this Scene */
	protected Move[] moves;
	/** The sceneActions belonging to this Scene */
	protected SceneAction[] sceneActions;
	/** The transform of this Scene */
	protected FuTransform transform;
	/** The semaphores belonging to this Scene */
	protected Semaphore[] semaphores;
	/** The logisticPositions belonging to this Scene */
	protected LogisticPosition[] logisticPositions;

	/**
	 * Indicates if this Scene is changed.
	 * <p>
	 * Should be set true if in run-time moves are added. Changes will be drawn in
	 * virtuality when sceneAvatar.setState() is called.
	 */
	protected boolean sceneChanged = false;

	// is set in setSuperScene():
	private Scene superScene = null;
	/**
	 * An array of the sub-scenes belonging to this Scene.
	 * <p>
	 * The subScenes in this array will be set as soon as they are available. When
	 * they are not available anymore, they will be set to null. All this is done by
	 * the RemoteSceneListener's method notify(re).
	 * 
	 * @see RemoteSceneListener
	 */
	protected Scene[] subScenes = null;
	private RemoteSceneListener listener = null;// set in uploadScene, will call setSubScenes if available
	/**
	 * An array of the class-names of the sub-scenes belonging to this Scene.
	 * <p>
	 * Must be set in the constructor of the specific Scene. This array is needed to
	 * lookup the Scenes by the RemoteSceneListener.<br>
	 * If this Scene does not have any sub-scenes then this array should be null.
	 * 
	 * @see RemoteSceneListener
	 */
	protected Class[] subSceneClasses = null;
	/**
	 * An array of the positions of the sub-scenes belonging to this Scene.
	 * <p>
	 * Must be set in the constructor of the specific Scene. This array is needed to
	 * lookup the Scenes by the RemoteSceneListener.<br>
	 * If this Scene does not have any sub-scenes then this array should be null.
	 * 
	 * @see RemoteSceneListener
	 */
	protected FuTransform[] subScenePositions = null;

	/** Empty Constructor. Not used. */
	public SceneIB() throws java.rmi.RemoteException {
	}

	/**
	 * Default Constructor.
	 * <p>
	 * Calls super, sets name, initializes and uploads this Scene.
	 * 
	 * @see net.agileframes.server.ServerIB#ServerIB(String,ServiceID)
	 * @see #uploadScene()
	 * @see #initialize()
	 * @param name
	 *            the name of this Scene.
	 */
	public SceneIB(String name) throws java.rmi.RemoteException {
		super(name, null);
		initialize();// IMPORTANT: always FIRST initialize THEN uploadScene
		uploadScene();
	}

	/**
	 * Uploads this Scene to the Jini Lookup Services.
	 * <p>
	 * Creates the RemoteSceneListener and check for sub-scenes already
	 * available.<br>
	 * Sets the final sub-Scene positions by concatenating them with this Scene's
	 * own transform.<br>
	 * Register this Scene-Service by AgileSystem with as attributes the name, a
	 * SceneAvatarFactory (for creating the avatar of this Scene's body in a remote
	 * virtuality) and a transform-entry (for making the position of this scene
	 * available for the outside-world).
	 * 
	 * @see #transform
	 * @see net.agileframes.core.forces.FuTransform#transformT1T2(FuTransform,FuTransform)
	 * @see RemoteSceneListener#RemoteSceneListener(Scene, Class[], FuTransform[])
	 * @see net.agileframes.core.vr.SceneAvatarFactory
	 * @see net.agileframes.forces.TransformEntry
	 * @see AgileSystem#registerService(Server,ServiceID,Object,Entry[])
	 */
	public void uploadScene() {
		if (subScenePositions != null) {
			subScenes = new Scene[subScenePositions.length];
			for (int i = 0; i < subScenePositions.length; i++) {
				subScenePositions[i] = subScenePositions[i].transformT1T2(transform, subScenePositions[i]);// right
																											// order? or
																											// should it
																											// be (t,
																											// transform)
																											// ??
			}
			// it is needed to create the listener in a different class beacuse
			// it looks like, it is not allowed to be a ServiceListener and
			// a Service at the same time...
			try {
				listener = new RemoteSceneListener(this, subSceneClasses, subScenePositions);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listener.notify(null);
			System.out.println("Created RemoteSceneListener that will look for sub-scenes");
		}
		System.out.println("registering service");
		try {
			Object service = UnicastRemoteObject.toStub(this);
			Entry[] attributeSets = new Entry[] { new Name(this.getName()), new SceneAvatarFactory(),new TransformEntry(transform) };
			AgileSystem.registerService(this, null, service, attributeSets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// inherited from Scene
	public boolean getLifeSign() throws java.rmi.RemoteException {
		return true;
	}

	/**
	 * Initializes the Scene.
	 * <p>
	 * Should be implemented in the User-defined Scenes. Should create the
	 * semaphores, scene-actions, logistic-positions and moves.
	 */
	public void initialize() throws java.rmi.RemoteException {
	}

	////////////////////////////////////////////////////////////////////////////
	// inherited from Scene
	public Transaction getTransaction(Ticket[] tickets) {
		return AgileSystem.getTransaction();
	}

	////////////////////////////////////////////////////////////////////////
	/** Not implemented. */
	protected void add(Move[] moves) {
		// moves[i].setScene(this);
		// this.moves[counter+i] = moves[i];
	}

	/** Not implemented. */
	protected void add(Semaphore[] semaphores) {
		// semaphores[i].setScene(this);
		// this.semaphores[counter+i] = moves[i];
	}

	/** Not implemented. */
	protected void add(Move[][] moves) {
		// moves[i][j].setScene(this);
		// this.moves[counter+i*maxj+j] = moves[i][j];
	}

	/** Not implemented. */
	protected void add(Semaphore[][] semaphores) {
		// semaphores[i][k].setScene(this);
		// this.semaphores[counter+i*maxj+j] = moves[i][j];
	}

	/**
	 * Makes a clone of a scene-action in this Scene and returns it.
	 * <p>
	 * Searches a SceneAction that matches the given description.<br>
	 * <b><i>Make sure that after calling this method, you should call setActor on
	 * the SceneAction!</b></i> Registers the SceneAction and the Actor so that when
	 * the actor dies, the Scene can take care of destroying the SceneAction.<br>
	 * The name of the SceneAction is set in its Constructor. It is a convention
	 * that with parametrized SceneActions dots (.) and underscores(_) are being
	 * used. For example:<br>
	 * <blockquote>FromAToBAction_1.0.3</blockquote>
	 * 
	 * @see net.agileframes.core.traces.SceneAction#setActor(Actor)
	 * @param name
	 *            the description of the SceneAction to download
	 * @param actor
	 *            the actor that requests the download
	 * @return the requested SceneAction, or, if not available, null
	 */
	public synchronized SceneAction getSceneAction(String name, Actor actor) throws java.rmi.RemoteException {
		SceneAction action = null;
		SceneAction clone = null;
		for (int i = 0; i < sceneActions.length; i++) {
			if (sceneActions[i].getName().equals(name)) {
				action = sceneActions[i];
				break;
			}
		}
		if (action == null) {
			System.out.println(getName() + ": No sceneAction was found for :" + name);
			return null;
		}
		try {
			clone = (SceneAction) action.clone(actor);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		registerSA(actor, clone);
		clone.setActor(actor);// this doesnt help, because this actor will be serialized!
		return clone;
	}

	// inherited from Scene
	public synchronized FuTrajectory[] getSceneTrajects() throws RemoteException {
		if (moves == null) {
			return new FuTrajectory[] {};
		}
		FuTrajectory[] trajects = new FuTrajectory[moves.length];
		for (int i = 0; i < moves.length; i++) {
			trajects[i] = moves[i].getManoeuvre().getTrajectory();
		}
		return trajects;
	}

	/**
	 * Returns all the scene-actions vbelonging to this Scene.
	 * <p>
	 * 
	 * @return an array with all the scene-actions of this Scene.
	 */
	public SceneAction[] getSceneActions() {
		return sceneActions;
	}

	// inherited from Scene
	public synchronized SemaphoreRemote[] getSemaphores() {
		SemaphoreRemote[] sems = new SemaphoreRemote[semaphores.length];
		for (int i = 0; i < semaphores.length; i++) {
			sems[i] = semaphores[i];
		}
		return sems;
	}

	// inherited from Scene
	public boolean isChanged() throws RemoteException {
		if (!sceneChanged) {
			return false;
		} else {
			sceneChanged = false;
			return true;
		}
	}

	private ServiceID[] actorIDs = new ServiceID[] {};
	private SceneAction[] actions = new SceneAction[] {};
	private SceneAction[] prevActions = new SceneAction[] {};

	// action must be a cloned action, otherwise the actor is not set in its tickets
	private synchronized void registerSA(Actor actor, SceneAction clonedAction) {
		// if the clonedAction has a non-null super-action, we have to clone that one as
		// well
		SceneAction thisAction = clonedAction;
		Action superAction = clonedAction.superSceneAction;
		ServiceID thisID = null;
		try {
			thisID = actor.getServiceID();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		while ((superAction != null) && (superAction instanceof SceneAction)) {
			try {
				thisAction.superSceneAction = (SceneAction) ((SceneAction) superAction).clone(actor);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			thisAction = (SceneAction) superAction;
			superAction = thisAction.superSceneAction;
		}
		int index = -1;
		for (int i = 0; i < actorIDs.length; i++) {
			if (actorIDs[i].equals(thisID)) {
				index = i;
				break;
			}
		}
		if (index == -1) {// never seen this actor before
			ServiceID[] newActorIDs = new ServiceID[actorIDs.length + 1];
			SceneAction[] newActions = new SceneAction[actorIDs.length + 1];
			SceneAction[] newPrevActions = new SceneAction[actorIDs.length + 1];
			for (int i = 0; i < actorIDs.length; i++) {
				newActorIDs[i] = actorIDs[i];
				newActions[i] = actions[i];
				newPrevActions[i] = prevActions[i];
			}
			newActorIDs[newActorIDs.length - 1] = thisID;
			newActions[newActorIDs.length - 1] = clonedAction;
			newPrevActions[newActorIDs.length - 1] = null;
			actorIDs = new ServiceID[newActorIDs.length];
			actions = new SceneAction[newActorIDs.length];
			prevActions = new SceneAction[newActorIDs.length];
			for (int i = 0; i < actorIDs.length; i++) {
				actorIDs[i] = newActorIDs[i];
				actions[i] = newActions[i];
				prevActions[i] = newPrevActions[i];
			}
		} else {// we know this actor
			prevActions[index] = actions[index];
			actions[index] = clonedAction;
		}
	}

	/**
	 * Destroys the SceneAction(s) of an Actor.
	 * <p>
	 * Necessary when an Actor dies and without cleaning its scene-actions. Called
	 * in ActorIB or in superScene.destroySceneAction. Calls
	 * subScenes.destroySceneAction and kills the last 2 sceneactions in this scene
	 * that are downloaded by the actor.
	 * 
	 * @see net.agileframes.core.traces.SceneAction#dispose()
	 * @param actor
	 *            the actor of which the SceneActions should be destroyed
	 */
	public synchronized void destroySceneAction(Actor actor) throws RemoteException {
		if (subScenes != null) {
			for (int i = 0; i < subScenes.length; i++) {
				if (subScenes[i] != null) {
					subScenes[i].destroySceneAction(actor);
				}
			}
		}
		SceneAction action = null;
		ServiceID actorID = null;
		try {
			actorID = actor.getServiceID();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < actorIDs.length; i++) {
			if (actorIDs[i].equals(actorID)) {
				// it is necessay to destroy the previous action because we are not certain that
				// its exit-ticket is freed
				if (prevActions[i] != null) {
					prevActions[i].dispose();
				} else {
					System.out.println("Scene.destroySceneAction: Actor not known");
				}
				if (actions[i] != null) {
					actions[i].dispose();
				} else {
					System.out.println("Scene.destroySceneAction: Actor not known");
				}
				break;
			}
		}
	}

	// inherited from Scene
	public void setSubScene(Scene subScene, int index, RemoteSceneListener listener) throws RemoteException {
		if (listener != this.listener) {
			System.out.println("WARNING: SceneIB.setSubScene(): Only the RemoteSceneListener may set subScenes.");
			System.out.println("WARNING: This call will be ignored.");
			return;
		}
		// System.out.println("SubScene "+index+" set.");
		this.subScenes[index] = subScene;
		if (subScene != null) {
			subScene.setSuperScene(this);
		}
		setSAs();
	}

	/**
	 * Sets the (dynamic) SceneActions of this Scene.
	 * <p>
	 * To be implemented in specific user-defined (super-)Scenes. When Scene-Actions
	 * are defined where sub-Scenes are needed, they do not become valid until the
	 * sub-Scenes are available. This method is called in setSubScene().
	 * 
	 * @see #setSubScene(Scene,int,RemoteSceneListener)
	 */
	protected void setSAs() {
		// to be overloaded
	}

	// inherited from Scene
	public void setSuperScene(Scene superScene) {
		if ((this.superScene != null) && (!superScene.getClass().equals(this.superScene.getClass()))) {
			System.out.println("WARNING: SceneIB.setSuperScene(): SuperScene was set already.");
			System.out.println("WARNING: This call will be ignored.");
			return;
		}
		this.superScene = superScene;
	}

	// inherited from Scene
	public LogisticPosition getClosestLogisticPosition(FuSpace p) throws java.rmi.RemoteException {
		double minDist = Double.MAX_VALUE;
		LogisticPosition lp = null;
		try {
			// calculate own minimal distance
			if (logisticPositions != null) {
				for (int i = 0; i < logisticPositions.length; i++) {
					if (logisticPositions[i] != null) {
						double d = logisticPositions[i].location.stateDistance(p);
						if (d < minDist) {
							minDist = d;
							lp = logisticPositions[i];
						}
					}
				}
			}
			// calculate minimal distance of subScenes
			if (subScenes != null) {
				for (int i = 0; i < subScenes.length; i++) {
					if (subScenes[i] != null) {
						LogisticPosition subLp = subScenes[i].getClosestLogisticPosition(p);
						if (subLp != null) {
							double d = subLp.location.stateDistance(p);
							if (d < minDist) {
								minDist = d;
								lp = subLp;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("getClosestLP: p="+p+",lp="+lp);
		return lp;// can be NULL
	}

	// inherited from scene
	public LogisticPosition whereAmI(FuSpace p) throws java.rmi.RemoteException {
		System.out.println("Received WhereAmI request");
		try {
			// this implementation is not so good, esp. when you have a LARGE scene
			Scene topScene = this.getTopScene();
			return topScene.getClosestLogisticPosition(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// inherited from scene
	public synchronized SceneAction join(Actor actor, LogisticPosition lp) throws java.rmi.RemoteException {
		// return the sa to the requested lp
		System.out.println("SceneIB.join: actor="+actor+",lp="+lp);
		FuSpace p = actor.getMachine().getState();
		// create sa that claims right semaphore
		SceneAction sa = new JoinSceneAction(lp);
		try {
			sa = (SceneAction) sa.clone(actor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		registerSA(actor, sa);
		sa.setActor(actor);// this doesnt help, because this actor will be serialized!
		return sa;
	}

	// inherited from scene
	public Scene getTopScene() throws java.rmi.RemoteException {
		if (superScene != null) {
			return superScene.getTopScene();
		} else {
			return this;
		}
	}

	// inherited from scene
	public Scene find(String sceneActionName) throws java.rmi.RemoteException {
		System.out.println("find called");
		try {
			if (sceneActions != null) {
				for (int i = 0; i < sceneActions.length; i++) {
					if (sceneActions[i] != null) {
						System.out.println("sa " + i + " is called " + sceneActions[i].getName());
						if (sceneActions[i].getName().equals(sceneActionName)) {
							return this;
						}
					} else {
						System.out.println("sa " + i + " is null");
					}
				}
			}
			if (subScenes != null) {
				for (int i = 0; i < subScenes.length; i++) {
					if (subScenes[i] != null) {
						if (subScenes[i].find(sceneActionName) != null) {
							return subScenes[i];
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("find: return null");
		return null;
	}

	// ---------------inherited from body-----------------------
	public void addAvatar(Avatar avatar) throws RemoteException {
	}

	public void removeAvatar(Avatar avatar) throws RemoteException {
	}

	public FuSpace getState() throws RemoteException {
		return null;
	}

	public int getGeometryID() throws RemoteException {
		return 0;
	}

	public int getAppearanceID() throws RemoteException {
		return 0;
	}

	public FuSpace removeChild(BodyRemote child) throws RemoteException {
		return null;
	}

	public Body.StateAndAvatar addChild(BodyRemote child, FuSpace state) throws RemoteException {
		return null;
	}

	public void setParent(BodyRemote parent) throws RemoteException {
	}
}
