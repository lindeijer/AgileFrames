package com.agileways.forces.miniagv;

import java.rmi.RemoteException;

import org.junit.Test;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.forces.JoinMove;
import net.agileframes.forces.MachineIB;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.server.AgileSystem;
import net.agileframes.traces.ActorIB;
import net.jini.core.lookup.ServiceID;

public class MiniAgvManoeuvreDriverTest2 {

	@Test
	public void test() throws InterruptedException, RemoteException {
		System.setProperty("agilesystem.mute","TRUE");
		System.setProperty("agilesystem.visible","FALSE");
		
		MiniAgv4Test2 miniAgv4Test = new MiniAgv4Test2();
		
		FuSpace p = new XYASpace(10,10,0);
		FuTransform t = XYATransform.IDENTITY;
		
		ServiceID actorID = AgileSystem.getServiceID();
		ActorIB actor = new ActorIB(actorID, miniAgv4Test, "fuzz");
		
		
		JoinMove aJoinMove = new JoinMove(t,p,0);
		aJoinMove.setActor(actor);
		
		 {  // protected void sceneActionScript()
		    try {
		      // tickets[0].insist();
		      double dev = Double.MAX_VALUE;
		      do {
		    	  System.out.println("JOINSA: mov0.run: before");
		    	aJoinMove.run(new Ticket[] {} );
		    	System.out.println("JOINSA: mov0.run: after");
		        watch(aJoinMove.getSign(0));
		        System.out.println("JOINSA: mov0.sign 0 received");
		        dev = aJoinMove.getManoeuvre().getCalcDeviation();
		        System.out.println("deviation = "+dev);
		      } while (dev > 2);
		    } catch (Exception e) {
		      System.out.println("Exception in JoinSceneAction: The scene-action will be aborted.");
		      e.printStackTrace();
		    }
		    // finish(tickets[0]);
		    // signs[0].broadcast();
		  }
		
		
		//JoinManoeuvre m = new JoinManoeuvre(t, p, 0);
		
		//miniAgv4Test.begin(m);
		Thread.sleep(1000*1000);
	}
	
	public void watch(Sign sign){ watch(new Sign[] {sign}); }
	
	public void watch(Sign[] signs) { // copied from SceneAction
	    boolean broadcasted = false;
	    for (int i = 0; i < signs.length; i++) {
	      signs[i].addListener(this);
	      if (!broadcasted) { broadcasted = signs[i].isBroadcasted();}
	    }
	    while (!broadcasted) {
	      try{ synchronized(this) { this.wait();  }  }
	      catch (Exception e) {
	        System.out.println("Exception in SceneAction.watch():"+e.getMessage());
	        e.printStackTrace();
	      }
	      for (int i = 0; i < signs.length; i++) { if (!broadcasted) { broadcasted = signs[i].isBroadcasted();} }
	    }
	  }

}



class MiniAgv4Test2 extends MachineIB {

	public MiniAgv4Test2() throws RemoteException {
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
