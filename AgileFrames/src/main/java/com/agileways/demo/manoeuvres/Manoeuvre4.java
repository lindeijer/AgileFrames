package com.agileways.demo.manoeuvres;
// com
import com.agileways.demo.DemoParameters;
import com.agileways.demo.manoeuvres.DemoManoeuvre;
// net
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.xyaspace.trajectories.*;
import net.agileframes.forces.xyaspace.XYATransform;

public class Manoeuvre4 extends DemoManoeuvre {
  //-- Attributes --
  //-- Constructor --
  public Manoeuvre4(int direct, int lane) {
    super(null,direct, lane);
    //-- Defining Trajectory --
    double p1 = l - agv / 2 - r;
    double p2 = l + 2 * r;
    FuTrajectory[] compTraj = new FuTrajectory[3];

    if (lane == 0) {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new SCurveLeft(p2, l, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p1, new XYATransform(p1 + p2, 0, 0));
    } else {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new SCurveRight(p2, l, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p1, new XYATransform(p1 + p2, 0, 0));
    }

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform(x, y, a)
    );
    //-- Defining Flags --
    //-- Defining Precautions --
  }
}