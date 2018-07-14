package com.agileways.carpark.sceneactions;

import com.agileways.carpark.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.LogisticPosition;

import net.agileframes.traces.ticket.SelectTicket;

public class CarParkSuperAction extends SceneAction {
  //--- Attributes ---
  private int currentEntrance = -1;//(int)(2 * Math.random());// random number betw 0..1
  // should not be here but after it is on car
  private int currentParkSide = -1;
  private int currentParkLane = -1;
  private final boolean DEBUG = true;

  //--- Constructor ---
  public CarParkSuperAction() throws java.rmi.RemoteException {}
  public CarParkSuperAction(Scene scene, Action superSceneAction) throws java.rmi.RemoteException {
    this.name = "CarParkSuperAction";
    this.scene = scene;
    this.superSceneAction = superSceneAction;

    if (DEBUG) System.out.println("*D* Initial Position: entrance: "+currentEntrance);

    this.tickets = new Ticket[2*4];
    int counter = 0;
    for (int side = 0; side < 2; side++) {
      for (int lane = 0; lane < 4; lane++){
        tickets[counter] = new PrimeTicket(this, CarParkScene.semPark[side][lane]);
        counter++;
      }
    }

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  //--- Methods ---
  protected void initialize() {
    if (DEBUG) System.out.println("*D* CarParkSuperAction: initializing!");
  }

  //--- The Script ---
  Ticket entranceTicket = null;
  protected void sceneActionScript() {
    if (DEBUG) System.out.println("*D* CarParkSuperAction: started!");
    // start-lane
    if ( (externalTickets == null ) || (externalTickets.length != 3) ) {
      System.out.println("CarParkSuperAction was called incompletely");
      return;
    }
    Ticket exitTicketPrevious = externalTickets[0];
    Ticket[] ticEntrance = new Ticket[2];
    ticEntrance[0] = externalTickets[1];
    ticEntrance[1] = externalTickets[2];


    if (beginPosition.params.length == 1) {
      currentEntrance = beginPosition.params[0];
    } else {
      currentParkSide = beginPosition.params[0];
      currentParkLane = beginPosition.params[1];
    }

    while (true) {
      SceneAction sa = null;
      try { sa = (SceneAction)scene.getSceneAction("ParkInAction_"+currentEntrance, actor); }
      catch (Exception e) { e.printStackTrace(); }

      sa.setActor(actor);
      sa.run(new Ticket[] {exitTicketPrevious});

      watch(sa.getSign(0));
      if (DEBUG) System.out.println("*D* CarParkSuperAction: Received sign 0");
      exitTicketPrevious = sa.getExitTicket();
      this.endPosition = sa.getEndPosition();

///////////////////////

      currentParkSide = endPosition.params[0];
      currentParkLane = endPosition.params[1];
      currentEntrance = -1;

      try { sa = (SceneAction)scene.getSceneAction("ParkOutAction_"+currentParkSide+"."+currentParkLane, actor); }
      catch (Exception e) { e.printStackTrace(); }

      sa.setActor(actor);
      sa.run(new Ticket[] {exitTicketPrevious, ticEntrance[0], ticEntrance[1]});

      watch(sa.getSign(0));
      if (DEBUG) System.out.println("*D* CarParkSuperAction: Received sign 0");
      exitTicketPrevious = sa.getExitTicket();
      this.endPosition = sa.getEndPosition();

      currentParkSide = -1;
      currentParkLane = -1;
      currentEntrance = endPosition.params[0];

    }
  }
}