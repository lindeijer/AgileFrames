package com.agileways.crossscene.moves;

import com.agileways.crossscene.manoeuvres.*;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;

public class Move8 extends Move {
  //--- Constructor ---
  public Move8(FuTransform transform) throws java.rmi.RemoteException {
    super(transform);
    this.manoeuvre = new Manoeuvre8(transform);
    //this.finishedSign = new Sign();
    this.signs = new Sign[2];
    signs[0] = new Sign();
    signs[1] = new Sign();
  }
  //--- Methods ---
  public void moveScript(){
    System.out.println("moveScript of Move 8 is running");
    manoeuvre.startExecution();
    watch(manoeuvre.getFlag(1));// passedFlag (90%)
    System.out.println("Move 8 passed 90 percent!");
    signs[1].broadcast();
    watch(manoeuvre.getFlag(0));// finishedFlag
    System.out.println("Move 8 is finished! (finishedFlag raised)");
    signs[0].broadcast();
  }
}