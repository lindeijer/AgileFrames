package com.agileways.forces.miniagv;
import net.agileframes.forces.*;
import net.agileframes.core.forces.*;

import java.awt.*;
import net.agileframes.server.AgileSystem;

public class MiniAgvManoeuvreDriver extends ManoeuvreDriverIB {
  public MiniAgvManoeuvreDriver(MiniAgvStateFinder stateFinder, MiniAgvInstructor instructor,
                                MiniAgvPhysicalDriver physicalDriver, MiniAgvMechatronicsIB mechatronics) {
    super(stateFinder, instructor, physicalDriver, mechatronics);
  }

  private long prevTime = AgileSystem.getTime();
  public void cycle() {
    long sleepTime = prevTime + (long)(MiniAgvConfig.MAIN_CYCLE_TIME_S * 1000) - AgileSystem.getTime();
    if (sleepTime < 10) { sleepTime = 10; }
    try { synchronized(this) { this.wait(sleepTime); } }
    catch (Exception e) { e.printStackTrace(); }
    prevTime = AgileSystem.getTime();
    super.cycle();
  }

  public synchronized void prepare(Manoeuvre m) {
    m.cycleTime = MiniAgvConfig.MAIN_CYCLE_TIME_S;
    super.prepare(m);
  }

  public synchronized void begin(Manoeuvre m) {
    m.cycleTime = MiniAgvConfig.MAIN_CYCLE_TIME_S;
    super.begin(m);
  }

  // these two specific methods are used only to give extra information to the user
  public double getAlpha() {
    MiniAgvInstructor.MiniAgvInstruction instruction = ((MiniAgvPhysicalDriver)physicalDriver).getRealisedInstruction();
    if (instruction == null) { return Double.NaN; }
    return instruction.angleA;
  }
  public double getRefSpeed() {
    if (getManoeuvre() == null) { return 0; }
    return getManoeuvre().getTrajectory().getProfileSpeed(getManoeuvre().getCalcEvolution());
  }
}
