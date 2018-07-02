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
//
import com.agileways.traces.scene.jumboterminal.logisticmoves.ParkWest;
import com.agileways.traces.scene.jumboterminal.logisticmoves.LeaveStackForCenterWest;
import com.agileways.traces.scene.jumboterminal.logisticmoves.CrossoverArea2West;
//
import net.agileframes.traces.ticket.SelectTicket;
import net.agileframes.core.traces.Ticket;

/**
 * Action for driving from a stack area westwards to a quay area
 */
public class StackAreaToWestToQuayArea extends SceneAction {

  private int stackAreaID, stackParkAreaID, quayAreaID;
  private int quayParkAreaID = -1;
  private StackAreaToWest             stackAreaToWest;
  private CrossoverAreaToWest[]       crossoverAreaToWest;
  private CrossoverAreaToQuayAreaWest crossoverAreaToQuayAreaWest;

  private CrossoverScene cS;
  private CrossoverTerminal cT;

  /**
   * @param stackAreaID     origin stackAreaID      0 <= stackAreaID < numberOfStacks
   * @param stackParkAreaID origin stackParkAreaID  0 <= stackParkAreaID < lanesPerStack
   * @param quayAreaID      destination quayAreaID  0 <= quayAreaID < numberOfParks
   */
  public StackAreaToWestToQuayArea(
      CrossoverScene cS,int stackAreaID, int stackParkAreaID, int quayAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=CrossoverScene.cT;
    this.stackAreaID     = stackAreaID;
    this.stackParkAreaID = stackParkAreaID;
    this.quayAreaID      = quayAreaID;
    createSubActions();
  }

  private void createSubActions() {
    stackAreaToWest = new StackAreaToWest(cS,stackAreaID,stackParkAreaID);
    crossoverAreaToWest = new CrossoverAreaToWest[cT.numberOfStacks];
    for (int nr=(stackAreaID-2); nr>=(quayAreaID+2); nr--) {
      crossoverAreaToWest[nr] = new CrossoverAreaToWest(cS,nr);
    }
    crossoverAreaToQuayAreaWest = new CrossoverAreaToQuayAreaWest(cS,quayAreaID);
  }

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    //
    stackAreaToWest.assimilate(actor);
    for (int nr=(stackAreaID-2); nr>=(quayAreaID+2); nr--) {
      crossoverAreaToWest[nr].assimilate(actor);
    }
    crossoverAreaToQuayAreaWest.assimilate(actor);
  }

  /**
   * Executes the appropriate logistic move and claims
   * semaphores by insisting on their respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    // select the destination quay park lane.
    System.out.println("executing StackToParkWest");
    SelectTicket quayParkAreaST = new SelectTicket(
      (PrimeTicket)crossoverAreaToQuayAreaWest.getAccessTicket(0),
      (PrimeTicket)crossoverAreaToQuayAreaWest.getAccessTicket(1),
      (PrimeTicket)crossoverAreaToQuayAreaWest.getAccessTicket(2),
      (PrimeTicket)crossoverAreaToQuayAreaWest.getAccessTicket(3),
      (PrimeTicket)crossoverAreaToQuayAreaWest.getAccessTicket(4)
    );
    quayParkAreaST.insist();
    quayParkAreaID = quayParkAreaST.getSelectedIndex();
    stackAreaToWest.execute();
    for (int nr=(stackAreaID-2); nr>=(quayAreaID+2); nr--) {
      crossoverAreaToWest[nr].execute();
    }
    crossoverAreaToQuayAreaWest.execute();
  }

  /**
   * Gets the selected quayParkArea index.
   * Called by superAction.
   */
  public int getQuayParkAreaID() { return quayParkAreaID; }

}

////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////

class CrossoverAreaToQuayAreaWest extends SceneAction {

  private int quayAreaID;
  private ParkWest[][] crossoverAreaToQuayAreaWest;
  private PrimeTicket primTicCenter1, primTicCenter2;
  private PrimeTicket[] primTicPark;
  private PrimeTicket[] primTicParkEntrance;
  private CrossoverScene cS;
  private CrossoverTerminal cT;

  public CrossoverAreaToQuayAreaWest(CrossoverScene cS,int quayAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.quayAreaID = quayAreaID;
  }

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    //
    primTicParkEntrance = new PrimeTicket[cT.lanesPerPark];
    primTicPark = new PrimeTicket[cT.lanesPerPark];
    //
    primTicCenter1 = new PrimeTicket (this, cS.semCenterNorth[quayAreaID+1]);
    primTicCenter2 = new PrimeTicket (this, cS.semCenterSouth[quayAreaID+1]);
    for (int lane=0; lane<cT.lanesPerPark; lane++) {
      primTicPark[lane] = new PrimeTicket(this, cS.semPark[quayAreaID][lane]);
    }
    // selTicPark = new SelectTicket("selectParkWest",this,primTicParkLane);
    for (int lane=0;lane<cT.lanesPerPark;lane++){
      primTicParkEntrance[lane] =
       new PrimeTicket(this,cS.semParkEntrance[quayAreaID][lane]);
    }
    crossoverAreaToQuayAreaWest =
      new ParkWest[cT.numberOfStacks-1][cT.lanesPerPark];
    for (int quayAreaID=0;quayAreaID<cT.numberOfStacks-1;quayAreaID++)
    {
      for (int parkLane=0;parkLane<cT.lanesPerPark;parkLane++)
      {
        crossoverAreaToQuayAreaWest[quayAreaID][parkLane] = (ParkWest)
          cS.parkWest[quayAreaID][parkLane].clone(actor, null);
      }
    }

  }

  protected void script() throws BlockException,RemoteException {
    SelectTicket quayParkAreaST = new SelectTicket(
      primTicPark[0],
      primTicPark[1],
      primTicPark[2],
      primTicPark[3],
      primTicPark[4]
    );
    // selection already forced in superScript.
    quayParkAreaST.insist();
    int parkLane = quayParkAreaST.getSelectedIndex();
    primTicCenter1.insist();
    primTicCenter2.insist();
    for ( int lne=parkLane ; lne<cT.lanesPerPark ; lne++ )
    {
      primTicParkEntrance[lne].insist();
    }
    PrimeTicket[] allTickets = new PrimeTicket[primTicParkEntrance.length + 4];
    allTickets[0]=primTicCenter1;
    allTickets[1]=primTicCenter2;
    allTickets[2]=primTicPark[parkLane];
    allTickets[3]=null; // used to be  primTicTotalPark, is now freed afters selection of parklane in super action.;
    for (int i=0;i<primTicParkEntrance.length;i++)
    {
      allTickets[i+4] = primTicParkEntrance[i];
    }
    crossoverAreaToQuayAreaWest[quayAreaID][parkLane].reset();
    crossoverAreaToQuayAreaWest[quayAreaID][parkLane].exec(null, allTickets, new Brief[]{});
  }

  Ticket getAccessTicket(int i) {
    Ticket accessTicket = null;
    switch (i) {
      case 0 : accessTicket = primTicPark[0]; break;
      case 1 : accessTicket = primTicPark[1]; break;
      case 2 : accessTicket = primTicPark[2]; break;
      case 3 : accessTicket = primTicPark[3]; break;
      case 4 : accessTicket = primTicPark[4]; break;
    }
    return accessTicket;
  }

}



