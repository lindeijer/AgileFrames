package net.agileframes.forces.mfd;

import net.jini.space.JavaSpace;
import net.jini.core.lookup.ServiceID;
import net.agileframes.server.ServiceProxy;
import net.agileframes.server.ServerImplBase;
import net.agileframes.brief.MoveBrief;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.brief.BooleanBrief;
import net.jini.core.entry.Entry;
import net.agileframes.core.forces.Machine;
import net.agileframes.core.server.Server;
import net.agileframes.core.forces.Rule;
import net.agileframes.traces.Move;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.core.services.Job;
import net.agileframes.core.forces.Machine.NotTrustedException;
import net.agileframes.core.forces.Constraint;
import net.agileframes.traces.SceneAction;
import net.agileframes.core.forces.MFDriver;
/**temp**///import com.agileways.traces.scene.ClaimFrame;

/**
Service of a Machine. accepts jobs and produces a sequence of moves
possible with the help of scenes. Is serializebe.
*/


public class ActorProxy extends ServiceProxy
      implements net.agileframes.core.forces.Actor,
                 MFDriver,
                 Runnable { // Serializable

  JavaSpace moveSpace = null; //
  ServiceID driverID = null;  // the serviceID the machines driver listens to
  ServiceID actorID = null;   // the serviceID the actor was uploaded with
  public Machine machine = null;
  /**temp**///public ClaimFrame claimFrame = new ClaimFrame();

  /**
  @param machine
  @param driverID of the machine, this is the destination-id of movebriefs (may be identical to machineID)
  @param actorID of the actor, this is the serviceID the actor was uploaded with
  @param moveSpace through which the machine/driver and actor communicate
  */
  public ActorProxy(Machine machine,ServiceID driverID,ServiceID actorID,JavaSpace moveSpace) {
    super((Server)machine);
    this.driverID = driverID;
    this.actorID = actorID;
    this.moveSpace = null;   // dont use movespace at all
    this.machine = machine;

  }

  public ActorProxy(){}

  public MFDriver getMFDriver() { return this; }

  /////////////// interface actor /////////////////////////////

  public boolean acceptJob(ServiceID basicServerID,Job job) throws NotTrustedException { return false; }

  public boolean acceptJob(SceneAction sceneAction,Object service) {
    this.service = service;
    this.sceneAction = sceneAction;
    new Thread(this).start();
    return true;
  }

  SceneAction sceneAction;

  public void run() {
    System.out.println("ActorProxy is running to execute sceneAction=" + sceneAction.getClass());
    this.sceneAction.assimilate(this);
    this.sceneAction.execute();
  }


  private Object service;

  public Object getService() {
    return service;
  }

  ////////////////////////////////////////////////////////////////////

  public void begin(Move move) {
    MoveImplBase moveIB = (MoveImplBase)move;
    this.acceptMove(moveIB.serviceID,moveIB.trajectory,moveIB.rules);
  }

  ////////////////////////////////////////////////////////////////////

  public synchronized boolean acceptTrajectory(ServiceID id,Trajectory trajectory) {
    return this.acceptMove(id,trajectory,null,null);
  }

  public synchronized boolean acceptRule(ServiceID id,Rule rule) {
    return this.acceptMove(id,null,new Rule[]{rule},null);
  }

  public synchronized boolean acceptConstraint(ServiceID id,Constraint constraint) {
    return this.acceptMove(id,null,null,new Constraint[]{constraint});
  }

  ///////////////////////////////////////////////////////////////////

  /**
  Deprecated
  */
  public synchronized boolean acceptMove(ServiceID id,Trajectory trajectory,Rule[] rules) {
    return this.acceptMove(id,trajectory,rules,null);
  }

  public synchronized boolean acceptMove(ServiceID id,Trajectory trajectory,Rule[] rules,Constraint[] constraints) {
    boolean accepted = false;
    BooleanBrief moveAcceptedReply = null;
    if (moveSpace == null) { // it is
      try {
        //System.out.println("acceptMove in actor to call machine.acceptMove");
        moveAcceptedReply = this.machine.acceptMove(id,trajectory,rules,null);
        accepted = moveAcceptedReply.value.booleanValue();
        //System.out.println("acceptMove in actor called machine.acceptMove successfully");
      } catch(Exception e) {
        System.out.println("Error in ActorProxy.acceptMove:"+e.getMessage());
        e.printStackTrace();
      }
    }
    else {
      try {
        MoveBrief moveBrief = new MoveBrief(id,driverID,actorID,trajectory,null);
        moveSpace.write(moveBrief,null,100000);
        System.out.println("about to moveReply...");
        moveAcceptedReply = (BooleanBrief)moveSpace.take((Entry)moveReplyTempl,null,Long.MAX_VALUE);
        System.out.println("...moveReply done!!");
        accepted = moveAcceptedReply.value.booleanValue();
      } catch (Exception e) {
        System.out.println("Exception in ActorProxy.acceptTrajectory: " + e.getMessage());
      }
    }
    return accepted;
  }

  BooleanBrief moveReplyTempl = new BooleanBrief(driverID,null);



/*

  // overload toString
  private Object scene_object = null;
  SceneAction scene_action = null;
  SceneAction previous_scene_action = null;
  SceneAction next_scene_action = null;

  public boolean findScene(String name) {
    Entry[] serverAttributes = new Entry[]
      { new Name(name) };
    Class[] serverClasses = new Class[]
      { AgileFrames.TRACES.MissionControl.class };
    ServiceTemplate template =
      new ServiceTemplate(null,serverClasses,serverAttributes);
    try {
      scene_object = getRegistrar().lookup(template);
    }
    catch (RemoteException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(2);
    }
    if (scene_object != null) {
        System.out.println("scene " + name + " found.");
        return true;
    }
    else {
      System.out.println("scene " + name + " not found.");
    }
    return false;
  }

  public MissionControl getMissionControl(String name) {
    if (findScene(name)) {
      return (MissionControl) scene_object;
    }
    return null;
  }

 */

}