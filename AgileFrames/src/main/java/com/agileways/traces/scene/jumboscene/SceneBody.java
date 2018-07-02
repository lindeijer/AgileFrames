package com.agileways.traces.scene.jumboscene;

import com.agileways.traces.scene.jumboscene.SceneState;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import javax.vecmath.*;

/**
 * SceneBody contains the state of the Scene.
 * All trajectories that need to be visualized should be added to the SceneBody
 * by using addTrajectory.
 *
 * Use getState as input for setState on SceneAvatar.
 * Use SceneAvatar to add the visualization of the trajectories to the 3d environment.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class SceneBody {
  public SceneState scene;
  private int index;

  /**
   * Constructor.
   * @param numberOfTrajectories    Total number of trajectories that need to be visualized.
   *                                If this parameter will be taken smaller than the actual
   *                                number of trajectories added by addTrajectory, errors
   *                                will occur.
   */
  public SceneBody(int numberOfTrajectories) {
    this.scene = new SceneState(new Trajectory[numberOfTrajectories]);
    this.index = 0;
  }

  /**
   * Gives the SceneState with all the visualized trajectories
   * @return  SceneState
   */
  public SceneState getState(){return scene;}

  /**
   * Adds a trajectory to the SceneState.
   * @param trajectory  Trajectory to be added
   */
  public void addTrajectory(Trajectory trajectory) {
    if (index>=scene.trajectories.length) {System.out.println("out of bounds in addTrajectory:"+index);}
    else {
      scene.trajectories[index] = trajectory;
      scene.trajectories[index].setTransform(trajectory.initialTransform);
    }
    index++;
  }

  /**
   * Sets resolution of the visualization.
   * Resolution is 5 dots/meter as default.
   * @param resolution  number of dots per meter of trajectory that need to be visualized
   */
  public void setResolution(int resolution) {scene.resolution = resolution;}

  /**
   * Sets color of the visualization.
   * Color is yellow by default.
   * @param color   Color3f
   */
  public void setColor(Color3f color) {scene.color = color;};

  /**
   * Sets transform of the visualization.
   * Transform is null by default.
   * @param transform   Transform
   */
  public void setTransform(Transform transform) {scene.transform = transform;}

  /**
   * Deletes the current state of the scene.
   */
  public void clear() {
    for (int i=0; i<index; i++) {
      scene.trajectories[i] = null;
    }
    index = 0;
  }
}