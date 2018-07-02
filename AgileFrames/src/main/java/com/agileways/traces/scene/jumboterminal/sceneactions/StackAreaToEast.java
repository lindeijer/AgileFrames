package com.agileways.traces.scene.jumboterminal.sceneactions;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.BlockException;
import java.rmi.RemoteException;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.traces.ticket.CollectTicket;
import net.agileframes.core.brief.Brief;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import net.agileframes.traces.MoveImplBase;
import net.agileframes.forces.rule.EvolutionRule;
import net.agileframes.core.forces.Rule;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import net.agileframes.traces.ticket.SelectTicket;
import net.agileframes.core.traces.Ticket;
import com.agileways.traces.scene.jumboterminal.logisticmoves.LeaveStackForCenterEast;;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class StackAreaToEast extends SceneAction {

  public StackAreaToEast(CrossoverScene cS,int stackAreaID,int stackParkAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.stackAreaID = stackAreaID;
    this.stackParkAreaID = stackParkAreaID;
    //
    tStackExit    = new PrimeTicket[2*(cT.lanesPerStack+1)];
    int index = 0;
    for (int exit=stackParkAreaID; exit<=cT.lanesPerStack; exit++) {
      tStackExit[index] = new PrimeTicket(this, cS.semStackExit[stackAreaID][exit]);
      index++;
    }
    for (int exit=0;exit<=cT.lanesPerStack/2;exit++){
      tStackExit[index] = new PrimeTicket(this, cS.semStackExit[stackAreaID+1][exit]);
      index++;
    }
    tCenterNorth = new PrimeTicket(this, cS.semCenterNorth[stackAreaID+1]);
    tCenterSouth = new PrimeTicket(this, cS.semCenterSouth[stackAreaID+1]);
    tCenterStaySouth = new PrimeTicket(this, cS.semCenterStaySouth[stackAreaID+1]);
  }

  private CrossoverScene cS;
  private CrossoverTerminal cT;
  private int stackAreaID;
  private int stackParkAreaID;
  private PrimeTicket tCenterNorth, tCenterSouth, tCenterStaySouth;
  private PrimeTicket[] tStackExit;
  private LeaveStackForCenterEast leaveStackForCenterEast;

  public void assimilate(Actor actor) {
    super.assimilate(actor);
    leaveStackForCenterEast=(LeaveStackForCenterEast)
      cS.leaveStackForCenterEast[stackAreaID][stackParkAreaID].clone(actor,this);
  }


  protected void script() throws BlockException {
     tCenterStaySouth.insist();
     CollectTicket collectTicket = new CollectTicket(tStackExit);
     collectTicket.insist();
     tCenterNorth.insist();
     tCenterSouth.insist();
     leaveStackForCenterEast.reset();
     actor.getMFDriver().begin(leaveStackForCenterEast);
     try {
       synchronized(leaveStackForCenterEast.finishing) {
         leaveStackForCenterEast.finishing.wait();
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

  public void beyondCenterNorth() {
    System.out.println("beyondCenterNorth");
    try {
      tCenterNorth.free();
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }

  public void finished() {
    System.out.println("finished");
    try {
      tCenterSouth.free();
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }

  public void beyond() {
    System.out.println("beyond");
    try {
      tCenterStaySouth.free();
    } catch (Exception e) { e.printStackTrace(); System.exit(1); }
  }


}