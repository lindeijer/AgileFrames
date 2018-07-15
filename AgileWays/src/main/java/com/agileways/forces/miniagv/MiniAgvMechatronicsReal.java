package com.agileways.forces.miniagv;
import com.agileways.forces.miniagv.MiniAgvConfig;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.*;

public class MiniAgvMechatronicsReal extends MiniAgvMechatronicsIB {
  //--- Attributes ---
  private byte[] setting = new byte[10];
  private File devAgv = null;
  private RandomAccessFile agvMechatronics = null;
  public static final boolean DEBUG = false;

  //--- Constructor ---
  public MiniAgvMechatronicsReal()  {
    super();
    // getting file to read and write bytes from/to
    devAgv = new java.io.File("/dev/agv");
    System.out.println("          MiniAgvMechatronicsReal /dev/agv==" + devAgv.toString());
    if (!devAgv.canRead()) {  System.out.println("MiniAgvMechatronicsReal: /dev/agv can not be read from"); }
    if (!devAgv.canWrite()) { System.out.println("MiniAgvMechatronicsReal: /dev/agv can not be written to"); }
    try {
      agvMechatronics = new java.io.RandomAccessFile(devAgv,"rw");
      if (DEBUG) System.out.println("MiniAgvMechatronicsReal: new RandomAccessFile succeeded");
    } catch (Exception e) {
      System.out.println("MiniAgvMechatronicsReal: /dev/agv try write-to failed, reason=" + e.getMessage());
    }
    flush();
  }

  //--- Methods ---
  // using method readPowerValue results in resetting the power-value in device driver
  public boolean readPowerValue() {
    int battery = 0;
    try {
      agvMechatronics.seek(6);
      // the strange thing about this method in Java is that when reading an unsigned byte
      // the result must be stored in a int, because a Java-byte always [-128, 127]
      int b1 = agvMechatronics.readUnsignedByte();// after reading: value will be reset
      int b2 = agvMechatronics.readUnsignedByte();// after reading: value will be reset
      // WATCH OUT!
      // The Device Driver stores its values in Little Endian
      // Little Endian = [b1][b2]
      // Big Endian =    [b2][b1]
      // in which [b1] = least significant byte and [b2] = most significant byte
      battery = b1 + b2*256;
      if (DEBUG) System.out.println("*D* powerValue: b1 = "+b1+"  b2 = "+b2+"  power = "+(battery==1));
    } catch (java.io.IOException e) {
      System.out.println("IOException while reading powerValue in Mechatronics. Exception ignored.");
      e.printStackTrace();
    }
    return (battery == 1);// battery is read in flush()
  }

  // using method readSpikes results in resetting the spikes-value in device driver
  public int readSpikes() {
    int spikes = 0;
    try {
      agvMechatronics.seek(8);
      int b1 = agvMechatronics.readUnsignedByte();// after reading: value will be reset
      int b2 = agvMechatronics.readUnsignedByte();// after reading: value will be reset
      spikes = b1 + b2*256;
      if (DEBUG) System.out.println("*D* spikes: b1 = "+b1+"  b2 = "+b2+"  spikes = "+spikes);
    } catch (java.io.IOException e) {
      System.out.println("IOException while reading powerValue in Mechatronics. Exception ignored.");
      e.printStackTrace();
    }
    return spikes;
  }

  public void writeSetting(byte[] settingToWrite) {
    // be within range!!
    int maxSetting = 127;
    for (int i = 0; i < 4; i++) {
      if (settingToWrite[i] > 0) {
        settingToWrite[i] = (byte)Math.min((int)settingToWrite[i],  maxSetting);
      } else {
        settingToWrite[i] = (byte)Math.max((int)settingToWrite[i], -maxSetting);
      }
    }
    for (int i = 0; i < 4; i++) { setting[i] = settingToWrite[i]; }
    flush();
  }

  public boolean setLed(boolean ledOn){
    if (ledOn) { setting[4] = 1; } else { setting[4] = 0; }
    if (DEBUG) { System.out.println("MechReal.setLed called : request for ledOn=" + ledOn); }
    flush();
    return (setting[4] != 0);
  }

  public byte[] getCurrentSetting() {
    return this.setting;
  }

  public void flush() {
    try {
      agvMechatronics.seek(0);
      agvMechatronics.writeByte(setting[0]);// front motor
      agvMechatronics.writeByte(setting[1]);// rear motor - not used
      agvMechatronics.writeByte(setting[2]);// front bogie
      agvMechatronics.writeByte(setting[3]);// rear bogie
      agvMechatronics.writeByte(setting[4]);// led
      // NB byte 5 is currently not used!
      // 6 - power 1 (battery), full = 1, empty = 0 (read in readPowerValue)
      // 7 - power 2
      // 8 - odo 1 (read in readSpikes)
      // 9 - odo 2

      if (DEBUG) System.out.println("*D* MechReal [motor=" + setting[0] + " servoA=" + setting[2] + " servoB=" + setting[3] + "]\n");
      agvMechatronics.getFD().sync();  // why does this throw an exception and also work ??
    } catch (Exception e) {
      //System.out.println("MMReal.flush() failed to set");//:[motor=" + settings[0] + " aA=" + settings[2] + " aB=" + settings[3] + "] exception=" + e.toString());
      //e.printStackTrace();
    }
  }

}