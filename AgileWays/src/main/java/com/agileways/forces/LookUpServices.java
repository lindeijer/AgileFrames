package com.agileways.forces;

import net.jini.core.lookup.*;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.EventRegistration;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.agileframes.server.AgileSystem;
import net.agileframes.forces.MachineProxy;
import net.agileframes.traces.ActorProxy;
import net.agileframes.core.traces.Actor;
import net.agileframes.services.ActionJob;

import com.agileways.ui.LookUpFrame;
//import com.agileways.forces.miniagv.MiniAgv;
import net.agileframes.forces.MachineIB;
import net.agileframes.forces.MachineProxy;
import com.agileways.vr.agv.AgvAvatarFactory;

public class LookUpServices implements RemoteEventListener {
  private static final int MAX_SERVICES = 100;
  private LookUpFrame frame;
  private ServiceTemplate serviceTemplate = new ServiceTemplate(null, new Class[] { net.agileframes.server.ServiceProxy.class }, null);
  private ServiceItem[] serviceItems;
  private ActorProxy[] actors = new ActorProxy[MAX_SERVICES];
  private MachineProxy[] machines = new MachineProxy[MAX_SERVICES];
  private ServiceID[] serviceIDs = new ServiceID[MAX_SERVICES];
  private Object[] services = new Object[MAX_SERVICES];
  private int registeredServices = 0;
  private int actorCounter = 0;
  private int machineCounter = 0;
  private EventRegistration er = null;

  //----------------- Constructor ----------------------------
  public LookUpServices() throws java.rmi.RemoteException {
    UnicastRemoteObject.exportObject(this);//needed for RemoteEventListener
    er = AgileSystem.registerServiceListener(serviceTemplate, this);
    frame = new LookUpFrame(this);
    notify(null);
  }

  //----------------- Method from remoteEventListener -----------
  public void notify(RemoteEvent e) {
    System.out.println("*** LookUp notified");

    evaluateServices();
    frame.setActors(actors);
    frame.setMachines(machines);
  }
  //---------------------------- Methods ------------------------------
  public void evaluateServices() {
    serviceItems = AgileSystem.lookup(serviceTemplate, MAX_SERVICES);

    // check if new item is added
    for (int i = 0; i < serviceItems.length; i++) {
      boolean registered = false;
      for (int j = 0; j < registeredServices; j++) {
        if (serviceIDs[j].equals(serviceItems[i].serviceID)) { registered = true; break; }
      }
      if (!registered) { addService(serviceItems[i]); }
    }
    // check if item is gone
    for (int i = 0; i < registeredServices; i++) {
      boolean available = false;
      for (int j = 0; j < serviceItems.length; j++) {
        if (serviceItems[j].serviceID.equals(serviceIDs[i])) { available = true; break; }
      }
      if (!available) { removeService(i); }
    }
  }
  private void addService(ServiceItem serviceItem) {
    System.out.println("Service appeared, Service will be added");
    serviceIDs[registeredServices] = serviceItem.serviceID;
    services[registeredServices] = serviceItem.service;
    registeredServices++;
    if (serviceItem.service instanceof ActorProxy) { addActor((ActorProxy)serviceItem.service); }
    else if (serviceItem.service instanceof MachineProxy) { addMachine((MachineProxy)serviceItem.service); }
    else { System.out.println("WARNING: LookUpServices added an unknown Service."); }
  }
  private void removeService(int index) {
    System.out.println("Service is disappeared, ServiceID will be removed");
    if (services[index] instanceof ActorProxy) { removeActor((ActorProxy)services[index]); }
    else if (services[index] instanceof MachineProxy) { removeMachine((MachineProxy)services[index]); }
    else { System.out.println("WARNING: LookUpServices removed an unknown Service."); }
    for (int i = index; i < registeredServices; i++) {
      services[i] = services[i+1];
      serviceIDs[i] = serviceIDs[i+1];
    }
    registeredServices--;
  }
  public void removeServiceManually(int index) {
    System.out.println("Service is manually removed, AgileSystem.unregisterService will be called.");
    //System.out.println(serviceIDs[index].toString());
    System.out.println("Trying to dispose its proxy");
    ActorProxy actor = this.actors[index];
    MachineProxy machine = this.machines[index];

    try {System.out.println("Actor to be disposed: "+actor.getName());}
    catch (Exception e) {}

    try { actor.dispose(); }
    catch (Exception e) { e.printStackTrace(); }
    //AgileSystem.unregisterService(serviceIDs[index]);
/*    System.out.println("Registering and unregistering with DummyServer");
    //MiniAgv dummy = null;
    try {
      MachineIB dummy = new MachineIB("dummy", actor.getServiceID());
      MachineProxy proxy = new MachineProxy(dummy,new AgvAvatarFactory(666));
    } catch (Exception e) { e.printStackTrace(); }

    //AgileSystem.registerService(dummy,id,service,null);
    System.out.println("Registered Dummy Service");
    try { Thread.currentThread().sleep(3000); }
    catch (Exception e) { e.printStackTrace(); }
    try { AgileSystem.unregisterService(actor.getServiceID()); }
    catch (Exception e) { e.printStackTrace(); }*/
    //to be sure:
    this.removeActor(actor);
    this.removeMachine(machine);

    frame.setActors(actors);
    frame.setMachines(machines);

    System.out.println("Finished removing service manually.");
  }
  private void addActor(ActorProxy actor) {
    actors[actorCounter] = actor;
    actorCounter++;
    // let's see if we find corresponding machine:
    if ( actor.getMachineProxy() == null ) {
      for (int i = 0; i < machineCounter; i++) {
        try { if ( actor.getActorID().equals(machines[i].getServiceID()) ) {
                actor.setMachineProxy(machines[i]);
/*TEMP*/        this.giveJob(actor); }
        } catch (Exception e) { e.printStackTrace(); }
      }
    }
  }
  private void addMachine(MachineProxy machine) {
    machines[machineCounter] = machine;
    machineCounter++;
    // let's see if we find corresponding actor:
    for (int i = 0; i < actorCounter; i++) {
      if ( actors[i].getMachineProxy() == null ) {
        try { if ( actors[i].getActorID().equals(machine.getServiceID()) ) {
                actors[i].setMachineProxy(machine);
/*TEMP*/        this.giveJob(actors[i]); }
        } catch (Exception e) { e.printStackTrace(); }
      }
    }
  }
  private void removeActor(ActorProxy actor) {
    int indent = 0;
    for (int i = 0; i < actorCounter; i++) {
      if (actors[i].equals(actor)) { indent = 1; }
      actors[i] = actors[i + indent];
    }
    actorCounter--;
  }
  private void removeMachine(MachineProxy machine) {
    int indent = 0;
    for (int i = 0; i < machineCounter; i++) {
      if (machines[i].equals(machine)) { indent = 1; }
      machines[i] = machines[i + indent];
    }
    machineCounter--;
  }


  //----------------- Main -----------------------

  public static void main(String args[]) {
    try {  LookUpServices lookUp = new LookUpServices(); }
    catch (java.rmi.RemoteException e) { e.printStackTrace(); }
  }

/*TEMP*/  /// lets see if it works...
/**/  public static void giveJob(ActorProxy actor) {
/**/    boolean result = false;
/**/    try {
/**/      Thread.sleep(10000);
/**/      //result = actor.acceptJob(null, new ActionJob("DemoSuperAction")); }
/**/      //result = actor.acceptJob(null, new ActionJob("LeftToRight")); }
/**/      //result = actor.acceptJob(null, new ActionJob("CarParkSuperAction")); }
/**/      //result = actor.acceptJob(null, new ActionJob("DemoCarParkSuperAction")); }
/**/      //result = actor.acceptJob(null, new ActionJob("SASuperJumbo")); }
/**/      result = actor.acceptJob(null, new ActionJob("PlatformSuperAction")); }
/**/    catch (Exception e) { e.printStackTrace(); }
/**/    if (result) { System.out.println("## TestLookUp: Actor accepted a job!"); }
/**/    else { System.out.println("## TestLookUp: Actor refused to do a job!"); }
/**/  }
}
