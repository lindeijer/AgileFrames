package net.agileframes.forces;
import net.agileframes.core.forces.Flag;


public interface Satisfyer {

  public class Satisfaction {
    public Satisfaction() { }
    public float velocity = Float.NaN;
    public float accelleration = Float.NaN;
  }

  public Satisfaction satisfy(Flag[] constraints,TrajectoryState trajectoryState,Satisfaction container);

} 