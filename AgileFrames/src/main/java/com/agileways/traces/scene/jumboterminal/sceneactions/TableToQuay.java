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
import net.agileframes.traces.Move;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import net.agileframes.traces.ticket.PrimeTicket;
import com.agileways.traces.scene.jumboterminal.logisticmoves.EnterTurntable;
import com.agileways.traces.scene.jumboterminal.logisticmoves.LeaveTurntable;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;

/**
 * The SceneAction that drives from the table to the quay. This action should be created
 * dynamically as the positions of the turntables can change.
 */
public class TableToQuay extends SceneAction {
  private int turnDirection, lanePositionQuay, ttableNr;
  private PrimeTicket primTicParkExit;
  private PrimeTicket[] primTicQuayLane;

  /**
   * @param ttableNr          origin ttableNr         0 <= ttableNr < numberOfTtables
   * @param turnDirection     direction of agv        0=counterclockwise; 1=cw
   * @param lanePositionQuay  position of quay-lane   0=innerlane; 1=outerlane
   *
   * @throws RemoteException
   */
  public TableToQuay(CrossoverScene cS,
      int ttableNr ,int turnDirection, int lanePositionQuay) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    //
    this.turnDirection = turnDirection;
    this.lanePositionQuay = lanePositionQuay;
    this.ttableNr=ttableNr;
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;

  /**
   * Creates the necessary tickets and moves for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);

  }

  /**
   * Executes the appropriate logistic move and claims
   * semaphores by insisting on their respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    float firstPart = (cT.TTABLE_HEIGHT/2 + cT.FREE_TTABLE_EXIT +
          lanePositionQuay * cT.WIDTH_DRIVING_LANE - cT.TURN_RADIUS)
          + (float)(cT.TURN_RADIUS*Math.PI/2);
    MoveImplBase move2 = new LeaveTurntable(ttableNr,turnDirection,lanePositionQuay);
    move2 = move2.clone(actor,null);
    int numbOfSems = (int)Math.ceil((move2.trajectory.domain - firstPart + cT.TURN_RADIUS)/cT.AGV_LENGTH);
    // create tickets
    if (lanePositionQuay==0) {
      primTicQuayLane = new PrimeTicket[numbOfSems];
    } else {
      primTicQuayLane = new PrimeTicket[numbOfSems+1];
    }

    for (int i=0; i<numbOfSems; i++) {
      if (turnDirection==0) {
        primTicQuayLane[i] =
          new PrimeTicket(this,
            cS.semQuayLane[lanePositionQuay][cT.NUMBER_OF_AGVS_AT_STACKLANE-numbOfSems+i]);
      } else {
        primTicQuayLane[i] =
          new PrimeTicket(this,
            cS.semQuayLane[lanePositionQuay][numbOfSems-i-1]);
      }
    }
    move2.reset();
    if (lanePositionQuay==0) {move2.rules = new Rule [numbOfSems*2+1];} else {move2.rules = new Rule [numbOfSems*2+3];}
    move2.rules[0] = new Move.EvolutionRule(move2, move2.trajectory, move2.trajectory.domain - cT.AGV_LENGTH, 0);
    int nr = 1;
    float evolution1, evolution2;
    for (int i=0; i<numbOfSems; i++) {
      if (turnDirection==0) {
        evolution1 = Math.max(0,move2.trajectory.domain - cT.LAST_WIDTH_STACKLANE +
              (i-numbOfSems+0.5f)*cT.AGV_LENGTH);
        evolution2 = move2.trajectory.domain + (i-numbOfSems+1.5f)*cT.AGV_LENGTH;
      } else {
        evolution1 = Math.max(0,move2.trajectory.domain + (i-numbOfSems-0.5f)*cT.AGV_LENGTH);
        evolution2 = move2.trajectory.domain + (i-numbOfSems+1.5f)*cT.AGV_LENGTH;
      }
      //odd rule-numbers: to insist on ticket
      move2.rules[nr] = new Move.EvolutionRule(move2, move2.trajectory, evolution1, nr);
      nr++;
      //even rule-numbers: to free a ticket
      move2.rules[nr] = new Move.EvolutionRule(move2, move2.trajectory, evolution2, nr);
      nr++;
    }
    //crossingLane??
    if (lanePositionQuay==1) {
      float crossEvolution;
      if (turnDirection==0) {
        primTicQuayLane[numbOfSems] = new PrimeTicket(this, cS.semQuayLane[0][cT.NUMBER_OF_AGVS_AT_STACKLANE-numbOfSems]);
        crossEvolution = Math.max(0,move2.trajectory.domain -cT.LAST_WIDTH_STACKLANE -
                              (numbOfSems-0.5f)*cT.AGV_LENGTH);
      } else {
        primTicQuayLane[numbOfSems] = new PrimeTicket(this, cS.semQuayLane[0][numbOfSems-1]);
        crossEvolution = Math.max(0,move2.trajectory.domain - (numbOfSems+0.5f)*cT.AGV_LENGTH);
      }
      move2.rules[nr] = new Move.EvolutionRule(move2, move2.trajectory, crossEvolution, nr);
      nr++;
      //even rule-numbers: to free a ticket
      move2.rules[nr] = new Move.EvolutionRule(move2, move2.trajectory,
              move2.trajectory.domain - (numbOfSems-1.5f)*cT.AGV_LENGTH, nr);
      nr++;
    }
    move2.exec(null, primTicQuayLane, new Brief[]{});
  }
}