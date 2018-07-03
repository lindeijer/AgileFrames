package net.agileframes.traces.viewer;
/**
 * <b>Interface for visualizing Semaphores.</b><p>
 * This is a remote interface with only one method.<br>
 * The modelChanged-method must be called by Semaphore when its state is changed.<br>
 * See SemaphoreViewer for more comments.
 * @see     SemaphoreViewer
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface SemaphoreViewerRemote extends java.rmi.Remote {
  /**
   * Redraws Semaphore. Called when semaphore changed capacity.<p>
   * @see net.agileframes.core.traces.SemaphoreRemote
   */
  public void modelChanged() throws java.rmi.RemoteException;
}