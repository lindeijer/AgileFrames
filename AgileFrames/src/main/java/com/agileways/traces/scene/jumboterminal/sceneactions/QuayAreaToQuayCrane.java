package com.agileways.traces.scene.jumboterminal.sceneactions;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.BlockException;
import java.rmi.RemoteException;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.brief.Brief;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import com.agileways.traces.scene.jumboterminal.CrossoverAgv;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import net.agileframes.traces.ticket.PrimeTicket;
import com.agileways.traces.scene.jumboterminal.logisticmoves.EnterTurntable;
import com.agileways.traces.scene.jumboterminal.logisticmoves.LeaveTurntable;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;

/**
 * The SceneAction that drives from the park-place into the turntable. This scene-action should be
 * created dynamically as the runtables can change position.
 */
public class QuayAreaToQuayCrane extends SceneAction {
  private int quayAreaID, quayParkAreaID, quayCraneID;
  private PrimeTicket primTicParkExit;
  private double bestAlpha;

  /**
   * @param quayAreaID     origin quayAreaID     0 <= quayAreaID < numberOfParks
   * @param quayParkAreaID origin quayParkAreaID 0=innerlane; 1=outerlane
   * @param quayCraneID    destination turntable 0 <= quayCraneID < numberOfTtables
   */
  public QuayAreaToQuayCrane(
      CrossoverScene cS,int quayAreaID, int quayParkAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    //
    this.quayAreaID = quayAreaID;
    this.quayParkAreaID = quayParkAreaID;
  }

   private CrossoverScene cS;
   private CrossoverTerminal cT;

  /**
   * Initializes this scene-action.
   */
  public void assimilate(Actor actor) {
    super.assimilate(actor);
  }

  /**
   * Executes and creates the appropriate logistic moves and claims semaphores by creating and
   * insisting on their respective tickets.
   */
  protected void script() throws BlockException,RemoteException {
    CrossoverAgv crossoverAgv = (CrossoverAgv)actor.getService();
    this.quayCraneID=crossoverAgv.getQuayCraneID(quayParkAreaID);
    PrimeTicket primTicTtable = new PrimeTicket(this, cS.semTtable[quayCraneID]);
    primTicParkExit = new PrimeTicket(this, cS.semParkExit[quayAreaID]);
    primTicTtable.insist();
    primTicParkExit.insist();

    float lastDiff = 999;
    float length = 0;
    float dy, dx, diff;
    float ttableX = cT.TTABLE_X[quayCraneID]+
                    cT.FREE_TERMINAL_LEFT+
                    cT.WIDTH_DRIVING_LANE+
                    cT.FREE_SPACE_AT_SIDES+
                    cT.TTABLE_WIDTH/2+
                    cT.STACK_WIDTH_INCL/2;
    for (float alph=0;alph<(float)(Math.PI/2);alph+=0.001f) {
      dy = (float)(cT.FREE_PARK_EXIT+
              0.5f*cT.TTABLE_HEIGHT-
                   cT.TURN_RADIUS*Math.sin(alph)
      );
      dx = (float)(ttableX-cT.getParkLaneX(quayAreaID, quayParkAreaID)-
                           cT.TURN_RADIUS*(1-Math.cos(alph)));
      if (ttableX < cT.getParkLaneX(quayAreaID, quayParkAreaID)) {
        dx = (float)(-ttableX+cT.getParkLaneX(quayAreaID, quayParkAreaID)-cT.TURN_RADIUS*(1-Math.cos(alph)));
      }
      diff = (float)( Math.atan(dy/dx) - (Math.PI/2 - alph) );
      if (Math.abs(diff)>lastDiff) {break;}
      lastDiff = Math.abs(diff);
      bestAlpha = alph;
      length = (float)Math.sqrt(dx*dx+dy*dy);
    }

    if (ttableX > cT.getParkLaneX(quayAreaID, quayParkAreaID)) {
      bestAlpha = - bestAlpha;
    }

    MoveImplBase move1 = new EnterTurntable(quayAreaID,quayParkAreaID,quayCraneID);
    move1 = move1.clone(actor,null);

    move1.exec(null, new PrimeTicket[]{primTicParkExit,primTicTtable}, new Brief[]{});
  }

  /**
   * gives the angle of the agv on the turntable.
   * @return the end-angle of the agv
   */
  public double getEndAngle() {
    return bestAlpha;
  }

}