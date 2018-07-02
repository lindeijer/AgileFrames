package com.agileways.traces.scene.jumboterminal.sceneactions;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.BlockException;
import java.rmi.RemoteException;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.brief.Brief;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import net.agileframes.traces.ticket.SelectTicket;
import net.agileframes.core.traces.Ticket;

/**
 * The SceneAction that drives from the stack to the park through the center.
 */
public class StackAreaToSouthToQuayArea extends SceneAction {
  private int stackNr, stackLane, parkNr;
  private int parkLane = -1;
  private PrimeTicket primTicCenter1, primTicCenter2, primTicTotalPark;
  private PrimeTicket[] primTicStackExit;
  private PrimeTicket[] primTicParkEntrance;
  private PrimeTicket[] primTicPark;
  //private SelectTicket selTicPark;
  private MoveImplBase move1;

  /**
   * @param stackNr     origin stackNr      0 <= stackNr < numberOfStacks
   * @param stackLane   origin stackLane    0 <= stackLane < lanesPerStack
   * @param parkNr      destination parkNr  0 <= parkNr < numberOfParks
   *
   * @throws RemoteException
   */
  public StackAreaToSouthToQuayArea(
      CrossoverScene cS,int stackNr, int stackLane, int parkNr) {
    super(cS);
    this.crossoverScene=cS;
    this.cT=cS.cT;
    //
    this.stackNr = stackNr;
    this.stackLane = stackLane;
    this.parkNr = parkNr;
  }

   private CrossoverScene crossoverScene;
   private CrossoverTerminal cT;

  /**
   * Creates the necessary tickets for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
    //
    primTicStackExit = new PrimeTicket[2*(cT.lanesPerStack+1)];
    primTicParkEntrance = new PrimeTicket[cT.lanesPerPark];
    primTicPark = new PrimeTicket[cT.lanesPerPark];
    //
    switch (parkNr-stackNr) {
      case 0:
      case -1:// straight through center: only one exit has to be claimed
        primTicStackExit[0] = new PrimeTicket(this, crossoverScene.semStackExit[stackNr][stackLane]);
      break;
      case -2:// to west: all exits to the west have to be claimed until the middle stackLane
        int index = 0;
        for (int exit=cT.lanesPerStack/2; exit<=cT.lanesPerStack; exit++) {
          primTicStackExit[index] = new PrimeTicket(this, crossoverScene.semStackExit[stackNr-1][exit]);
          index++;
        }
        for (int exit=0;exit<=stackLane;exit++){
          primTicStackExit[index] = new PrimeTicket(this, crossoverScene.semStackExit[stackNr][exit]);
          index++;
        }
      break;
      case 1:// to east: all exits to the east have to be claimed until the middle stackLane
        index = 0;
        for (int exit=stackLane; exit<=cT.lanesPerStack; exit++) {
          primTicStackExit[index] = new PrimeTicket(this, crossoverScene.semStackExit[stackNr][exit]);
          index++;
        }
        for (int exit=0;exit<=cT.lanesPerStack/2;exit++){
          primTicStackExit[index] = new PrimeTicket(this, crossoverScene.semStackExit[stackNr+1][exit]);
          index++;
        }
      break;
    }

    int centerNr = 99;
    if ((stackNr==parkNr)||(stackNr-1==parkNr)) {centerNr = stackNr;}
    else {
      if (stackNr>parkNr) {centerNr = parkNr + 1;} else {centerNr = parkNr;}
    }
    primTicCenter1 = new PrimeTicket(this, crossoverScene.semCenterNorth[centerNr]);
    primTicCenter2 = new PrimeTicket(this, crossoverScene.semCenterSouth[centerNr]);

    for (int lane=0;lane<cT.lanesPerPark;lane++){
      primTicParkEntrance[lane] = new PrimeTicket(this,crossoverScene.semParkEntrance[parkNr][lane]);
    }

    primTicTotalPark = new PrimeTicket(this, crossoverScene.semTotalPark[parkNr]);
    for (int lane=0; lane<cT.lanesPerPark; lane++) {
      primTicPark[lane] = new PrimeTicket(this, crossoverScene.semPark[parkNr][lane]);
    }
  }

  /**
   * Creates and executes the appropriate logistic move and claims
   * semaphores by insisting on their respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    //selTicPark = new SelectTicket("selectParkCenter",this,primTicParkLane);
    //selTicPark.insist();
    //parkLane = selTicPark.snip()-1;
    primTicTotalPark.insist();
    boolean gotOne = false;
    int lane = 0;
    while (!gotOne) {
      gotOne = primTicPark[lane].attempt();
      lane++;
    }
    parkLane = lane - 1;

    for (int exit=0; exit<primTicStackExit.length; exit++) {
      if (primTicStackExit[exit]!=null) {primTicStackExit[exit].insist();}
    }
    primTicCenter1.insist();
    primTicCenter2.insist();

    if (parkNr>=stackNr) {// entering park from west to east
      for (int lne=0; lne<=parkLane;lne++){
        primTicParkEntrance[lne].insist();
      }
    } else {//entering park from east to west
      for (int lne=parkLane; lne<cT.lanesPerPark;lne++){
        primTicParkEntrance[lne].insist();
      }
    }

    move1 = crossoverScene.leaveStackForParking[stackNr][stackLane][parkNr][parkLane].clone(actor,null);
    move1.reset();

    PrimeTicket[] allTickets = new PrimeTicket[primTicParkEntrance.length + 4 + primTicStackExit.length];
    allTickets[0]=primTicCenter1;
    allTickets[1]=primTicCenter2;
    allTickets[2]=primTicPark[parkLane];
    allTickets[3]=primTicTotalPark;
    for (int i=0;i<cT.lanesPerPark;i++){ allTickets[i+4] = primTicParkEntrance[i];  }
    for (int i=0;i<primTicStackExit.length;i++){  allTickets[4+cT.lanesPerPark+i] = primTicStackExit[i];  }

    move1.exec(null, allTickets, new Brief[]{});
  }

  /**
   * gives the chosen stacklane
   * @return the stacklane selected in this sceneaction
   */
  public int getParkLane(){
    return parkLane;
  }
}