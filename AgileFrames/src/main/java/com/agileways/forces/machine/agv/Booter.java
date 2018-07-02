package com.agileways.forces.machine.agv;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

public class Booter {

  public Booter() {
  }

  static {
    //System.setSecurityManager(new RMISecurityManager());
    //System.out.println("RMISecurityManager set");
  }

  public static void main(String[] args) {
    doA();
    // doB();
  }

  private static void doA() {
    Booter booter = new Booter();
    AgvRegistryImplBase agvRegistry = null;
    try {
      agvRegistry = new AgvRegistryImplBase();
      Naming.rebind("agvRegistry",agvRegistry);
      System.out.println("agvRegistry bound as agvRegistry");
    } catch (Exception e) {
      System.out.println("Booter main 1: new AgvRegistry() failed: " + e.toString());
      System.exit(0);
    }
    AGV miniAgv = null ;
    while (true) {
      synchronized(booter) {
        try {
          booter.wait(2000);
          miniAgv = agvRegistry.lookup("nobody");
          System.out.println("YO before: ");
          System.out.println("YO: " +  miniAgv.getName());
        }
        catch (Exception e) {
          System.out.println("BBBB" + e.toString());
        }
      }
    }
  }

  private static void doB() {
    try {
      AGV miniAgv = (AGV)Naming.lookup("//130.161.22.182/pipo");
      System.out.println("Booter main B: remote registry lookup succeded");
    } catch (Exception e) {
      System.out.println("Booter main B: remote registry lookup failed: " + e.toString());
      System.exit(0);
    }

  }

}

