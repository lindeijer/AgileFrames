
package com.agileways.crossscene.manoeuvres;
import net.agileframes.core.forces.Manoeuvre;
import com.agileways.crossscene.CrossParameters;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.forces.flags.*;
import net.agileframes.forces.precautions.*;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.xyaspace.trajectories.*;
import net.agileframes.forces.xyaspace.XYATransform;

public class Manoeuvre8 extends Manoeuvre {

  public Manoeuvre8(FuTransform transform) {
    super(transform,CrossParameters.MAX_SPEED,CrossParameters.MAX_ACCELERATION,CrossParameters.MAX_DECELERATION,CrossParameters.MAX_DEVIATION);
    //-------------- Defining Trajectory -----------------------------
    double r = CrossParameters.LENGTH_PARK_A;
    double xt = 0;//CrossParameters.LAYOUT_TRANSX;
    double yt = 0;//CrossParameters.LAYOUT_TRANSY;


//    FuTrajectory[] compTraj = new FuTrajectory[1];
//    compTraj[0] = new GoStraight(50, new XYATransform(0,0,0));

    FuTrajectory[] compTraj = new FuTrajectory[8];

    compTraj[0] = new CircularBendLeft(r, Math.PI/2, new XYATransform(0, 0, 0));
    compTraj[1] = new CircularBendLeft(r, Math.PI/2, new XYATransform(r, r, Math.PI/2));
    compTraj[2] = new CircularBendLeft(r, Math.PI/2, new XYATransform(0, 0+2*r, Math.PI));
    compTraj[3] = new CircularBendLeft(r, Math.PI/2, new XYATransform(-r, 0+r, -Math.PI/2));

    compTraj[4] = new CircularBendRight(r, Math.PI/2, new XYATransform(0, 0, 0));
    compTraj[5] = new CircularBendRight(r, Math.PI/2, new XYATransform(0+r, 0-r, -Math.PI/2));
    compTraj[6] = new CircularBendRight(r, Math.PI/2, new XYATransform(0, 0-2*r, Math.PI));
    compTraj[7] = new CircularBendRight(r, Math.PI/2, new XYATransform(0-r, 0-r, Math.PI/2));

    trajectory = new FuTrajectory(
      compTraj,
      new XYATransform((XYATransform)transform, new XYATransform(0, 0, Math.PI))
    );
    //--------------- Defining Flags ---------------------------------
    Flag passedFlag = new PassedFlag(this, this.getTrajectory().getEvolutionEnd() * 0.9);
    Flag finishedFlag = new PassedFlag(this, this.getTrajectory().getEvolutionEnd() * 0.98);
    this.flags = new Flag[] {finishedFlag, passedFlag};
    //--------------- Defining Precautions ---------------------------
    Precaution timedStop = new TimedStop(this, 7.0, CrossParameters.MAX_DECELERATION*2.0/3.0);
    this.precautions = new Precaution[] { timedStop };
  }
}