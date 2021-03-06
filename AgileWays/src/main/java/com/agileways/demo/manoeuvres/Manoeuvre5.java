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

public class Manoeuvre5 extends DemoManoeuvre {
  //-- Attributes --
  //-- Constructor --
  public Manoeuvre5(int direct, int lane) {
    super(null,direct, lane);
    //-- Defining Trajectory --

    //WATCH out: wtrong trajectory: must be S-Curve!
    double p1 = 3*l - agv;
    FuTrajectory[] compTraj = new FuTrajectory[1];
    compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform(x, y, a)
    );
    //-- Defining Flags --
    //-- Defining Precautions --
  }
}