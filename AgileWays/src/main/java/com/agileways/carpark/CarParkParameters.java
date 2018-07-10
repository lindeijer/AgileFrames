package com.agileways.carpark;

public class CarParkParameters {
    /** Scales all indices */
    // watch out: doesnot scale the scene.transform!!
  public static double scale = 0.8;

  //-- Following will be defined in Scene!!! --
  /** Translation of entire model in x direction */
  //public static double xTrans = -60.0*scale + 120*scale;
  /** Translation of entire model in y direction */
  //public static double yTrans = -60.0*scale;


  /** Length of the AGVs in this demo is meters */
  public static double AGV_LENGTH = 17.5*scale;//actually it is 15.0
  public static double AGV_WIDTH = 7.0*scale;//actually it is 3.5
  /** Turn Radius of AGVs in meters */
  public static double TURN_RADIUS = 15.0*scale;
  public static double PARK_LENGTH = 32.5 * scale;

  /** Distance between lanes in meters
   * Conditions:
   * 1. DIST_BETW_LANES - TURN_RADIUS - AGV_LENGTH/2 >= 0
   * 2. DIST_BETW_LANES - TURN_RADIUS*2 >= 0
   */
  public static double DIST_BETW_LANES = 40.0*scale;

  public static double MAX_SPEED = 9.675;
  public static double MAX_DECEL = 5.0;
  public static double MAX_ACCEL = 2.0;
  public static double MAX_DEVI = 0.5;

}