package com.agileways.forces.simagv;

import java.rmi.RemoteException;

import com.agileways.forces.machine.agv.sim.SimAgv;

import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.services.ActionJob;

public class SimAgvMain {
	
	public static void main(String[] args) throws RemoteException, NotTrustedException {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		System.setProperty("java.rmi.server.codebase","http://localhost:8087/agileways-1.0.2-SNAPSHOT.jar http://localhost:8088/");
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		SimAgv aSimAgv = new SimAgv("testSimAgv",100);
		//
		String scenaActioName = "DemoSuperAction";
		ActionJob anActionJob = new ActionJob(scenaActioName);
		aSimAgv.getActor().acceptJob(null,anActionJob);
	}

}
