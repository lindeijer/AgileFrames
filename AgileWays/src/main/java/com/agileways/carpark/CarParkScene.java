package com.agileways.carpark;

import net.agileframes.traces.SceneIB;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.forces.Move;

import com.agileways.carpark.moves.*;
import com.agileways.carpark.sceneactions.*;
import net.agileframes.core.traces.Actor;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.core.traces.LogisticPosition;

public class CarParkScene extends SceneIB {
  //-------------------------- Semaphores --------------------------------
  public static Semaphore[][] semPark = new Semaphore[2][4];
  public static Semaphore[] semRoad = new Semaphore[2];
  public static Semaphore[] semEntrance = new Semaphore[2];
  private static int totalSems = 4*2 + 2*2;
  //-------------------------- Moves -------------------------------------
  public static Move[][][] moveIn = new Move[2][2][4];
  public static Move[][][] moveOut = new Move[2][2][4];
  private static int totalMoves = 2*2*4*2;
  //-------------------------- SceneActions ------------------------------
  public static SceneAction carParkSuperAction;
  public static SceneAction[] parkInAction = new SceneAction[2];
  public static SceneAction[][] parkOutAction = new SceneAction[2][4];
  private static int totalSAs = 1 + 2 + 2*4;
  //-------------------------- Constructor -------------------------------
  public CarParkScene() throws java.rmi.RemoteException {
    super("CarParkScene");
  }

  //-------------------------- Methods -----------------------------------
  public void initialize() {
    try {
      // Transform:
      this.transform = new XYATransform(52.5*0.8, -60.0*0.8, 0);


      // Sub-Scenes:
      this.subSceneClasses = null;
      this.subScenePositions = null;

      // Semaphores:
      this.semaphores = new Semaphore[totalSems];
      int semCounter = 0;
      for (int direct = 0; direct < 2; direct++) {
        for (int lane = 0; lane < 4; lane++) {
          semPark[direct][lane] = new Semaphore("semPark_"+direct+"."+lane, 1);
          semaphores[semCounter] = semPark[direct][lane]; semCounter++;
        }
        semRoad[direct] = new Semaphore("semRoad_"+direct, 1);
        semaphores[semCounter] = semRoad[direct]; semCounter++;
        semEntrance[direct] = new Semaphore("semEntrance_"+direct, 1);
        semaphores[semCounter] = semEntrance[direct]; semCounter++;
      }

      // Logistic Positions:
      double r = CarParkParameters.TURN_RADIUS;
      double w = CarParkParameters.AGV_WIDTH;
      double d = CarParkParameters.DIST_BETW_LANES;
      this.logisticPositions = new LogisticPosition[8];
//      logisticPositions[0] = new LogisticPosition("Entrance", new XYASpace(0    , 2*d, 0), this, semEntrance[0], new int[] {0});
//      logisticPositions[1] = new LogisticPosition("Entrance", new XYASpace(0    ,   d, 0), this, semEntrance[1], new int[] {0});
      logisticPositions[0] = new LogisticPosition("Park", new XYASpace(r    , 3*d, Math.PI/2), this, semPark[0][0], new int[] {0,0});
      logisticPositions[1] = new LogisticPosition("Park", new XYASpace(r+w  , 3*d, Math.PI/2), this, semPark[0][1], new int[] {0,1});
      logisticPositions[2] = new LogisticPosition("Park", new XYASpace(r+2*w, 3*d, Math.PI/2), this, semPark[0][2], new int[] {0,2});
      logisticPositions[3] = new LogisticPosition("Park", new XYASpace(r+3*w, 3*d, Math.PI/2), this, semPark[0][3], new int[] {0,3});
      logisticPositions[4] = new LogisticPosition("Park", new XYASpace(r    , 0  , Math.PI/2), this, semPark[1][0], new int[] {1,0});
      logisticPositions[5] = new LogisticPosition("Park", new XYASpace(r+w  , 0  , Math.PI/2), this, semPark[1][1], new int[] {1,1});
      logisticPositions[6] = new LogisticPosition("Park", new XYASpace(r+2*w, 0  , Math.PI/2), this, semPark[1][2], new int[] {1,2});
      logisticPositions[7] = new LogisticPosition("Park", new XYASpace(r+3*w, 0  , Math.PI/2), this, semPark[1][3], new int[] {1,3});
      for (int i = 0; i < logisticPositions.length; i++) {
        logisticPositions[i].location = transform.transform(logisticPositions[i].location);
      }


      // Moves:
      this.moves = new Move[totalMoves];
      int moveCounter = 0;
      for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
          for (int k = 0; k < 4; k++) {
            moveIn[i][j][k] = new MoveIn(transform, i, j, k);
            this.moves[moveCounter] = moveIn[i][j][k]; moveCounter++;
            moveOut[i][j][k] = new MoveOut(transform, i, j, k);
            this.moves[moveCounter] = moveOut[i][j][k]; moveCounter++;
          }
        }
      }

      // SceneActions:
      this.sceneActions = new SceneAction[totalSAs];
      int saCounter = 0;
      carParkSuperAction = new CarParkSuperAction(this, null);
      sceneActions[saCounter] = carParkSuperAction; saCounter++;
      for (int i = 0; i < 2; i++ ) {
        parkInAction[i] = new ParkInAction(this, carParkSuperAction, i);
        sceneActions[saCounter] = parkInAction[i]; saCounter++;
      }
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < 4; k++) {
          parkOutAction[j][k] = new ParkOutAction(this, carParkSuperAction, j, k);
          sceneActions[saCounter] = parkOutAction[j][k]; saCounter++;
        }
      }

    } catch (Exception e) {
      System.out.println("Exception while initializing Scene: "+e.getMessage());
      e.printStackTrace();
    }
  }



  //-- Methods --

  // make sure that after downloading an sa, the actor will be set by using setActor(Actor)
/*  public synchronized SceneAction getSceneAction(String name, Actor actor) throws java.rmi.RemoteException {
    // implementation should be in SceneIB
    SceneAction action = null;
    SceneAction clone = null;
    if (name.equals(""))                 { action = null; }
    else if (name.equals("CarParkSuperAction")) { action = carParkSuperAction; }
    else if (name.startsWith("ParkInAction")) {
      // use: for example: ParkInAction_1.0.3 (ParkInAction, entrance = 1, side = 0, lane = 3)
      // DemoAction1_
      if (name.length() != 18) {
        System.out.println("CarParkScene.getSceneAction: No sceneAction was found for :"+name);
        return null;
      }
      int entrance = Integer.parseInt(name.substring(13, 14));
      int side = Integer.parseInt(name.substring(15, 16));
      int lane = Integer.parseInt(name.substring(17, 18));
      //System.out.println("name = "+name+": CarParkScene understood to get sa ParkInAction: entrance="+entrance+"  side="+side+"  lane="+lane);
      action = parkInAction[entrance][side][lane];
    }
    else if (name.startsWith("ParkOutAction")) {
      if (name.length() != 19) {
        System.out.println("CarParkScene.getSceneAction: No sceneAction was found for :"+name);
        return null;
      }
      int entrance = Integer.parseInt(name.substring(14, 15));
      int side = Integer.parseInt(name.substring(16, 17));
      int lane = Integer.parseInt(name.substring(18, 19));
      //System.out.println("name = "+name+": CarParkScene understood to get sa ParkOutAction: entrance="+entrance+"  side="+side+"  lane="+lane);
      action = parkOutAction[entrance][side][lane];
    }
//    else if   enzovoorts
    else {
      System.out.println("CarParkScene.getSceneAction: No sceneAction was found for :"+name);
      return null;
    }
    // here the cloning begins

    try { clone =(SceneAction)action.clone(actor); }
    catch (CloneNotSupportedException e) { e.printStackTrace(); }
    registerSA(actor, clone);//important!!! we have to remember sa otherwise we can never destroy it
    clone.setActor(actor);// this doesnt help, because this actor will be serialized!
    return clone;
  }*/


  //-- MAIN --
  private static CarParkScene scene = null;
  public static void main(String[] args) {
    System.out.println("Creating CarParkScene...");
    try { scene = new CarParkScene(); }
    catch (Exception e) {
      System.out.println("Exception in CarParkScene.main: "+e.getMessage());
      e.printStackTrace();
    }
  }
}
