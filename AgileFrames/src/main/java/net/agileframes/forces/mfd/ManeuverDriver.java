package net.agileframes.forces.mfd;
import net.agileframes.forces.MachineImplBase;

/**
object that can drive trajectories passed to the machine,
and it can avoid alien entities.  Really now!?

assumes max speed, accelleration and decelleration.
*/

public class ManeuverDriver extends TrajectoryDriver {

  public ManeuverDriver(MachineImplBase machine) {
    super(machine,3.0f);
  }
} 