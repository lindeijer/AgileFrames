package net.agileframes.forces;

import net.agileframes.server.ServerImplBase;
import net.agileframes.core.vr.Body;
import net.agileframes.core.vr.Avatar;
import net.agileframes.core.forces.Rule;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.forces.State;
import net.agileframes.brief.BooleanBrief;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Constraint;
import net.agileframes.core.forces.Machine;
import net.agileframes.forces.trajectory.Start;
import net.agileframes.forces.flag.AbstractFlag;
import net.jini.core.lookup.ServiceID;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;
import java.rmi.RemoteException;
import net.agileframes.forces.space.Obstacle;

/**
Abstact base-class for Machine implementations, these are objects that can accept
and execute 'moves' autonomously. A machine uploads a Machine (remote interface)
service to the JLS, the Machine service may be used to control the machine directly.
An Machine service is associated with the following attributes:
<ul>
<li>a Name attribute containing the name of the machine
<li>a Name attribute containing the name of the loginbase
<li>an Avatar attribute for 3D visualization
<li>a GUI for human interaction with the Machine service
</ul>
@see net.agileframes.core.traces.Move
@see net.agileframes.core.forces.Trajectory
@see net.agileframes.core.forces.Rule
*/
public class MachineImplBase extends ServerImplBase implements Machine,Runnable,Body {

  /**
  name=name, serviceID=null, uploadProxy=true, startMachineFunctionDriver=true.
  @param first name of the machine.
  */
  public MachineImplBase(String name) throws RemoteException {
    this(name,null,true);
  }

  /**
  name=name, serviceID=serviceID, uploadProxy=true, startMachineFunctionDriver=true.
  @param first name of the machine.
  @param the machine's serviceID, it must be unique.
  */
  public MachineImplBase(String name,ServiceID serviceID) throws RemoteException {
    this(name,serviceID,true);
  }

  /**
  Creates a machine with name and serviceID,
  a proxy is uploaded if desired,
  the machine-function-driver thread is started iff desired.
  @param first name of the machine.
  @param the machine's serviceID, it must be unique.
  @param upload machine-proxy iff true.
  @param start machine-function-driver thread iff true.
  */
  public MachineImplBase(String name,ServiceID serviceID,boolean upload) throws RemoteException {
    super(name,serviceID);
    // machineID is set to serviceID or to a new ServiceID iff it was null
    driverID = AgileSystem.getServiceID();
    uploadMachineProxy(upload);
  }

  public ServiceID driverID = null;

  //////////////////////////////////////////////////////////////////////

  /**
  The machine-function-driver thread is started and will execute this.run()
  */
  public void startMachineFunctionDriver() {
    Thread machineFunctionDriverThread = new Thread(this);
    machineFunctionDriverThread.setName("MachineFunctionDriver@" + name);
    machineFunctionDriverThread.start(); // see this.run()
    System.out.println(getName() + "'s MachineFunctionDriver-thread started, associated serviceID =" + driverID.toString());
  }

  /**
  Upload the Machine service.
  */
  private void uploadMachineProxy(boolean upload) {   // and Avatar
    if (upload) {
      Entry[] attributeSets = { new Name(this.name)  /* name of loginbase */ };
      AgileSystem.registerService(this,this.serviceID,this,attributeSets);
      System.out.println(getName() + " uploaded Machine_Stub");
    } else {
      System.out.println(getName() + " did not upload Machine_Stub");
    }
  }

  ////////////////////////////////////////////////////////////////////////

  /**
  time the driver last awakened after a sleep
  */
  public long awakenTime = AgileSystem.getTime();
  public long sleepDuration = 0;
  public static final int NOTIFY = -1;

  public void run() {
    for (;;) {
      try {
        prepareForCompute();
        compute();
      } catch (Exception e) {
        System.out.println("&&&&&&&&& Exception by " + Thread.currentThread().getName() + " in run():"+e.getMessage());
        e.printStackTrace();
        System.out.println("&&&&&&&&& Exception= " + e.getMessage());
        System.out.println("&&&&&&&&& Exception ignored, the show must go on.");
      }
    }
  }


  /**
   * clean up all the mess that has been made
   * sleep until it is time to compute, this is when machineStep.t has become the currentTime
   * The awakenTime is set as a side-effect.
   */
  private void prepareForCompute() {
    boolean endOfTrajectory = cleanTrajectory();
    if (endOfTrajectory) {
      sleepUntilMachineReceivesAnotherMove();
    } else {
      sleepDuration = machineStep.t - AgileSystem.getTime();
      // machineStep.t has been set to awakenTime + cycletime
      if (sleepDuration > 0) {
        sleepAsMachineResponds(sleepDuration);
      }
    }
    this.awakenTime = AgileSystem.getTime();
  }

  /**
   * Return when a new move has arrived.
   */
  private void sleepUntilMachineReceivesAnotherMove() {
    while (trajectory.beginTrajectory.afterTrajectory == null) {
      try {
        synchronized(trajectory) { trajectory.wait(5000);}
      } catch (InterruptedException e) {
        System.out.println("InterruptedException 1 in sleepUntilMachineReceivesAnotherMove by " + Thread.currentThread().getName() + " = " + e.getMessage());
        System.out.println("InterruptedException 1 in sleepUntilMachineReceivesAnotherMove ignored, the show must go on.");
      }
    }
  }

  /**
   * Return when sleepduration has passed, may return several milliseconds too late but never too early.
   */
  private synchronized void sleepAsMachineResponds(long sleepDuration) {
    try {
      this.wait(sleepDuration);
    } catch (InterruptedException e) {}
  }

  /** true iff machine.trajectory is being cleaned up */
  public boolean unstable = false;

  /*
  Removes the first (basic) trajectory in (the compose) machine.trajectory iff it
  has already been driven. Iff the machine.trajectory becomes empty by removal of
  the last (basic) trajectory a Start-trajectory is inserted. During this clean-up
  operation the boolean unstable is true.
  @return true iff end-of-track is true, the only atomic trajectory in this.trajectory is now a Start
   */
  private synchronized boolean cleanTrajectory() {
    boolean endOfTrajectory = false;
    if ((g.u - trajectory.beginTrajectory.initialEvolution - trajectory.beginTrajectory.domain) >= 50) {
      unstable = true;
      if (g.u > (resetValue+100)){  resetEvolution();  }
      if (trajectory.beginTrajectory.afterTrajectory != null) {
        endOfTrajectory = false;
        trajectory.initialEvolution = trajectory.beginTrajectory.afterTrajectory.initialEvolution;
        // get rid of too many decimals...
        trajectory.initialEvolution=((float)((int)(trajectory.initialEvolution*10000)))/10000;
        trajectory.domain = trajectory.endTrajectory.initialEvolution + trajectory.endTrajectory.domain - trajectory.initialEvolution;
        trajectory.domain=((float)((int)(trajectory.domain*10000)))/10000;
        trajectory.beginTrajectory = trajectory.beginTrajectory.afterTrajectory;

      } else { // we are at the end of the track...make new Start to begin next track with

        State currentState = trajectory.beginTrajectory.getEnd();
        Trajectory start = new Start(currentState);
        start.initialEvolution = trajectory.domain + trajectory.initialEvolution;
        trajectory.initialEvolution += trajectory.beginTrajectory.domain;
        trajectory.domain = 0;
        start.afterTrajectory = trajectory.beginTrajectory.afterTrajectory;
        trajectory.beginTrajectory = start;
        trajectory.beginTrajectory.initialTransform = null;
        endOfTrajectory = true;
      }

      trajectory.beginTrajectory.beforeTrajectory = null;
      unstable = false;
    }

    // stop before obstacle is reached
    float stoppingPoint = trajectory.initialEvolution + trajectory.domain;
    if ( (environmentInformation!=null) && (environmentInformation[0]!=null) ) {
      stoppingPoint = ((Obstacle)environmentInformation[0]).horizon -
                      ((Obstacle)environmentInformation[0]).offset;
      if ( stoppingPoint < g.u ) { stoppingPoint = trajectory.initialEvolution + trajectory.domain; }// this is an initializing problem
    }

    if ( g.u >= stoppingPoint ) { endOfTrajectory=true; }
    return endOfTrajectory;
  }

  // total evolution for this machine (without resetting)
  private float totalEvolutionSinceCreation = 0;
  // evolution after which the evolution-parameter u of this machine should be resetted
  private float resetValue = 1000;

  /**
   * Gives the total evolution of this machine
   *
   * @return a float representing the total evolution since the creation of this machine.
   */
  public float getTotalEvolutionSinceCreation(){ return totalEvolutionSinceCreation + g.u; }

  /**
   * Resets evolution-parameters of this machine and its trajectories
   * after resetValue is reached. This method exists because otherwise the float u becomes
   * to large and therefore less precise:
   * for example: float u = pi ---------> u = 3.1415927
   *              float u = pi*10000 ---> u = 31415.926
   * (the last digit of a float is not reliable)
   * In steads of 6 significant decimals in the second case, there are only 2 decimals.
   */
  private void resetEvolution(){
    synchronized(trajectory) {
      if (rules!=null){
        for (int i=0;i<rules.length;i++){
          if (rules[i]!=null) {
            rules[i].absoluteEvolution -= resetValue;
          }
        }
      }
      if (constraints!=null){
        for (int i=0;i<constraints.length;i++){
          if (constraints[i]!=null) {
           constraints[i].absoluteEvolution -= resetValue;
          }
        }
      }
      if (environmentInformation!=null){
        for (int i=0;i<environmentInformation.length;i++){
          if (environmentInformation[i]!=null) {
           ((Obstacle)environmentInformation[i]).horizon -= resetValue;
          }
        }
      }
      trajectory.setComposedEvolution(-resetValue);
      maneuverStep.state.u -= resetValue;
      g.u -= resetValue;
      totalEvolutionSinceCreation += resetValue;
    }
  }

  ///////////////////////////////////////////////////////////////////////////////

  /**
  rules are ordered: first those without trajectories, then with incleasing absoluteEvolution
  */
  public AbstractFlag[] rules = new AbstractFlag[100];

  /**
  implementation: a rule is associated with a trajectory
  */
  public void add(Rule rule) {
    FlagManager.add(rules,(AbstractFlag)rule);
  }

  public void remove(Rule rule) {
    FlagManager.remove(rules,(AbstractFlag)rule);
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
  constraints are ordered: first those without trajectories, then with incleasing absoluteEvolution
  */
  public AbstractFlag[] constraints = new AbstractFlag[100];

  public void add(Constraint constraint) {
    FlagManager.add(constraints,(AbstractFlag)constraint);
  }

  public void remove(Constraint constraint) {
    FlagManager.remove(constraints,(AbstractFlag)constraint);
  }

  ///////////////////////////////////////////////////////////////////////////////

  public State g = new State();
  public TrajectoryState trajectoryState = null;
  public ManeuverStep maneuverStep = new ManeuverStep();
  public State machineStep = new State();
  public Object[] environmentInformation = new Object[10];
  public float[] responseInformation = null;
  public float[] controlSettings = null;
  public Satisfyer.Satisfaction satisfaction = new Satisfyer.Satisfaction();

  /**
  responseModel[0] = maxAcceleration at this time (iff gas-handle is put to the max)
  responseModel[1] = maxDeceleration at this time (iff brake-handle is put to the max)
  */
  public float[] responseModel = null;

  /**
  The run-method for the MachineFunctionDriver-thread (MFD-thread), you
  are free to overload this method. This method is called when
  AgileSystem.getTime becomes machineStep.t.  We may be a bit late.
  */
  public void compute() {  // proposed MFD-thread cycle, you are free to overload
    this.g                      = computeState();
    this.trajectoryState        = computeTrajectoryState();
    this.g.u = trajectoryState.state.u;
    this.maneuverStep.state         = interpretMove();  // only computes the pilot, does not call satisfy
    this.maneuverStep.referenceAccelleration = satisfaction.accelleration;
    this.maneuverStep.referenceVelocity      = satisfaction.velocity;
    this.machineStep            = interpretManeuverStep();
    this.controlSettings        = computeControls();
    this.setControls(null,controlSettings);  // timeLastSet is set
    evaluateRules();
    satisfyConstraints();
    this.environmentInformation = computeEnvironmentalInformation();
    this.responseInformation    = computeResponseInformation();
    this.responseModel          = adaptResponseModel();
  }

  //////////////////////////////////////////////////////////////////////

  /**
  Compute the current functional state (using all available data), set this.g
  @return current functional state (g) and uVelocity and uAccelleration
  */
  public State computeState() {
    return proprioceptor.computeState();
  }

  /**
  Compute the current functional state (using all available data), set this.g
  @return current functional state (g).
  */
  public TrajectoryState computeTrajectoryState() {
    return proprioceptor.computeTrajectoryState(this.trajectory,this.g);
  }

  /**
  Compute the (current) functional state (using all available data) of objects
  observed in the surrounding environment. Such an external object is modelled
  as a (speed at) rule which indicates the observed objects speed ans distance.
  @return environmentInformation in the form of SpeedAt rules
  */
  public Object[] computeEnvironmentalInformation() {
    // environmental information is info about the functional state of external entities, inclusive their velocity and accelleration
    Object[] externalObjects = proprioceptor.computeEnvironmentalInformation();
    if (externalObjects != null) {
      for (int i=0;i<externalObjects.length;i++) {
        this.environmentInformation[i+1] = externalObjects[i];
      }
    }
    return this.environmentInformation;
  }

  /**
  Compute how the machine has responded to the controls, such values would be
  something like accelleration or traversed path.
  @return responseInformation (such as achieved accelleration).
  */
  public float[] computeResponseInformation() {
    return proprioceptor.computeResponseInformation();
  }

  /**
  Interpret the accepted moves wrt the current functional state g, this means
  interpret the prescribed trajectory and evaluate all the prescribed rules
  (move-script) wrt g. The interpetation of the prescibed trajectory results in
  a move-step which is a state 1 evolution-unit further in the desired direction.
  @param current functional state (g)
  @return move-step a state 1 evolution-unit further in the desired direction.
  */
  public State interpretMove() {
    // passing the costraints is deprecated
    return moveInterpreter.interpretMove(this.trajectory,this.trajectoryState,this.g);
  }

  /**
  Evaluate all the rules in the move-script and the rules generated as environmental
  information, in the context of the current functional state.
  @param environmentInformation in the form of SpeedAt rules
  @param current functional state (g)
  */
  public synchronized void evaluateRules() {
    evaluator.evaluateRules(rules,this.trajectoryState);
  }

  public synchronized void satisfyConstraints() {
    //Satisfyer.Satisfaction satisfactionContainer = satisfaction;
    //satisfaction = satisfyer.satisfy(constraints,trajectoryState,satisfactionContainer);
  }


  /**
  Interpret the move-step wrt the machine configuration and environmental information.
  Interpretation in the context of the machine configurationn results in move-steps
  (with length 1) for the active parts of the machine. Interpretation in the context
  of environmental information results in speed/accelleration directives for the
  active parts of the machine.
  @return machine-step the goal-state of the machine.
  */
  public State interpretManeuverStep() {
    return maneuverStepInterpreter.interpretManeuverStep(this.maneuverStep,this.environmentInformation);
  }

  /**
  Control settings to achieve the move-step (or rather machine-step) were
  computed in the context of a machine serponse model that predicted how the
  machine would respond the the controls, the model must be adapted in order to
  incorperate the latest response information derived from observations.
  @return the parameters of the adapted response model.
  */
  public float[] adaptResponseModel() {
    return setter.adaptResponseModel(this.responseInformation);
  }

  /**
  Compute the control-settings necessary in order to achieve the move-step,
  or rather the machine-step. This computation is made in the context of a machine
  response model (which may or may not be adapted).
  @param machine-step the goal-state of the machine.
  @return the new settings of the machines controls (such as the angle of the accellerator)
  */
  public float[] computeControls() {
    return setter.computeControls(this.machineStep);
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
  implements computeFunctionalState(), computeEnvironmentalInformation(), computeResponseInformation().
  This object probably downloads the infrastructure service from the JLS.
  */
  public Proprioceptor proprioceptor = null;

  /**
  implements evaluateRules(), interpretMove(). The object probably uploads the
  actor service to the JLS.
  */
  public MoveInterpreter moveInterpreter = null;

  /**
  implements interpretMoveStep()
  */
  public ManeuverStepInterpreter maneuverStepInterpreter = null;

  /**
  implements adaptResponseModel(), computeControls()
  */
  public Setter setter = null;

  /////////////////////////////////////////////////////////////////////

  public Satisfyer satisfyer = null;
  public Evaluator evaluator = null;

  //////////////////////////////////////////////////////////////////////

  public MoveManager moveManager = null;

  /**
  All accepted trajectories in moves are stored in this composed trajectory
  The MachineFunctionDriver-thread and the Brief-Getter-thread interact on this object !!!!
  */
  public Trajectory trajectory = null;

  /////////////// implementation of Machine //////////////////////

  /**
  Used when the Actor does not possess a reference to the movespace
  */
  public BooleanBrief acceptMove(
      ServiceID serviceID,Trajectory trajectory,
        Rule[] rules,Constraint[] constraints)
          throws Machine.NotTrustedException {
    if (false) {
      System.out.println(this.getName() + " method acceptMove called");
      //
      System.out.print("    trajectory=");
      if (trajectory != null) {
        System.out.println(trajectory.toString());
      } else { System.out.println("null"); }
      //
      System.out.print("    rules[0]=");
      if (rules != null) {
        System.out.println(rules[0].toString());
      } else { System.out.println("null"); }
      //
      System.out.print("    constraints[0]=");
      if (constraints != null) {
        System.out.println(constraints[0].toString());
      } else { System.out.println("null"); }
    }
    return this.moveManager.acceptMove(serviceID,trajectory,rules,constraints);
  }

  /** time when the controls were last set. @see setControls() */
  public long timeLastSet = AgileSystem.getTime();

  /** read the settings on the mechatronic module */
  public float[] getControls(ServiceID serviceID) {
    // serviceID should be tested for trustworthyness
    // return setter.getControls();
    return null;
  }

  /** set the machatronic module, @see timeLastSet */
  public void setControls(ServiceID serviceID,float[] controls){
    // serviceID should be tested for trustworthyness
    setter.setControls(controls);
    this.timeLastSet = AgileSystem.getTime();
  }

  /** read all the machines sensors, diagnose the internal state */
  public State getInternalstate() { return proprioceptor.getInternalState(); }

  /////////////////////////////////////////////////////////////

  /**
  the machine must emit some kind of signal so the infrastructure can identify and locate it.
  @param time when the machine must emit the signal (presumably now).
  */
  public void echo(long time) { /* do some call to the mechatronics */ }

  /**
  Called by infrastructre to give an (external) update of the functional state of the machine
  @param state update
  @param time when the state was observed
  */
  public void setState(ServiceID serviceID,State state,long time){
    // serviceID should be tested for trustworthyness
    g = state;
    //proprioceptor.setState(state,time);
  }

  //////////////////// implementation of (the old) Body /////////////////////

  public Body body = null;  // delegate

  /** this method is abused to get evolution */
  public State getState(ServiceID serviceID) {
    // serviceID should be tested for trustworthyness
    //return this.getState();
    State state = new State();
    state.u = this.totalEvolutionSinceCreation + g.u;
    return state;
  }

  /** the avatar wants to know where we are */
  public State getState() {
    if (unstable) {
      return null;
    }
    return proprioceptor.getState();
  }

  /* @exception RemoteException never thrown */
  public int getGeometryID() throws RemoteException {  return body.getGeometryID(); }
  /* @exception RemoteException never thrown */
  public int getAppearanceID() throws RemoteException{ return body.getAppearanceID(); }
  /* @exception RemoteException never thrown */
  public void addAvatar(Avatar avatar) throws RemoteException{ body.addAvatar(avatar);}
  /* @exception RemoteException never thrown */
  public void removeAvatar(Avatar avatar) throws RemoteException{ body.removeAvatar(avatar); }

  public void setParent(Body parent) {}
  public Body.StateAndAvatar addChild(Body child,State absState) { return null; }
  public State removeChild(Body child) { return null; }

 /////////////////////////////////////////////////////////////////////

} // end of Machine



















