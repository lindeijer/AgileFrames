package com.agileways.carpark.moves;

import com.agileways.carpark.manoeuvres.ParkIn;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.forces.FuTransform;

public class MoveIn extends Move {
  private final boolean DEBUG = true;
  //-- Constructor --
  public MoveIn(FuTransform transform, int entrance, int parkSide, int parkLane) {
    super(transform);
    this.manoeuvre = new ParkIn(transform, entrance, parkSide, parkLane);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }

  //-- Methods --
  public void moveScript() {
    try {
      if (DEBUG) System.out.println("*D* MoveIn: started!");
      // must have two entry-tickets:
      Ticket ticExitPrev = entryTickets[0];
      Ticket ticRoad1 = entryTickets[1];
      Ticket ticRoad2 = entryTickets[2];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      if (DEBUG) System.out.println("*D* MoveIn: about to watch flag 0!");
      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (DEBUG) System.out.println("*D* MoveIn: flag 0 (exit-tic) raised!");
      if (ticExitPrev != null) ticExitPrev.free();
      if (DEBUG) System.out.println("*D* MoveIn: 1");

      watch(manoeuvre.getFlag(3));
      if (DEBUG) System.out.println("*D* MoveIn: flag 3 (road) raised!");
      ticRoad1.free();

      if (ticRoad2 != null) {
        if (DEBUG) System.out.println("*D* MoveIn: Waiting to Cross");
        watch(manoeuvre.getFlag(4));// cross-road
        // if road2 != null, it means that we only crossed
        // road2 and we are now driving on parking, thus road1 can be freed
        if (DEBUG) System.out.println("*D* MoveIn: flag 4 (cross-road) raised!");
        ticRoad2.free();
      }

      watch(manoeuvre.getFlag(1)); // 90% of manoeuvre done..anticipate
      if (DEBUG) System.out.println("*D* MoveIn: flag 1 (anticipate 90%) raised!");
      signs[0].broadcast();

      watch(manoeuvre.getFlag(2));// total manoeuvre done..finished
      if (DEBUG) System.out.println("*D* MoveIn: flag 3 (finished) raised!");
      signs[1].broadcast();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}