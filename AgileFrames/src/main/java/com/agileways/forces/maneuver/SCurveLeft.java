package com.agileways.forces.maneuver;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.forces.space.POSTransform;
import net.agileframes.forces.space.Position;


/**
 * A S-curve to the left with parameterized length, width and p.
 * @author Lindeijer, Evers, Wierenga
 * @version 0.0.1
 */


public class SCurveLeft extends Trajectory{
  float length;     // length of S-Curve
  float width;      // width of S-Curve
  float p; // distance between turning point and center of vehicle
  float radius;     // radius of S-Curve
  float max_radians;// radians for a half S-Curve

  public SCurveLeft() {}

  /**
   * SCurveLeft with p=0.
   */
  public SCurveLeft(float length, float width) {
    this(length,width,0);
  }


  /**
   * If width > length then the values of width and length are swapped.
   * @param length is the length of the s-curve.
   * @param width is the width of the s-curve.
   * @param p is distance between turning point and center of vehicle
   */
  public SCurveLeft(float length, float width, float p) {
    // width should always be the smallest
    if (width>length) {
      this.length = width;
      this.width = length;
      length = this.length;
      width = this.width;
    }
    else {
      this.length = length;
      this.width = width;
    }
    this.p = p;
    this.radius = (float)0.25 * ( width + length*length/width);
    this.max_radians = (float) Math.asin(length/(2*radius));
    this.domain = max_radians * radius * 2;
    this.ownTransform = new POSTransform(length, width, 0);
  }

 /**
  * Computes position.
  * @param u float being the evolution parameters on this trajectory.
  */
  public State compute(float u) {
    u = u - initialEvolution;
    if (u < 0)      { return null; }
    if (u > domain) { return null; }

    float x,y,orientation;

    if (u < (domain/2)) {
      float radian = u/radius;

      x = radius * (float)Math.sin(radian) + p*(float)Math.cos(radian);
      y = radius * (1 - (float)Math.cos(radian))+p*(float)Math.sin(radian);
      orientation = radian;
    }
    else {
      float radian = (u-domain/2)/radius;

      x = length - radius * (float)Math.sin(max_radians-radian)+p*(float)Math.cos(max_radians-radian);
      y = width - radius * (1 - (float)Math.cos(max_radians-radian))+p*(float)Math.sin(max_radians-radian);
      orientation = max_radians - radian;
    }
    if (currentValue == null) {currentValue = new POS();}
    ((POS)currentValue).x = x;
    ((POS)currentValue).y = y;
    ((POS)currentValue).alpha = orientation;

    return currentValue;

  }
}