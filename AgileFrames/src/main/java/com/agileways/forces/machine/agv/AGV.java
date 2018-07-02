package com.agileways.forces.machine.agv;
import java.rmi.RemoteException;
import net.agileframes.core.brief.Brief;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Machine;

/**
Interface with which to find and agv on the JLS
Should be the only class in the package together with the manuevers it standardly supports.
<p>
You are free to define other maneuvers for the machine to execute.
In order to do so you must:
<ol>
<li>optional: define a new trajectory for the agv.
<li>select a trajectory for the maneuver
<li>create the constraits that are valid in the context of the trajectory
<li>create the rules that are valid in the context of the trajectory and
<li>define the actions to be execute when the rules flag is raised, note that such actions may:
  <ul>
  <li>add new rules or remove old rules
  <li>add new constraints or remove old constraints
  <li>call methods defined in the interface AGV we see below.
  <ol>
</ol>


*/

public interface AGV extends Machine {

  ///////////////////////////////////////////////////////////////////
  ///// AGV supports all rules defined in net.agileframes.forces.rules

  // define a new rule here, make sure this Machine.evaluate supports it.

  ///////////////////////////////////////////////////////
  ///// AGV supports all constraints defined in net.agileframes.forces.constraint

  // define a new constraint here, make sure this Machine.sati ///////// methods available to generic maneuver programs //////////

  /**
  Fine positioning is used when the agv is close to docking stations.
  @param isFinePositioning true to turn finepositioning on, false to turn it off.
  *
  public void setFinePositioning(boolean isFinePositioning);

  /**
  Fine positioning is used when the agv is close to docking stations.
  @return true if fine-positioning is turned on, false otherwise.
  *
  public boolean isFinePositioning();
  */
  ////////////////////////////////////////////////////////////////////////
}
