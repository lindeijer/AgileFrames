package net.agileframes.forces.mfd;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.core.forces.State;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Transform;
import net.agileframes.forces.trajectory.Start;
import net.agileframes.forces.space.Obstacle;

import net.agileframes.forces.Proprioceptor;
import net.agileframes.forces.MoveInterpreter;
import net.agileframes.forces.ManeuverStepInterpreter;
import net.agileframes.forces.mfd.MoveInterpreterImplBase;
import net.agileframes.forces.ManeuverStep;
import net.agileframes.forces.Setter;
import net.agileframes.forces.TrajectoryState;
import net.agileframes.core.forces.Constraint;
import net.agileframes.forces.constraint.SatisfyerImplBase;
import net.agileframes.forces.rule.EvaluatorImplBase;
import net.agileframes.forces.mfd.MoveManagerImplBase;


/**
* Object that can drive trajectories passed to the machine,
* it can not work with alien entities.
* Rules and implicit end-of-trajectory-obstacle are implemented.
* The trajectory driver assumes velocity = 0 or v u/s, thats it.
*
* @author Evers, Lindeijer, Wierenga
* @version 0.0.1
*/

public class TrajectoryDriver extends MoveInterpreterImplBase implements
    Proprioceptor,ManeuverStepInterpreter,Setter   {

  /** Evolution per second */
  public float maxVelocity;
  /** Zero or maximal velocity */
  public float velocity;

  /**
   * Constructor. Starts machine-function-driver and assigns methods.
   *
   * @machine       MachineImplBase refers to the machine to be driven.
   * @maxVelocity   float indicating the maximal velocity of the machine
   */
  public TrajectoryDriver(MachineImplBase machine,float maxVelocity) {
    super(machine);
    this.maxVelocity = maxVelocity;
    System.out.println(this.getClass().toString() + " started for " + machine.getName());
    machine.satisfyer = new SatisfyerImplBase(machine);
    machine.evaluator = new EvaluatorImplBase(machine);
    try{
      machine.moveManager = new MoveManagerImplBase(machine,null,null,true,false);
    } catch (Exception e) {System.out.println("Exception in TrajectoryDriver():"+e);e.printStackTrace();}
    machine.proprioceptor = this;
    machine.moveInterpreter = this;
    machine.maneuverStepInterpreter = this;
    machine.setter = this;

    machine.startMachineFunctionDriver();
  }

  /**
   * Computes the new state of the machine.
   * This method is synchronized.
   *
   * @return State-object indicating next state the machine will be in
   */
  public synchronized State computeState() {// proprioceptor
    State newCurrentState = null;
    newCurrentState = machine.maneuverStep.state;
    newCurrentState.t = AgileSystem.getTime();
    return newCurrentState;
  }

  /**
   * Computes TrajectoryState...
   */
  public synchronized TrajectoryState computeTrajectoryState(Trajectory trajectory,State state) {
    TrajectoryState trajectoryState = new TrajectoryState();
    trajectoryState.trajectory = trajectory;
    trajectoryState.accelleration = Float.NaN; // should be observed and set
    trajectoryState.velocity = state.uVelocity;// should be observed and set
    trajectoryState.state = state;
    trajectoryState.distance = 0;
    // System.out.println("computeTrajectoryState" + trajectoryState.state);
    return trajectoryState;
  }

  /** Not implemented */
  public Object[] computeEnvironmentalInformation() { return null; }
  /** Not implemented */
  public float[] computeResponseInformation()     { return null; }
  /** Not implemented */
  public void setState(State state,long time)     {              }
  /** Not implemented */
  public State getInternalState()                 { return null; }


  private State currentG = null;
  private State lastG = null;// lastG is a backup-value of currentG in case of problems

  /**
   * Calculates state at this moment by calling getEvolution.
   * This method is synchronized.
   *
   * @return State containing current state.
   */
  public synchronized State getState() {
    // System.out.println("getState");
    currentG = machine.trajectory.compute(getEvolution());
    return currentG;
  }

  /**
   * Calculates evolution at this moment. Called by getState.
   * This method is synchronized.
   *
   * @return float containing current evolution.
   */
  public synchronized float getEvolution() {
    long dt = AgileSystem.getTime() - machine.timeLastSet;
    return machine.g.u + (velocity*dt/1000);
  }

  /** Not implemented. */
  public void evaluateRules(Rule[] rules,State state) { }

  /**
   * Determines the next state and evolution for the machine to wake up.
   * The machine needs to wake up every time a rule should be executed,
   * an obstacle or the end of the trajectory is reached.
   *
   * When the end of the trajectory is reached the machine should go to sleep until
   * the next move is received.
   *
   * This method is synchronized.
   *
   * @param trajectory        Trajectory: not implemented, machine.trajectory is used
   * @param trajectoryState   TrajectoryState: not implemented
   * @param g                 State: not implemented, machine.g is used.
   *
   * @return State indicating state to wake up in and with attribute u indicating
   *         at which evolution the machine should wake up.
  */
  public synchronized State interpretMove(Trajectory trajectory, TrajectoryState trajectoryState, State g) {
    State nextManeuverStep = null;

    float U = Float.POSITIVE_INFINITY;
    if ( (machine.rules != null) && (machine.rules[0] != null) ) {
      U = machine.rules[0].getEvolution();
      if (U < machine.g.u) {U = machine.g.u; }
    }

    // select sub-trajectory which is being driven:
    Trajectory helper = machine.trajectory.beginTrajectory;
    Trajectory lastHelper = helper;
    while (machine.g.u >= (helper.domain + helper.initialEvolution)) {   //> was >=
      lastHelper = helper;// i added this
      helper = helper.afterTrajectory;
      if (helper==null) {
        helper= lastHelper;
        break;
      }
    }
    float UU = helper.domain + helper.initialEvolution;
    float UUU = Float.POSITIVE_INFINITY;
    if ( (machine.environmentInformation != null) && (machine.environmentInformation[0] != null) ) {
      UUU = ((Obstacle)machine.environmentInformation[0]).horizon -
            ((Obstacle)machine.environmentInformation[0]).offset;
      if ( UUU < machine.g.u ) { UUU = Float.POSITIVE_INFINITY; }// this is an initializing problem
    }
    float smallestU;
    if ( (U <= UU) && (U <= UUU) ){ // evolution-rule is first stop
      smallestU = U;
    } else {  if ( (UU <= U) && (UU <= UUU) ) { // trajectory-end is first stop
        smallestU = UU;
      } else { // safetyObstacle is first stop
        smallestU = UUU;
      }
    }

    nextManeuverStep = machine.trajectory.compute(smallestU);
    if (nextManeuverStep==null){// we probably have an error due to roundings-problems
      System.out.println("U="+U+"  UU="+UU+"  UUU="+UUU);
      machine.trajectory.toOutput("machine.trajectory:  ");
      float correctedU = smallestU;

      if (smallestU < machine.trajectory.initialEvolution) {// U is a few decimals too small -> increase
        System.out.println("rounding-error...U is too small and we have to increase: U="+smallestU+"  U-machine.trajectory.initialEvolution="+(smallestU-machine.trajectory.initialEvolution));
        while (nextManeuverStep==null) {
          correctedU+=0.001f;
          nextManeuverStep = machine.trajectory.compute(correctedU);
        }
      }
      else {// U is a few decimals too large -> decrease
        System.out.println("rounding-error...U is too large and we have to decrease: U="+smallestU+"  U-machine.trajectory.initialEvolution-machine.trajectory.domain="+(smallestU-machine.trajectory.initialEvolution-machine.trajectory.domain));
        while (nextManeuverStep==null) {
          correctedU-=0.001f;
          nextManeuverStep = machine.trajectory.compute(correctedU);
        }
      }
      smallestU = correctedU;
      System.out.println("After correction U = "+correctedU);
    }
    nextManeuverStep.u = smallestU;

    return  nextManeuverStep;
  }

  /*
  In the context of simulation the next desired step is simply the end of the current trajectory,
  @param current trajectory of the machine
  @param current functional state, this is somewhere on the trajectory, an f ).
  @return the end of the trajectory.
  public synchronized State interpretTrajectory(Trajectory trajectory,State g) {
    State nextManeuverStep = (State)machine.trajectory.getEnd().clone();
    nextManeuverStep.u = machine.trajectory.beginTrajectory.domain + machine.trajectory.initialEvolution;
    return nextManeuverStep;
  }*/

  /**
  Computes how long it will take to reach maneuverStep, set that time on it.
  @param maneuverStep is g or end-of-trajectory
  @param environmentInformation is null, we are alone on our trajectory,
  @return machineStep=the functional state to be achieved in the next cycle.
  */
  public synchronized State interpretManeuverStep(ManeuverStep maneuverStep,Object[] environmentInformation) {
    State localStep = maneuverStep.state;
    State machineStep;
    adaptSpeed(environmentInformation,localStep); // sets velocity
    if (velocity==0) { return machine.g; }
    float du = localStep.u - machine.g.u;
    long dt = (long) Math.ceil(du*1000/velocity);
    machineStep = localStep;
    machineStep.t = machine.g.t + dt; // this affectively sets sleepDuration !!
    return machineStep;
  }

  /**
  compute the speed with which we plan to go to localStep.
  @param environmentInformation is null, we are alone on our trajectory
  @param localStep is g or end-of-trajectory
  @return the new desired speed.
  */
  public synchronized State adaptSpeed(Object[] environmentInformation,State localStep) {
    if (Math.abs(machine.g.u - localStep.u) <= 0.001) { this.velocity = 0; }
    else { this.velocity = this.maxVelocity; }
    return null;
  }

  /** Not implemented. */
  public State translateMoveStep(State moveStep) { return null; }

  /** Not implemented. */
  public void setControls(float[] controls) {}

  /**
  Compute nothing, there is no machine.
  @param machineStep is g or end-of-trajectory
  @return null;
  */
  public float[] computeControls(State machineStep) {
    return null;
  }

  /** Not implemented. */
  public float[] adaptResponseModel(float[] responseValues) { return null; }

}