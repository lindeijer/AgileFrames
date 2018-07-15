package com.agileways.forces.miniagv;

import java.rmi.RemoteException;

import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.services.ActionJob;
import net.jini.core.lookup.ServiceID;

public class CreateMiniAgvMain {
	
	public static void main(String [] args) throws RemoteException, InterruptedException, NotTrustedException {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		String version = "1.0.3-SNAPSHOT";
		String codeBaseAgileFramesCore = " http://localhost:5001/agileframes-core-"+version+".jar";
		String codeBaseAgileFramesFoRcEs = " http://localhost:5002/agileframes-forces-"+version+".jar";
		String codeBaseAgileFramesTrAcEs = " http://localhost:5003/agileframes-traces-"+version+".jar";
		// String codeBaseAgileFramesSeRvEs = " http://localhost:5004/agileframes-services-"+version+".jar";
		String codeBaseAgileFramesVR = " http://localhost:5005/agileframes-vr-"+version+".jar";
		String codeBaseAgileWays = " http://localhost:6001/agileways-"+version+".jar";
		System.setProperty("java.rmi.server.codebase", codeBaseAgileFramesCore + //
				codeBaseAgileFramesFoRcEs + //
				codeBaseAgileFramesTrAcEs + //
				codeBaseAgileFramesVR + //
				codeBaseAgileWays);
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
