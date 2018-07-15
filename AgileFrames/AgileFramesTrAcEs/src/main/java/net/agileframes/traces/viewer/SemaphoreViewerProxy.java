package net.agileframes.traces.viewer;
/**
 * <b>Proxy for SemaphoreViewer to communicate with Semaphore.</b><p>
 * The semaphore for which this viewer will be created will get a reference
 * to this object, because in case that the SemaphoreViewer disappears, we do
 * not want to generate a RemoteException on the Semaphore.
 * @see     SemaphoreViewer
 * @see     SemaphoreViewerRemote
 * @see     net.agileframes.core.traces.Semaphore
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class SemaphoreViewerProxy implements java.io.Serializable {
  private SemaphoreViewerRemote viewer = null;
  /**
   * Default Constructor.<p>
   * @param viewer  the semaphore-viewer of which this is the proxy
   */
  public SemaphoreViewerProxy(SemaphoreViewerRemote viewer) {
    this.viewer = viewer;
  }
  /**
   * Called by Semaphore when its state is changed.<p>
   * Calls SemaphoreViewer.modelChanged. If connection to the viewer
   * is lost, this method will not do anything.
   * @see SemaphoreViewer#modelChanged()
   * @see net.agileframes.core.traces.SemaphoreRemote
   */
  public void modelChanged() {
    if (viewer != null) {
      try {
        viewer.modelChanged();
      } catch (java.rmi.RemoteException e) {
        //connectionLost
        viewer = null;
      }
    }
  }
}