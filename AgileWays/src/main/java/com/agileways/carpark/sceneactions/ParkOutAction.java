package com.agileways.carpark.sceneactions;

import com.agileways.carpark.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.SelectTicket;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.LogisticPosition;

public class ParkOutAction extends SceneAction {
  //--- Attributes ---
  private int side, lane;
  private final boolean DEBUG = true;
  // WATCH OUT: the following parameters will not be cloned!
  // So, they will be reset after cloning the SceneAction
  private Ticket ticRoad1 = null;
  private Ticket ticRoad2 = null;
//  private Ticket ticEntrance = null;
  private Move move1 = null;


  //--- Constructor ---
  public ParkOutAction() throws java.rmi.RemoteException {}
  public ParkOutAction(Scene scene, Action superSceneAction, int side, int lane) throws java.rmi.RemoteException {
    this.name = "ParkOutAction_"+side+"."+lane;
    this.scene = scene;
    this.superSceneAction = superSceneAction;
    this.side = side;
    this.lane = lane;

    // create the tickets and store them in array tickets[]
    // take care of the order in which the tickets are being stored!
    this.tickets = new Ticket[2];
    tickets[0] = new PrimeTicket(this, CarParkScene.semRoad[side]);//road1
    tickets[1] = new PrimeTicket(this, CarParkScene.semRoad[1-side]);//road2 //may not be used
//    tickets[2] = null;//new PrimeTicket(this, CarParkScene.semEntrance[entrance]);//entrance

    this.moves = new Move[2];
    moves[0] = CarParkScene.moveOut[0][side][lane];
    moves[1] = CarParkScene.moveOut[1][side][lane];

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  //--- Methods ---
  protected void initialize() {
    // regain the tickets from the tickets[] array: SAME ORDER as stored above!!
    ticRoad1 = tickets[0];
    ticRoad2 = tickets[1];
    //ticEntrance = tickets[2];//exit-ticket of this SA: freed in next SA
    // idem with moves
    //move1 = moves[0];// moves are cloned at the same time as scene-action
  }

  //--- The Script ---
  protected void sceneActionScript() {
    try {
      if (DEBUG) System.out.println("*D* ParkOutAction: script started!, this="+this.toString()+"   actor="+this.actor.toString());

      if ( (externalTickets == null ) || (externalTickets.length != 3) ) {
        System.out.println("ParkOutAction was called incompletely");
        return;
      }
      Ticket exitTicketPrevious = externalTickets[0];
      Ticket[] ticEntrance = new Ticket[2];
      ticEntrance[0] = externalTickets[1];
      ticEntrance[1] = externalTickets[2];


      Ticket entranceTicket = new SelectTicket(this, ticEntrance[0], ticEntrance[1]);
      if (DEBUG) System.out.println("*D* ParkOutAction: About to insist on select ticket");
      entranceTicket.insist();

      if (DEBUG) System.out.println("*D* ParkOutAction: Insisted on select ticket");
      int entrance = entranceTicket.snip() - 1;
      if (entrance == 0) { move1 = moves[0]; } else { move1 = moves[1]; }
      if (DEBUG) System.out.println("*D* ParkOutAction: entrance="+entrance);

      if (entrance > side) {
        // always claim in same order (first 0-lane then 1-lane) otherwise deadlock-danger
        ticRoad1.insist();//parkSide-lane
        if (DEBUG) System.out.println("*D* ParkOutAction: ticRoad1 insisted");
        ticRoad2.insist();//entrance-lane
        if (DEBUG) System.out.println("*D* ParkOutAction: ticRoad2 insisted");
      } else if (entrance < side) {
        ticRoad2.insist();//entrance-lane
        if (DEBUG) System.out.println("*D* ParkOutAction: ticRoad2 insisted");
        ticRoad1.insist();//parkSide-lane
        if (DEBUG) System.out.println("*D* ParkOutAction: ticRoad1 insisted");
      } else {//they're equal
        ticRoad1.insist();
        if (DEBUG) System.out.println("*D* ParkOutAction: ticRoad1 insisted, ticRoad2=null");
        ticRoad2 = null;
      }

      if (DEBUG) System.out.println("*D* ParkOutAction: about to run move1!");
      // start move1 (with:  exitTicPrev, ticRoad1, ticRoad2)
      //       move1 takes care of: freeing exitTicPrev, ticRoad1, ticRoad2 (ticRoad2 may be null)
      move1.run(new Ticket[] { exitTicketPrevious, ticRoad1, ticRoad2 });

      // wait for anticipatingSign of move1
      watch(move1.getSign(0));
      if (DEBUG) System.out.println("*D* ParkOutAction: move1 raised anticipating sign");
      // remove timedStop-precaution of move1
      move1.getManoeuvre().getPrecaution(0).remove();

      watch(move1.getSign(1));
      if (DEBUG) System.out.println("*D* ParkOutAction: move1 raised finished sign");


      // specify exit-ticket: finish(ticEndPArk)
      finish(entranceTicket);
      endPosition = new LogisticPosition("Entrance", scene, new int[] {entrance});

    } catch (Exception e) {
      System.out.println("Exception in ParkOutAction: The scene-action will be aborted.");
      e.printStackTrace();
    }


    signs[0].broadcast();
  }
}