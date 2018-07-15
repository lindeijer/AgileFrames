package net.agileframes.services;
import java.io.Serializable;

import net.agileframes.core.services.Job;
/**
 * <b>Most basic possible implementation of a Job.</b>
 * <p>
 * A Job that consists of a description only. The description can be used -for
 * example- to pick the right SceneAction to perform the Job. The description
 * can be the exact name of the SceneAction.
 * @see net.agileframes.core.traces.Scene
 * @see net.agileframes.core.traces.SceneAction
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class ActionJob extends Job implements Serializable {
  /** The description of the Action to be performed by this Job. */
  protected String action;
  /**
   * Constructor with a descrption of this Job.
   * @param action  the description
   */
  public ActionJob(String action) { this.action = action; }
  /**
   * Returns the description belonging to this Job.
   * @return the description
   */
  public String getDescription() { return action; }
}