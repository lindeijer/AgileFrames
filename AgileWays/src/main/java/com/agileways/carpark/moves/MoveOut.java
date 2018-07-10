package com.agileways.carpark.moves;

import com.agileways.carpark.manoeuvres.ParkOut;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;
import net.agileframes.core.traces.Ticket;

public class MoveOut extends Move {
  private final boolean DEBUG = true;
  //-- Constructor --
  public MoveOut(FuTransform transform, int entrance, int parkSide, int parkLane) {
    super(transform);
    this.manoeuvre = new ParkOut(transform, entrance, parkSide, parkLane);
    this.signs = new Sign[2];
    signs[0] = new Sign(); // anticipating
    signs[1] = new Sign(); // finished
  }

  //-- Methods --
  public void moveScript() {
    try {
      if (DEBUG) System.out.println("*D* MoveOut: started!");
      // must have two entry-tickets:
      Ticket ticExitPrev = entryTickets[0];
      Ticket ticRoad1 = entryTickets[1];
      Ticket ticRoad2 = entryTickets[2];

      // necessary tickets are claimed in the scene-action
      manoeuvre.startExecution();

      if (DEBUG) System.out.println("*D* MoveOut: about to watch flag 0!");
      watch(manoeuvre.getFlag(0));// exit-tic can be freed
      if (DEBUG) System.out.println("*D* MoveOut: flag 0 (exit free) raised!");
      if (ticExitPrev != null) ticExitPrev.free();
      if (DEBUG) System.out.println("*D* MoveOut: 1");

      if (ticRoad2 != null) {
        if (DEBUG) System.out.println("*D* MoveOut: Waiting to Cross");
        watch(manoeuvre.getFlag(3));// cross-road
        // if road2 != null, it means that we only crossed
        // road1 and we are now driving on road2, thus road1 can be freed
        if (DEBUG) System.out.println("*D* MoveOut: flag 3 (cross-road) raised!");
        ticRoad1.free();
      }
      if (DEBUG) System.out.println("*D* MoveOut: 2");

      watch(manoeuvre.getFlag(1)); // 90% of manoeuvre done..anticipate
      if (DEBUG) System.out.println("*D* MoveOut: flag 1 (anticipate 90%) raised!");
      signs[0].broadcast();

      watch(manoeuvre.getFlag(2));// total manoeuvre done..finished
      if (DEBUG) System.out.println("*D* MoveOut: flag 3 (finished) raised!");
      if (ticRoad2 == null) { ticRoad1.free(); } else { ticRoad2.free(); }
      signs[1].broadcast();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}