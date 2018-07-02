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
import com.agileways.traces.scene.jumboterminal.logisticmoves.LeaveStackForCenterEast;
import com.agileways.traces.scene.jumboterminal.logisticmoves.ParkEast;
import com.agileways.traces.scene.jumboterminal.logisticmoves.GoEastAtCenter;

/**
 * The SceneAction that drives from the stack to the park eastwards.
 */
public class StackAreaToEastToQuayArea extends SceneAction {

  StackAreaToEast             stackAreaToEast;
  CrossoverAreaToEast[]       crossoverAreaToEast;
  CrossoverAreaToQuayAreaEast crossoverAreaToQuayAreaEast;

  /**
   * @param stackAreaID     origin stackAreaID      0 <= stackAreaID < numberOfStacks
   * @param stackParkAreaID   origin stackParkAreaID    0 <= stackParkAreaID < lanesPerStack
   * @param quayAreaID      destination quayAreaID  0 <= quayAreaID < numberOfParks
   */
  public StackAreaToEastToQuayArea(
      CrossoverScene cS,int stackAreaID, int stackParkAreaID, int quayAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.stackAreaID = stackAreaID;
    this.stackParkAreaID = stackParkAreaID;
    this.quayAreaID = quayAreaID;
    createSubActions();
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;
  private int stackAreaID;
  private int stackParkAreaID;
  private int quayAreaID;
  private int quayParkAreaID = -1;

  private void createSubActions() {
    stackAreaToEast = new StackAreaToEast(cS,stackAreaID,stackParkAreaID);
    crossoverAreaToEast = new CrossoverAreaToEast[cT.numberOfStacks];
    for (int nr=(stackAreaID+2); nr<=(quayAreaID-1); nr++) {
      crossoverAreaToEast[nr] = new CrossoverAreaToEast(cS,nr);
    }
    crossoverAreaToQuayAreaEast = new CrossoverAreaToQuayAreaEast(cS,quayAreaID);
  }

  /**
   * Creates the necessary tickets and moves for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
    stackAreaToEast.assimilate(actor);
    for (int nr=(stackAreaID+2); nr<=(quayAreaID-1); nr++) {
      crossoverAreaToEast[nr].assimilate(actor);
    }
    crossoverAreaToQuayAreaEast.assimilate(actor);
  }

  /**
   * Executes the appropriate logistic move and claims
   * semaphores by insisting on their respective tickets.
   */
  protected void script() throws BlockException {
    System.out.println("executing StackAreaToEastToQuayArea");
    SelectTicket quayParkAreaST = new SelectTicket(
      (PrimeTicket)crossoverAreaToQuayAreaEast.getAccessTicket(0),
      (PrimeTicket)crossoverAreaToQuayAreaEast.getAccessTicket(1),
      (PrimeTicket)crossoverAreaToQuayAreaEast.getAccessTicket(2),
      (PrimeTicket)crossoverAreaToQuayAreaEast.getAccessTicket(3),
      (PrimeTicket)crossoverAreaToQuayAreaEast.getAccessTicket(4)
    );
    quayParkAreaST.insist();
    quayParkAreaID = quayParkAreaST.getSelectedIndex();
    stackAreaToEast.execute();
    for (int nr=(stackAreaID+2); nr<=(quayAreaID-1); nr++) {
      crossoverAreaToEast[nr].execute();
    }
    crossoverAreaToQuayAreaEast.execute();
  }

  /**
   * gives the chosen stacklane
   * @return the stacklane selected in this sceneaction
   */
  public int getParkLane(){
    System.out.println("StackAreaToEastToQuayArea.getPark(AreaID)Lane=" + quayParkAreaID);
    return quayParkAreaID;
  }
}

///////////////////////////////////////////////////////

class CrossoverAreaToEast extends SceneAction {

  public CrossoverAreaToEast(CrossoverScene cS,int stackAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.stackAreaID = stackAreaID;
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;
  private int stackAreaID;
  private PrimeTicket primTicExtraCenter;
  private PrimeTicket primTicExtraSouth;
  private GoEastAtCenter goEastAtCenter;

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    primTicExtraCenter  =
      new PrimeTicket(this,cS.semCenterSouth[stackAreaID]);
    primTicExtraSouth   =
      new PrimeTicket(this,cS.semCenterStaySouth[stackAreaID]);
    goEastAtCenter = (GoEastAtCenter)
      cS.goEastAtCenter[stackAreaID].clone(actor,null);
  }

  protected void script() throws BlockException {
    primTicExtraSouth.insist();
    primTicExtraCenter.insist();
    goEastAtCenter.reset();
    goEastAtCenter.exec(
      null,
      new PrimeTicket[] { primTicExtraCenter , primTicExtraSouth },
      new Brief[]{}
    );
  }

}

/////////////////////////////////////////////////////////////////

class CrossoverAreaToQuayAreaEast extends SceneAction {

  public CrossoverAreaToQuayAreaEast(CrossoverScene cS,int quayAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.quayAreaID = quayAreaID;
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;
  private int quayAreaID;
  private PrimeTicket[] primTicPark;
  private PrimeTicket primTicLastCenter, primTicTotalPark;
  private PrimeTicket[] primTicParkEntrance;
  private ParkEast parkEast;

  // ---------------------------------------------------

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    // primTicTotalPark    = new PrimeTicket(this, cS.semTotalPark[quayAreaID]);
    primTicLastCenter   = new PrimeTicket(this, cS.semCenterSouth[quayAreaID]);
    primTicParkEntrance = new PrimeTicket[cT.lanesPerPark];
    for (int lane=0;lane<cT.lanesPerPark;lane++)
    {
      primTicParkEntrance[lane] =
        new PrimeTicket(this,cS.semParkEntrance[quayAreaID][lane]);
    }
    primTicPark = new PrimeTicket[cT.lanesPerPark];
    for (int lane=0; lane<cT.lanesPerPark; lane++) {
      primTicPark[lane] = new PrimeTicket(this,cS.semPark[quayAreaID][lane]);
    }
    primTicLastCenter = new PrimeTicket(this, cS.semCenterSouth[quayAreaID]);
  }

  // -------------------------------------------------

  protected void script() throws BlockException
  {
    primTicLastCenter.insist();
    //
    SelectTicket quayParkAreaST = new SelectTicket(
      (PrimeTicket)this.getAccessTicket(0),
      (PrimeTicket)this.getAccessTicket(1),
      (PrimeTicket)this.getAccessTicket(2),
      (PrimeTicket)this.getAccessTicket(3),
      (PrimeTicket)this.getAccessTicket(4)
    );
    quayParkAreaST.insist();
    int quayParkAreaID = quayParkAreaST.getSelectedIndex();
    //
    for ( int lne=0 ; lne<=quayParkAreaID ; lne++ )
    {
      primTicParkEntrance[lne].insist();
    }
    PrimeTicket[] allTickets = new PrimeTicket[primTicParkEntrance.length + 3];
    allTickets[0] = primTicLastCenter;
    allTickets[1] = primTicPark[quayParkAreaID];
    allTickets[2] = primTicTotalPark;
    for (int i=0;i<primTicParkEntrance.length;i++)
    {
      allTickets[i+3] = primTicParkEntrance[i];
    }

    parkEast=(ParkEast)
      cS.parkEast[quayAreaID][quayParkAreaID].clone(actor, null);
    parkEast.reset();
    parkEast.exec( null , allTickets , new Brief[]{} );
  }

  // ------------------------------------------------------

  Ticket getAccessTicket(int i)
  {
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
