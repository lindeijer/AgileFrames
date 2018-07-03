package com.agileways.democarpark;

import net.agileframes.core.traces.*;

import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.traces.ticket.SelectTicket;

import com.agileways.demo.DemoScene;
import com.agileways.carpark.CarParkScene;


public class DemoCarParkSuperAction extends SceneAction {
  //-- Attributes --
  private Scene demoScene;
  private Scene carParkScene;
  private boolean DEBUG = true;
  private Ticket ticSuperDemo = null;
  private Ticket[][] ticCrossing = new Ticket[4][2];

  //-- Constructors --
  public DemoCarParkSuperAction() throws java.rmi.RemoteException {}
  public DemoCarParkSuperAction(Scene superScene, Scene demoScene, Scene carParkScene, Action superSceneAction) throws java.rmi.RemoteException {
    this.name = "DemoCarParkSuperAction";
    this.scene = superScene;
    this.superSceneAction = superSceneAction;
    this.demoScene = demoScene;
    this.carParkScene = carParkScene;

    this.tickets = new Ticket[9];
    tickets[0] = new PrimeTicket(this, DemoCarParkSuperScene.semSuperDemo);
    for (int i = 0 ; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        tickets[1+i*2+j] = new PrimeTicket(this, DemoCarParkSuperScene.semCrossing[i][j]);
      }
    }

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  protected void initialize() {
    ticSuperDemo = tickets[0];
    for (int i = 0 ; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        ticCrossing[i][j] = tickets[1+i*2+j];
      }
    }
  }

  protected void sceneActionScript() throws net.agileframes.core.traces.BlockException {
    if ( (externalTickets == null ) || (externalTickets.length != 1) ) {
      System.out.println("DemoCarParkSuperAction was called incompletely");
      return;
    }
    Ticket exitTicketPrevious = externalTickets[0];

    if ((demoScene == null) || (carParkScene == null) ) {
      if ( (demoScene == null) && (carParkScene == null) ) {
        System.out.println("Both DemoScene and CarParkScene are not available.");
        System.out.println("This sceneaction (DemoCarParkSuperAction) will be aborted.");
        return;
      }
      SceneAction sa = null;
      Ticket[] saTickets = null;
      while (sa == null) {
        try {
          if (demoScene == null) {
            sa = carParkScene.getSceneAction("CarParkSuperAction",actor);
            System.out.println("DemoScene is not available.");
            System.out.println("Only CarParkScene will be used.");
            saTickets = new Ticket[] {exitTicketPrevious, ticCrossing[1][0], ticCrossing[1][1]};
            beginPosition = new LogisticPosition("Entrance", carParkScene, new int[] {0});
          }
          if (carParkScene == null) {
            sa = demoScene.getSceneAction("DemoSuperAction",actor);
            System.out.println("CarParkScene is not available.");
            System.out.println("Only DemoScene will be used.");
            saTickets = new Ticket[9];
            saTickets[0] = exitTicketPrevious;
            for (int i = 0 ; i < 4; i++) {
              for (int j = 0; j < 2; j++) {
                 saTickets[1+i*2+j] = ticCrossing[i][j];
              }
            }
          }
        } catch (java.rmi.RemoteException e) {
          System.out.println("RemoteException while downloading SceneAction in DemoCarParkSuperAction. We will try again.");
          System.out.println("Exception = "+e.getMessage());
          sa = null;
        }
      }//while
      sa.setBeginPosition(beginPosition);
      sa.setActor(actor);
      sa.run(saTickets);
      this.watch(sa.getSign(0));
      endPosition = sa.getEndPosition();
      exitTicketPrevious = sa.getExitTicket();
      finish(exitTicketPrevious);
      signs[0].broadcast();
      return;//thread dies or execute comes back
    }
    //(beginsituation: agv is somewhere in the scene and has claimed its semaphore)
    //its location is represented by beginPosition
    endPosition = beginPosition;

    while (true) {
      System.out.println("DemoCarParkSuperAction: beginPosition = "+beginPosition.toString());
      SceneAction sa = null;
      Ticket[] saTickets = null;
      while (sa == null) {
        try {
          if (endPosition.scene.equals(demoScene)) {
            //[scene=demo]
            if (endPosition.params[0] == 1) {
              sa = carParkScene.getSceneAction("ParkInAction_"+endPosition.params[1], actor);
              this.ticSuperDemo.free();
              //we want to park ((1))
              beginPosition = new LogisticPosition("Entrance", carParkScene, new int[] {endPosition.params[1]} );
              saTickets = new Ticket[] {exitTicketPrevious};
            } else {
              //we probably just started up: continue driving in demo ((*))
              sa = demoScene.getSceneAction("DemoSuperAction", actor);
              beginPosition = endPosition;//beginPosition next action is same as endPosition last one
              saTickets = new Ticket[9];
              saTickets[0] = exitTicketPrevious;
              for (int i = 0 ; i < 4; i++) {
                for (int j = 0; j < 2; j++) {
                   saTickets[1+i*2+j] = ticCrossing[i][j];
                }
              }
            }
          } else if (endPosition.scene.equals(carParkScene)) {
            //[scene=carpark]
            if (endPosition.params.length == 1) {
              //we are at entrance: want to demo ((3))
              sa = demoScene.getSceneAction("DemoSuperAction", actor);
              beginPosition = new LogisticPosition("Park", demoScene, new int[] {1, endPosition.params[0]} );
              saTickets = new Ticket[9];
              saTickets[0] = exitTicketPrevious;
              for (int i = 0 ; i < 4; i++) {
                for (int j = 0; j < 2; j++) {
                   saTickets[1+i*2+j] = ticCrossing[i][j];
                }
              }
            } else {
              //we are at park: want to parkout ((2))
              // first wait until there's room for us
              sa = carParkScene.getSceneAction("ParkOutAction_"+endPosition.params[0]+"."+endPosition.params[1], actor);
              this.ticSuperDemo.insist();
              beginPosition = endPosition;//beginPosition next action is same as endPosition last one
              saTickets = new Ticket[] {exitTicketPrevious, ticCrossing[1][0], ticCrossing[1][1]};
            }
          } else if (endPosition.scene.equals(scene)) {
            //[scene=super]
            //we probably just started up: park in ((*))
            sa = carParkScene.getSceneAction("ParkInAction_"+endPosition.params[1], actor);
            beginPosition = new LogisticPosition("Entrance", carParkScene, new int[] {1, endPosition.params[1]} );
            saTickets = new Ticket[] {exitTicketPrevious};
          } else {
            System.out.println("ERROR! Did not recognise scene in beginPosition!");
            System.out.println("This SceneAction (DemoCarParkSuperAction) will be aborted.");
            endPosition = beginPosition;//you never know
            return;
          }
        } catch (java.rmi.RemoteException e) {
          System.out.println("RemoteException while downloading SceneAction in DemoCarParkSuperAction. We will try again.");
          System.out.println("Exception = "+e.getMessage());
          sa = null;
        }
      }//while

      sa.setBeginPosition(beginPosition);
      sa.setActor(actor);
      System.out.println("DemoCarParkSuperAction: decided to drive action: "+sa.toString());
      sa.run(saTickets);
      this.watch(sa.getSign(0));
      endPosition = sa.getEndPosition();
      exitTicketPrevious = sa.getExitTicket();
    }// and again.
  }
}