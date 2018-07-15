package com.agileways.forces.simagv;

import java.rmi.RemoteException;

import com.agileways.forces.machine.agv.sim.SimAgv;

import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.services.ActionJob;

public class SimAgvMain {
	
	public static void main(String[] args) throws RemoteException, NotTrustedException {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");

		String codeBaseAgileFramesCore = " http://localhost:5001/agileframes-core-1.0.2-SNAPSHOT.jar";
		String codeBaseAgileFramesFoRcEs = " http://localhost:5002/agileframes-forces-1.0.2-SNAPSHOT.jar";
		String codeBaseAgileFramesTrAcEs = " http://localhost:5003/agileframes-traces-1.0.2-SNAPSHOT.jar";
		// String codeBaseAgileFramesSeRvEs = " http://localhost:5004/agileframes-services-1.0.2-SNAPSHOT.jar";
		String codeBaseAgileFramesVR = " http://localhost:5005/agileframes-vr-1.0.2-SNAPSHOT.jar";
		String codeBaseAgileWays = " http://localhost:6001/agileways-1.0.2-SNAPSHOT.jar";
		System.setProperty("java.rmi.server.codebase", codeBaseAgileFramesCore + //
				codeBaseAgileFramesFoRcEs + //
				codeBaseAgileFramesTrAcEs + //
				codeBaseAgileFramesVR + //
				codeBaseAgileWays);
		
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		SimAgv aSimAgv = new SimAgv("testSimAgv",100);
		//
		String scenaActioName = "DemoSuperAction";
		ActionJob anActionJob = new ActionJob(scenaActioName);
		aSimAgv.getActor().acceptJob(null,anActionJob);
	}

}
