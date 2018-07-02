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
 * The SceneAction that drives through a corner.
 */
public class TurnAction extends SceneAction {
  private int corner, vertLanePosition, horLanePosition;
  private MoveImplBase move1;
  private PrimeTicket primTicTurn1, primTicTurn2, primTicTurnCollect;

  /**
   * @param corner            place where action will be performed  0=NorthWest;1=NE;2=SE;3=SW
   * @param vertLanePosition  position of vertical lane             0=innerlane; 1=outerlane
   * @param horLanePosition   position of horizontal lane           0=innerlane; 1=outerlane
   *
   * @throws RemoteException
   */
  public TurnAction(CrossoverScene cS,
      int corner, int vertLanePosition, int horLanePosition) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    //
    this.corner = corner;
    this.vertLanePosition = vertLanePosition;
    this.horLanePosition = horLanePosition;
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;

  /**
   * Creates the necessary tickets and moves for this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
    //
    primTicTurn1 = new PrimeTicket(this, cS.semTurn[corner][vertLanePosition]);
    primTicTurn2 = new PrimeTicket(this, cS.semTurn[corner][horLanePosition]);
    primTicTurnCollect = new PrimeTicket(this, cS.semTurnCollect[corner]);
    move1 = cS.turn[corner][vertLanePosition][horLanePosition].clone(actor, null);
  }

  /**
   * Executes the appropriate logistic move and claims
   * semaphores by insisting on their respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    primTicTurnCollect.insist();
    primTicTurn1.insist();
    if (vertLanePosition!=horLanePosition) {primTicTurn2.insist();}
    primTicTurnCollect.free();

    move1.reset();
    if (vertLanePosition==horLanePosition) {// only one area to claim:
      move1.exec(null, new PrimeTicket[]{primTicTurn1}, new Brief[]{});
    } else {// two areas to claim: 2nd will be freed first
      if (corner<2){//northern corners
        move1.exec(null, new PrimeTicket[]{primTicTurn2,primTicTurn1}, new Brief[]{});
      } else {// southern corners
        move1.exec(null, new PrimeTicket[]{primTicTurn1,primTicTurn2}, new Brief[]{});
      }
    }
  }
}