package com.agileways.carpark.manoeuvres;

import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;

import net.agileframes.forces.flags.PassedFlag;
import net.agileframes.forces.flags.FinishedFlag;
import net.agileframes.forces.precautions.TimedStop;

import net.agileframes.forces.xyaspace.XYATransform;

import com.agileways.carpark.CarParkParameters;

import net.agileframes.forces.xyaspace.trajectories.CircularBendLeft;
import net.agileframes.forces.xyaspace.trajectories.CircularBendRight;
import net.agileframes.forces.xyaspace.trajectories.GoStraight;

public class ParkIn extends Manoeuvre {
  //-- Attributes --

  //-- Constructor --
  public ParkIn(FuTransform transform, int entranceLane, int parkSide, int parkLane) {
    super(transform, CarParkParameters.MAX_SPEED, CarParkParameters.MAX_ACCEL, CarParkParameters.MAX_DECEL, CarParkParameters.MAX_DEVI);
    //-- parameters --
    double x = 0;
    double y = (2 - entranceLane) * CarParkParameters.DIST_BETW_LANES;
    double a = 0;

    double p1 = parkLane * CarParkParameters.AGV_WIDTH;
    double p2 = CarParkParameters.TURN_RADIUS;
    double p3 = Math.abs(entranceLane - parkSide) * CarParkParameters.DIST_BETW_LANES - CarParkParameters.TURN_RADIUS + CarParkParameters.PARK_LENGTH;

    //-- Defining Trajectory --
    FuTrajectory[] compTraj = new FuTrajectory[3];

    if (parkSide == 0) {//park North
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new CircularBendLeft(p2, Math.PI/2, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p3, new XYATransform(p1 + p2, p2, Math.PI/2));
    } else {//park South
      compTraj[0] = new GoStraight(p1, new XYATransform(0, 0, 0));
      compTraj[1] = new CircularBendRight(p2, Math.PI/2, new XYATransform(p1, 0, 0));
      compTraj[2] = new GoStraight(p3, new XYATransform(p1 + p2, -p2, -Math.PI/2));
    }

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform((XYATransform)transform, new XYATransform(x, y, a))
//      new XYATransform(new XYATransform(x, y, a), (XYATransform)transform)
    );

    //-- Defining Flags --
    double evolEnd = getTrajectory().getEvolutionEnd();
    this.flags = new Flag[5];
    // standard
    flags[0] = new PassedFlag(this, CarParkParameters.AGV_LENGTH/2);
    flags[1] = new PassedFlag(this, 0.9*evolEnd);
    flags[2] = new FinishedFlag(this);
    // specific
    flags[3] = new PassedFlag(this, evolEnd - p3);
    //flag 3: free road
    flags[4] = new PassedFlag(this, evolEnd - p3 + CarParkParameters.DIST_BETW_LANES);
    //flag 4: free cross road....if no road to cross, this flag will not be used
    //-- Defining Precautions --
    this.precautions = new Precaution[1];
    precautions[0] = new TimedStop(this, 10.0, (1.0/3.0)*maxDeceleration);
  }

}