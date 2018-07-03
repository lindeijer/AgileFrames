package com.agileways.miniworld.lps;
import net.agileframes.core.forces.FuSpace;

public interface RemoteStateListener extends java.rmi.Remote {
  public void setState(FuSpace state) throws java.rmi.RemoteException;
  public boolean setLed(boolean ledOn) throws java.rmi.RemoteException;
  /**
   * In miniworld-context the entire demo can be scaled, the state listener
   * must know the scale, because otherwise it will calculate its own
   * position in the wrong way.
   * Think of the way odometry is used: if the agv drives 0.5 m, it will
   * calculate a state (in miniagvstatefinder) that is 0.5 m further. If the
   * entire demo is scaled (lets say two times as big), the miniagvstatefinder
   * should calculate the state that is 0.5/2 = 0.25 m further.
   *
   * scale factor should be between 0.9 and 1.1 and in a real-size-world
   * should always be one!
   *
   * @param scale the scale to set
   */
  public void setScale(double scale) throws java.rmi.RemoteException;
}
