package com.agileways.forces.miniagv;
import net.agileframes.forces.InstructorIB;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.server.AgileSystem;

public class MiniAgvInstructor extends InstructorIB {
  //----------------------- Attributes -------------------------------
  private MiniAgvInstruction advInstruction = new MiniAgvInstruction(0,0,0,0);
  private static final double CYCLETIME = MiniAgvConfig.MAIN_CYCLE_TIME_S;
  private static final double MAXSPEED = MiniAgvConfig.scaleToRealWorld(MiniAgvConfig.SPEED_MAX_M$S);
  private static final double MAXANGLE = MiniAgvConfig.STEERANGLE_MAX_RAD;
  private static final double DISTANCE = MiniAgvConfig.scaleToRealWorld(MiniAgvConfig.CENTER2CROWNCENTER_M);
  private static final int WHEELSA = 0;
  private static final int WHEELSB = 1;
  private final boolean DEBUG = false;

  //----------------------- Constructor ------------------------------
  public MiniAgvInstructor(){}

  //----------------------- Methods ----------------------------------
  public void update() {
    timeStamp = AgileSystem.getTime();
    XYASpace.XYAPath pilotCourse = (XYASpace.XYAPath)manoeuvreDriver.getPilotCourse();
    // Calculate Speed:
    double refAccel = manoeuvreDriver.getReferenceAcceleration();
    double speedA = computeSpeed(WHEELSA, pilotCourse, refAccel);
    double speedB = computeSpeed(WHEELSB, pilotCourse, refAccel);
    // Calculate Angle: ( be sure to be in range [-MAXANGLE, MAXANGLE] )
    double distance = Math.abs(speedA)*CYCLETIME;
    double angleA = computeAngle(WHEELSA, pilotCourse, distance);
    double angleB = computeAngle(WHEELSB, pilotCourse, distance);
    double ratio = 1.0;
    if (angleA > 0) { ratio = Math.max(ratio, angleA / MAXANGLE); }
    else { ratio = Math.max(ratio, - angleA / MAXANGLE); }
    if (angleB > 0) { ratio = Math.max(ratio, angleB / MAXANGLE); }
    else { ratio = Math.max(ratio, - angleB / MAXANGLE); }
    angleA = angleA / ratio;
    angleB = angleB / ratio;
    // Set advised Instruction:
    advInstruction = new MiniAgvInstruction(angleA, angleB, speedA, speedB);
    if (DEBUG) System.out.println("*D* Instructor: advInstruction="+advInstruction.toString());
  }

  private double computeAngle(int wheelSystem, XYASpace.XYAPath pilotCourse, double distance){
    if (pilotCourse == null) { return 0; }
    int SIGN = 1;
    if (wheelSystem == WHEELSB) { SIGN = -1; }

    XYASpace current = (XYASpace)pilotCourse.getConnectionPoint(0);
    XYASpace pilot = (XYASpace)pilotCourse.getConnectionPoint(distance);

//    if (DEBUG) System.out.println("*D* Instructor.computeAngle: wheels = "+wheelSystem+"  distance = "+distance);
//    if (DEBUG) System.out.println("*D* Instructor.computeAngle: current = "+current.toString());
//    if (DEBUG) System.out.println("*D* Instructor.computeAngle: pilot = "+pilot.toString());

    double dX = pilot.getX() - current.getX();
    double dY = pilot.getY() - current.getY();
    double dA = pilot.getAlpha() - current.getAlpha();

    double cosA = Math.cos(current.getAlpha());
    double sinA = Math.sin(current.getAlpha());
    double cosdA = Math.cos(dA);
    double sindA = Math.sin(dA);

    double lng = SIGN * (cosA * dX + sinA * dY) + DISTANCE * (cosdA - 1);
    double lat = SIGN * (sinA * dX - cosA * dY) - DISTANCE * sindA;

    return (-Math.atan(lat/lng));
  }

  public XYASpace.XYAPath deductPilotCourse(double angleA, double angleB, double distance, XYASpace origState) {
    double distA = distance; double distB = -distance;
    // distance must be a signed value!
    // with value <0 if motorA drives backwards!
    double latA = -distA * Math.sin(angleA);
    double latB = -distB * Math.sin(angleB);
    double lngA = distA * Math.cos(angleA);
    double lngB = distB * Math.cos(angleB);

    double alpha2 = origState.getAlpha() - Math.asin((latA + latB) / (2*DISTANCE));

    double cosA1 = Math.cos(origState.getAlpha());
    double sinA1 = Math.sin(origState.getAlpha());
    double cosA2 = Math.cos(alpha2);
    double sinA2 = Math.sin(alpha2);

    double dXa =   cosA1*lngA + sinA1*latA + (cosA2 - cosA1)*DISTANCE;
    double dYa =   sinA1*lngA - cosA1*latA + (sinA2 - sinA1)*DISTANCE;
    double dXb = - cosA1*lngB - sinA1*latB - (cosA2 - cosA1)*DISTANCE;
    double dYb = - sinA1*lngB + cosA1*latB - (sinA2 - sinA1)*DISTANCE;

    XYASpace newState = new XYASpace(origState.getX() + (dXa + dXb)/2,
                                     origState.getY() + (dYa + dYb)/2,
                                     alpha2);

    return (XYASpace.XYAPath)origState.createPath(newState);
  }

  private double computeSpeed(int wheelSystem, XYASpace.XYAPath pilotCourse, double refAccel) {
    if (pilotCourse == null) { return 0; }
    //if (DEBUG) System.out.println("*D* Instructor.computeSpeed: wheelSystem="+wheelSystem+"  refAccel="+refAccel);
    double currentSpeed = Math.abs(advInstruction.speedA);
    if (wheelSystem == WHEELSB) { currentSpeed = Math.abs(advInstruction.speedB); }
    double nextSpeed = currentSpeed + refAccel * CYCLETIME;// vt = v0 + a*t

    //System.out.println("## refAccel="+refAccel+"  currentSpeed="+currentSpeed+"  nextSpeed="+nextSpeed);
////    nextSpeed = (nextSpeed + currentSpeed)/2;
    if (nextSpeed > MAXSPEED) { nextSpeed = MAXSPEED; } else if (nextSpeed < 0) { nextSpeed = 0; }

    double distance = Math.abs(nextSpeed) * CYCLETIME;
    XYASpace pilot = (XYASpace)pilotCourse.getConnectionPoint(distance);
    XYASpace current = (XYASpace)pilotCourse.getConnectionPoint(0);
    double dX = pilot.getX() - current.getX();
    double dY = pilot.getY() - current.getY();
    double longC = dX*Math.cos(current.getAlpha()) + dY*Math.sin(current.getAlpha());

    int SIGN = 1;
    if ((wheelSystem == WHEELSA) ^ (longC > 0)) { SIGN = -1; } // operator ^ is XOR: condition true iff one of the elements is true

    return SIGN * nextSpeed;
  }

  //----------------------- Getters and Setters ----------------------
  public MiniAgvInstruction getAdvisedInstruction() { return advInstruction; }
  public MiniAgvInstruction createInstruction(double angleA, double angleB, double speedA, double speedB) {
    return new MiniAgvInstruction(angleA, angleB, speedA, speedB);
  }

  //------------------------- Class ------------------------------------
  public class MiniAgvInstruction implements MachineInstruction {
    public double angleA = 0.0; public double angleB = 0.0; public double speedA = 0.0; public double speedB = 0.0;
    public MiniAgvInstruction(double angleA, double angleB, double speedA, double speedB){
      this.angleA = angleA; this.angleB = angleB; this.speedA = speedA; this.speedB = speedB;
    }
    public String toString(){ return "angleA="+angleA+"; angleB = "+angleB+"; speedA = "+speedA+"; speedB = "+speedB; }
  }
}
