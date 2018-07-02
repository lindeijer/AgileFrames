package net.agileframes.forces;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Trajectory;

/**
Is responsible for maintaining the current functional state,
CURRENT NAME = StateFinder.
This is achieved using internal information provided by internal sensors,
external sensors, and the infrastructure.
*/

public interface Proprioceptor {

  /**
  Compute the current functional state and set its associated evolution and time.
  Use all data available.
  @return g=currentState, t and u are set.
  */
  public State computeState();

  /**
  Compute the evolution corresponding with the (functional) state.
  @param state, possibly the current (functional State)
  @return evolution associated with the state.
  */
  public TrajectoryState computeTrajectoryState(Trajectory trajectory,State state);

  /**
  Computes SpeedAtRules to model the dynamic functional state of external
  entities observed by sensors.
  */
  public Object[] computeEnvironmentalInformation();

  /**
  The setter sets the controls according to a response model with which it makes
  predictions of vehicle responses (such as how it will accellerate).
  These predicted values must be compared with result-values in order to learn.
  The proprioceptor must compute the result-values from observations.
  */
  public float[] computeResponseInformation();

  /**
  An external information-source has provided an update of the functional state.
  @param functional state of this machine observed by the external source
  @param time at which the functional state was observed.
  */
  public void setState(State state,long time);

  /**
  @return the current internal (not the functional) state, with t and u set.
  */
  public State getInternalState();

  /**
  An external entity, possibly the avatar, wants to know the current state.
  @return the (freshly computed) current functional state, with t=now and u set.
  */
  public State getState();

  /**
  An external entity, possibly the avatar, wants to know the current evolution.
  @return the (freshly computed) current evolution.
  */
  public float getEvolution();



}

/**
  public State computeState();
  public float computeEvolution(Trajectory trajectory,State state);
  public Rule[] computeEnvironmentalInformation();
  public float[] computeResponseInformation();
  public void setState(State state,long time);
  public State getState();
  public State getInternalState();
  public float getEvolution();
*/