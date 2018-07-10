package com.agileways.demo.moves;

import com.agileways.demo.manoeuvres.LeaveTurning;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class Move4 extends Move {
  //-- Attributes --
  public final boolean DEBUG = false;

  //-- Constructor --
  public Move4(FuTransform transform, int direct, int lane) {
    super(transform);
    this.manoeuvre = new LeaveTurning(transform, direct, lane);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }
  //-- Methods --
  public void moveScript() {
    try {
      if (DEBUG) System.out.println("*D* Move4: started!, this="+this.toString());
      // must have two entry-tickets:
      Ticket ticEndPark = entryTickets[0];
      Ticket ticCross = entryTickets[1];
      if (DEBUG) System.out.println("*D* Move4: ticCross ="+ticCross.toString());

      // necessary tickets are claimed in the scene-action
      if (DEBUG) System.out.println("*D* Move4: manoeuvre="+manoeuvre.toString());
      manoeuvre.startExecution();

      if (DEBUG) System.out.println("*D* Move4: about to watch flag 0!");
      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (ticEndPark != null) {
        if (DEBUG) System.out.println("*D* Move4: ticEndPark.free() ="+ticEndPark.toString());
        ticEndPark.free();
      }
      if (DEBUG) System.out.println("*D* Move4: flag 0 raised!");

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