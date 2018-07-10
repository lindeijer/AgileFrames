package com.agileways.democarpark;
import net.agileframes.traces.SceneIB;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.forces.Move;

import com.agileways.demo.DemoParameters;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.forces.xyaspace.XYASpace;

import net.agileframes.core.traces.Actor;
import net.agileframes.forces.xyaspace.XYATransform;;

public class DemoCarParkSuperScene extends SceneIB {
  //-- Atrributes --
  private static int CAPACITY_DEMO_SCENE = 4;
  //-------------------------- Semaphores --------------------------------
  public static Semaphore[][] semCrossing = new Semaphore[4][2];
  public static Semaphore semSuperDemo = null;
  private static int totalSems = 2*4+1;
  //-------------------------- Moves -------------------------------------
  //-------------------------- SceneActions ------------------------------
  public static SceneAction demoCarParkSuperAction;
  private static int totalSAs = 1;
  //-------------------------- Constructor -------------------------------
  public DemoCarParkSuperScene() throws java.rmi.RemoteException {
    super("DemoCarParkSuperScene");
  }

  //-------------------------- Methods -----------------------------------
  public void initialize() {
    try {
      // Transform:
      this.transform = new XYATransform(0, 0, 0);

      System.out.println("creating subscene classes");
      // Sub-Scenes:
      this.subSceneClasses = new Class[2];
      subSceneClasses[0] = com.agileways.demo.DemoScene.class;
      subSceneClasses[1] = com.agileways.carpark.CarParkScene.class;
      this.subScenePositions = new XYATransform[2];
      subScenePositions[0] = new XYATransform(-60*0.8, -60*0.8, 0);
      subScenePositions[1] = new XYATransform(52.5*0.8, -60*0.8, 0);

      // Semaphores:
      this.semaphores = new Semaphore[totalSems];
      int semCounter = 0;
      for (int direct = 0; direct < 4; direct++) {
        for (int lane = 0; lane < 2; lane++) {
          semCrossing[direct][lane] = new Semaphore("semCrossing_"+direct+"."+lane, 1);
          semaphores[semCounter] = semCrossing[direct][lane]; semCounter++;
        }
      }
      semSuperDemo = new Semaphore("semSuperDemo", CAPACITY_DEMO_SCENE);
      semaphores[semCounter] = semSuperDemo; semCounter++;

      // Logistic Positions:
      double l = DemoParameters.AGV_LENGTH;
      double d = DemoParameters.DIST_BETW_LANES;
      this.logisticPositions = new LogisticPosition[8];
      logisticPositions[0] = new LogisticPosition("Crossing", new XYASpace(d      , 3*d-l/2, Math.PI/2), this, semCrossing[0][0], new int[] {0,0});
      logisticPositions[1] = new LogisticPosition("Crossing", new XYASpace(2*d    , 3*d-l/2, Math.PI/2), this, semCrossing[0][1], new int[] {0,1});
      logisticPositions[2] = new LogisticPosition("Crossing", new XYASpace(3*d-l/2, 2*d    , 0        ), this, semCrossing[1][0], new int[] {1,0});
      logisticPositions[3] = new LogisticPosition("Crossing", new XYASpace(3*d-l/2, d      , 0        ), this, semCrossing[1][1], new int[] {1,1});
      logisticPositions[4] = new LogisticPosition("Crossing", new XYASpace(2*d    , l/2    , Math.PI/2), this, semCrossing[2][0], new int[] {2,0});
      logisticPositions[5] = new LogisticPosition("Crossing", new XYASpace(d      , l/2    , Math.PI/2), this, semCrossing[2][1], new int[] {2,1});
      logisticPositions[6] = new LogisticPosition("Crossing", new XYASpace(l/2    , d      , 0        ), this, semCrossing[3][0], new int[] {3,0});
      logisticPositions[7] = new LogisticPosition("Crossing", new XYASpace(l/2    , 2*d    , 0        ), this, semCrossing[3][1], new int[] {3,1});
      XYATransform demoT = new XYATransform(-60, -60 ,0);
      for (int i = 0; i < logisticPositions.length; i++) {
        logisticPositions[i].location = demoT.transform(logisticPositions[i].location);
      }


      // Moves:

      // SceneActions:
      this.sceneActions = new SceneAction[totalSAs];
      int saCounter = 0;
      demoCarParkSuperAction = null;
      // can be set as soon as scenes are available
      //new DemoCarParkSuperAction(this, subScenes[0], subScenes[1], null);
      sceneActions[saCounter] = demoCarParkSuperAction; saCounter++;


    } catch (Exception e) {
      System.out.println("Exception while initializing Scene: "+e.getMessage());
      e.printStackTrace();
    }
  }

  protected void setSAs() {
    // to be overloaded
    System.out.println("setSAs called");
    try {
      this.sceneActions[0] = new DemoCarParkSuperAction(this, subScenes[0], subScenes[1], null);
      System.out.println("setSAs: sceneaction 0 set");
    } catch (Exception e) { e.printStackTrace(); }
  }



  //-- MAIN --
  private static DemoCarParkSuperScene scene = null;
  public static void main(String[] args) {
    System.out.println("Creating DemoCarParkScene...");
    try { scene = new DemoCarParkSuperScene(); }
    catch (Exception e) {
      System.out.println("Exception in DemoCarParkScene.main: "+e.getMessage());
      e.printStackTrace();
    }
  }
}

