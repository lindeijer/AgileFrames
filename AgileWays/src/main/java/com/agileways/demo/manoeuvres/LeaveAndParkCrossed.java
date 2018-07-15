package com.agileways.demo.manoeuvres;
// com
import com.agileways.demo.DemoParameters;
import com.agileways.demo.manoeuvres.DemoManoeuvre;
// net
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.xyaspace.trajectories.*;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.forces.flags.*;
import net.agileframes.forces.precautions.*;

public class LeaveAndParkCrossed extends DemoManoeuvre {
  //-- Attributes --
  //-- Constructor --
  public LeaveAndParkCrossed(FuTransform transform, int direct, int lane) {
    super(transform, direct, lane);
    //-- Defining Trajectory --
    double p1 = l - agv / 2 - r;
    double p2 = l + 2 * r;
    FuTrajectory[] compTraj = new FuTrajectory[3];

    if (lane == 0) {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new SCurveLeft(p2, l, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p1, new XYATransform(p1 + p2, l, 0));
    } else {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new SCurveRight(p2, l, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p1, new XYATransform(p1 + p2, -l, 0));
    }

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform((XYATransform)transform, new XYATransform(x, y, a))
    );
    //-- Defining Flags --
    double evolEnd = getTrajectory().getEvolutionEnd();
    this.flags = new Flag[6];
    // standard
    flags[0] = new PassedFlag(this, agv/2);
    flags[1] = new PassedFlag(this, 0.9*evolEnd);
    flags[2] = new FinishedFlag(this);
    // specific
    flags[3] = new PassedFlag(this, (p1 + 2*r) );// estimation!, passed semCross1
    flags[4] = new PassedFlag(this, (evolEnd/2 + r) );// estimation!, passed semCntrCross
    flags[5] = new PassedFlag(this, (evolEnd - p1) );// passed semCross2
    //-- Defining Precautions --
    this.precautions = new Precaution[1];
    precautions[0] = new TimedStop(this, 10.0, (1.0/3.0)*maxDeceleration);
  }
}