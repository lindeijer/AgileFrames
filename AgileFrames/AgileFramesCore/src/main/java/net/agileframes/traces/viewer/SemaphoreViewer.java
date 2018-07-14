package net.agileframes.traces.viewer;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.traces.SemaphoreRemote;
import java.rmi.server.UnicastRemoteObject;
/**
 * <b>Visualizes Semaphores with the colors indicating their capacities.</b><p>
 * This viewer also functions
 * in a remote context.<br>
 * The frame that will contain this semaphore-viewer must implement the interface
 * SceneViewer.<br>
 * The semaphore for which this viewer will be created will not get a direct reference
 * to this object, but a reference to this object's proxy (SemaphoreViewerProxy), because
 * in case that this viewer disappears, we do not want to generate a RemoteException
 * on the Semaphore.
 * @see     SemaphoreViewerProxy
 * @see     SemaphoreViewerRemote
 * @see     SceneViewer
 * @see     net.agileframes.core.traces.Semaphore
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class SemaphoreViewer extends Component implements SemaphoreViewerRemote {
  SemaphoreRemote semaphore = null;
  SemaphoreProperties semProperties = null;
  /** Indicates if the state of the viewer is changed. */
  public boolean changed = true;
  /** Indicates if the connection with the Semaphore is lost. */
  public boolean connectionLost = false;
  SceneViewer sceneViewer;

  /**
   * Default Constructor.<p>
   * Exports this viewer using UnicastRemoteObject.exportObject.
   * After this constructor, this semaphore-viewer should be set on the
   * semaphore using setSemaphoreViewer.
   * @see   net.agileframes.core.traces.Semaphore#setViewer(SemaphoreViewerProxy)
   * @param semaphore     the semaphore for which this viewer is created
   * @param semProperties the visualization properties for this viewer
   * @param sceneViewer   the scene-viewer in which this semaphore-viewer will be viewed.
   */
  public SemaphoreViewer(SemaphoreRemote semaphore, SemaphoreProperties semProperties, SceneViewer sceneViewer) throws java.rmi.RemoteException {
    try { UnicastRemoteObject.exportObject(this); }
    catch (Exception e) { e.printStackTrace(); }
    this.validate();
    this.sceneViewer = sceneViewer;
    this.semaphore = semaphore;
    this.semProperties = semProperties;
  }

  private int x, y, x2, y2, width, height;
  /**
   * Paints semaphore in right color on right position.<p>
   * Graphics g will only be used if no graphics was set using setGraphics.<br>
   * Uses the SemaphoreProperties for this semaphore.
   * The Scale will be obtained from the scene-viewer.<br>
   * Colors:<br>
   * <ul>
   * <li> Red: capacity = 0
   * <li> Green: capacity = 1
   * <li> Yellow: capacity = 2
   * <li> Orange: capacity = 3
   * <li> Magenta: capacity = 4
   * <li> Cyan: capacity = 5
   * <li> Blue: capacity > 5
   * <li> Gray: connection to semaphore lost
   * </ul>
   * @see   #setGraphics(Graphics)
   * @see   SemaphoreProperties
   * @see   SceneViewer#getXScale()
   * @see   SceneViewer#getYScale()
   * @see   net.agileframes.core.traces.SemaphoreRemote#getCapacity()
   * @param g the graphics on which the semaphore will be painted
   */
  public void paint (Graphics g) {
    Graphics graph = g;
    if (graphics != null) { graph = graphics; }
    if (changed) {
      int capacity = 0;
      if (connectionLost) { capacity = -1; }
      else {
        try {
          capacity = semaphore.getCapacity();
        } catch (java.rmi.RemoteException e) {
          connectionLost = true;
          capacity = -1;
        }
      }

      switch (capacity) {
        case -1: graph.setColor(Color.lightGray);break;
        case 0:graph.setColor(Color.red);break;
        case 1:graph.setColor(Color.green);break;
        case 2:graph.setColor(Color.yellow);break;
        case 3:graph.setColor(Color.orange);break;
        case 4:graph.setColor(Color.magenta);break;
        case 5:graph.setColor(Color.cyan);break;
        default:graph.setColor(Color.blue);break;
      }

      double xScale = sceneViewer.getXScale();
      double yScale = sceneViewer.getYScale();
      x = (int)(semProperties.x * xScale);
      y = (int)(semProperties.y * yScale);
      x2 = (int)(semProperties.x * xScale - semProperties.width * xScale/2);
      y2 = (int)(semProperties.y * yScale - semProperties.height * yScale/2);
      width = (int)(semProperties.width * xScale);
      height = (int)(semProperties.height * yScale);
//      System.out.println("semViewer: x="+x+"  y="+y+"  x2="+x2+"  y2="+y2+"  w="+width+"  h="+height+"  xscale="+xScale+"  yScale="+yScale);

      if (semProperties.shape == SemaphoreProperties.RECTANGLE) {
        graph.fillRect(x, y, width, height);
        graph.setColor(Color.black);
        graph.drawRect(x, y, width, height);
        changed = false;
      } else {
        graph.fillOval(x2, y2, width, height);
        graph.setColor(Color.black);
        graph.drawOval(x2, y2, width, height);
        changed = false;
      }
    }
  }

  private Graphics graphics = null;
  /**
   * Sets the graphics on which the viewer will be painted. <p>
   * If not set, the graphics given in the method paint(g) will be used.
   * @see   #paint(Graphics)
   * @param g the graphics to paint on
   */
  public void setGraphics(Graphics g) {
    this.graphics = g;
  }


  /**
   * Redraws Semaphore. Called when semaphore changed capacity.<p>
   * Called by SemaphoreViewerProxy.
   * @see SemaphoreViewerProxy#modelChanged()
   * @see net.agileframes.core.traces.SemaphoreRemote
   */
  public void modelChanged() throws java.rmi.RemoteException {
    changed = true;
    sceneViewer.modelChanged();
  }

  /**
   * Repaints SceneViewer.<p>
   * Empty method.
   */
  public void repaint(){
    //empty!
  }

}