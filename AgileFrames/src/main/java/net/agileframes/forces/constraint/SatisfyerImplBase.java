package net.agileframes.forces.constraint;
import net.agileframes.core.forces.Constraint;
import net.agileframes.forces.MachineImplBase;
import net.agileframes.forces.TrajectoryState;
import net.agileframes.core.forces.Flag;
import net.agileframes.forces.Satisfyer;
// import java.util.

/**
Computes the evolution-velocity and evolution-accelleration according to the constraints.
The constarint defined in this package.

<p>AbstractFlag : (begin <= u <= end) wrt a trajectory :
<ul>
<li>if (u <= begin) then { return v>0 && a>0 }
<li>if (begin <= u) then return null; discard the constraint
</ul>

<p>AbstractVelocityFlag : (begin <= u(t) <= end) and (du(t)/dt = v(u)) wrt a trajectory :
<ul>
<li>if (u <= begin) then { return "(v=v_now) and (v*op*(dv/dt))" }
<li>if (begin <= u <= end) then { return "(v op v(u) and (a>0) or (a<0) or (a=null) depending on the current velocity situation" }
<li>if (end <= u) then { "discard the constraint" }
</ul>

<p>AbstractAccellerationFlag : (begin <= u(t) <= end) and (ddu(t)/ddt = a(u)) wrt a trajectory :
<ul>
<li>if (u <= begin) then { return "v op a(begin) ...." }
<li>if (begin <= u <= end) then { return "(a op a(u)) .." }
<li>if (end <= u) then { "discard the constraint" }
</ul>
*/

// is there a generic satisfyer????


public class SatisfyerImplBase implements Satisfyer {   // should be an interface machineIB looks at

  private MachineImplBase machine = null;

  public SatisfyerImplBase(MachineImplBase machine) {
    this.machine = machine;
  }

  private SafetyConstraint nearestSafetyConstraint = null;

  /**
  constraints are ordered to increasing absolute-evolution. There always is a constraints[0]
  */
  public Satisfyer.Satisfaction satisfy(Flag[] constraints,TrajectoryState trajectoryState,Satisfyer.Satisfaction container) {
    this.trajectoryState = trajectoryState;
    this.satisfactionContainer = satisfactionContainer;
    if (constraints[0] != null) {
      satisfactionContainer = satisfy((SafetyConstraint)constraints[0]);
    }
    else {
      satisfactionContainer.velocity = Float.NaN;
      satisfactionContainer.accelleration = Float.NaN;
    }
    return satisfactionContainer;
  }

  Satisfyer.Satisfaction satisfactionContainer = null;
  TrajectoryState trajectoryState = null;

  /**
  machine.responseModel[1] is decelleration characteristic
  */
  public Satisfyer.Satisfaction satisfy(SafetyConstraint safetyConstraint) {
    float horizon = safetyConstraint.getEvolution();
    float d = horizon - trajectoryState.state.u;
    float a = machine.responseModel[1];
    float maxVelocity = (float)Math.sqrt(2*a*d);
    satisfactionContainer.velocity = maxVelocity;
    satisfactionContainer.accelleration = a;   // maxDecelleration
    return satisfactionContainer;
  }



}


