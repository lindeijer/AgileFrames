package com.agileways.forces.miniagv;

import java.rmi.RemoteException;

import net.agileframes.forces.xyaspace.XYASpace;
import net.jini.core.lookup.ServiceID;

public class CreateMiniAgvMain {
	
	public static void main(String [] args) throws RemoteException {
		System.setProperty("java.rmi.server.ignoreStubClasses","true");
		System.setProperty("java.rmi.server.codebase","http://localhost:8087/agileways-1.0.1-SNAPSHOT.jar");
		System.setProperty("agileframes.loginbase.hostname","localhost");
		System.setProperty("agilesystem.quit","FALSE");
		
		String name = "boogaloo";
		boolean isAgileSystemMute = false;
		ServiceID serviceID = null;
		MiniAgv.STATEFINDER_TYPE = MiniAgv.SIMULATED;
		MiniAgv miniAgv = new MiniAgv(name, isAgileSystemMute, serviceID);
	}
	

}
