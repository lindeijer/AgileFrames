package com.agileways.demo;

import net.agileframes.traces.SceneIB;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.forces.Move;

import com.agileways.demo.moves.*;
import com.agileways.demo.sceneactions.*;
import net.agileframes.core.traces.Actor;
import net.agileframes.forces.xyaspace.XYATransform;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.core.traces.LogisticPosition;

/**/import java.io.*;

public class DemoScene extends SceneIB {
/*DEPR*/  private static int MAX_AGVS_IN_SCENE = 2;
  //-------------------------- Semaphores --------------------------------
  public static Semaphore[][] semEndPark = new Semaphore[4][2];
  public static Semaphore[] semCntrPark = new Semaphore[4];
  public static Semaphore[] semCross = new Semaphore[4];
  public static Semaphore[] semCntrCross = new Semaphore[1];
/*DEPR*/  public static Semaphore[] semSuper = new Semaphore[1];
  private static int totalSems = 4*2 + 4 + 4 + 1 + 1;
  //-------------------------- Moves -------------------------------------
  public static Move[][] move1 = new Move[4][2];
  public static Move[][] move2 = new Move[4][2];
  public static Move[][] move3 = new Move[4][2];
  public static Move[][] move4 = new Move[4][2];
  public static Move[][] move5 = new Move[4][2];
  public static Move[][] move6 = new Move[4][2];
  public static Move[][] move7 = new Move[4][2];
  private static int totalMoves = 4*2*7;
  //-------------------------- SceneActions ------------------------------
  public static SceneAction demoSuperAction;
  public static SceneAction[][] demoAction1 = new SceneAction[4][2];
  public static SceneAction[][] demoAction2 = new SceneAction[4][2];
  public static SceneAction[][] demoAction3 = new SceneAction[4][2];
  public static SceneAction[][] demoAction4 = new SceneAction[4][2];
  public static SceneAction[][] demoAction5 = new SceneAction[4][2];
  public static SceneAction[][] demoAction6 = new SceneAction[4][2];
  public static SceneAction[][] demoAction7 = new SceneAction[4][2];
  private static int totalSAs = 1 + 4*2*7;
  //-------------------------- Constructor -------------------------------
  public DemoScene() throws java.rmi.RemoteException {
    super("DemoScene");
  }

  //-------------------------- Methods -----------------------------------
  public void initialize() {
    try {
      // Transform
      this.transform = new XYATransform(0,0 ,0); // new XYATransform(-60*0.8, -60*0.8 ,0);

      // Semaphores:
      this.semaphores = new Semaphore[totalSems];
      int semCounter = 0;
      for (int direct = 0; direct < 4; direct++) {
        semCntrPark[direct] = new Semaphore("semCntrPark_"+direct, 1);
        semaphores[semCounter] = semCntrPark[direct]; semCounter++;
        semCross[direct] = new Semaphore("semCross_"+direct, 1);
        semaphores[semCounter] = semCross[direct]; semCounter++;
/*DEPR*/        for (int lane = 0; lane < 2; lane++) {
/*DEPR*/          semEndPark[direct][lane] = new Semaphore("semEndPark_"+direct+"."+lane, 1);
/*DEPR*/          semaphores[semCounter] = semEndPark[direct][lane]; semCounter++;
/*DEPR*/        }
      }
      semCntrCross[0] = new Semaphore("semCntrCross_0", 1);
      semaphores[semCounter] = semCntrCross[0]; semCounter++;
/*DEPR*/      semSuper[0] = new Semaphore("semSuper_0", MAX_AGVS_IN_SCENE);
/*DEPR*/      semaphores[semCounter] = semSuper[0]; semCounter++;

      // Logistic Positions:
      double l = DemoParameters.AGV_LENGTH;
      double d = DemoParameters.DIST_BETW_LANES;
      this.logisticPositions = new LogisticPosition[0];
/*      logisticPositions[ 0] = new LogisticPosition("Park", new XYASpace(d      , 3*d-l/2, Math.PI/2), this, semEndPark[0][0], new int[] {0,0});
      logisticPositions[ 1] = new LogisticPosition("Park", new XYASpace(2*d    , 3*d-l/2, Math.PI/2), this, semEndPark[0][1], new int[] {0,1});
      logisticPositions[ 2] = new LogisticPosition("Park", new XYASpace(3*d-l/2, 2*d    , 0        ), this, semEndPark[1][0], new int[] {1,0});
      logisticPositions[ 3] = new LogisticPosition("Park", new XYASpace(3*d-l/2, d      , 0        ), this, semEndPark[1][1], new int[] {1,1});
      logisticPositions[ 4] = new LogisticPosition("Park", new XYASpace(2*d    , l/2    , Math.PI/2), this, semEndPark[2][0], new int[] {2,0});
      logisticPositions[ 5] = new LogisticPosition("Park", new XYASpace(d      , l/2    , Math.PI/2), this, semEndPark[2][1], new int[] {2,1});
      logisticPositions[ 6] = new LogisticPosition("Park", new XYASpace(l/2    , d      , 0        ), this, semEndPark[3][0], new int[] {3,0});
      logisticPositions[ 7] = new LogisticPosition("Park", new XYASpace(l/2    , 2*d    , 0        ), this, semEndPark[3][1], new int[] {3,1});
/*      logisticPositions[ 8] = new LogisticPosition("CntrPark_0", new XYASpace(1.5*d  , 2*d    , 0        ), this, semCntrPark[0]);
      logisticPositions[ 9] = new LogisticPosition("CntrPark_1", new XYASpace(2*d    , 1.5*d  , Math.PI/2), this, semCntrPark[1]);
      logisticPositions[10] = new LogisticPosition("CntrPark_2", new XYASpace(1.5*d  , d      , 0        ), this, semCntrPark[2]);
      logisticPositions[11] = new LogisticPosition("CntrPark_3", new XYASpace(d      , 1.5*d  , Math.PI/2), this, semCntrPark[3]);*/
      for (int i = 0; i < logisticPositions.length; i++) {
        logisticPositions[i].location = transform.transform(logisticPositions[i].location);
      }

      // Moves:
      this.moves = new Move[totalMoves];
      int moveCounter = 0;
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 2; j++) {
          move1[i][j] = new Move1(transform, i, j);
          this.moves[moveCounter] = move1[i][j]; moveCounter++;
          move2[i][j] = new Move2(transform, i, j);
          this.moves[moveCounter] = move2[i][j]; moveCounter++;
          move3[i][j] = new Move3(transform, i, j);
          this.moves[moveCounter] = move3[i][j]; moveCounter++;
          move4[i][j] = new Move4(transform, i, j);
          this.moves[moveCounter] = move4[i][j]; moveCounter++;
          move5[i][j] = new Move5(transform, i, j);
          this.moves[moveCounter] = move5[i][j]; moveCounter++;
          move6[i][j] = new Move6(transform, i, j);
          this.moves[moveCounter] = move6[i][j]; moveCounter++;
          move7[i][j] = new Move7(transform, i, j);
          this.moves[moveCounter] = move7[i][j]; moveCounter++;
        }
      }
      // SceneActions:
      this.sceneActions = new SceneAction[totalSAs];
      int saCounter = 0;
      demoSuperAction = new DemoSuperAction(this, null);
      sceneActions[saCounter] = demoSuperAction; saCounter++;
      for (int i = 0; i < 4; i++ ) {
        for (int j = 0; j < 2; j++) {
          demoAction1[i][j] = new DemoAction1(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction1[i][j]; saCounter++;
          demoAction2[i][j] = new DemoAction2(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction2[i][j]; saCounter++;
          demoAction3[i][j] = new DemoAction3(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction3[i][j]; saCounter++;
          demoAction4[i][j] = new DemoAction4(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction4[i][j]; saCounter++;
          demoAction5[i][j] = new DemoAction5(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction5[i][j]; saCounter++;
          demoAction6[i][j] = new DemoAction6(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction6[i][j]; saCounter++;
          demoAction7[i][j] = new DemoAction7(this, demoSuperAction, i, j);
          sceneActions[saCounter] = demoAction7[i][j]; saCounter++;
        }
      }

    } catch (Exception e) {
      System.out.println("Exception while initializing Scene: "+e.getMessage());
      e.printStackTrace();
    }
  }

  //-- MAIN --
  private static DemoScene scene = null;
  public static void main(String[] args) {
    System.out.println("Creating DemoScene...");
    try {
      scene = new DemoScene();
    }
    catch (Exception e) {
      System.out.println("Exception in DemoScene.main: "+e.getMessage());
      e.printStackTrace();
    }
  }
}
