package com.agileways.forces.miniagv;

// java
import java.io.*;
import java.net.InetAddress;
import java.util.Properties;
import java.rmi.RemoteException;

import net.jini.core.entry.Entry;
// net.jini
import net.jini.core.lookup.*;
import net.jini.lookup.entry.Name;
// net.agileframes
import net.agileframes.core.traces.Actor;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.MachineProxy;
import net.agileframes.forces.TransformEntry;
import net.agileframes.forces.MachineIB;
import net.agileframes.core.forces.MachineRemote;
import net.agileframes.traces.ActorProxy;
import net.agileframes.traces.ActorIB;
import net.agileframes.server.AgileSystem;
import net.agileframes.forces.mfd.*;
import com.agileways.vr.agv.AgvAvatarFactory;
import net.agileframes.core.vr.AvatarFactory;
import net.agileframes.core.vr.SceneAvatarFactory;

import com.agileways.miniworld.lps.RemoteStateListener;
import com.agileways.miniworld.lps.RemoteDistributor;
import java.rmi.server.UnicastRemoteObject;


public class MiniAgv extends MachineIB implements RemoteStateListener {
  //---------------- Attributes -----------------------
  private static MiniAgv miniAgv;
  private static int agvNumber;
  private static String miniagvIdName;
  private static String miniagvIdNumber;
  private static MachineProxy machineProxy;
  private static ActorProxy actorProxy;
  private static ActorIB actor;
  private static AvatarFactory avatarFactory;
  private static final long awakeTime = AgileSystem.getTime();
  private static ServiceID storedServiceID = null;
  private static boolean storedSuccesfully = false;
  private RemoteDistributor distributor = null;

  public final static int SIMULATED = 1;
  public final static int ODOMETRIC = 2;
  public final static int EXTERNAL = 3;
  public final static int EXTERNAL_AND_ODOMETRIC = 4;
  public final static int EXTERNAL_AND_SIMULATED = 5;
  public static int STATEFINDER_TYPE = 0;
  public static int MECHATRONICS_TYPE = 1;

  //---------------- Constructor ----------------------
  public MiniAgv(String name, boolean isAgileSystemMute, ServiceID serviceID) throws RemoteException {
    super(name, serviceID);

    serviceID = super.serviceID;

    // SERVICE-ID: if not stored on disk yet: do it now
    if (!storedSuccesfully) {
      try {
        File file = new File(AgileSystem.agileframesDataPath + "ServiceID_AGV"+String.valueOf(agvNumber));
        file.createNewFile();
        if (file.canWrite()) {
          FileOutputStream fos = new FileOutputStream(file);
          DataOutputStream dos = new DataOutputStream(fos);
          serviceID.writeBytes(dos);
          System.out.println("ServiceID succesfully written to disk: "+serviceID.toString());
        } else { System.out.println("Cannot write to file to store serviceID"); }
      } catch (Exception e) {
        System.out.println("Exception while writing ServiceID: Exception ignored.");
        e.printStackTrace();
      }
    }


    // get a remote-distributor for data!
    if (!lookupDistributor()) {
      System.out.println("MiniAgv didnot register with a RemoteDistributor.");
      if (STATEFINDER_TYPE >= EXTERNAL) {
        System.out.println("MiniAgv will cause the system to exit, because localInfo.TYPE = EXTERNAL.");
        System.exit(0);
      } else {
        System.out.println("MiniAgv will not receive any external states.");
      }
    } else {
      try {
        //UnicastRemoteObject.exportObject(this);
        distributor.registerStateListener(agvNumber, this);
      } catch (Exception e) {
        System.out.println("Exception while registering MiniAgv with RemoteDistributor.");
        e.printStackTrace();
        if (STATEFINDER_TYPE >= EXTERNAL) {
          System.out.println("MiniAgv will cause the system to exit, because localInfo.TYPE = EXTERNAL.");
          System.exit(0);
        } else {
          System.out.println("MiniAgv will not receive any external states.");
        }
      }
    }


    stateFinder = new MiniAgvStateFinder(STATEFINDER_TYPE);
    instructor = new MiniAgvInstructor();
    physicalDriver = new MiniAgvPhysicalDriver();

    if (MECHATRONICS_TYPE == MiniAgvMechatronicsIB.SIMULATED) {
      mechatronics = new MiniAgvMechatronicsSimulated();
      System.out.println("MiniAgv: Mechatronics are Simulated!!");
    } else {
      mechatronics = new MiniAgvMechatronicsReal();
      System.out.println("MiniAgv: Mechatronics are Real!!");
    }

    manoeuvreDriver = new MiniAgvManoeuvreDriver((MiniAgvStateFinder)stateFinder, (MiniAgvInstructor)instructor,
                                                 (MiniAgvPhysicalDriver)physicalDriver, (MiniAgvMechatronicsIB)mechatronics);


    this.start();

    ServiceID actorID = AgileSystem.getServiceID();
    actor = new ActorIB(actorID, (MachineRemote)UnicastRemoteObject.toStub(this), "actor"+agvNumber);
    avatarFactory = new AgvAvatarFactory(agvNumber);

    if (!isAgileSystemMute) {
      machineProxy = new MachineProxy((MachineRemote)UnicastRemoteObject.toStub(this), avatarFactory);
      actorProxy = new ActorProxy(actor, serviceID);
    } else {
      machineProxy = null;
      actorProxy = null;
    }
  }

  //---------------- Main ------------------
  public static void main(String args[]) {
    try {
      if (args.length == 1) {
        int nr = Integer.parseInt(args[0]);
        if ((nr > 25) && (nr < 1000)) {
          agvNumber = nr;
          miniagvIdName = "agv"+nr;
          System.getProperties().setProperty("miniagv.id.name","agv" + agvNumber);
        }
      }
      getMiniAgvProperties();
      miniAgv = new MiniAgv(miniagvIdName, AgileSystem.isMute, storedServiceID);
      System.out.println("MiniAgv.main(): Started miniAgv: "+ miniAgv.getName());
    } catch (RemoteException e) {
      System.out.println("RemoteException in main: " + e.getMessage());
      e.printStackTrace();
    }
  }

  //---------------- Methods --------------------
  public static void getMiniAgvProperties() {
    java.util.Properties properties = System.getProperties();
    //
    //
    String mechatronics = properties.getProperty("miniagv.mechatronics","simulated");
    System.out.println("miniagv.mechatronics=" + mechatronics);
    if (mechatronics.equals("real")) { MECHATRONICS_TYPE = MiniAgvMechatronicsIB.REAL; }
    else { MECHATRONICS_TYPE = MiniAgvMechatronicsIB.SIMULATED; }
    //
    String statefinder = properties.getProperty("miniagv.statefinder","simulated");
    System.out.println("miniagv.statefinder=" + statefinder);
    if      (statefinder.equals("odometric")) { STATEFINDER_TYPE = ODOMETRIC; }
    else if (statefinder.equals("simulated")) { STATEFINDER_TYPE = SIMULATED; }
    else if (statefinder.equals("external"))  { STATEFINDER_TYPE = EXTERNAL; }
    else if (statefinder.equals("external&odometric")) { STATEFINDER_TYPE = EXTERNAL_AND_ODOMETRIC; }
    else if (statefinder.equals("external&simulated")) { STATEFINDER_TYPE = EXTERNAL_AND_SIMULATED; }
    System.out.println("STATEFINDER_TYPE=" + STATEFINDER_TYPE);
    //
    //
    miniagvIdName = properties.getProperty("miniagv.id.name","null");
    if (miniagvIdName.equals("null")) {
      byte[] rawAddress  = new byte[4];
      try {
        // Obtain the agvNumber from the last byte of it's own IP-address
        InetAddress agvAddr = InetAddress.getLocalHost();
        rawAddress = agvAddr.getAddress();
        agvNumber  = rawAddress[3];
      } catch (Exception e) {
        System.out.println("Error getting id, e="+e.toString());
        e.printStackTrace();
        System.exit(0);
      }
      miniagvIdName = "agv" + agvNumber;
      System.out.println("miniagv.id.name=" + miniagvIdName + " (taken from ipNumber)");
    }
    else {
      System.out.println("miniagv.id.name=" + miniagvIdName + " (taken from -D command line)");
    }

    // SERVICE-ID: if stored on disk: get it
    storedServiceID = null;
    try {
      File file = new File(AgileSystem.agileframesDataPath + "ServiceID_AGV"+String.valueOf(agvNumber));
      if (file.canRead()) {
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        storedServiceID = new ServiceID(dis);
        storedSuccesfully = true;
        System.out.println("ServiceID succesfully read from disk: "+storedServiceID.toString());
      } else { System.out.println("Cannot read from file to read serviceID"); }
    } catch (Exception e) {
      System.out.println("Exception while reading ServiceID: Exception ignored.");
    }
  }

  public FuSpace getState() throws RemoteException {
    System.out.println("getState: "+stateFinder.getObservedState().toString());
    return stateFinder.getObservedState();
  }
  public int getMachineNumber() throws RemoteException { return agvNumber; }

  public MachineRemote.Properties getProperties() throws RemoteException {
    MachineIB.MachineProperties props = new MiniAgvProperties();// sets spec props
    // general props:
    props.name = getName();
    props.activeTime = AgileSystem.getTime() - awakeTime;
    return props;
  }
  private boolean lookupDistributor() {
    ServiceTemplate st = new ServiceTemplate(null, new Class[] {com.agileways.miniworld.lps.RemoteDistributor.class} , null);
    Object obj = AgileSystem.lookup(st);
    if (obj == null) {
      System.out.println("MiniAgv didnot find a RemoteDistributor");
      return false;
    }
    distributor = (RemoteDistributor)obj;
    return true;
  }

  //--- Methods inherited from RemoteStateListener ---
  public boolean setLed(boolean ledOn) throws java.rmi.RemoteException {
    return ((MiniAgvMechatronicsIB)mechatronics).setLed(ledOn);
  }

  /**
   * Inherit from RemoteStateListener.
   * Called by a RemoteDistributor (MiniWorldXYADistributor).
   */
  public void setState(FuSpace state) throws java.rmi.RemoteException {
    ((MiniAgvStateFinder)stateFinder).setExternalState(state);
  }
  /**
   * Inherited from RemoteStateListener.
   * Needed to know in StateFinder
   * See comments in RemoteStateListener.
   */
  public void setScale(double scale) throws java.rmi.RemoteException {
    //System.out.println("Scale set --> "+scale);
    MiniAgvConfig.scale = scale;
  }

  public void dispose() {
    super.dispose();
    try {
      if (distributor != null) {
        distributor.unregisterStateListener(agvNumber);
      }
    } catch (Exception e) {
      System.out.println("Exception while disposing MiniAgv. Exception ignored.");
    }
  }

  //---------------- Class MiniAgvProperties --------------------
  public class MiniAgvProperties extends MachineIB.MachineProperties implements java.io.Serializable {
    public MiniAgvProperties() {
      try{
        specProps = new MachineIB.MachineProperties.Property[7];
        specProps[0] = new MachineIB.MachineProperties.Property();
        specProps[0].description = "Speed";
        specProps[0].type = DOUBLE;
        specProps[1] = new MachineIB.MachineProperties.Property();
        specProps[1].description = "Ref.Speed";
        specProps[1].type = DOUBLE;
        specProps[2] = new MachineIB.MachineProperties.Property();
        specProps[2].description = "Acceleration";
        specProps[2].type = DOUBLE;
        specProps[3] = new MachineIB.MachineProperties.Property();
        specProps[3].description = "Evolution";
        specProps[3].type = DOUBLE;
        specProps[4] = new MachineIB.MachineProperties.Property();
        specProps[4].description = "Deviation";
        specProps[4].type = DOUBLE;
        specProps[5] = new MachineIB.MachineProperties.Property();
        specProps[5].description = "Battery";
        specProps[5].type = BOOLEAN;
        specProps[6] = new MachineIB.MachineProperties.Property();
        specProps[6].description = "Angle A";
        specProps[6].type = DOUBLE;

        if (manoeuvreDriver.getManoeuvre() != null) {
          specProps[0].value = manoeuvreDriver.getManoeuvre().getCalcSpeed();
          specProps[1].value = ((MiniAgvManoeuvreDriver)manoeuvreDriver).getRefSpeed();
          specProps[2].value = manoeuvreDriver.getManoeuvre().getReferenceAcceleration();
          specProps[3].value = manoeuvreDriver.getManoeuvre().getCalcEvolution();
          specProps[4].value = manoeuvreDriver.getManoeuvre().getCalcDeviation();

          if (((MiniAgvPhysicalDriver)physicalDriver).getBatteryValue()) {
            specProps[5].value = 1.0;
          } else {
            specProps[5].value = 0.0;
          }
          specProps[6].value = ((MiniAgvManoeuvreDriver)manoeuvreDriver).getAlpha();
        }
      } catch (Exception e) { e.printStackTrace(); }
    }
  }

}



