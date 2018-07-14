package net.agileframes.traces;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.entry.Entry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.forces.TransformEntry;
import net.agileframes.server.AgileSystem;
/**
 * <b>Remote listener that waits for other Scenes to come up.</b>
 * <p>
 * This class is needed because Scenes cannot implement the RemoteEventListener
 * interface (because they are Server).<br>
 * The RemoteSceneListener is used by super-scenes that need to be notified if
 * (one of) their sub-Scenes appear, disappear or change. In this case notify(RemoteEvent)
 * will be called.
 * @author  H.J. wierenga
 * @version 0.1
 */
public class RemoteSceneListener implements RemoteEventListener {
  //-- Attributes --
  private ServiceTemplate[] templates = null;
  private boolean[] isAvailable = null;
  private Scene superScene = null;

  //-- Constructors
  /** empty Constructor. Not used. */
  public RemoteSceneListener() throws RemoteException {}
  /**
   * The default Constructor.<p>
   * Exports this object (using UnicastRemoteObject.exportObject). Sets
   * parameters. Creates the listener-structures to listen for the specified
   * scenes.<br>
   * The scene for which this object has to listen are specified by two things:<br><ul>
   * <li> The class-name of the scene
   * <li> The position of the scene
   * </ul>
   * @see   net.agileframes.server.AgileSystem#registerServiceListener(ServiceTemplate, RemoteEventListener)
   * @param superScene        the superScene on behalf of which this object is listening
   * @param subSceneClasses   an array of all the classes this objec t has to listen for
   * @param subScenePositions an array of all the positions(transforms) of the scenes
   */
  public RemoteSceneListener(Scene superScene, Class[] subSceneClasses, FuTransform[] subScenePositions) throws RemoteException {
    UnicastRemoteObject.exportObject(this);
    System.out.println("Exported: "+this.toString());

    this.superScene = superScene;

    if (subSceneClasses != null) {
      Entry[] entries = new Entry[subSceneClasses.length];
      templates = new ServiceTemplate[subSceneClasses.length];
      isAvailable = new boolean[subSceneClasses.length];
      for (int i = 0; i < subSceneClasses.length; i++) {
        templates[i] = new ServiceTemplate(
          null,
          new Class[] {net.agileframes.core.traces.Scene.class},
          new Entry[] {new TransformEntry(subScenePositions[i])}
        );
        AgileSystem.registerServiceListener(templates[i], this);
        // it is needed to make a service-listener for every sub-scene
        // because if you add multiple entries to a template, the Registrar
        // will only notify us if a scene is found that matches ALL entries
        // (there doesnot exist an OR-serach, only AND-search)
      }
    }
  }
  //-- Methods --
  /**
   * Notifies this object that one of the scenes (dis)appeared or changed.<p>
   * Sets the subScene to the superScene if the scene appeared. Otherwise
   * removes the subScene from the superScene.
   * @see   net.agileframes.core.traces.Scene#setSubScene(Scene,int,RemoteSceneListener)
   * @param re  the remote-event (ignored)
   */
  public void notify(RemoteEvent re) {
    //System.out.println("notified!!");
    if (templates != null) {
      for (int i = 0; i < templates.length; i++) {
        Object obj = AgileSystem.lookup(templates[i]);
        if ( (obj != null) && (obj instanceof Scene) ) {
          Scene subScene = (Scene)obj;
          if (!isAvailable[i]) {
            System.out.println("Found subScene("+i+")");
            isAvailable[i] = true;
            try { superScene.setSubScene(subScene, i, this); }
            catch (Exception e) { e.printStackTrace(); }
         }
        } else {
          if (isAvailable[i]) {
            System.out.println("Subscene("+i+") is gone!");
            isAvailable[i] = false;
            try { superScene.setSubScene(null, i, this);}
            catch (Exception e) { e.printStackTrace(); }
          }
        }
      }
    }
    //etcetera
  }
}