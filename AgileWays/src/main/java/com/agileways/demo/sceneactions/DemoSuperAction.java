package com.agileways.demo.sceneactions;

import com.agileways.demo.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.forces.Sign;

/**
 * DemoSuperAction is made without any constructor-parameters, but the initial position
 * should be set using setBeginPosition
 * The end-location can be obtained by reading getEndPosition
 */

public class DemoSuperAction extends SceneAction {
  //--- Attributes ---
  private long DRIVING_TIME = 180000;//3 minutes
  private int currentDirect = -1;//(int)(4 * Math.random());// random number betw 0..3
  private int currentLane = -1;//(intought(2 * Math.random());// random number betw 0..1
  private final boolean DEBUG = true;

  //--- Constructor ---
  public DemoSuperAction() throws java.rmi.RemoteException {}
  public DemoSuperAction(Scene scene, Action superSceneAction) throws java.rmi.RemoteException {
    this.name = "DemoSuperAction";
    this.scene = scene;
    this.superSceneAction = superSceneAction;

    this.tickets = new Ticket[8];
    tickets[0] = new PrimeTicket(this, DemoScene.semEndPark[0][0]);
    tickets[1] = new PrimeTicket(this, DemoScene.semEndPark[0][1]);
    tickets[2] = new PrimeTicket(this, DemoScene.semEndPark[1][0]);
    tickets[3] = new PrimeTicket(this, DemoScene.semEndPark[1][1]);
    tickets[4] = new PrimeTicket(this, DemoScene.semEndPark[2][0]);
    tickets[5] = new PrimeTicket(this, DemoScene.semEndPark[2][1]);
    tickets[6] = new PrimeTicket(this, DemoScene.semEndPark[3][0]);
    tickets[7] = new PrimeTicket(this, DemoScene.semEndPark[3][1]);


/*    if (DEBUG) System.out.println("*D* Initial Position: direct: "+currentDirect+", lane: "+currentLane);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        tickets[i*2 + j] = new PrimeTicket(this, DemoScene.semEndPark[i][j]);
      }
    }*/

    this.signs = new Sign[1];
    signs[0] = new Sign();

  }

  //--- Methods ---
  protected void initialize() {
    if (DEBUG) System.out.println("*D* DemoSuperAction: initializing!");
/*    if (exitTicketPrevious == null) {
      exitTicketPrevious = tickets[currentDirect*2 + currentLane];
    } else {
      // find out which park we have...
      for (int direct = 0; direct < 4; direct++) {
        for (int lane = 0; lane < 2; lane ++) {
//          exitTicketPrevious.attempt()//
        }
      }
    }*/
  }

  //--- The Script ---
  protected void sceneActionScript() {
    if (DEBUG) System.out.println("*D* DemoSuperAction: started!");

    /*
    if ( (externalTickets == null ) || (externalTickets.length != 9) ) {
      System.out.println("DemoSuperAction was called incompletely");
      return;
    }
    */
    Ticket exitTicketPrevious = externalTickets[0];
    /*
    Ticket[] ticEndPark = new Ticket[8];
    for (int i = 0 ; i < 8; i++) {
      ticEndPark[i] = externalTickets[i+1];
    }
    */
    Ticket[] ticEndPark = tickets;
    // start-lane
    long startTime = System.currentTimeMillis();
    boolean goOn = true;
    if (beginPosition != null) {
      currentDirect = beginPosition.params[0];
      currentLane = beginPosition.params[1];
    } else {
      System.out.println("WARNING: DemoSuperAction: BeginPosition is not set");
      currentDirect = (int)(4 * Math.random());// random number betw 0..3
      currentLane = (int)(2 * Math.random());// random number betw 0..1
    }

    //try { exitTicketPrevious.insist(); } catch (Exception e) { e.printStackTrace(); }
    while (goOn) {
      // let's first pick a park that is still free
      if ((startTime + DRIVING_TIME) < System.currentTimeMillis()) {
        if (DEBUG) System.out.println("*D* DemoSuperAction: About to leave, we stayed "+(System.currentTimeMillis() - startTime)+" ms.");
        goOn = false;
        //we have no time anymore!
      }
      boolean[][] isTried = new boolean[4][2];
      boolean isFound = false;
      int tryCounter = 0; if (!goOn) { tryCounter = 6; }
      int direct = -1; int lane = -1;
      Ticket parkTicket = null;
      while (!isFound) {
        while ( (direct == -1) || (isTried[direct][lane]) ) {
          direct = (int)(4 * Math.random());// random number betw 0..3
          if (!goOn) { direct = 1; }
          lane = (int)(2 * Math.random());// random number betw 0..1
          if (DEBUG) System.out.println("*D* ("+direct+","+lane+") ??");
        }
        if (DEBUG) System.out.println("*D* TRYING ("+direct+","+lane+")");
        try { parkTicket = (Ticket)ticEndPark[direct*2 + lane].clone(actor); }
        catch (Exception e) {e.printStackTrace(); }
        boolean isSucceeded = false;
        try {
          //parkTicket.free(); // because it might still be assigned from the last try (in this same action of course)
          if ((direct==currentDirect) && (lane==currentLane)) {
            isSucceeded = false;//you are standing here already, fool
          } else {
            isSucceeded = parkTicket.attempt();
          }
        }
        catch (Exception e) { e.printStackTrace(); }
        if ( isSucceeded ) {
          isFound = true;// we found an empty place!
          if (DEBUG) System.out.println("*D* FOUND A PLACE!! ("+direct+","+lane+")");
        } else {
          // this one was not free
          isTried[direct][lane] = true;
          tryCounter++;
          if (DEBUG) System.out.println("*D* PLACE OCCUPIED ("+direct+","+lane+")");
          if (tryCounter == 8) {
            //tried them all.. let's sleep some time before we'll try again
            System.out.println("It's busy out there..We could not find an empty parkplace.");
            System.out.println("We will try again within 5 seconds...");
            tryCounter = 0;
            if (!goOn) { tryCounter = 6; }
            for (int i = 0; i < 4; i++) {
              for (int j = 0; j < 2; j++) {
                isTried[i][j] = false;
              }
            }
            try { Thread.currentThread().sleep(5000); }
            catch (Exception e) { e.printStackTrace(); }
          }
        }
      }
      int dest = direct * 2 + lane;
      int orig = currentDirect * 2 + currentLane;
      int r = -1;
      if (currentLane == 0) { r = (( dest - orig + 8) % 8 ); }
      else { r = (( orig - dest + 8) % 8 ); }
      if (DEBUG) System.out.println("*D* Parking translated in next action: "+r);
      if (DEBUG) System.out.println("*D* Current Position: direct: "+currentDirect+", lane: "+currentLane);

      SceneAction sa = null;
      while (sa == null) {
        try {
          switch (r) {
            case 1:
              sa = (SceneAction)scene.getSceneAction("DemoAction1_"+currentDirect+"."+currentLane, actor);
              // direct stays same, lane changes
              currentLane = (currentLane + 1) % 2;
              break;
            case 2:
              sa = (SceneAction)scene.getSceneAction("DemoAction2_"+currentDirect+"."+currentLane, actor);
              // lane stays same, direct changes
              currentDirect = (currentDirect + 1 + 2 * currentLane) % 4;
              break;
            case 3:
              sa = (SceneAction)scene.getSceneAction("DemoAction3_"+currentDirect+"."+currentLane, actor);
              // direct and lane change
              currentDirect = (currentDirect + 1 + 2 * currentLane) % 4;
              currentLane = (currentLane + 1) % 2;
              break;
            case 4:
              sa = (SceneAction)scene.getSceneAction("DemoAction4_"+currentDirect+"."+currentLane, actor);
              // lane stays same, direct changes
              currentDirect = (currentDirect + 2) % 4;
              break;
            case 5:
              sa = (SceneAction)scene.getSceneAction("DemoAction5_"+currentDirect+"."+currentLane, actor);
              // direct and lane change
              currentDirect = (currentDirect + 2) % 4;
              currentLane = (currentLane + 1) % 2;
              break;
            case 6:
              sa = (SceneAction)scene.getSceneAction("DemoAction6_"+currentDirect+"."+currentLane, actor);
              // direct changes, lane not
              currentDirect = (currentDirect + 3 - currentLane * 2) % 4;
              break;
            case 7:
              sa = (SceneAction)scene.getSceneAction("DemoAction7_"+currentDirect+"."+currentLane, actor);
              // direct and lane change
              currentDirect = (currentDirect + 3 - currentLane * 2) % 4;
              currentLane = (currentLane + 1) % 2;
              break;
            default:
              System.out.println("ERROR: No Action could be found in DemoSuperAction...");
              System.out.println("ERROR: SceneAction Aborted");
              endPosition = new LogisticPosition("Park", scene, new int[] {currentDirect, currentLane});
              return;
          }
        } catch (java.rmi.RemoteException e) {
          System.out.println("RemoteException while downloading SceneAction in DemoSuperAction.");
          System.out.println("We try again.");
        }
      }//while
      sa.setActor(actor);
      sa.run(new Ticket[] {exitTicketPrevious, parkTicket});
      watch(sa.getSign(0));
      exitTicketPrevious = sa.getExitTicket();

      this.endPosition = new LogisticPosition("Park", scene, new int[] {currentDirect, currentLane});
    }
    this.finish(exitTicketPrevious);
    this.signs[0].broadcast();
    if (DEBUG) System.out.println("*D* DemoSuperAction: endPosition="+endPosition.toString());
  }
}