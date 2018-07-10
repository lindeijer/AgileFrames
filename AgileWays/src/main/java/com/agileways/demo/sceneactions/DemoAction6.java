package com.agileways.demo.sceneactions;

import com.agileways.demo.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.traces.*;
import net.agileframes.traces.ticket.PrimeTicket;
import net.agileframes.core.forces.Sign;

public class DemoAction6 extends SceneAction {
  //--- Attributes ---
  private int direct, lane;
  private final boolean DEBUG = true;
  // WATCH OUT: the following parameters will not be cloned!
  // So, they will be reset after cloning the SceneAction
  private Ticket ticCross1 = null;
  private Ticket ticCntrPark = null;
  private Ticket ticCross2 = null;
  //private Ticket ticEndPark = null;
  private Move move1 = null;
  private Move move2 = null;


  //--- Constructor ---
  public DemoAction6() throws java.rmi.RemoteException {}
  public DemoAction6(Scene scene, Action superSceneAction, int direct, int lane) throws java.rmi.RemoteException {
    this.name = "DemoAction6_"+direct+"."+lane;
    this.scene = scene;
    this.superSceneAction = superSceneAction;
    this.direct = direct;
    this.lane = lane;
    // create the tickets and store them in array tickets[]
    // take care of the order in which the tickets are being stored!
    this.tickets = new Ticket[3];
    tickets[0] = new PrimeTicket(this, DemoScene.semCross[(direct + lane) % 4]);//cross1
    int endDirect = (direct + 3 + lane * 2) % 4;
    tickets[1] = new PrimeTicket(this, DemoScene.semCntrPark[endDirect]);//center-park
    endDirect = (direct + 3 + lane * 3) % 4;
    tickets[2] = new PrimeTicket(this, DemoScene.semCross[endDirect]);//cross2
    endDirect = (direct + 3 + lane * 2) % 4;
    //tickets[3] = new PrimeTicket(this, DemoScene.semEndPark[endDirect][lane]);//end-park

    this.moves = new Move[2];
    moves[0] = DemoScene.move3[direct][lane];
    moves[1] = DemoScene.move7[(direct + 3 - 2*lane) % 4][(lane + 1) % 2];

    this.signs = new Sign[1];
    signs[0] = new Sign();
  }

  //--- Methods ---
  protected void initialize() {
    // regain the tickets from the tickets[] array: SAME ORDER as stored above!!
    ticCross1 = tickets[0];
    ticCntrPark = tickets[1];
    ticCross2 = tickets[2];
    //ticEndPark = tickets[3];//exit-ticket of this SA: freed in next SA
    // idem with moves
    move1 = moves[0];// moves are cloned at the same time as scene-action
    move2 = moves[1];

  }

  //--- The Script ---
  protected void sceneActionScript() {
    try {
      if ( (externalTickets == null ) || (externalTickets.length != 2) ) {
        System.out.println("DemoAction6 was called incompletely");
        return;
      }
      Ticket exitTicketPrevious = externalTickets[0];
      Ticket ticEndPark = externalTickets[1];


      // claim ticEndPark
      ticEndPark.insist();
      // claim ticCntrPark
      ticCntrPark.insist();
      // claim ticCross1
      ticCross1.insist();
      // start move1 (with:  exitTicPrev, ticCross1)
      //       move1 takes care of: freeing exitTicPrev, ticCross1
      move1.run(new Ticket[] { exitTicketPrevious, ticCross1 });
      // wait for anticipatingSign of move1
      watch(move1.getSign(0));
      if (DEBUG) System.out.println("*D* DemoAction6: move1 raised anticipating sign");
      // claim ticCross2
      ticCross2.insist();
      // remove timedStop-precaution of move1
      move1.getManoeuvre().getPrecaution(0).remove();
      // anticipate on move2
      move2.prepare();
      // wait for finishedSign of move1
      watch(move1.getSign(1));
      if (DEBUG) System.out.println("*D* DemoAction6: move1 raised finished sign");

      // start move2 (with: ticCntrPark, ticCross2)
      //       move2 takes care of: freeing ticCntrPark, ticCross2
      move2.run(new Ticket[] { ticCntrPark, ticCross2});
      // wait for anticipatingSign of move2
      watch(move2.getSign(0));
      if (DEBUG) System.out.println("*D* DemoAction6: move2 raised anticipating sign");
      move2.getManoeuvre().getPrecaution(0).remove();
      // wait for finishedSign of move2
      watch(move2.getSign(1));
      if (DEBUG) System.out.println("*D* DemoAction6: move2 raised finished sign");
      // raise a sign

      // specify exit-ticket: finish(ticEndPArk)
      finish(ticEndPark);
    } catch (Exception e) {
      System.out.println("Exception in DemoAction6: The scene-action will be aborted.");
      e.printStackTrace();
    }


    signs[0].broadcast();
  }
}