package com.agileways.miniworld.lps;
import net.agileframes.server.ServerIB;
import net.agileframes.server.AgileSystem;
import net.jini.core.lookup.ServiceID;
import com.agileways.forces.miniagv.MiniAgvConfig;
import java.io.*;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.forces.xyaspace.XYATransform;

import net.agileframes.forces.xyaspace.XYASpace;

// this object uploads its stub, which can be downloaded by a RemoteStateListener
// the stateListener will be registered and a MWXYADispatcher (inner-class) will
// be created for every listener.
public class MiniWorldXYADistributor extends ServerIB implements RemoteDistributor {
  //--- Attributes ---
  private CameraSystem cameraSystem = null;
  private MiniWorldXYADispatcher[] dispatchers = new MiniWorldXYADispatcher[50];
  private int[] listenerList = new int[50];
  private int dispatcherCounter = 0;

  //--- Constructor ---
  public MiniWorldXYADistributor(String name, ServiceID id) throws java.rmi.RemoteException {
    super(name, id);
    // create downloadable stub for this object:
    AgileSystem.registerService(this, id, this, null);

    readTransformAndScaleValues();

    // create connection to the camera system
    cameraSystem = new CameraSystem(this);
    System.out.println("CameraSystem started in MiniWorldXYADistributor");
  }

  //--- Methods ---
  public void registerStateListener(int machineNr, RemoteStateListener listener) {
    try {
      System.out.println("Machine "+machineNr+" is registering.");
      for (int i = 0; i < dispatcherCounter; i++) {
        if (listenerList[i] == machineNr ) { unregisterStateListener(machineNr); break; }
      }
      dispatchers[dispatcherCounter] = new MiniWorldXYADispatcher(listener, cameraSystem, machineNr);
      listenerList[dispatcherCounter] = machineNr;
      dispatchers[dispatcherCounter].setTransformAndScale(transform,scale);
      dispatcherCounter++;
      try { listener.setScale(scale); }
      catch (Exception e) { System.out.println("Could not set scale on listener."); }

      sendListenerList();
    } catch (Exception e) { e.printStackTrace(); }
  }

  public void unregisterStateListener(int machineNr) {
    System.out.println("Machine "+machineNr+" is unregistering...");
    for (int i = 0; i < dispatcherCounter; i++) {
      if (listenerList[i] == machineNr ) {
        System.out.println("Machine "+machineNr+" found in list...now checking out.");
        dispatchers[i].quit();
        System.arraycopy(dispatchers, i+1, dispatchers, i, dispatcherCounter-i-1);
        System.arraycopy(listenerList, i+1, listenerList, i, dispatcherCounter-i-1);
        dispatcherCounter--;
        break;
      }
    }
    sendListenerList();
  }

  public synchronized void sendListenerList() {
    int[] list = new int [dispatcherCounter];
    try { System.arraycopy(listenerList, 0, list, 0, dispatcherCounter); }
    catch (Exception e) { e.printStackTrace(); }
    System.out.println("MiniWorldXYADistributor.sendList() is called: "+dispatcherCounter+" machines are registered");
    cameraSystem.setListenerList(list);
  }

  public void distributeXYAData(int machineNr, double x, double y, double alpha) {
    int i = 0;
    for (i = 0; i < dispatcherCounter; i++) {  if (listenerList[i] == machineNr) { break; } }
    // when there are many agvs, this method is not so smart...
    if (Double.isNaN(x)) { System.out.println("uhoh, x=NAN data not dispatched"); return; }
    if (Double.isNaN(y)) {     System.out.println("uhoh, y=NAN data not dispatched");     return; }
    if (Double.isNaN(alpha)) { System.out.println("uhoh, alpha=NAN data not dispatched"); return; }
    if ( (dispatchers != null) && (dispatchers.length >= i ) && (dispatchers[i] != null) )
          dispatchers[i].distributeXYAData(x, y, alpha);
    //System.out.println("well, x=" + x + "   y=" + y + "   a=" + alpha);
  }

  public void distributeLedRequest(int machineNr, boolean ledOn) {
    int i = 0;
    for (i = 0; i < dispatcherCounter; i++) {
      if (listenerList[i] == machineNr) {
        dispatchers[i].distributeLedRequest(ledOn);
        break;
      }
    }
    // when there are many agvs, this method is not so smart...
  }

  public void setTransformAndScale(FuTransform t, double s) throws java.rmi.RemoteException {
    try {
      this.transform = t;
      this.scale = s;
      if (dispatchers != null) {
        for (int i = 0; i < dispatchers.length; i++) {
          if (dispatchers[i] != null) {
            dispatchers[i].setTransformAndScale(transform,scale);
          }
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
  }

  private FuTransform transform = null;
  private double scale = 1;//scale, 1 = normal
  private void readTransformAndScaleValues() {
    File file = new File(AgileSystem.agileframesDataPath + "MiniWorldPositionData");
    try {
      FileInputStream fis = new FileInputStream(file);
      DataInputStream dis = new DataInputStream(fis);
      // see MiniWorldPositionerFrame!
      // all ints, rot in degrees, scale in percent.
      int x = dis.readInt();
      int y = dis.readInt();
      int rotation = dis.readInt();//degrees!
      double rad = Math.PI * ((double)rotation) / 180.0;
      transform = new XYATransform((double)x, (double)y, rad);
      scale = ((double)dis.readInt())/100;
      System.out.println("MiniWorldXYADistributor: readTransformAndScale: t="+transform.toString()+"  scale="+scale);
    } catch (Exception e) { e.printStackTrace(); }
    if (dispatchers != null) {
     for (int i = 0; i < dispatchers.length; i++) {
       if (dispatchers[i] != null) {
         dispatchers[i].setTransformAndScale(transform,scale);
       }
     }
    }
  }

  //--- MAIN ---
  public static void main(String[] args) {
    System.out.println("Starting MiniWorldXYADistributor...");
    try { MiniWorldXYADistributor distributor = new MiniWorldXYADistributor("MiniWorldXYADistributor", null); }
    catch (Exception e) {
      System.out.println("Execption while creating MiniWorldXYADistributor. System will exit.");
      e.printStackTrace();
      System.exit(-1);
    }
  }

}
//--- Inner-class: MiniWorldXYADispatcher ---
// An instance of this class will be created for every listener registered
class MiniWorldXYADispatcher extends Thread {
  //--- Attributes ---
  private RemoteStateListener listener = null;
  private boolean active = true;
  private boolean externalXYAUpdate = false;
  private boolean externalLedRequest = false;
  private boolean ledOn;
  private double x_real_m, y_real_m, alpha;
  private CameraSystem camera = null;
  private int machineNr;
  //--- Constructor ---
  MiniWorldXYADispatcher(RemoteStateListener listener, CameraSystem camera, int machineNr) {
    this.listener = listener;
    this.camera = camera;
    this.machineNr = machineNr;
    this.start();
  }
  //--- Methods ---
  // receive data in millimeters and for miniAgv
  // we want in meters and for the BIG ones
  // we want a transform here!!! and maybe scale too !!!
  public synchronized void distributeXYAData(double x_mini_mm, double y_mini_mm, double alpha) {
    x_real_m = MiniAgvConfig.scaleToRealWorld(x_mini_mm / 1000.0);
    y_real_m = MiniAgvConfig.scaleToRealWorld(y_mini_mm / 1000.0);
    this.alpha = alpha;

    if (Double.isNaN(x_real_m)) { System.out.println("uhoh, x_real_m=NAN data not dispatched"); return; }
    if (Double.isNaN(y_real_m)) { System.out.println("uhoh, y_real_m=NAN data not dispatched");     return; }
    if (Double.isNaN(alpha))    { System.out.println("uhoh, alpha_read=NAN data not dispatched"); return; }


    XYASpace p = (XYASpace)transform.transform(new XYASpace(x_real_m, y_real_m, alpha));
    x_real_m    = p.getX() / scale;
    y_real_m    = p.getY() / scale;
    this.alpha  = p.getAlpha();
    //System.out.println("x = "+x_real_m+"    y = "+y_real_m);
    externalXYAUpdate = true;
    this.notify();
  }

  private XYATransform transform = new XYATransform(0,0,0);
  private double scale = 1;
  public synchronized void setTransformAndScale(FuTransform t, double scale) {
    this.transform = (XYATransform)t;
    this.scale = scale;
    try { this.listener.setScale(scale); }
    catch (Exception e) { System.out.println("scale could not be set"); }
    System.out.println("setTandS "+t.toString()+"  and s = "+scale);
  }

  public synchronized void distributeLedRequest(boolean ledOn) {
    //System.out.println("led-request: "+ledOn);
    this.ledOn = ledOn;
    externalLedRequest = true;
    this.notify();
  }

  public void run() {
    while (active) {
      try {
        synchronized (this) {
          this.wait();// notify in distributeXYAData/-LedRequest methods
        }
        if (externalXYAUpdate) {
          listener.setState(new XYASpace(x_real_m, y_real_m, alpha));
          externalXYAUpdate = false;
        }
        if (externalLedRequest) {
          boolean reply = listener.setLed(ledOn);
          camera.setLed(machineNr ,reply);
          externalLedRequest = false;
        }
      } catch (Exception e) {
        System.out.println("MiniWorldXYADispatcher failed. Exception will be ignored.");
        //e.printStackTrace();
      }
    }
  }

  public void quit() {
    active = false;
    synchronized (this) { this.notify(); }
  }
}