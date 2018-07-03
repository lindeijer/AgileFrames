package net.agileframes.traces.viewer;
import java.awt.Graphics;
/**
 * <b>Interface for viewing the state of a Scene</b>
 * <p>
 * Other traces.view objects will call modelChanged and paint on the
 * SceneViewer, indicating that their state has changed.
 * @see     net.agileframes.traces.viewer
 * @see     net.agileframes.core.traces.Scene
 * @author  H.J. Wierenga
 * @version 0.1
 */
public interface SceneViewer {
  /**
   * View Frame and create all SemaphoreViewers.
   */
  public void jbInit() throws Exception;
  /**
   * Repaints the frame. Called by the system when needed.
   * The sizes of semaphores are made variable to the size of the frame.
   */
  public void repaint();
  /**
   * Paints all SemaphoreViewers. Called when model is changed.
   */
  public void modelChanged();
  /**
   * Calls repaint.
   * @param g the graphics on which should be painted
   */
  public void paint(Graphics g);
  /**
   * Returns the x-scale of the scene-viewer.<p>
   * @return the x-scale of the scene-viewer.
   */
  public double getXScale();
  /**
   * Returns the y-scale of the scene-viewer.<p>
   * @return the y-scale of the scene-viewer.
   */
  public double getYScale();
}