package com.agileways.traces.scene.jumboterminal.sceneactions;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.BlockException;
import java.rmi.RemoteException;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.brief.Brief;
import net.agileframes.traces.SceneAction;
import com.agileways.traces.scene.jumboterminal.CrossoverScene;
import com.agileways.traces.scene.jumboterminal.CrossoverTerminal;
import com.agileways.traces.scene.jumboterminal.logisticmoves.CrossoverArea2West;

/**
 * @author Wierenga,Lindeijer,Evers
 */


public class CrossoverAreaToWest extends SceneAction {

  private int stackAreaID;
  private CrossoverArea2West crossoverArea2West;
  private PrimeTicket tCenterNorth;
  private PrimeTicket tCenterStayNorth;
  private CrossoverScene cS;
  private CrossoverTerminal cT;

  public CrossoverAreaToWest(CrossoverScene cS,int stackAreaID) {
    super(cS);
    this.cS=cS;
    this.cT=cS.cT;
    this.stackAreaID = stackAreaID;
    tCenterNorth     = new PrimeTicket(this, cS.semCenterNorth[stackAreaID]      );
    tCenterStayNorth = new PrimeTicket(this, cS.semCenterStayNorth[stackAreaID-1]);
    crossoverArea2West=(CrossoverArea2West)cS.crossoverArea2West[stackAreaID].clone();
  }

  public void assimilate(Actor actor,Action superAction) {
    super.assimilate(actor,superAction);
    crossoverArea2West.assimilate(actor,this);
  }

  protected void script() throws BlockException,RemoteException {
    tCenterStayNorth.insist();
    tCenterNorth.insist();
    crossoverArea2West.reset();
    actor.getMFDriver().begin(crossoverArea2West);
    try {
      synchronized(crossoverArea2West.finishing) {
        crossoverArea2West.finishing.wait();
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  public void finished() {
    try {
      tCenterNorth.free();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void beyond()   {
    try {
      tCenterStayNorth.free();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
