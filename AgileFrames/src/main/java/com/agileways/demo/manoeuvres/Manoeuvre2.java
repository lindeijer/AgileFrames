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

public class Manoeuvre2 extends DemoManoeuvre {
  //-- Attributes --
  //-- Constructor --
  public Manoeuvre2(int direct, int lane) {
    super(null,direct, lane);
    //-- Defining Trajectory --
    double p1 = l - agv/2 - r;
    double p2 = 2*l - r - agv/2;
    FuTrajectory[] compTraj = new FuTrajectory[3];

    if (lane == 0) {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new CircularBendLeft(r, Math.PI/2, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p2, new XYATransform(p1 + r, r, Math.PI/2));
    } else {
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new CircularBendRight(r, Math.PI/2, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p2, new XYATransform(p1 + r, -r, -Math.PI/2));
    }

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform(x, y, a)
    );
    //-- Defining Flags --
    //-- Defining Precautions --
  }
}