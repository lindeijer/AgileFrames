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
import net.agileframes.traces.ticket.CollectTicket;
import net.agileframes.core.traces.Ticket;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class StackAreaToWest extends SceneAction {

  private int stackAreaID, stackParkAreaID, quayAreaID;
  private LeaveStackForCenterWest leaveStackForCenterWest;
  private PrimeTicket   tCenterStayNorth;
  private PrimeTicket[] tStackExit;
  private PrimeTicket   tCenterNorth;
  private CrossoverScene cS;
  private CrossoverTerminal cT;

  public StackAreaToWest(
      CrossoverScene cS,int stackAreaID, int stackParkAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=CrossoverScene.cT;
    //
    this.stackAreaID = stackAreaID;
    this.stackParkAreaID = stackParkAreaID;
    this.quayAreaID = quayAreaID;
    //
    tStackExit = new PrimeTicket[2*(cT.lanesPerStack+1)];
    int index = 0;
    for (int exit=cT.lanesPerStack/2; exit<=cT.lanesPerStack; exit++) {
      tStackExit[index] = new PrimeTicket(this, cS.semStackExit[stackAreaID-1][exit]);
      index++;
    }
    for (int exit=0;exit<=stackParkAreaID;exit++){
      tStackExit[index] = new PrimeTicket(this, cS.semStackExit[stackAreaID][exit]);
      index++;
    }
    tCenterNorth    = new PrimeTicket(this, cS.semCenterNorth[stackAreaID-1]);
    tCenterStayNorth = new PrimeTicket(this, cS.semCenterStayNorth[stackAreaID-2]);
    //leaveStackForCenterWest =
    //  new LeaveStackForCenterWest(stackAreaID,stackParkAreaID);
  }

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    leaveStackForCenterWest=(LeaveStackForCenterWest)
      cS.leaveStackForCenterWest[stackAreaID][stackParkAreaID].clone(actor,this);
    // leaveStackForCenterWest.assimilate(actor,this);
  }

  protected void script() throws BlockException,RemoteException {
    tCenterStayNorth.insist();
    CollectTicket collectTicket = new CollectTicket(tStackExit);
    collectTicket.insist();
    tCenterNorth.insist();
    leaveStackForCenterWest.reset();
    actor.getMFDriver().begin(leaveStackForCenterWest);
    try {
      synchronized(leaveStackForCenterWest.finishing) {
        leaveStackForCenterWest.finishing.wait();
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  public void beyondStackArea() {
    System.out.println("beyondStackArea");
    try {
      for (int i=0;i<tStackExit.length;i++) {
        if (tStackExit[i]!=null) { tStackExit[i].free(); }
      }
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }


  public void finished() {
    System.out.println("finished");
    try {
      tCenterNorth.free();
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }

  public void beyond() {
    System.out.println("beyond");
    try {
      tCenterStayNorth.free();
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }



}

