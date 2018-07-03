package com.agileways.crossscene;

import net.agileframes.forces.xyaspace.XYATransform;
import com.agileways.forces.miniagv.MiniAgvConfig;

public class CrossParameters {
  public static final double LAYOUT_SCALE = 1.25;//was: 0.8
//  public static final double LAYOUT_TRANSX = -60.0 * LAYOUT_SCALE;
//  public static final double LAYOUT_TRANSY = 0.0 * LAYOUT_SCALE;

  //public static final double LAYOUT_TRANSX = 0.0 * LAYOUT_SCALE;
  //public static final double LAYOUT_TRANSY = 0.0 * LAYOUT_SCALE;//-30

  public static final double WIDTH_LANE = 30.0 * LAYOUT_SCALE;//crabs: u = 35.14, x = 33.17
  public static final double LENGTH_PARK_A = 20.0 * LAYOUT_SCALE;//40
  public static final double LENGTH_PARK_C = 25.0 * LAYOUT_SCALE;//80
  public static final double LENGTH_PARK_CENTER = 10.0 * LAYOUT_SCALE;//20
  public static final double TOTAL_LENGTH = LENGTH_PARK_A + LENGTH_PARK_CENTER +
                                            LENGTH_PARK_C + 2 * 3.317 * WIDTH_LANE/2;

  public static final double MAX_SPEED = 9.25;
  public static final double MAX_DECELERATION = 3.0;
  public static final double MAX_ACCELERATION = 3.0;
  public static final double MAX_DEVIATION = 0.5;
  public static final double CYCLE_TIME = MiniAgvConfig.MAIN_CYCLE_TIME_S;   // in seconds

  public static final XYATransform TRANSFORM = new XYATransform(0,0,0);

}
