package com.agileways.demo;

public class DemoParameters {
  /** Scales all indices */
  public static double scale = 0.8;

  /** Translation of entire model in x direction */
  //public static double xTrans = -60.0*scale;
  /** Translation of entire model in y direction */
  //public static double yTrans = -60.0*scale;


  /** Length of the AGVs in this demo is meters */
  public static double AGV_LENGTH = 17.5*scale;
  /** Turn Radius of AGVs in meters */
  public static double TURN_RADIUS = 15.0*scale;

  /** Distance between lanes in meters
   * Conditions:
   * 1. DIST_BETW_LANES - TURN_RADIUS - AGV_LENGTH/2 >= 0
   * 2. DIST_BETW_LANES - TURN_RADIUS*2 >= 0
   */
  public static double DIST_BETW_LANES = 40.0*scale;

  public static double MAX_SPEED = 5.0;
  public static double MAX_DECEL = 5.0;
  public static double MAX_ACCEL = 2.0;
  public static double MAX_DEVI = 0.5;

//  public static double OFFSET_AT_PARK = 0;
//  public static double NOTIFY_DISTANCE =1f;

  public static double getY(int direct, int lane){
    switch (lane) {
      case 0: switch (direct) {
        case 0: return 3*DIST_BETW_LANES - AGV_LENGTH/2;
        case 1: return 2*DIST_BETW_LANES;
        case 2: return AGV_LENGTH/2;
        case 3: return DIST_BETW_LANES;
      }
      case 1: switch (direct) {
        case 0: return 3*DIST_BETW_LANES - AGV_LENGTH/2;
        case 1: return DIST_BETW_LANES;
        case 2: return AGV_LENGTH/2;
        case 3: return 2*DIST_BETW_LANES;
      }
    }
    return Double.NaN;
  }

  public static double getX(int direct, int lane){
    switch (lane) {
      case 0: switch (direct) {
        case 0: return DIST_BETW_LANES;
        case 1: return 3*DIST_BETW_LANES - AGV_LENGTH/2;
        case 2: return 2*DIST_BETW_LANES;
        case 3: return AGV_LENGTH/2;
      }
      case 1: switch (direct) {
        case 0: return 2*DIST_BETW_LANES;
        case 1: return 3*DIST_BETW_LANES - AGV_LENGTH/2;
        case 2: return DIST_BETW_LANES;
        case 3: return AGV_LENGTH/2;
      }
    }
    return Double.NaN;
  }

  public static double getAlpha(int direct){
    switch (direct) {
      case 0: return -Math.PI/2;
      case 1: return -Math.PI;
      case 2: return Math.PI/2;
      case 3: return 0;
    }
    return Double.NaN;
  }

}