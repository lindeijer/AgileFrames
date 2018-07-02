package net.agileframes.core.forces;

import net.agileframes.core.server.Service;
import net.agileframes.core.forces.Trajectory;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.traces.Move;
import net.agileframes.core.services.Job;
import net.agileframes.core.forces.Machine.NotTrustedException;
import net.agileframes.traces.SceneAction;
import net.agileframes.core.forces.MFDriver;


/**
A machine-server actor-service should implement this interface. Such an
implementation can accept jobs assigned to the machine and translate them
into a sequence of moves for the machine to execute.

@see net.agileframes.forces.ActorProxy

 * @since AgileFrames 1.0.0
 * @author Lindeijer, Evers
 * @version 0.0.1

*/

public interface Actor extends Service { // serializable !!


  /**
  Accepts a job assigned to the actors machine by a basicServer. The actor must
  translate the job into a sequence of moves the machine can execute. The actor may
  delegate the translation to an external process such as a scene-action.
  @param basicServerID the serviceID of the the basic server that assigned the job to the machine.
  @param job assigned to the machine by the basic-server
  @throws NotTrustedException iff the serviceID or the job is not trusted.
  */
  public boolean acceptJob(ServiceID basicServerID,Job job) throws NotTrustedException ;


  public Object getService();
  public boolean acceptJob(SceneAction sceneAction,Object service);

  /**
  Accepts a trajectory to be executed by the machine.
  Due to the absence of rules nothing will be known about the result of the execution.
  @param serviceID of the calling process.
  @param trajectory to be executed by the machine
  @return true iff the trajectory is coherent wrt previously accepted trajectories.
  @throws NotTrustedException if the serviceID or the Trajectory is not trusted
  */
  public boolean acceptTrajectory(ServiceID serviceID,Trajectory trajectory) throws NotTrustedException ;

  /**
  Accepts rules to be evaluated by the machine.
  @param serviceID of the calling process.
  @param rules to be evaluated by the machine.
  @return true iff the trajectory is coherent wrt previously accepted trajectories.
  @throws NotTrustedException if the serviceID or the Trajectory is not trusted
  */
  public boolean acceptRule(ServiceID serviceID,Rule rule) throws NotTrustedException ;

  public boolean acceptConstraint(ServiceID serviceID,Constraint constraint) throws NotTrustedException ;

  /**
  Accepts a trajectory to be executed by the machine.
  Due to the absence of rules nothing will be known about the result of the execution.
  @param moveID the ServiceID of the calling move within the event-process.
  @param trajectory to be executed by the machine
  @param rules to be evaluated by the machine in the context of the trajectory.
  @return true iff the trajectory is coherent wrt previously accepted trajectories.
  @throws NotTrustedException if the serviceID or the Trajectory is not trusted
  */

  public boolean acceptMove(ServiceID moveID,Trajectory trajectory,Rule[] rules,Constraint[] constraints) throws NotTrustedException ;


  public MFDriver getMFDriver();

}