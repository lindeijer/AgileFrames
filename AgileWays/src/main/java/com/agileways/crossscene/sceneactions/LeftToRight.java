package com.agileways.crossscene.sceneactions;

import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.Ticket;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.SelectTicket;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.forces.Move;

import com.agileways.crossscene.CrossParameters;
import com.agileways.crossscene.CrossScene;
import com.agileways.crossscene.moves.*;

import net.agileframes.server.AgileSystem;
import net.jini.core.lookup.ServiceTemplate;

public class LeftToRight extends SceneAction {
  //------------- Attributes --------------
  private int snip = (int)Math.random() + 1;
  //------------- Constructor -------------
  public LeftToRight() throws java.rmi.RemoteException { System.out.println("******* Empty constructor LeftToRight called"); }
  public LeftToRight(CrossScene scene, Action superSceneAction) throws java.rmi.RemoteException {
    this.scene = scene;
    this.superSceneAction = superSceneAction;
    CrossScene crossScene = (CrossScene) scene;
    Ticket tickA = new PrimeTicket(this, crossScene.semA);
    Ticket tickB1 = new PrimeTicket(this, crossScene.semB1);
    Ticket tickB2 = new PrimeTicket(this, crossScene.semB2);
////              Ticket tickB1OrB2 = null;
/**/    Ticket tickB1OrB2 = new SelectTicket(this, tickB1, tickB2);
////              if (snip == 1) { tickB1OrB2 = tickB1; } else { tickB1OrB2 = tickB2; }
    Ticket tickC = new PrimeTicket(this, crossScene.semC);
    this.tickets = new Ticket[] { tickA, tickB1OrB2, tickC };
    Move moveA = crossScene.moveA;
    Move moveB1 = crossScene.moveB1;
    Move moveB2 = crossScene.moveB2;
    Move moveC = crossScene.moveC;
    Move move8 = null;
    try {
      move8 = (Move)crossScene.move8.clone();
    } catch (Exception e) { e.printStackTrace(); }
////    this.moves = new Move[] { moveA, moveB1, moveB2, moveC };
/**/    this.moves = new Move[] { move8 };
  }

  protected void sceneActionScript(){
/*    Ticket tickA = tickets[0];
    Ticket tickB1OrB2 = tickets[1];
    Ticket tickC = tickets[2];
    Move moveA = moves[0];
    Move moveB1 = moves[1];
    Move moveB2 = moves[2];
    Move moveC = moves[3];*/

    Move move8 = null;//moves[0].clone();


    try {
      System.out.println("SceneActionScript of LeftToRight started!, actor="+actor.toString());

      //this.startAction();

      net.agileframes.core.forces.Sign prevSign = null;
      for (;;) {
        move8 = (Move)moves[0].clone();
        move8.prepare();
        System.out.println("move8 prepared..waiting for last one to be finished");

        if (prevSign != null) {
          watch(prevSign);
          System.out.println("prevMove done..starting new one");
        }
        move8.run(new Ticket[] {});
        watch(move8.getSign(1));
        System.out.println("move8 passed 90%..preparing new one and removing precaution");
        move8.getManoeuvre().getPrecaution(0).remove();
        prevSign = move8.getSign(0);

      }

/*      System.out.println("SceneActionScript of LeftToRight done!");

      moveA.prepare();
      tickA.insist();
      moveA.run(tickA);

      this.watch(moveA.getSign(0));
      System.out.println("LeftToRight received moveA.anticipatingSign");
      tickB1OrB2.insist();
      System.out.println("--> tickB1OrB2.insist done!");
      Move moveB = null;
////      if (tickB1OrB2.snip() == 1) { moveB = moveB1; } else { moveB = moveB2; }
/**      moveB = moveB1;
////      if (snip == 1) { moveB = moveB1; } else { moveB = moveB2; }
      System.out.println("And chose to ride "+moveB.toString());
      moveB.prepare();

      this.watch(moveA.getFinishedSign());
      System.out.println("LeftToRight received moveA.finshedSign");
      moveB.run(new Ticket[] { tickC, tickB1OrB2 });

      this.watch(moveB.getSign(0));
      System.out.println("LeftToRight received moveB.anticipatingSign");
      moveC.prepare();

      this.watch(moveB.getFinishedSign());
      System.out.println("LeftToRight received moveB.finshedSign");
      moveC.run(tickC);

      this.watch(moveC.getSign(0));
      System.out.println("LeftToRight received moveC.anticipatingSign");
      System.out.println("LeftToRight-script almost ended.");

      this.watch(moveC.getFinishedSign());
      System.out.println("LeftToRight received moveC.finshedSign");
      System.out.println("LeftToRight-script ended.");*/
    } catch (Exception e) {
      System.out.println("Exception in LeftToRight.script(): "+e.getMessage());
      e.printStackTrace();
    }
  }

  public Ticket[] getExitTickets(){
    return null;
  }
}