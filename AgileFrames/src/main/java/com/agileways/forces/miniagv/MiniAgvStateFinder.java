package com.agileways.forces.miniagv;
import java.util.Properties;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.StateFinderIB;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.server.AgileSystem;

public class MiniAgvStateFinder extends StateFinderIB {
  //----------- Attributes ------------
  private MiniAgvLocalInformation localInfo = null;
  private FuSpace prevObservedState = null;//new XYASpace(0,0,0);
  private int TYPE = 0;
  private long prevTimeStamp;
  private static double odometricFactor = 1.0;//will be loaded in static
  private boolean begon = false;
  //----------- Constructor -----------
  //BE SURE that initialize is called after creation!!
  public MiniAgvStateFinder(int AGV_TYPE) {
    this.TYPE = AGV_TYPE;
    // should be:
    // this.TYPE = MiniAgv.STATEFINDER_TYPE;
    this.localInfo = new MiniAgvLocalInformation();
  }
  //----------- Methods ---------------
  /**/private double prevObsEvol = 0.0;
  public void update(double prevCalcEvol) {
    prevTimeStamp = timeStamp;
    prevObservedState = observedState;
    //System.out.println("1 prevObsEvol="+prevObsEvol+"  obsEvol="+observedEvolution);
/**/    if (!Double.isNaN(observedEvolution)) prevObsEvol = observedEvolution;
    //System.out.println("2 prevObsEvol="+prevObsEvol+"  obsEvol="+observedEvolution);
    timeStamp = AgileSystem.getTime();

    this.observedState = localInfo.getCurrentState();
////    this.observedEvolution =
////      XYASpace.evolutionDistance((XYASpace)prevObservedState, (XYASpace)observedState) + prevCalcEvol;
/**/    this.observedEvolution =
/**/      XYASpace.evolutionDistance((XYASpace)prevObservedState, (XYASpace)observedState) + prevObsEvol;
/**/    if (Double.isNaN(observedEvolution)) {observedEvolution=0;}
  }

  public void setExternalState(FuSpace extState) {
    this.localInfo.setExternalState(extState);
  }

  //----------- Classes ---------------
  public class MiniAgvLocalInformation implements LocalInformation {
    //-- Attributes --
    private FuSpace currentState = null;//new XYASpace(0,0,0);
    private FuSpace prevState = null;
    private long timeStamp, prevTimeStamp;
    private double dT = 0;
    //-- Constructor --
    MiniAgvLocalInformation(){}
    //-- Methods --
    public FuSpace getCurrentState() {
      prevTimeStamp = timeStamp;
      prevState = currentState;
      timeStamp = AgileSystem.getTime();
      dT = ((double)(timeStamp - prevTimeStamp))/1000;// in seconds
      switch(TYPE) {
        case MiniAgv.SIMULATED:
          currentState = getSimulatedState();
          break;
        case MiniAgv.ODOMETRIC:
          currentState = getOdometricState();
          break;
        case MiniAgv.EXTERNAL:
          if (externalUpdate) {
            ((MiniAgvPhysicalDriver)physicalDriver).setNewObservationReceived(true);
          } else {
            ((MiniAgvPhysicalDriver)physicalDriver).setNewObservationReceived(false);
          }
          currentState = getExternalState();
          break;
        case MiniAgv.EXTERNAL_AND_ODOMETRIC:
          FuSpace odoState = getOdometricState();

          if ( (!begon) && (!externalUpdate) ) { break; }// have to start with external
          begon = true;

          // the odometric-sensor has to be read all cycles, because
          // it must be reset every cycle. If camera-updates are available
          // the odometric will not be used, however.
          if (externalUpdate) { currentState = getExternalState(); }
          else { currentState = odoState; }
          break;
        case MiniAgv.EXTERNAL_AND_SIMULATED:
          if ( (!begon) && (!externalUpdate) ) { break; }// have to start with external
          begon = true;

          if (externalUpdate) {
            currentState = getExternalState();
          } else {
            currentState = getSimulatedState();
          }
          break;
      }
//      if (currentState != null) { System.out.println("curState = "+currentState.toString()); } else {System.out.println("curState = null");}
      return currentState;
    }
    //--------- Simulated -----
    private FuSpace getSimulatedState() {
      double speed = physicalDriver.getInducedSpeed();
      double distance = dT * speed; // acceleration is zero in interval
      //distance /= MiniAgvConfig.scale;// not here, but in physDriver
      return ((MiniAgvPhysicalDriver)physicalDriver).getRealisedState(distance, prevState);
    }
    //--------- Odometric ------
    private FuSpace getOdometricState() {
      FuSpace state = null;
      double distance = ((MiniAgvPhysicalDriver)physicalDriver).getDistanceCovered();
      // this distance is a signed value!
      // returns NaN if driving too slow (no reliable odometric outcome)
      if (Double.isNaN(distance)) { return getSimulatedState(); }
      distance *= odometricFactor;//must be around 1
      //distance /= MiniAgvConfig.scale;// not here, but in physDriver!!
//      System.out.println("distance="+distance);
      return ((MiniAgvPhysicalDriver)physicalDriver).getRealisedState(distance, prevState);
    }
    //--------- External ------
    private FuSpace externalState = null;
    private boolean externalUpdate = false;
    private long extTimeStamp = 0;
    private FuSpace getExternalState() {
      externalUpdate = false;
      return externalState;
    }
    public void setExternalState(FuSpace extState) {
      //System.out.println("@@@@ setExtState");
      this.extTimeStamp = AgileSystem.getTime();
      this.externalState = extState;
      this.externalUpdate = true;
    }
  }

  public class MiniAgvResponseInformation implements ResponseInformation {}

  static {
    Properties properties = System.getProperties();
    //
    String speedfactor = properties.getProperty("miniagv.speedfactor","null");
    if (!speedfactor.equals("null")) {
      odometricFactor = ((double)Integer.parseInt(speedfactor))/1000;
    }
  }

}
