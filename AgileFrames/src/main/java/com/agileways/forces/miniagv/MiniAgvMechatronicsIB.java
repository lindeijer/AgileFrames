package com.agileways.forces.miniagv;
import net.agileframes.forces.Mechatronics;

public abstract class MiniAgvMechatronicsIB implements Mechatronics {
  //--- Attributes ---
  public final static int SIMULATED = 1;
  public final static int REAL = 2;
  protected byte[] setting = new byte[10];
  //--- Constructor ---
  public MiniAgvMechatronicsIB() {}
  //--- Methods ---
  public void writeSetting(byte[] setting) {
//    System.out.println("MiniAgvMechatronicsIB: writeSetting not implemented");
  }
  public boolean readPowerValue() {
//    System.out.println("MiniAgvMechatronicsIB: readPowerValue not implemented");
    return false;
  }
  public int readSpikes(){
//    System.out.println("MiniAgvMechatronicsIB: readSpikes not implemented");
    return 0;
  }
  public byte[] getCurrentSetting() {
    return setting;
//    System.out.println("MiniAgvMechatronicsIB: getCurrentSetting not implemented");
  }
  public boolean setLed(boolean ledOn) {
//    System.out.println("MiniAgvMechatronicsIB: setLed not implemented");
    return false;
  }
}