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
import net.agileframes.traces.ticket.PrimeTicket;
import com.agileways.traces.scene.jumboterminal.logisticmoves.SpinAtCurrentPosition;
import net.agileframes.forces.space.POS;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;

public class AtTurntable extends SceneAction {

  private CrossoverScene crossoverScene;
  private CrossoverTerminal cT;
  private double beginAngle, turnAngle;
  private int ttable;

  public AtTurntable(CrossoverScene cS,int ttable,double beginAngle, double turnAngle) throws java.rmi.RemoteException {
    super(cS);
    this.crossoverScene=crossoverScene;
    this.ttable = ttable;
    this.beginAngle = beginAngle;
    this.turnAngle = turnAngle;
  }

  // called after creation.
  public void assimilate(Actor actor) {
    super.assimilate(actor);
  }

  protected void script() throws BlockException,RemoteException {
    this.cT=crossoverScene.cT;
    float ttableX = cT.TTABLE_X[ttable]+
                    cT.FREE_TERMINAL_LEFT+
                    cT.WIDTH_DRIVING_LANE+
                    cT.FREE_SPACE_AT_SIDES+
                    cT.TTABLE_WIDTH/2+
                    cT.STACK_WIDTH_INCL/2;
    POS position = new POS(ttableX, cT.TTABLE_Y, beginAngle);
    MoveImplBase move1 = new SpinAtCurrentPosition(turnAngle,position);
    move1 = move1.clone(actor,null);
    move1.exec(null, new PrimeTicket[]{}, new Brief[]{});
  }
}
