package com.agileways.demo.moves;

import com.agileways.demo.manoeuvres.LeaveAndPark;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class Move1 extends Move {
  private final boolean DEBUG = false;
  //-- Constructor --
  public Move1(FuTransform transform, int direct, int lane) {
    super(transform);
    this.manoeuvre = new LeaveAndPark(transform, direct, lane);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }

  //-- Methods --
  public void moveScript() {
    try {
      if (DEBUG) System.out.println("*D* Move1: started!");
      // must have two entry-tickets:
      Ticket ticEndPark = entryTickets[0];
      Ticket ticCross = entryTickets[1];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      if (DEBUG) System.out.println("*D* Move1: about to watch flag 0!");
      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (ticEndPark != null) ticEndPark.free();

      watch(manoeuvre.getFlag(3));// cross-tic can be freed
      ticCross.free();

      watch(manoeuvre.getFlag(1)); // 90% of manoeuvre done..anticipate
      signs[0].broadcast();

      watch(manoeuvre.getFlag(2));// total manoeuvre done..finished
      signs[1].broadcast();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}