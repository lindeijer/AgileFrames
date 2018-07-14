package com.agileways.forces.miniagv;

import java.rmi.RemoteException;

import org.junit.Test;

import junit.framework.TestCase;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.core.traces.Ticket;
import net.agileframes.forces.JoinManoeuvre;
import net.agileframes.forces.MachineIB;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.server.AgileSystem;
import net.agileframes.traces.ActorIB;
import net.jini.core.lookup.ServiceID;

public class MiniAgvManoeuvreDriverTest {

	@Test
	public void test() throws InterruptedException, RemoteException {
		System.setProperty("agilesystem.mute","TRUE");
		System.setProperty("agilesystem.visible","FALSE");
		
		MiniAgv4Test miniAgv4Test = new MiniAgv4Test();
		
		FuSpace p = new XYASpace(10,10,0);
		FuTransform t = XYATransform.IDENTITY;
		
		JoinManoeuvre manoeuvre = new JoinManoeuvre(t,p,0);
		miniAgv4Test.begin(manoeuvre);

		 { // public void moveScript()
			try {
				System.out.println("JOINMOVE: about to start exec: manoeuvre="+manoeuvre);
				manoeuvre.startExecution();
				System.out.println("JOINMOVE: about to watch flag="+manoeuvre.getFlag(0));
				watch(manoeuvre.getFlag(0));
				System.out.println("JOINMOVE: watched raised flag="+manoeuvre.getFlag(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TestCase.assertTrue(manoeuvre.getFlag(0).isRaised()); 
	}
	
	public void watch(Flag flag){ watch(new Flag[] {flag}); }
	
	protected synchronized void watch(Flag[] flags) {
	    boolean raised = false;
	    for (int i = 0; i < flags.length; i++) {
	    	  flags[i].addListener(this);
	      if (!raised) { raised = flags[i].isRaised();}
	      }
	    while (!raised) {
	      try{ synchronized(this) { this.wait();  }  }
	      catch (Exception e) {
	        System.out.println("Exception in Move.watch():"+e.getMessage());
	        e.printStackTrace();
	      }
	      for (int i = 0; i < flags.length; i++) { if (!raised) { raised = flags[i].isRaised();} }
	    }
	  }

}



class MiniAgv4Test extends MachineIB {

	public MiniAgv4Test() throws RemoteException {
		super("MiniAgv4Test",null);
		stateFinder = new MiniAgvStateFinder(MiniAgv.SIMULATED); 
		instructor = new MiniAgvInstructor();
		physicalDriver = new MiniAgvPhysicalDriver();
		mechatronics = new MiniAgvMechatronicsSimulated();
		manoeuvreDriver = new MiniAgvManoeuvreDriver(//
				(MiniAgvStateFinder) stateFinder, //
				(MiniAgvInstructor) instructor, //
				(MiniAgvPhysicalDriver) physicalDriver, //
				(MiniAgvMechatronicsIB) mechatronics);
		start();
	}
	
}
