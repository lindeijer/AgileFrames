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
import net.agileframes.traces.ticket.PrimeTicket;

/**
 * The SceneAction that drives over the side-lane from the quay
 * to the top.
 */
public class QuayToTop extends SceneAction {
  private int lanePositionSide, turnDirection;
  private MoveImplBase[] move1;
  private PrimeTicket[] primTicSideLane;
  private int NUMBER_OF_AGVS_AT_SIDES;

  /**
   * @param lanePositionSide    position of side-lane   0=innerlane; 1=outerlane
   * @param turnDirection       direction of agv        0=counterclockwise; 1=clockwise
   *
   * @throws RemoteException
   */
  public QuayToTop(CrossoverScene cS,int lanePositionSide, int turnDirection) throws java.rmi.RemoteException {
    super(cS);
    this.cS=cS;
    this.lanePositionSide = lanePositionSide;
    this.turnDirection = turnDirection;
  }

  CrossoverScene cS;

  /**
   * Creates the necessary moves and tickets for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
    this.NUMBER_OF_AGVS_AT_SIDES = CrossoverScene.cT.NUMBER_OF_AGVS_AT_SIDES;
    //
    move1 = new MoveImplBase[NUMBER_OF_AGVS_AT_SIDES];
    primTicSideLane = new PrimeTicket[NUMBER_OF_AGVS_AT_SIDES];
    //create all moves and tickets
    for (int index=0; index<NUMBER_OF_AGVS_AT_SIDES; index++) {
      if (turnDirection == 1) {
        move1[index] = cS.goNorth[1-lanePositionSide][index].clone(actor,null);
        primTicSideLane[index] = new PrimeTicket(this, cS.semSideLane[1-lanePositionSide][index]);
      } else {
        move1[index] = cS.goNorth[2+lanePositionSide][index].clone(actor,null);
        primTicSideLane[index] = new PrimeTicket(this, cS.semSideLane[2+lanePositionSide][index]);
      }
    }
  }

  /**
   * Executes the appropriate logistic moves and claims semaphores by insisting on their
   * respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    for (int index=0; index<NUMBER_OF_AGVS_AT_SIDES; index++) {
      primTicSideLane[index].insist();
      move1[index].reset();
      move1[index].exec(null, new PrimeTicket[]{primTicSideLane[index]}, new Brief[]{});
    }
  }
}