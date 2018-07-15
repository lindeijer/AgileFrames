package com.agileways.ui;

import java.io.*;

public class DriveAgvManually {
  //-- Attributes --
  //-- Constructor --
  public DriveAgvManually() {
    try {
      InputStreamReader isr = new InputStreamReader(System.in);
      //BufferedReader br = new BufferedReader(isr);
      for (;;){
        int input = isr.read();//br.read();
        switch (input) {
          default: System.out.println("@@ we read: "+input);
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
  }
  //-- Main --
  public static void main(String[] args) {
    DriveAgvManually main = new DriveAgvManually();
  }
}