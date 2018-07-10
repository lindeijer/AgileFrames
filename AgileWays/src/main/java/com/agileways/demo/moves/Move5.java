package com.agileways.demo.moves;

import com.agileways.demo.manoeuvres.TurnCenter;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class Move5 extends Move {
  //-- Constructor --
  public Move5(FuTransform transform, int parkNr, int turnDirect) {
    super(transform);
    this.manoeuvre = new TurnCenter(transform, parkNr, turnDirect);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }

  //-- Methods --
  public void moveScript() {
    try {
      // must have two entry-tickets:
      Ticket ticPark = entryTickets[0];
      Ticket ticCross = entryTickets[1];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (ticPark != null) ticPark.free();

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