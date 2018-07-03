package com.agileways.carpark.sceneactions;

import com.agileways.carpark.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.LogisticPosition;

public class ParkInAction extends SceneAction {
  //--- Attributes ---
  private int entrance;
  private final boolean DEBUG = true;
  // WATCH OUT: the following parameters will not be cloned!
  // So, they will be reset after cloning the SceneAction
  private Ticket ticRoad1 = null;
  private Ticket ticRoad2 = null;
  private Ticket[] ticPark = new Ticket[8];
  private Move move1 = null;


  //--- Constructor ---
  public ParkInAction() throws java.rmi.RemoteException {}
  public ParkInAction(Scene scene, Action superSceneAction, int entrance) throws java.rmi.RemoteException {
    this.name = "ParkInAction_"+entrance;
    this.scene = scene;
    this.superSceneAction = superSceneAction;
    this.entrance = entrance;

    // create the tickets and store them in array tickets[]
    // take care of the order in which the tickets are being stored!
    this.tickets = new Ticket[10];
    tickets[0] = new PrimeTicket(this, CarParkScene.semRoad[entrance]);//road1
    tickets[1] = new PrimeTicket(this, CarParkScene.semRoad[1-entrance]);//road2 (can be equal to road1)
    //tickets[2] = null;//new PrimeTicket(this, CarParkScene.semPark[side][lane]);//park

    this.moves = new Move[8];
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 4; j++) {
        moves[i*4+j] = CarParkScene.moveIn[entrance][i][j];
        tickets[i*4+j+2] = new PrimeTicket(this, CarParkScene.semPark[i][j]);
      }
    }

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  //--- Methods ---
  protected void initialize() {
    // regain the tickets from the tickets[] array: SAME ORDER as stored above!!
    ticRoad1 = tickets[0];
    ticRoad2 = tickets[1];

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 4; j++) {
        ticPark[i*4+j] = tickets[i*4+j+2];
      }
    }

    // idem with moves
    //move1 = moves[0];// moves are cloned at the same time as scene-action
  }

  //--- The Script ---
  protected void sceneActionScript() {
    try {
      if (DEBUG) System.out.println("*D* ParkInAction: script started!, this="+this.toString()+"   actor="+this.actor.toString());

      if ( (externalTickets == null ) || (externalTickets.length != 1) ) {
        System.out.println("ParkInAction was called incompletely");
        return;
      }
      Ticket exitTicketPrevious = externalTickets[0];

      // let's first pick a park that is still free
      boolean isFound = false;
      Ticket parkTicket = null;
      int lane = 0; int side = 0;
      while (!isFound) {
        while ((!isFound) && (side < 2)) {
          parkTicket = ticPark[side*4 + lane];
          if (DEBUG) System.out.println("*D* ParkInAction: TRYING: ("+side+","+lane+")  ....");
          try {
            //parkTicket.free(); // because it might still be assigned from the last try (in this same action of course)
            isFound = parkTicket.attempt();
          } catch (Exception e) { e.printStackTrace(); }
          if (!isFound) side++;
        }
        if (!isFound) {lane++; side = 0;}
      }
      if (DEBUG) System.out.println("*D* ParkInAction: FOUND a parkplace: ("+side+","+lane+")");
      move1 = moves[side*4+lane];

      //ticPark.insist();
      if (DEBUG) System.out.println("*D* ParkInAction: ticPark insisted");

      if (DEBUG) System.out.println("*D* ParkInAction: entrance = "+entrance);

      if (entrance < side) {
        // always claim in smae order (first 0-lane then 1-lane) otherwise deadlock-danger
        ticRoad1.insist();//entrance-lane
        if (DEBUG) System.out.println("*D* ParkInAction: ticRoad1 insisted");
        ticRoad2.insist();//parkSide-lane
        if (DEBUG) System.out.println("*D* ParkInAction: ticRoad2 insisted");
      } else if (entrance > side) {
        ticRoad2.insist();//parkSide-lane
        if (DEBUG) System.out.println("*D* ParkInAction: ticRoad2 insisted");
        ticRoad1.insist();//entrance-lane
        if (DEBUG) System.out.println("*D* ParkInAction: ticRoad1 insisted");
      } else {//they're equal
        ticRoad1.insist();
        if (DEBUG) System.out.println("*D* ParkInAction: ticRoad1 insisted, ticRoad2 = null");
        ticRoad2 = null;
      }

      if (DEBUG) System.out.println("*D* ParkInAction: about to run move1!");
      // start move1 (with:  exitTicPrev, ticRoad1, ticRoad2)
      //       move1 takes care of: freeing exitTicPrev, ticRoad1, ticRoad2 (ticRoad2 may be null)
      move1.run(new Ticket[] { exitTicketPrevious, ticRoad1, ticRoad2 });

      // wait for anticipatingSign of move1
      watch(move1.getSign(0));
      if (DEBUG) System.out.println("*D* ParkInAction: move1 raised anticipating sign");
      // remove timedStop-precaution of move1
      move1.getManoeuvre().getPrecaution(0).remove();

      watch(move1.getSign(1));
      if (DEBUG) System.out.println("*D* ParkInAction: move1 raised finished sign");

      // specify exit-ticket: finish(ticEndPArk)
      finish(parkTicket);
      endPosition = new LogisticPosition("Park", scene, new int[] {side, lane});

    } catch (Exception e) {
      System.out.println("Exception in ParkInAction: The scene-action will be aborted.");
      e.printStackTrace();
    }


    signs[0].broadcast();
  }
}