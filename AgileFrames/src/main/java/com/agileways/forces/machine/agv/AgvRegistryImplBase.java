package com.agileways.forces.machine.agv;
import java.rmi.Naming;
import java.rmi.RemoteException;
import net.agileframes.server.AgileSystem;
import java.util.Properties;
import com.agileways.forces.machine.agv.AGV;

public class AgvRegistryImplBase extends java.rmi.server.UnicastRemoteObject
      implements AgvRegistry {

  public AgvRegistryImplBase() throws RemoteException {
    super();
    Properties properties = System.getProperties();
    String agvregistryHostname = properties.getProperty("agvregistry.hostname","dutw1700.wbmt.tudelft.nl");
    System.setProperties(properties);
    AgileSystem.getServiceID();
    // AgileSystem.view.setName(...)
    try {
      System.out.println("Naming.rebind(agvRegistry,this);");
      Naming.rebind("agvRegistry",this);
      System.out.println("AgvRegistryImplBase bound in local registry, or so it seems");
      String url = "//" + agvregistryHostname + "/agvRegistry";
      System.out.println("Naming.lookup(" + url + ");");
      AgvRegistry agvRegistry = (AgvRegistry)Naming.lookup(url);
      System.out.println("agvRegistry lookup request made it back home");
      System.out.println("agvRegistry is a stub=" + agvRegistry.toString());
    } catch (Exception e) {
      System.out.println("AgvRegistryImplBase something failed: " + e.getMessage());
    }
  }

  AGV agv;

  public void rebind(String name,AGV agv) {
    this.agv = agv;
    System.out.println("AgvRegistryImplBase.rebind agv with name=" + name + " =" + agv.toString());
  }

  public AGV lookup(String name) {
    System.out.println("AgvRegistryImplBase.lookup agv with name=" + name);
    return agv;
  }

  public static void main(String[] args) {
    try {
      new AgvRegistryImplBase();
    }
    catch (RemoteException e) {
      System.out.println("AgvRegistryImplBase.main RemoteException=" + e.getMessage());
    }
  }



} 