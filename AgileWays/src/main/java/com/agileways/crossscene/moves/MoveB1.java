package com.agileways.crossscene.moves;

import com.agileways.crossscene.manoeuvres.ManoeuvreB1;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;

public class MoveB1 extends Move {

  public MoveB1(FuTransform transform) throws java.rmi.RemoteException {
    super(transform);
    this.manoeuvre = new ManoeuvreB1(transform);  // ManoeuvreB1 has 3 Flags: started and finished
    this.signs = new Sign[2];
    this.signs[0] = new Sign();
//    this.finishedSign = new Sign();
    this.signs[1] = new Sign();
  }

  public void moveScript(){
    try {
      manoeuvre.startExecution();

      this.watch(manoeuvre.getFlag(0)); // startedFlag
      System.out.println("B1: started!");
      this.signs[0].broadcast();

      entryTickets[0].insist();

      this.watch(manoeuvre.getFlag(2));// finishedFlag
      System.out.println("B1: finished!");
      this.signs[1].broadcast();

      entryTickets[1].free();

    } catch (Exception e) {
       System.out.println("Exception in MoveB1.moveScript(): "+e.getMessage());
       e.printStackTrace();
    }
  }
}
