package net.agileframes.core.forces;

import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Constraint;
import net.agileframes.core.forces.Trajectory;

import java.rmi.RemoteException;
import net.agileframes.core.server.Server;
import net.agileframes.brief.BooleanBrief;
import net.jini.core.lookup.ServiceID;


/**
A machine-server should implement this interface in order to provide remote access
to the machines controls, functional state, and machine-function-driver.

All methods contain a ServiceID in their signature which must identify the calling process
as trusted. The method <code>acceptMove</code> may involve dynamic (down)loading of classes,
the loaded classes must be trusted by the security manager.

 * @since AgileFrames 1.0.0
 * @author Lindeijer, Evers
 * @version 1.0.0 beta

*/


public interface Machine extends Server {

  /**
  Gets the current control-settings of the machine.
  @param serviceID of the remote client that is to be trusted
  @throws NotTrustedException if the serviceID is not trusted
  @return the current control settings
  */
  public float[] getControls(ServiceID serviceID) throws RemoteException,NotTrustedException;

  /**
  Sets the new control-settings of the machine.
  @param serviceID of the remote client that is to be trusted
  @throws NotTrustedException if the serviceID is not trusted
  */
  public void setControls(ServiceID serviceID,float[] controls) throws RemoteException,NotTrustedException;

  ////////////////////////////////////////////////////////

  /*
  Returns the current state of the machine.
  The returned state must at least be the functional state, but may also be the internal state.
  @param serviceID of the remote client that is to be trusted
  @throws NotTrustedException if the serviceID is not trusted
  @return current functional state
  */
  public State getState(ServiceID serviceID)       throws RemoteException,NotTrustedException;

  /**
  Sets the current functional state of the machine.
  @param serviceID of the remote client that is to be trusted
  @param state functional state observed (by the infrastructure)
  @param time when the functional state was observed
  @throws NotTrustedException if the serviceID is not trusted
  */
  public void setState(ServiceID serviceID,State state,long time) throws RemoteException,NotTrustedException;

  ////////////////////////////////////////////////////////////

  /**
  Appends this move to the current sequence of accepted moves iff the move is coherent.
  @param serviceID of the remote client that is to be trusted
  @param trajectory the machine must follow. The trajectory must be coherent with respect to the previous trajectory and its class-definition trusted by the security manager
  @param rules associated with the trajectory. The rules must be coherent with respect to the trajectory and their class-definition trusted by the security manager
  @throws NotTrustedException if the serviceID, the trajectory-class, or any of the rule-classes, are not trusted
  @return true if the move's trajectory and rules are coherent
  @deprecated use addMove(ServiceID,Trajectory,Constraint,Flag)
  */
  public BooleanBrief acceptMove(
      ServiceID serviceID,Trajectory trajectory,
        Rule[] rules,Constraint[] constraints)
          throws RemoteException,NotTrustedException;

  ////////////////////////////////////////////////////////////////////////

  /**
  Thrown when the method is invoked with a serviceID that is not trusted.
  */
  public class NotTrustedException extends Exception {
    public NotTrustedException(){}
  }

  //////////////////////////////////////////////////////////////////////////

  /**
  <ul>
  <li>v=V&&u=U : true iff you can have the velocity equal to V at U
  <li>v=V&&u<U : true iff you can have the velocity equal to V before U
  <li>v=V&&u>U : true iff you can have the velocity equal to V after U

  <li>v<V&&u=U : true iff you can keep the velocity less than V at U
  <li>v<V&&u<U : true iff you can keep the velocity less than V before U
  <li>v<V&&u>U : true iff you can keep the velocity less than V after U :: iff V==0 then this is the safety flag

  <li>v>V&&u=U : true iff you can keep the velocity greater than V at U
  <li>v>V&&u<U : true iff you can keep the velocity greater than V before U
  <li>v>V&&u>U : true iff you can keep the velocity greater than V after U
  </ul>
  */
  public interface VelocityFlag extends Flag {
    public float getVelocity(float evolution);
    public float getVelocity();
    public int getVelocityOperator();
  }

  //////////////////////////////////////////////////////////////////////////

  /** not implemented yet, it should be. To be used to avoid thrashig your load around the hold */
  public interface AccellerationFlag extends Flag {
    public float getAccelleration(float evolution);
    public float getAccelleration();
    public int getAccellerationOperator();
  }

  //////////////////////////////////////////////////////////////////////////

  /**
  Not implemented.
  <ul>
  <li>t=T&&u=U : true iff you can reach U at the moment the bell rings, you are just in time. The margins are set by the machine.
  <li>t=T&&u<U : true iff you can avoid passing U before the bell
  <li>t=T&&u>U : true iff you can pass U before the bell.

  <li>t<T&&u=U : true iff you can reach U before the bell.
  <li>t<T&&u<U : true iff you can avoid passing U before the bell
  <li>t<T&&u>U : true iff you can pass U before the bell.

  <li>t>T&&u=U : true iff you can reach U after the bell.
  <li>t>T&&u<U : nonsense
  <li>t>T&&u>U : true iff you can pass U after the bell.
  </ul>
  */
  public interface TimeFlag extends Flag {
    public long getTime(float evolution);
    public long getTime();
    public int getTimeOperator();
  }

  //////////////////////////////////////////////////////////////////////////

  /**
  Raised iff a sign has arrived in the sign-box. Signs must be removed from the box?
  A sign is simply an object that implements interface SignFlag.
  The inbox is not implemented yet, it should be.
  */
  public interface SignFlag extends Flag {}

  //////////////////////////////////////////////////////////////////////////

}





















