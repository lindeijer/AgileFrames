package com.agileways.demo.moves;

import com.agileways.demo.manoeuvres.LeaveAndParkCrossed;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class Move2 extends Move {
  //-- Constructor --
  public Move2(FuTransform transform, int direct, int lane) {
    super(transform);
    this.manoeuvre = new LeaveAndParkCrossed(transform, direct, lane);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }

  //-- Methods --
  public void moveScript() {
    try {
      // must have four entry-tickets:
      Ticket ticEndPark = entryTickets[0];
      Ticket ticCross1 = entryTickets[1];
      Ticket ticCntrCross = entryTickets[2];
      Ticket ticCross2 = entryTickets[3];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (ticEndPark != null) ticEndPark.free();

      watch(manoeuvre.getFlag(3));// cross-tic1 can be freed
      ticCross1.free();

      watch(manoeuvre.getFlag(4));// cntr-cross-tic1 can be freed
      ticCntrCross.free();

      watch(manoeuvre.getFlag(5));// cross-tic2 can be freed
      ticCross2.free();

      watch(manoeuvre.getFlag(1)); // 90% of manoeuvre done..anticipate
      signs[0].broadcast();

      watch(manoeuvre.getFlag(2));// total manoeuvre done..finished
      signs[1].broadcast();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}