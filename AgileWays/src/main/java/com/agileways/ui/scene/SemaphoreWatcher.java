package com.agileways.ui.scene;

import java.io.*;
import java.rmi.server.UnicastRemoteObject;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
//import net.jini.core.lookup.ServiceID;
import java.rmi.RemoteException;
import net.agileframes.server.AgileSystem;

import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SemaphoreRemote;
import net.agileframes.core.traces.Semaphore;

import net.agileframes.traces.viewer.*;


public class SemaphoreWatcher {
  //-- Attributes --
  private static SemaphoreWatcher watcher = null;
  private ServiceTemplate sceneTemplate = new ServiceTemplate(null, new Class[] {net.agileframes.core.traces.Scene.class}, null);
  private final int MAX_SCENES = 10;
  private boolean watchStarted = false;
  private SemaphoreWatcherFrame frame;
  private SemaphoreRemote[] semaphores;
  private Scene[] scenes = null;

  //-- Constructor --
  public SemaphoreWatcher() {
    frame = new SemaphoreWatcherFrame(this);
    frame.setTitle("-- SemaphoreWatcher: Searching for Scene --");
    choseScene();
  }

  //-- Methods --
  private void choseScene() {
    ServiceItem[] serviceItems = AgileSystem.lookup(sceneTemplate, MAX_SCENES);
    switch (serviceItems.length) {
      case 0:
        frame.setTitle("-- SemaphoreWatcher: No Scene Found --");
        break;
      case 1:
        //System.out.println("A Scene was found. SemaphoreWatcher will watch the semaphores in this Scene");
        scenes = new Scene[] {(Scene)serviceItems[0].service};
        frame.replySelectScene(scenes[0]);
        break;
      default:
        //System.out.println("Multiple Scenes were found. SemaphoreWatcher will ask you which to chose.");
        frame.selectScene(null);
        break;
    }
  }

  private SemaphoreProperties[] props = null;// in last element we store frameProps
  private Scene scene = null;
  public void watchScene(Scene scene) {
    try {
      this.scene = scene;
      frame.setTitle("-- Semaphores in "+scene.getName()+" --");
      semaphores = scene.getSemaphores();

      props = getSceneData(scene);
      SemaphoreViewer[] semViewers = new SemaphoreViewer[semaphores.length];
      for (int i = 0; i < semaphores.length; i++) {
        //System.out.println("about to add viewers");
        semViewers[i] = new SemaphoreViewer(semaphores[i], props[i], frame);
        //System.out.println(i+"  "+semaphores[i].toString()+"  = "+semaphores[i].getName());
        SemaphoreViewerProxy svp = new SemaphoreViewerProxy(semViewers[i]);
        semaphores[i].setViewer(svp);
      }
      //System.out.println("width="+props[semaphores.length].width+"  height="+props[semaphores.length].height);
      frame.w = props[semaphores.length].width;
      frame.h = props[semaphores.length].height;//+30
      frame.setLocation((int)props[semaphores.length].x, (int)props[semaphores.length].y);
      frame.setSemaphoreViewers(semViewers);



    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private SemaphoreProperties[] getSceneData(Scene scene) {
    SemaphoreProperties[] props = null;
    try {
      int numberOfSems = semaphores.length;
      props = new SemaphoreProperties[numberOfSems+1];
      File file = new File(AgileSystem.agileframesDataPath + "SemaphoreWatcher_"+scene.getName());
      if (file.canRead()) {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        //getdata
        for (int i = 0; i < numberOfSems; i++) {
          try {
            props[i] = (SemaphoreProperties)ois.readObject();
          } catch (Exception e) {
            //exception while reading:
            props[i] = new SemaphoreProperties(0, 0, 0, 0, 0);
          }
        }
        try {
          props[numberOfSems] = (SemaphoreProperties)ois.readObject();//frameProps
        } catch (Exception e) {
          //exception while reading:
          props[numberOfSems] = new SemaphoreProperties(600, 0, 300, 300, 0);
        }
        //System.out.println("Semaphore-data succesfully read from disk");
      } else {
        // no file yet, we have to make new one
        file.createNewFile();
        if (file.canWrite()) {
          FileOutputStream fos = new FileOutputStream(file);
          ObjectOutputStream oos = new ObjectOutputStream(fos);
          // putdata
          for (int i = 0; i < numberOfSems; i++) {
            props[i] = new SemaphoreProperties(i*5, i*5, 10, 10, 0);
            oos.writeObject(props[i]);
          }
          props[numberOfSems] = new SemaphoreProperties(600, 0, 300, 300, 0);
          oos.writeObject(props[numberOfSems]);//frameData
          //System.out.println("Semaphore-data succesfully written to disk");
        } else {
          System.out.println("Cannot write to file to store Semaphore-data");
        }
      }
    } catch (Exception e) {
      System.out.println("Exception while reading or writing Semaphore-data: Exception ignored.");
      e.printStackTrace();
    }

    return props;
  }

  public void setSemaphoreProps(int semaphoreNr, SemaphoreProperties properties) {
    try {
      props[semaphoreNr] = properties;

      SemaphoreViewer[] semViewers = new SemaphoreViewer[semaphores.length];
      for (int i = 0; i < semaphores.length; i++) {
        semViewers[i] = new SemaphoreViewer(semaphores[i], props[i], frame);
        SemaphoreViewerProxy svp = new SemaphoreViewerProxy(semViewers[i]);
        semaphores[i].setViewer(svp);
      }
      frame.w = props[semaphores.length].width;
      frame.h = props[semaphores.length].height;//+30
      frame.setLocation((int)props[semaphores.length].x, (int)props[semaphores.length].y);
      frame.setSemaphoreViewers(semViewers);


      File file = new File(AgileSystem.agileframesDataPath + "SemaphoreWatcher_"+scene.getName());
      FileOutputStream fos = new FileOutputStream(file);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      // putdata
      for (int i = 0; i <= scene.getSemaphores().length; i++) {
        oos.writeObject(props[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public SemaphoreProperties getProps(int semNr) {
    return props[semNr];
  }

  public Scene[] getScenes() {
    ServiceItem[] serviceItems = AgileSystem.lookup(sceneTemplate, MAX_SCENES);
    scenes = new Scene[serviceItems.length];
    for (int i = 0; i < serviceItems.length; i++) {
      scenes[i] = (Scene)serviceItems[i].service;
    }
    return scenes;
  }

  //-- Main --
  public static void main (String[] args) {
    try { watcher = new SemaphoreWatcher(); }
    catch (Exception e) { e.printStackTrace(); }
  }
}