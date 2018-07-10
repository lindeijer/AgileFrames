package net.agileframes.forces;

import java.util.Arrays;

import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.Precaution;
import net.agileframes.forces.flags.FinishedFlag;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.forces.xyaspace.trajectories.GoStraight;

/**
 * <b>Pre-defined Manoeuvre to join a Scene.</b>
 * <p>
 * @author  H.J. Wierenga
 * @version 0.1
 */
public class JoinManoeuvre extends Manoeuvre {
	
  private static final long serialVersionUID = 1L;
	
  double distance = 1.0;
  // int value nr is introduced to indicate in which direction to drive
  // nr = 0 -> from point p, distance m
  // nr = 1 -> to point p, distance m
  public JoinManoeuvre(FuTransform t, FuSpace p, int nr) {
    super(t, 25, 5, 10, 0.5);
    //-- Defining Trajectory --
    FuTrajectory[] compTraj = new FuTrajectory[1];

    if (nr == 0) {
      compTraj[0] = new GoStraight(distance, new XYATransform(0, 0, 0));//, 0, 0, 0.75, 0.25);
    } else {
      compTraj[0] = new GoStraight(distance, new XYATransform(0, 0, Math.PI));//, 0, 0, 0.75, 0.25);
      p = new XYASpace(((XYASpace)p).getX() + distance, ((XYASpace)p).getY(), ((XYASpace)p).getAlpha());
    }

    this.trajectory = new FuTrajectory(
      compTraj,
      new XYATransform((XYATransform)t, new XYATransform((XYASpace)p))
    );

    //-- Defining Flags --
    this.flags = new Flag[1];
    // standard
    flags[0] = new FinishedFlag(this);

    this.precautions = new Precaution[0];
    //precautions[0] = new TimedStop(this, 10.0, (2.0/3.0)*maxDeceleration);
  }
  
  public String toString() {
	  return "JoinManoeuvre@"+this.hashCode()+",flags="+Arrays.asList(this.flags);
  }
}
