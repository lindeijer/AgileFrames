package com.agileways.forces.machine.agv.sim;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.agileways.vr.agv.AgvAvatarFactory;

import net.agileframes.core.forces.MachineRemote;
import net.agileframes.core.traces.Actor;
import net.agileframes.forces.MachineIB;
import net.agileframes.forces.MachineProxy;
import net.agileframes.forces.ManoeuvreDriverIB;
import net.agileframes.forces.PhysicalDriverIB;
import net.agileframes.forces.StateFinderIB;
import net.agileframes.forces.mfd.Instructor;
import net.agileframes.forces.mfd.ManoeuvreDriver;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.server.AgileSystem;
import net.agileframes.traces.ActorIB;
import net.agileframes.traces.ActorProxy;
import net.jini.core.lookup.ServiceID;

public class SimAgv extends MachineIB  {
	
	//---------------- Constructor ----------------------
	  public SimAgv(String name,long CYCLETIME_MS) throws RemoteException {
	    super(name, null);
	    stateFinder = new StateFinderIB(new XYASpace(0, 0, 0));
	    instructor = new SimInstructor();
	    physicalDriver = new SimPhysicalDriver(CYCLETIME_MS);
	    manoeuvreDriver = new ManoeuvreDriverIB(stateFinder,instructor,physicalDriver,mechatronics);
	    this.start();

	    ServiceID actorID = AgileSystem.getServiceID();
	    actor = new ActorIB(actorID, this, name);
	    AgvAvatarFactory  avatarFactory = new AgvAvatarFactory(agvNumber++);

	    new MachineProxy((MachineRemote)UnicastRemoteObject.toStub(this), avatarFactory);
	    new ActorProxy((Actor)UnicastRemoteObject.toStub(actor), serviceID);
	  }
	  
	  static int agvNumber = 0;
	  ActorIB actor;

	  public Actor getActor() {
		  return actor;
	  }
}

class SimInstructor implements Instructor {

	public void initialize(ManoeuvreDriver manoeuvreDriver) {
		// TODO Auto-generated method stub
		
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public long getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

class SimPhysicalDriver extends PhysicalDriverIB {
	public SimPhysicalDriver(long CYCLETIME_MS) {
		this.CYCLETIME_MS = CYCLETIME_MS;
		this.timeStamp =  AgileSystem.getTime();
	}

	long CYCLETIME_MS;

	public long getCycleTime() {
		return CYCLETIME_MS;
	}

	@Override
	public synchronized void update() {
		long now_ms = AgileSystem.getTime();
		// long dT_ms = now_ms - this.timeStamp;
		long sleepTime_ms = this.timeStamp + CYCLETIME_MS - now_ms;
		if (sleepTime_ms>0) {
			// sleep until end of the cycle.
			try {
				wait(sleepTime_ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.timeStamp = AgileSystem.getTime(); // the next cycle starts now.
		
	}
	
}
