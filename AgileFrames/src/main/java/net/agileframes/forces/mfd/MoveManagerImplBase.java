package net.agileframes.forces.mfd;

import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.TrajectoryState;
import net.agileframes.core.forces.Rule;
import net.jini.core.entry.Entry;

import net.agileframes.server.AgileSystem;
import net.agileframes.brief.MoveBrief;
import net.agileframes.brief.BooleanBrief;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.entry.Name;
import net.agileframes.server.ServerImplBase;
import net.agileframes.brief.MoveSpace;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.Constraint;
import net.agileframes.core.forces.Machine;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.forces.constraint.SafetyConstraint;
import net.agileframes.forces.MoveManager;
import net.agileframes.forces.flag.AbstractFlag;
import net.agileframes.forces.space.Obstacle;
import java.rmi.RemoteException;


/**
Gets Moves from the space and replys and checks for coherency.
Works together with ActorProxy, this one throws the MoveBriefs in the space.
This this should be declared somewhere in the mfd package.
*/


public class MoveManagerImplBase extends ServerImplBase implements Runnable,MoveManager {

  public MachineImplBase machine = null;
  public ServiceID actorID = null;
  public ServiceID driverID = null;

  /**
  @param machine moves are sent to
  @param the serviceID of the machines driver, the id the actor uses as move-brief destination
  */
  public MoveManagerImplBase(MachineImplBase machine,
                             ServiceID driverID,
                             ServiceID actorID,
                             boolean uploadActor,
                             boolean useMoveSpace) throws RemoteException{
    super("MoveManager@"+machine.getName(),driverID);
    // if driverID was null we are assigned another new id.
    this.machine = machine;
    initializeTrajectory();
    if (uploadActor) {
      this.driverID = this.serviceID;
      net.jini.space.JavaSpace moveSpace = MoveSpace.getMoveSpace();
      if (actorID == null) { actorID = AgileSystem.getServiceID(); }
      if (useMoveSpace) {
        if (moveSpace != null) {
          Thread moveAcceptorThread = new Thread(this);
          moveAcceptorThread.setName("moveAcceptorThread");
          moveAcceptorThread.start();
          System.out.println(moveAcceptorThread.getName() + " started for " + machine.getName());
        }
        else {
          System.out.println("moveAcceptorThread not started for " + machine.getName() + " because there was no moveSpace");
        }
      }
      ActorProxy actor = new ActorProxy(this.machine,driverID,actorID,moveSpace);
      // iff moveSpace is null the rmi on the MachineStub is used
      Entry[] attributes = { new Name(this.machine.name) };
      AgileSystem.registerService(this,actorID,actor,attributes);

      System.out.println(getName() + " uploaded an Actor.");
    }
    else {
      System.out.println(getName() + "'s Actor not uploaded and moveAcceptorThread not started.");
    }
  }

  /*
  The initial trajectory contains a Start trajectory containing a POS(0,0,0).
  Should be moved to MoveManager
  */
  protected void initializeTrajectory() {
    Trajectory start = new net.agileframes.forces.trajectory.Start(new POS(0,0,0));
    machine.trajectory = new Trajectory(new Trajectory[]{start});
    machine.trajectoryState = new TrajectoryState();
    machine.trajectoryState.state = machine.trajectory.compute(0.0f);
    machine.trajectoryState.state.u = 0;
    safetyObstacle.trajectory = start;
    safetyObstacle.horizon = start.initialEvolution + start.domain;
    machine.environmentInformation[0] = safetyObstacle;
  }

  /////////////////////////////////////////////////////////////////////

  /**
  take moveBriefs out of the briefSpace. When you do, then accept the brief iff
  possible and notify, the MFD-thread may be sleeping.
  */
  public final void run() {
    System.out.println("++++++++++++ MoveManager.run(), to download moveBriefs from the moveSpace provided by the actor ");
    MoveBrief moveBriefTmpl = new MoveBrief(null,driverID,actorID,null,null);
    //System.out.println("moveBriefTempl = "+ moveBriefTmpl.toString());
    while(true) {
      // System.out.print(getName() + " wants to take a new MoveBrief: ");
      MoveBrief moveBrief = (MoveBrief)MoveSpace.take(moveBriefTmpl);
      BooleanBrief acceptReply = null; // acceptMove(moveBrief);
      MoveSpace.write(acceptReply);
    }
  }

  ///////////////////////////////////////////////////////////////////////////

  /**
  Accepts a maneuver as passed to the machine iff at all possible to execute.
  */
  public synchronized BooleanBrief acceptMove(
      ServiceID serviceID,Trajectory trajectoryCandidate,
      Rule[] ruleCandidates,Constraint[] constraintCandidates)
        throws Machine.NotTrustedException {

    //
    // forget about the serviceID, it should be the actorID i think.
    String reason = "no reason";
    if (!this.isCoherentTrajectory(trajectoryCandidate)) {
      reason = "Incoherent trajectory in moveBrief for " + getName() + " accepted anyway";
      System.out.println(reason);
    }
    if (!this.isCoherentRules(ruleCandidates)) {
      System.out.println("Incoherent rules in moveBrief for " + getName());
    }
    if (!this.isCoherentConstraints(constraintCandidates)) {
      System.out.println("Incoherent rules in moveBrief for " + getName());
    }
    add(trajectoryCandidate);
    //
    // add(ruleCandidates);
    if (ruleCandidates!=null) {
      for(int i=0;i<ruleCandidates.length;i++) {
        if (ruleCandidates[i]!=null) {
          AbstractFlag rule = (AbstractFlag)ruleCandidates[i];
          rule.setTrajectory(trajectoryCandidate);
          machine.add((Rule)rule);
        }
      }
    }
    // add(constraintCandidates);
    if (constraintCandidates!=null) {
      for(int i=0;i<constraintCandidates.length;i++) {
        if (constraintCandidates[i]!=null) {
          AbstractFlag constraint = (AbstractFlag)constraintCandidates[i];
          constraint.setTrajectory(trajectoryCandidate);
          machine.add((Constraint)constraint);
          System.out.println(machine.getName()+" added a constraint=" + constraint.toString());
        }
      }
    }
    return new BooleanBrief(null,null,true,reason);
  }

  public BooleanBrief acceptTrajectory(ServiceID serviceID,Trajectory trajectory)throws Machine.NotTrustedException {
    return this.acceptMove(serviceID,trajectory,null,null);
  }

  public BooleanBrief acceptRule(ServiceID serviceID,Rule rule)throws Machine.NotTrustedException {
    return this.acceptMove(serviceID,null,new Rule[]{rule},null);
  }

  public BooleanBrief acceptConstraint(ServiceID serviceID,Constraint constraint) throws Machine.NotTrustedException{
    return this.acceptMove(serviceID,null,null,new Constraint[]{constraint});
  }

  ////////////////////////////////////////////////////////////////////////////
  //////////////////// coherency methods //////////////////////////////////

  /**
  Accept a move wrt the previously accepted moves
  @param move containing a trajectory and rules.
  @return true iff the trajectory and all the rules are coherent.
  */
  public boolean isCoherentMove(Trajectory trajectory,Rule[] rules,Constraint[] constraints) {
    if (!this.isCoherentTrajectory(trajectory)) {
      System.out.println("Incoherent trajectory in moveBrief for " + getName());
      return false;
    }
    if (!this.isCoherentRules(rules)) {
      System.out.println("Incoherent rules in moveBrief for " + getName());
      return false;
    }
    if (!this.isCoherentConstraints(constraints)) {
      System.out.println("Incoherent rules in moveBrief for " + getName());
      return false;
    }
    return true;
  }

  /**
  Accept a trajectory wrt the previously accepted trajectories
  Checks if candidate fits on the end of the current machine.trajectory
  @param candidate trajectory to be accepted.
  @return true iff the trajectory is coherent.
  */
  public boolean isCoherentTrajectory(Trajectory candidate){
    State s1 = null;
    try{
      s1 = machine.trajectory.getEnd();
    } catch (Exception e) {
        System.out.println("exception while <<State s1 = machine.trajectory.getEnd();>> in isCoherentTrajectory:"+e.getMessage());
        if (s1!=null) {System.out.println("s1="+s1.toString()+"  (POS)s1="+((POS)s1).toString()+"  machine.trajectory=");} else {System.out.println("s1=null");}
        if (machine.trajectory!=null) {machine.trajectory.toOutput("error-->  ");} else {System.out.println("machine.trajectory=null");}
        e.printStackTrace();
        s1 = new POS(0,0,0);
    }
    State s2 = candidate.getBegin();
    float d = s1.distance(s2);
    if (d>0.1) {//return false;}
      //System.out.println(machine.getName()+" should reject a trajectory because of non-coherency.");
      //System.out.println("The distance between the two trajectories is "+d+" meters.");
      //System.out.println("....but the show must go on show; let's make the agv jump to the begin of the next trajectory.");

      //System.out.println("end of 1st traj ="+((POS) s1).toString());
      //System.out.println("begin of 2nd traj ="+((POS) s2).toString());
      //this.machine.trajectory.toOutput("N-C, machine.traj  :");
      //candidate.toOutput("N-C, candidate :");
    }
    return true;
  }

  /**
  Accept rules.
  @param candidate rules to be accepted, may be associated with a trajectory.
  @return true iff the all the rules can be evaluated and the associated handler is trusted.
  */
  public boolean isCoherentRules(Rule[] ruleCandidates){
    return true;
  }

  public boolean isCoherentRule(Rule ruleCandidate){
    return true;
  }

  public boolean isCoherentConstraints(Constraint[] constraintCandidates){
    return true;
  }

  public boolean isCoherentConstraint(Constraint constraintCandidate){
    return true;
  }

  //////////////////////////////////////////////////////////////////////////////

  public boolean isCoherentTrajectory(ServiceID serviceID,Trajectory trajectory) {
    // you should check the serviceID for trustworthyness.
    return isCoherentTrajectory(trajectory);
  }

  public boolean isCoherentRule(ServiceID serviceID,Rule rule){
    // you should check the serviceID for trustworthyness.
    return isCoherentRule(rule);
  }

  public boolean isCoherentConstraint(ServiceID serviceID,Constraint constraint){
    // you should check the serviceID for trustworthyness.
    return isCoherentConstraint(constraint);
  }

  /////////////////////////////////////////////////////////////////////////

  Obstacle safetyObstacle = new Obstacle(10);
  Obstacle previousSafetyObstacle = new Obstacle(10);

  /**
  This method claims machine.environmentInformation[0] to store its safetyObstacle rule
  */
  protected void add(Trajectory trajectory) {
    synchronized (machine.trajectory) {
      machine.trajectory.append(trajectory);
    }
    safetyObstacle = previousSafetyObstacle;
    previousSafetyObstacle = (Obstacle)machine.environmentInformation[0];
    safetyObstacle.trajectory = trajectory;
    if (trajectory.obstacleAtEnd) {
      safetyObstacle.horizon = trajectory.initialEvolution + trajectory.domain;
    } else {
      safetyObstacle.horizon = Float.POSITIVE_INFINITY;
    }
    machine.environmentInformation[0] = safetyObstacle;

    synchronized(machine.trajectory) {
        machine.trajectory.notify();
    }
  }
}