package com.agileways.demo.moves;

import com.agileways.demo.manoeuvres.ParkTurning;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class Move7 extends Move {
  //-- Attributes --
  private final boolean DEBUG = false;

  //-- Constructor --
  // turnDirect = 0 CW, turnDirect = 1 CCW
  public Move7(FuTransform transform, int parkNr, int turnDirect) {
    super(transform);
    this.manoeuvre = new ParkTurning(transform, parkNr, turnDirect);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }
  //-- Methods --
  public void moveScript() {
    try {
      // must have two entry-tickets:
      Ticket ticCntrPark = entryTickets[0];
      Ticket ticCross = entryTickets[1];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (DEBUG) System.out.println("*D* Move7: flag 0 raised, ticCntrPark="+ticCntrPark.toString());
      if (ticCntrPark != null) ticCntrPark.free();

      watch(manoeuvre.getFlag(3));// cross-tic can be freed
      if (DEBUG) System.out.println("*D* Move7: flag 3 raised");
      ticCross.free();

      watch(manoeuvre.getFlag(1)); // 90% of manoeuvre done..anticipate
      if (DEBUG) System.out.println("*D* Move7: flag 1 raised");
      signs[0].broadcast();

      watch(manoeuvre.getFlag(2));// total manoeuvre done..finished
      if (DEBUG) System.out.println("*D* Move7: flag 2 raised");
      signs[1].broadcast();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}