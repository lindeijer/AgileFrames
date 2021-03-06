package com.agileways.crossscene;

import net.agileframes.traces.SceneIB;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.core.forces.Move;

import com.agileways.crossscene.moves.*;
import com.agileways.crossscene.sceneactions.*;
import net.agileframes.core.traces.Actor;

import net.agileframes.core.vr.BodyRemote;
import net.agileframes.core.vr.Avatar;

import net.agileframes.forces.xyaspace.XYATransform;
//import net.agileframes.vr.SceneAvatar3D;

public class CrossScene extends SceneIB /*implements BodyRemote*/ {
  //-------------------------- Semaphores --------------------------------
  public static Semaphore semA;
  public static Semaphore semB1;
  public static Semaphore semB2;
  public static Semaphore semC;
  //-------------------------- Moves -------------------------------------
  public static Move moveA;
  public static Move moveB1;
  public static Move moveB2;
  public static Move moveC;
  public static Move move8;
  //-------------------------- SceneActions ------------------------------
  public static SceneAction leftToRight;

  //-------------------------- Constructor -------------------------------
  public CrossScene() throws java.rmi.RemoteException {
    super("CrossScene");
  }

  //-------------------------- Methods -----------------------------------
  public void initialize() {
    try {
      // Transform
      this.transform = new XYATransform(-60, 0, 0);

      // Semaphores:
      semA = new Semaphore("semA", 1);
      semB1 = new Semaphore("semB1", 1);
      semB2 = new Semaphore("semB2", 1);
      semC = new Semaphore("semC", 1);
      this.semaphores = new Semaphore[] {semA, semB1, semB2, semC};
      // Moves:
      moveA = new MoveA(transform);
      moveB1 = new MoveB1(transform);
      moveB2 = new MoveB2(transform);
      moveC = new MoveC(transform);
      move8 = new Move8(transform);
//      this.moves = new Move[] {moveA, moveB1, moveB2, moveC};
/**/      this.moves = new Move[] {move8};
      // SceneActions:
      leftToRight = new LeftToRight(this, null);
      this.sceneActions = new SceneAction[] {leftToRight};
    } catch (Exception e) {
      System.out.println("Exception while initializing Scene: "+e.getMessage());
      e.printStackTrace();
    }
  }
  //-------Scene--------
/*  public boolean isChanged() throws java.rmi.RemoteException {
    if (!sceneChanged) { return false; } else { sceneChanged = false; return true; }
  }
  public Move[] getMoves() throws java.rmi.RemoteException { return moves; }*/

  public synchronized SceneAction getSceneAction(String name, Actor actor) throws java.rmi.RemoteException {
    SceneAction action = null;
    SceneAction clone = null;
    if (name.equals(""))                 { action = null; }
    else if (name.equals("LeftToRight")) { action = leftToRight; }
    else if (name.equals("left2right"))  { action = leftToRight; }
//    else if   enzovoorts
    else {
      System.out.println("CrossScene.getSceneAction: No sceneAction was found for :"+name);
      return null;
    }
    try { clone =(SceneAction)action.clone(actor); }
    catch (CloneNotSupportedException e) { e.printStackTrace(); }
    //registerSA(actor, action);
    return clone;
  }

  //-- MAIN --
  private static CrossScene scene = null;
  public static void main(String[] args) {
    System.out.println("Creating CrossScene...");
    try { scene = new CrossScene(); }
    catch (Exception e) {
      System.out.println("Exception in CrossScene.main: "+e.getMessage());
      e.printStackTrace();
    }
  }

}
