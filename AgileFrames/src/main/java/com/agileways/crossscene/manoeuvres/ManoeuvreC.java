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
import net.agileframes.forces.xyaspace.XYATransform;

public class ManoeuvreC extends Manoeuvre {

  public ManoeuvreC(FuTransform transform) {
    super(transform,CrossParameters.MAX_SPEED,CrossParameters.MAX_ACCELERATION,CrossParameters.MAX_DECELERATION,CrossParameters.MAX_DEVIATION);
    //-------------- Defining Trajectory -----------------------------
    FuTrajectory[] compTraj = new FuTrajectory[1];
    XYATransform t = new XYATransform(
//      CrossParameters.TOTAL_LENGTH - CrossParameters.LENGTH_PARK_C + CrossParameters.LAYOUT_TRANSX,
//      CrossParameters.LAYOUT_TRANSY,
//      0
      new XYATransform((XYATransform)transform, new XYATransform(CrossParameters.TOTAL_LENGTH - CrossParameters.LENGTH_PARK_C, 0, 0))
    );
    compTraj[0] = new GoStraight(CrossParameters.LENGTH_PARK_C, XYATransform.IDENTITY);
    trajectory = new FuTrajectory(compTraj, t);
    //--------------- Defining Flags ---------------------------------
    Flag startFlag = new StartedFlag(this);
    Flag passedFlag = new PassedFlag(this, trajectory.getEvolutionEnd() * 0.8);
    Flag finishedFlag = new FinishedFlag(this);
    this.flags = new Flag[] {startFlag, passedFlag, finishedFlag};
    //--------------- Defining Precautions ---------------------------
    this.precautions = new Precaution[] {};
  }
}
