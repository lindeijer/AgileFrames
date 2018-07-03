package com.agileways.forces.miniagv;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.forces.PhysicalDriverIB;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.server.AgileSystem;

public class MiniAgvPhysicalDriver extends PhysicalDriverIB {
  //----------------------- Attributes -------------------------------
  private MiniAgvInstructor.MiniAgvInstruction realInstruction;
  private MiniAgvSetting setting = new MiniAgvSetting();
  private static final int WHEELSA = 0;
  private static final int WHEELSB = 1;
  private static final double MAXANGLE = MiniAgvConfig.STEERANGLE_MAX_RAD;
  private static final double MAXSPEED = MiniAgvConfig.SPEED_MAX_M$S;
  private static final int  SERVORANGE = MiniAgvConfig.SERVO_BYTE_RANGE;
  private static final int  MOTORRANGE = MiniAgvConfig.MOTOR_BYTE_RANGE;
  public static final boolean DEBUG = false;

  //----------------------- Constructor ------------------------------
  public MiniAgvPhysicalDriver() {
  }

  //----------------------- Methods ----------------------------------
  public void update() {
    long prevTime = timeStamp;
    this.timeStamp = AgileSystem.getTime();
    double dT = ((double)(timeStamp - prevTime)) / 1000.0;

    MiniAgvInstructor.MiniAgvInstruction advInstruction = ((MiniAgvInstructor)instructor).getAdvisedInstruction();
    MiniAgvSetting newSetting = computeSetting(advInstruction, setting);
    ((MiniAgvMechatronicsIB)mechatronics).writeSetting(newSetting.toBytes());
    setting = newSetting;
    realInstruction = computeInstruction(newSetting);
    if (DEBUG) System.out.println("*D* PhysDriv: realInstruction="+realInstruction.toString());
    if (DEBUG) System.out.println("*D* PhysDriv: setting="+setting.toString());

    double prevSpeed = inducedSpeed;
    this.inducedSpeed = (realInstruction.speedA - realInstruction.speedB) / 2;
    //System.out.println("@@ inuced speed="+inducedSpeed);
    int SIGN = 1; if (inducedSpeed < 0) { SIGN = -1; }
    this.mechatronicAcceleration = (inducedSpeed - prevSpeed) / dT;
    this.mechatronicCourse = ((MiniAgvInstructor)instructor).deductPilotCourse(realInstruction.angleA, realInstruction.angleB, SIGN, new XYASpace(0,0,0));
  }

  private MiniAgvSetting computeSetting(MiniAgvInstructor.MiniAgvInstruction instruction, MiniAgvSetting lastSetting){
    if ((instruction == null) || (lastSetting == null)) { return null; }
    if (!newObservationReceived) {
      // need to stand still when no observation could be made
      return new MiniAgvSetting((byte)0, (byte)0, (byte)0, (byte)0);
    }

    double miniAngleA = instruction.angleA;                     // don't scale angles!!
    if (miniAngleA > MAXANGLE) { miniAngleA = MAXANGLE; }       // check boundaries
    if (miniAngleA < -MAXANGLE) { miniAngleA = -MAXANGLE; }     // idem
    byte servoA = (byte) (SERVORANGE * (miniAngleA / MAXANGLE));// set to byte
    if (servoA * lastSetting.servoA < 0) { servoA = 0; }        // check sign-switch

    double miniAngleB = instruction.angleB;
    if (miniAngleB > MAXANGLE) { miniAngleB = MAXANGLE; }
    if (miniAngleB < -MAXANGLE) { miniAngleB = -MAXANGLE; }
    byte servoB = (byte) (SERVORANGE * (miniAngleB / MAXANGLE));
    if (servoB * lastSetting.servoB < 0) { servoB = 0; }

    double miniSpeedA = MiniAgvConfig.scaleToMiniWorld(instruction.speedA)*MiniAgvConfig.scale;
    if (miniSpeedA > MAXSPEED) { miniSpeedA = MAXSPEED; }
    if (miniSpeedA < -MAXSPEED) { miniSpeedA = -MAXSPEED; }
    //byte motorA = (byte) (MOTORRANGE * (miniSpeedA / MAXSPEED));
    // graph is NOT straight:
    // additions: 31 AUG 01 HJ Wierenga
    double range = (double)MOTORRANGE;
    byte motorA = 0;
    if (miniSpeedA >= 0) {
      if (miniSpeedA < MAXSPEED * 5.0/7.0 ) { motorA = (byte) (miniSpeedA * (range/MAXSPEED) * (42.0/65.0)); }
      else { motorA = (byte) (range * 6.0/13.0 + (miniSpeedA - MAXSPEED * 5.0/7.0) * (range/MAXSPEED) * (49.0/26.0)); }
      if ((motorA != 0) && (motorA < 25)) { motorA = 25; }
    }
    if (miniSpeedA < 0) {
      if (-miniSpeedA < MAXSPEED * 5.0/7.0 ) { motorA = (byte) (miniSpeedA * (range/MAXSPEED) * (42.0/65.0)); }
      else { motorA = (byte) (-range * 6.0/13.0 + (miniSpeedA + MAXSPEED * 5.0/7.0) * (range/MAXSPEED) * (49.0/26.0)); }
      if ((motorA != 0) && (motorA > -25)) { motorA = -25; }
    }
    //this was it..
  //  System.out.println("miniSpeedA = "+miniSpeedA+"  motorA = "+motorA);
    if (motorA * lastSetting.motorA < 0) { motorA = 0; }

    double miniSpeedB = MiniAgvConfig.scaleToMiniWorld(instruction.speedB)*MiniAgvConfig.scale;
    if (miniSpeedB > MAXSPEED) { miniSpeedB = MAXSPEED; }
    if (miniSpeedB < -MAXSPEED) { miniSpeedB = -MAXSPEED; }
    //byte motorB = (byte) (MOTORRANGE * (miniSpeedB / MAXSPEED));
    // graph is NOT straight:
    // additions: 31 AUG 01 HJ Wierenga
    byte motorB = 0;
    if (miniSpeedB >= 0) {
      if (miniSpeedB < MAXSPEED * 5.0/7.0 ) {
        motorB = (byte) (miniSpeedB * (range/MAXSPEED) * (42.0/65.0));
      } else {
        motorB = (byte) (range * 6.0/13.0 + (miniSpeedB - MAXSPEED * 5.0/7.0) * (range/MAXSPEED) * (49.0/26.0));
      }
      if ((motorB != 0) && (motorB < 25)) { motorB = 25; }
    }
    if (miniSpeedB < 0) {
      if (-miniSpeedB < MAXSPEED * 5.0/7.0 ) {
        motorB = (byte) (miniSpeedB * (range/MAXSPEED) * (42.0/65.0));
      } else {
        motorB = (byte) (-range * 6.0/13.0 + (miniSpeedB + MAXSPEED * 5.0/7.0) * (range/MAXSPEED) * (49.0/26.0));
      }
      if ((motorB != 0) && (motorB > -25)) { motorB = -25; }
    }
    //this was it..
    if (motorB * lastSetting.motorB < 0) { motorB = 0; }

    return new MiniAgvSetting(servoA, servoB, motorA, motorB);
  }

  private MiniAgvInstructor.MiniAgvInstruction computeInstruction(MiniAgvSetting setting){
    if (setting == null) { return null; }
    double angleA = MAXANGLE * ((double)setting.servoA)/((double)SERVORANGE);
    double angleB = MAXANGLE * ((double)setting.servoB)/((double)SERVORANGE);
    //// graph is not straight:
    double motorA = (double)setting.motorA;
    double RANGE = (double)MOTORRANGE;
    double miniSpeedA = 0;
    if (motorA >= 0) {
      if (motorA < RANGE * 6.0/13.0) {
        miniSpeedA = motorA * (MAXSPEED / RANGE) * (65.0/42.0);
      } else {
        miniSpeedA = MAXSPEED * (5.0/7.0) + (motorA - (6.0/13.0) * RANGE) * (MAXSPEED / RANGE) * (26.0 / 49.0);
      }
    } else { //motorA < 0
      if (-motorA < RANGE * 6.0/13.0) {
        miniSpeedA = motorA * (MAXSPEED / RANGE) * (65.0/42.0);
      } else {
        miniSpeedA = -MAXSPEED * (5.0/7.0) + (motorA + (6.0/13.0) * RANGE) * (MAXSPEED / RANGE) * (26.0 / 49.0);
      }
    }
    //double speedA = MiniAgvConfig.scaleToRealWorld(MAXSPEED * ((double)setting.motorA)/((double)MOTORRANGE) ) / MiniAgvConfig.scale;
    double speedA = MiniAgvConfig.scaleToRealWorld(miniSpeedA) / MiniAgvConfig.scale;
//    System.out.println("motorA = "+motorA+"  miniSpeedA = "+miniSpeedA);

    //// graph is not straight:
    double motorB = (double)setting.motorB;
    double miniSpeedB = 0;
    if (motorB >= 0) {
      if (motorB < RANGE * 6.0/13.0) {
        miniSpeedB = motorB * (MAXSPEED / RANGE) * (65.0/42.0);
      } else {
        miniSpeedB = MAXSPEED * (5.0/7.0) + (motorB - (6.0/13.0) * RANGE) * (MAXSPEED / RANGE) * (26.0 / 49.0);
      }
    } else { //motorB < 0
      if (-motorB < RANGE * 6.0/13.0) {
        miniSpeedB = motorB * (MAXSPEED / RANGE) * (65.0/42.0);
      } else {
        miniSpeedB = -MAXSPEED * (5.0/7.0) + (motorB + (6.0/13.0) * RANGE) * (MAXSPEED / RANGE) * (26.0 / 49.0);
      }
    }
    //double speedB = MiniAgvConfig.scaleToRealWorld(MAXSPEED * ((double)setting.motorB)/((double)MOTORRANGE) ) / MiniAgvConfig.scale;
    double speedB = MiniAgvConfig.scaleToRealWorld(miniSpeedB) / MiniAgvConfig.scale;

    return ((MiniAgvInstructor)instructor).createInstruction(angleA, angleB, speedA, speedB);
  }


  //----------------------- Getters and Setters ----------------------
  public MiniAgvInstructor.MiniAgvInstruction getRealisedInstruction() { return realInstruction; }

  public FuSpace getRealisedState(double distance, FuSpace origState) {
    //distance must be a signed value with <0 if motorA drives backwards
    if (origState == null) { return new XYASpace(0,0,0); }
    if (realInstruction == null) { return origState; }
    FuSpace.FuPath path =
      ((MiniAgvInstructor)instructor).deductPilotCourse(
        realInstruction.angleA,
        realInstruction.angleB,
        distance, //signed!!
        (XYASpace)origState
      );
    // now watch out! distance should be positive if you want to find a point on the
    // described path. so, if distance is negative (when driving backwards), the absolute
    // value should be taken
    return path.getConnectionPoint(Math.abs(distance));
  }
  // Returns signed distance covered since last update time that this method was called
  // if the read nr of spikes does not look reliable, Double.NaN should be returned
  public double getDistanceCovered() {
    int spikes = ((MiniAgvMechatronicsIB)mechatronics).readSpikes();
    // spikes is unsigned!    // methods readSpikes() resets spikes-value on agv
    // the value of spikes is not reliable if the speed < 25 bytes
    if ( (Math.abs((int)setting.motorA) < 25) ) {
      //System.out.println("!! spikes="+spikes+"   setting.motorA="+setting.motorA);
      if (DEBUG) System.out.println("*D* PhysDriv.getDistanceCovered = NaN, because of slow-driving");
      return Double.NaN;
    }

    double distance = spikes * MiniAgvConfig.scaleToRealWorld( MiniAgvConfig.DISTANCE_PER_SPIKE_M );
    // distance needs to be SIGNED:
    distance *= ((int)setting.motorA) / Math.abs((int)setting.motorA);
    //System.out.println("!! spikes="+spikes+"   distance="+distance);
    if (DEBUG) System.out.println("*D* PhysDriv.getDistCovered(): spikes = "+spikes+"  distance = "+distance);
    return distance;
  }
  // Returns true if enough power, otherwise false;
  // Dont look up the value more than once every 30 secs
  private long lastBatteryCheck = 0;
  private boolean lastBatteryValue = false;
  public boolean getBatteryValue() {
    if (lastBatteryCheck + 30000 < AgileSystem.getTime()) {
      lastBatteryCheck = AgileSystem.getTime();
      lastBatteryValue = ((MiniAgvMechatronicsIB)mechatronics).readPowerValue();
    }
    return lastBatteryValue;
  }

  // Have to be set false if no observations of current state (by StateFinder)
  // can be made at this time.
  private boolean newObservationReceived = true;
  public void setNewObservationReceived(boolean isReceived) {
    newObservationReceived = isReceived;
  }


  //------------------------- Class ------------------------------------
  public class MiniAgvSetting implements MachineSetting {
    byte servoA = 0; byte servoB = 0; byte motorA = 0; byte motorB = 0;
    public MiniAgvSetting() {}
    public MiniAgvSetting(byte[] byteSetting) {
      motorA = byteSetting[0];
      motorB = byteSetting[1];// motorB is unused, mech should solve this problem
      servoA = byteSetting[2];
      servoB = byteSetting[3];
    }
    public MiniAgvSetting(byte servoA, byte servoB, byte motorA, byte motorB){
      this.servoA = servoA; this.servoB = servoB; this.motorA = motorA; this.motorB = motorB;
    }
    public byte[] toBytes() {
      byte[] setting = new byte[10];
      setting[0] = motorA;
      setting[1] = motorB;//unused
      setting[2] = servoA;
      setting[3] = servoB;
      setting[4] = 0;//LED
      setting[5] = 0;//unused
      setting[6] = 0;//battery-status1, read
      setting[7] = 0;//battery-status2, read
      setting[8] = 0;//odometer, read
      setting[9] = 0;//odometer, read
      return setting;
    }
    public String toString() {
      return "servoA = "+servoA+"; servoB = "+servoB+"; motorA = "+motorA+"; motorB = "+motorB;
    }
  }
}
