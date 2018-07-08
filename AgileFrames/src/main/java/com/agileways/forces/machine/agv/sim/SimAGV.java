package com.agileways.forces.machine.agv.sim;

import net.agileframes.core.vr.Body;
import net.agileframes.forces.MachineIB;
import net.agileframes.core.vr.Avatar;
import net.jini.lookup.JoinManager; //   com.sun.jini.lookup.JoinManager;
import net.jini.core.lookup.ServiceID;
import net.agileframes.server.AgileSystem;
import net.jini.core.entry.Entry;


/**
Has two possible speeeds 0m/s and 3m/s. // extend with accelleration later
*/

public class SimAGV extends MachineIB {


  public SimAGV(String name) throws java.io.IOException {
    super(name,(ServiceID)null);

    // run at 3 u/s
    TrajectoryDriver trajectoryDriver = new TrajectoryDriver((MachineImplBase)this,10);//needs to be changed!!3 m/s!!
    trajectory.setTransform(new POSTransform(0,0,0));
    uploadAvatar(this.getServiceID());
  }

  JoinManager joinManager = null;

  private void uploadAvatar(ServiceID serviceID) {
    // set the joinmanager or the serviceregistration
    //joinManager = AgileSystem.getJoinManager(serviceID);
    //joinManager.addAttributes(new Entry[] { new AgvAvatarFactory() }); // entry
  }



  // these methods are abused in order to be able to add/remove a container to the AGV
  private int color = 0;
  public State removeChild(Body body) {
    color = ((AvatarImplBase)avatar).currentAppearanceID;
    ((AvatarImplBase)avatar).setGeometryAndAppearanceID(0,3);
    return null;
  }


  public Body.StateAndAvatar addChild(Body body, State state) {
    ((AvatarImplBase)avatar).setGeometryAndAppearanceID(1,color);
    return null;
  }

  private Avatar avatar;
  public void addAvatar(Avatar avatar) {
    this.avatar = avatar;
  }
}

//////////////////////////////////////////////


