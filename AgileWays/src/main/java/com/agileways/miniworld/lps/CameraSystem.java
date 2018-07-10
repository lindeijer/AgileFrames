package com.agileways.miniworld.lps;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.Thread;

public class CameraSystem {
  //--- Attributes ---
  private MiniWorldXYADistributor distributor = null;
  private ServerSocket cameraServerSocket = null;
  private boolean active = true;
  private CameraSocketReader cameraSocketReader = null;
  private CameraSocketWriter cameraSocketWriter = null;
  private Socket socket = null;
  public static boolean DEBUG = false;



  //--- Constructor ---
  public CameraSystem(MiniWorldXYADistributor distributor) {
    if (DEBUG) System.out.println("CAmeraSystem started");
    this.distributor = distributor;
    // wait for camera to connect,
    // upon connection spawns a new socket.
    Thread cameraConnectionThread = new Thread() {
      public void run() { connect(); }
    };
    cameraConnectionThread.start();
  }
  //--- Methods ---
  // called by constructor
  private void connect() {
    try { cameraServerSocket = new ServerSocket(1968,1); }
    catch (IOException e) {
      System.out.println("CameraSystem.connect: IOException(1)=" + e.getMessage());
      System.exit(-1);
    }
    while (active) {
      try {
        System.out.println("CameraSystem.connect: waiting for camera to connect ...");
        Socket socket = cameraServerSocket.accept();
        System.out.println("CameraSystem.connect: accepted connection camera on " + socket.getInetAddress().getHostName());
        synchronized (cameraServerSocket) { startTalking(socket); }
      } catch (IOException e) {
        System.out.println("CameraSystem.connect: IOException(2)=" + e.getMessage());
      } catch (Exception e) {
        System.out.println("CameraSystem.connect: Exception=" + e.getMessage());
      }
      distributor.sendListenerList(); // calls back to Distributor -> we want an agv-list
    }
    System.out.println("CameraSystem.connect: stopped waiting for clients to connect.");
  }

  // called by connect
  private void startTalking(Socket socket) throws Exception {
    this.socket = socket;
    try {
      cameraSocketReader = new CameraSocketReader(this, distributor, socket.getInputStream() );
      cameraSocketWriter = new CameraSocketWriter(this, distributor, socket.getOutputStream());
    } catch (Exception e) {
      System.out.println("CameraSystem.startTalking: Exception=" + e.toString());
      throw e;
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // called by distributor
  public void setLed(int machineNr, boolean ledOn) {
    if (DEBUG) System.out.println("CameraSystem: setLed called");
    if (cameraSocketWriter != null) {
      synchronized (cameraServerSocket) { cameraSocketWriter.setLed(machineNr, ledOn); }
    }
  }
  // called by distributor
  public void setListenerList(int[] machineNumbers) {
    if (DEBUG) System.out.println("CameraSystem: setListenerList called");
    if (cameraSocketWriter != null) {
      synchronized (cameraServerSocket) { cameraSocketWriter.setList(machineNumbers); }
    }
  }
  // called by CameraSocketReader and CameraSocketWriter
  public void quit() {
    if (DEBUG) System.out.println("CameraSystem: quit called");
    try {
      cameraSocketReader.quit();
      cameraSocketWriter.quit();
      socket.close();
    } catch (IOException e) {
      System.out.println("CameraSystem.quit: IOException=" + e.getMessage());
    }
  }
  // called in distributor
  public void closeConnection() {
    if (DEBUG) System.out.println("CameraSystem: CloseConnection called");
    System.out.println("CameraSystem.closeConnection called");
    active = false;
    try { cameraServerSocket.close(); }
    catch (IOException e) {
      System.out.println("CameraSystem.closeConnection: IOException=" + e.getMessage());
    }
  }
}// end of CameraSystem.class
//--- Inner-class CameraSocketReader ---
class CameraSocketReader extends Thread {
  //--- Attributes ---
  private InputStream cameraIn = null;
  private DataInputStream cameraDataIn = null;
  private CameraSystem cameraSystem = null;
  private boolean active = true;
  private MiniWorldXYADistributor distributor = null;
  public static boolean DEBUG = true;

  //--- Constructor ---
  public CameraSocketReader(CameraSystem cameraSystem,
                            MiniWorldXYADistributor distributor,
                            InputStream cameraServerSocketInputStream) {
    if (DEBUG) System.out.println("CameraSystem: CameraSocketReader Created");
    this.cameraSystem = cameraSystem;
    this.distributor = distributor;
    this.cameraIn = cameraServerSocketInputStream;
    this.setPriority(1);
    this.start();
  }
  //--- Methods ---
  // called by CameraSystem.quit
  public void quit() {
    if (DEBUG) System.out.println("CameraSocketReader: quit called");
    active=false;
    try {
      cameraDataIn.close();
      cameraIn.close();
    } catch (IOException e) {
      System.out.println("CameraSocketReader.quit: IOException=" + e.getMessage());
    }
  }

  /**
   * This thread processes CameraServer led-requests and pos-updates
   * arriving via the cameraSocket input-stream
   */
  public void run() {
    System.out.println("CameraSocketReader.run: " + this.toString() + " born");
    this.cameraDataIn = new DataInputStream(cameraIn);
    byte[] data      = new byte[4092];
    byte[] header    = new byte[4];
    if (DEBUG) System.out.println("CameraSocketReader.run: active="+active);
    while (active) {
      try {
        // our custom defined header is two shorts
        // indicating the data-type and the data-length
        // System.out.println("CameraSocketReader.run waiting for a packet-header");
        cameraDataIn.readFully(header);
        // System.out.println("CameraSocketReader.run: packetHeader=[" +header[0]+ "," +header[1]+ "," +header[2]+ "," +header[3] + "]");
        ByteArrayInputStream header_bais = new ByteArrayInputStream(header);
        DataInputStream      header_dis  = new DataInputStream(header_bais);
        short packetType = header_dis.readShort();
        short packetSize = header_dis.readShort();
        // System.out.println("CameraSocketReader.run: packetType=" +packetType+ " packetSize=" +packetSize);
        header_dis.close();   // don't think it will ever throw an exception.
        header_bais.close();  // don't think it will ever throw an exception.
        // System.out.println("CameraSocketReader.run waiting for a packet-body");
        cameraDataIn.readFully(data,0,packetSize-4);
        // System.out.println("CameraSocketReader.run: read a packet=" + data);
        switch (packetType) {
          case 0 : distributeXYAData(data); break;
          case 1 : distributeLedRequest(data); break;
          default :
            System.out.println("CameraSocketReader.run: unknown packetType=" + packetType);
            System.exit(-1);
        }
      } catch (EOFException e) {
        if (DEBUG) System.out.println("CameraSocketReader.run: EOFException while reading header, e=" + e.getMessage());
        active = false;
      } catch (IOException e) {
        if (DEBUG) System.out.println("CameraSocketReader.run: IOException(1)=" + e.getMessage());
        active = false;
      }
    }
    cameraSystem.quit();
    System.out.println("CameraSocketReader.run: " + this.toString() + " died");
  }

  // The xyaData will be distributed in mini-world values and in millimeters
  public void distributeXYAData(byte[] xyaData) {
    //System.out.print("... ");
    //long time = System.currentTimeMillis();
    ByteArrayInputStream bais = new ByteArrayInputStream(xyaData);
    DataInputStream      dis  = new DataInputStream(bais);
    try {
      if ( dis.available() > 0 ) {
        int frameNumber = dis.readInt ();
        short numberOfCars = dis.readShort ();
        while ( numberOfCars > 0 ) {
          int agvNumber = dis.readInt ();
          // incoming x-y data is in millimeters
          // the agv lives in mini-world, scale=25
          float x = dis.readFloat ();// / 1000 * MiniAgvConfig.SCALE  ;
          float y = dis.readFloat ();// / 1000 * MiniAgvConfig.SCALE  ;
          // incoming alpha data is in rad, we dont know the precision.
          float alpha = dis.readFloat ();

          if (!Float.isNaN(x)) {
            if (!Float.isNaN(y)) {
              if (!Float.isNaN(alpha)) {
                distributor.distributeXYAData(agvNumber,x,y,alpha);
              } else System.out.println("uhoh, johans alpha=NAN data not dispatched");
            } else System.out.println("uhoh, johans y=NAN data not dispatched");
          } else System.out.println("uhoh, johans x=NAN data not dispatched");
          numberOfCars--;
        }
      } else {
        System.out.println("CameraSocketReader.distributeXYAData: no XYA-data!!!!");
      }
      dis.close();  dis  = null;
      bais.close(); bais = null;
    } catch (IOException e) {
      System.out.println("CameraSocketReader.distributeXYAData: IOException=" + e.getMessage() + " connection with Sock probably lost");
      System.exit(-1);
    }
  }

  public void distributeLedRequest(byte[] requestData) {
    //System.out.print("LED ");
    ByteArrayInputStream bais = new ByteArrayInputStream(requestData);
    DataInputStream      dis  = new DataInputStream(bais);
    try {
      int agvNumber = Integer.MIN_VALUE;
      boolean setOn = false;
      if ( dis.available() > 0 ) {
        agvNumber   = dis.readInt ();
        short light = dis.readShort ();
        switch ( light ) {
          case 0 : setOn = false; break;
          case 1 : setOn = true; break;
          default : {
            System.out.println("CameraSocketReader.distributeLedRequest: unknown led-request=" + light);
            System.exit(-1);
          }
        }
      } else {
        System.out.println("CameraSocketReader.distributeLedRequest: no request data!!!!");
      }
      distributor.distributeLedRequest(agvNumber,setOn);
      dis.close();  dis  = null;
      bais.close(); bais = null;
    } catch (IOException e) {
      System.out.println("CameraSocketReader.distributeLedRequest: IOException=" + e.getMessage() + " connection with AgvSock probably lost");
      System.exit(-1);
    }
  }

}
//--- Inner-Class: CameraSocketWriter ---
class CameraSocketWriter {
  //--- Attributes ---
  private CameraSystem cameraSystem = null;
  private OutputStream cameraOut = null;
  private boolean active = false;
  private MiniWorldXYADistributor distributor = null;

  //--- Constructor ---
  public CameraSocketWriter(CameraSystem cameraSystem,
                            MiniWorldXYADistributor distributor,
                            OutputStream cameraOut) {
    this.cameraSystem = cameraSystem;
    this.distributor = distributor;
    this.cameraOut = cameraOut;
    active = true;
  }
  //--- Methods ---
  // called by CameraSystem.quit
  public void quit() {
    active = false;
    try {
      cameraOut.close();
    } catch (IOException e) {
      System.out.println("AgvTrackerProxySocketWriter.quit: IOException=" + e.getMessage());
    }
  }

  public void setList(int[] machineNumbers) {
    // System.out.println("CameraSocketWriter.setList. ");
    if (!active) { return; }
    short packetSize   = (short)(6 + 4 * machineNumbers.length);
    try {
      ByteArrayOutputStream agvListPacket_baos = new ByteArrayOutputStream(packetSize);
      DataOutputStream      agvListPacket_dos  = new DataOutputStream(agvListPacket_baos);
      // write the packetType
      agvListPacket_dos.writeShort(2);                  // 2 bytes, 2 total
      agvListPacket_dos.writeShort(packetSize);         // 2 bytes, 4 total
      agvListPacket_dos.writeShort(machineNumbers.length);  // 2 bytes, 6 total
      for (int i=0;i<machineNumbers.length;i++) {
        agvListPacket_dos.writeInt(machineNumbers[i]);      // 2 bytes, 6 + 2*i total
      }
      agvListPacket_dos.close();
      byte[] agvListPacket = agvListPacket_baos.toByteArray();
      agvListPacket_baos.close();
      cameraOut.write(agvListPacket,0,agvListPacket.length);
      // should send eop-character ?? !! ??
    } catch (IOException e) {
      System.out.println("CameraSocketWriter.setList: IOException=" + e.getMessage());
      active = false;
    }
  }

  public void setLed(int machineNumber, boolean ledOn) {
    // System.out.println("CameraSocketWriter.setLed: machineNumber=" + machineNumber + " ledOn=" + ledOnn);
    if (!active) { return; }
    byte[] replyData = new byte[14];
    try {
      ByteArrayOutputStream ledPacket_baos = new ByteArrayOutputStream(10);
      DataOutputStream      ledPacket_dos  = new DataOutputStream(ledPacket_baos);
      ledPacket_dos.writeShort(1);       // 2 bytes, 2 total
      ledPacket_dos.writeShort(10) ;     // 2 bytes, 4 total
      ledPacket_dos.writeInt(machineNumber); // 4 bytes, 8 total
      if (ledOn) {
        ledPacket_dos.writeShort ( 1 );  // 2 bytes, 10 total
      } else {
        ledPacket_dos.writeShort ( 0 );  // 2 bytes, 10 total
      }
      ledPacket_dos.close();
      byte[] ledPacket = ledPacket_baos.toByteArray();
      ledPacket_baos.close();
      cameraOut.write(ledPacket,0,ledPacket.length);
      // should send eop-character ?? !! ??
    } catch ( IOException e ) {
	    System.out.println("CameraSocketWriter.setLed: IOException=" + e.getMessage());
      active = false;
    }
  }

}
