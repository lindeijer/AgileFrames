package com.agileways.traces.scene.jumboscene;

import net.agileframes.core.forces.State;
import java.lang.Cloneable;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;

import javax.vecmath.*;
import net.agileframes.vr.Color3D;

/**
 * State of the scene, containing all trajectories that need to be visualized.
 * Use together with SceneAvatar and SceneBody.
 *
 * @author Wierenga
 * @version 0.0.1
 */
public class SceneState extends State implements Cloneable {
  /** Trajectories that need to be visualized */
  public Trajectory[] trajectories;
  /** Color of the visualization. Default is yellow. */
  public Color3f color;
  /** Resolution in dots per meter of visualisation. Default is 5 dots/meter */
  public int resolution;
  /** Transform of the visualization. Default is null */
  public Transform transform;

  ////////////////////////////////Constructors////////////////////////////////////////////
  /**
   * Constructor.
   *
   * @param trajectories    Array of Trajectory: trajectories to be visualized
   * @param resolution      Resolution in dots/meter of trajectory
   * @param transform       Transform of visualization.
   * @param color           Color3f of visualization
   */
  public SceneState(Trajectory[] trajectories, int resolution, Transform transform, Color3f color) {
    this.trajectories = trajectories;
    this.resolution = resolution;
    this.transform = transform;
    this.color = color;
  }

  /** Constructor 2. Constructor with POSTransform in (0,0,0) */
  public SceneState(Trajectory[] trajectories, int resolution, Color3f color) {
    this(trajectories,resolution,new POSTransform(0,0,0), color);
  }

  /** Constructor 3. Constructor with POSTransform in (0,0,0), resolution=5 and color=yellow */
  public SceneState(Trajectory[] trajectories) {
    this(trajectories, 5, Color3D.yellow);
  }

  ///////////////////////////////////////////////////////////////////////////////////////
}