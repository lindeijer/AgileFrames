package com.agileways.forces.miniagv;

import java.rmi.RemoteException;

import net.agileframes.core.services.Job;
import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.services.ActionJob;
import net.jini.core.lookup.ServiceID;

public class CreateMiniAgvMain {
	
	public static void main(String [] args) throws RemoteException, InterruptedException, NotTrustedException {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		System.setProperty("java.rmi.server.codebase","http://localhost:8087/agileways-1.0.1-SNAPSHOT.jar http://localhost:8088/");
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		
		String name = "boogaloo";
		boolean isAgileSystemMute = false;
		ServiceID serviceID = null;
		MiniAgv.STATEFINDER_TYPE = MiniAgv.SIMULATED;
		MiniAgv miniAgv = new MiniAgv(name, isAgileSystemMute, serviceID);
		String scenaActioName = "DemoSuperAction";
		ActionJob anActionJob = new ActionJob(scenaActioName);
		miniAgv.acceptJob(anActionJob);
	}
	

}
