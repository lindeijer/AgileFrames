package com.agileways.forces.miniagv;
import java.util.Properties;

public class MiniAgvConfig {
  public static final int    SERVO_BYTE_RANGE      = 127;
  public static final int    MOTOR_BYTE_RANGE      = 127;

  public static final double LENGTH_M              = 0.600;
  public static final double WIDTH_M               = 0.140;
  public static final double EDGE2CROWNCENTER_M    = 0.150;
  public static final double CENTER2CROWNCENTER_M  = (LENGTH_M - 2*EDGE2CROWNCENTER_M) / 2;
  public static final double WHEELBASE_M           = 0.138;
  public static final double WHEELDIAMETER_M       = 0.057;// checked it 14-MAY-01 (HW)
  public static final double WHEELWIDTH_M          = 0.028;
  public static final double SPEED_MAX_M$S         = 0.387;//0.330;//0.250;
  // het blijkt: max speed, 68 pulsen p sec: berekening: 8.22 m/s (real) = 0.33 m/s (mini)
  public static final double ACCELERATION_MAX_M$S2 = 0.120;
  public static final double DECELERATION_MAX_M$S2 = 0.120;
  public static final double STEERANGLE_MAX_DEG    = 40.0;//42.0??;  // used to be 30...
  public static final double STEERANGLE_MAX_RAD    = Math.toRadians(STEERANGLE_MAX_DEG);
  public static final double SCALE                 = 25.0;  // applies to speed and distances
  public static final int    SPIKES_ON_DISH        = 10;  // black and white dashes
  public static final double DRIVEWHEEL_RATIO      = 63.0 / 17.0;  // ratio between motorreduction and differentieel

  public static final double DISTANCE_PER_SPIKE_M =
    WHEELDIAMETER_M * Math.PI / (SPIKES_ON_DISH * DRIVEWHEEL_RATIO);

//  public static final int    STEERING_LATENCY_MS$DEG = 10;  // msec/degree (= 190msec / (60deg / 3))
//  public static final int    SAFETY_MARGIN_MM        = DISTANCE_PER_SPIKE_M / 1000 + 1;


  public static final int    SETTINGS_SPEED_MAX = 127; // + = in direction out of the agv
  public static final int    SETTINGS_ANGLE_MAX = 127; // + = looking from the center to the left

  // Settings below are altered due to the inaccuracy of Windows;Windows calculates only 18 times/sec = 55msec)\
  // a new elapsed time (used by currentTimeMillis()). I hope Linux does better!
  public static       double MAIN_CYCLE_TIME_S   = 0.050;//0.20; // 200 in case of Win98 && 50 on linux; // msec
//  public static final int    SETTINGS_POLLER_CYCLE_TIME_MS = 10;  //  55 in case of Win98 && 10 on linux; // msec; minimum accuracy of currentTimeMillis() = 55 thanks to Windows

  public static double scale = 1.0; //can be set in MiniAgv, read in instructor and statefinder
  /////////////////////////////////////////////////////////////////////

/*  static {
    Properties properties = System.getProperties();
    String miniagvMfdCycletime = properties.getProperty("miniagv.mfd.cycletime","null");
    if (miniagvMfdCycletime == "null") {
      SETTINGS_MAIN_CYCLE_TIME_MS = 250;
    } else {
      try {
        SETTINGS_MAIN_CYCLE_TIME_MS = Integer.parseInt(miniagvMfdCycletime);
      } catch (Exception e) {
        SETTINGS_MAIN_CYCLE_TIME_MS = 250;
      }
    }
    System.out.println("miniagv.mfd.cycletime=" + SETTINGS_MAIN_CYCLE_TIME_MS);
  }*/

  public static double scaleToRealWorld(double p) {  return p * MiniAgvConfig.SCALE; }
  public static double scaleToMiniWorld(double p) {  return p / MiniAgvConfig.SCALE; }
}
