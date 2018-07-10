package com.agileways.crossscene.moves;

import com.agileways.crossscene.manoeuvres.ManoeuvreA;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;

public class MoveA extends Move {

  public MoveA(FuTransform transform) throws java.rmi.RemoteException {
    super(transform);
    this.manoeuvre = new ManoeuvreA(transform);  // ManoeuvreA has 3 Flags: started, passed and finished
    this.signs = new Sign[2];
    this.signs[0] = new Sign();
    //this.finishedSign = new Sign();
    this.signs[1] = new Sign();
  }

  public void moveScript(){
    System.out.println("moveScript of Move A is running");
    manoeuvre.startExecution();

    this.watch(manoeuvre.getFlag(0)); // startedFlag
    System.out.println("Move A is started!");

    this.watch(manoeuvre.getFlag(1)); // passedFlag
    System.out.println("Move A: pastFlag raised!");

    this.signs[0].broadcast();

    this.watch(manoeuvre.getFlag(2));// finishedFlag
    System.out.println("Move A is finished!");
    this.signs[1].broadcast();
    try { entryTickets[0].free(); }
    catch (Exception e) {
       System.out.println("Exception in MoveA.moveScript(): "+e.getMessage());
       e.printStackTrace();
    }
  }
}