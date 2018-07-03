package com.agileways.crossscene.manoeuvres;
import net.agileframes.core.forces.Manoeuvre;
import com.agileways.crossscene.CrossParameters;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.forces.flags.*;
import net.agileframes.forces.precautions.*;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.xyaspace.trajectories.GoStraight;
import net.agileframes.forces.xyaspace.trajectories.ClockwiseCrab;
import net.agileframes.forces.xyaspace.trajectories.CounterClockwiseCrab;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;

public class ManoeuvreB2 extends Manoeuvre {

  public ManoeuvreB2(FuTransform transform) {
    super(transform,CrossParameters.MAX_SPEED,CrossParameters.MAX_ACCELERATION,CrossParameters.MAX_DECELERATION,CrossParameters.MAX_DEVIATION);
    //-------------- Defining Trajectory -----------------------------
    FuTrajectory[] compTraj = new FuTrajectory[3];
    double end = 0;
    compTraj[0] = new ClockwiseCrab(CrossParameters.WIDTH_LANE/2, XYATransform.IDENTITY);
    end += compTraj[0].getEvolutionEnd();
    compTraj[1] = new GoStraight(CrossParameters.LENGTH_PARK_CENTER,
                  new XYATransform((XYASpace)compTraj[0].getTrajectPoint(end)));
    end += compTraj[1].getEvolutionEnd();
    compTraj[2] = new CounterClockwiseCrab(CrossParameters.WIDTH_LANE/2,
                  new XYATransform((XYASpace)compTraj[0].getTrajectPoint(end)));
    trajectory = new FuTrajectory(
      compTraj,
//      new XYATransform(CrossParameters.LENGTH_PARK_A+ CrossParameters.LAYOUT_TRANSX,
//                       CrossParameters.LAYOUT_TRANSY,
//                       0)
      new XYATransform((XYATransform)transform, new XYATransform(CrossParameters.LENGTH_PARK_A, 0, 0))
    );
    //--------------- Defining Flags ---------------------------------
    Flag startFlag = new StartedFlag(this);
    Flag passedFlag = new PassedFlag(this, trajectory.getEvolutionEnd() * 0.8);
    Flag finishedFlag = new FinishedFlag(this);
    this.flags = new Flag[] {startFlag, passedFlag, finishedFlag};
    //--------------- Defining Precautions ---------------------------
    this.precautions = new Precaution[] {};
  }
}
