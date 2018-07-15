package com.agileways.crossscene.moves;

import com.agileways.crossscene.manoeuvres.ManoeuvreC;
import net.agileframes.core.forces.Move;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Sign;
import net.agileframes.core.forces.FuTransform;

public class MoveC extends Move {

  public MoveC(FuTransform transform) throws java.rmi.RemoteException {
    super(transform);
    this.manoeuvre = new ManoeuvreC(transform);  // ManoeuvreA has 3 Flags: started, passed and finished
    this.signs = new Sign[2];
    this.signs[0] = new Sign();
    //this.finishedSign = new Sign();
    this.signs[1] = new Sign();
  }

  public void moveScript(){
    manoeuvre.startExecution();
    boolean raised = false;
    this.watch(manoeuvre.getFlag(0));
    System.out.println("Move C is started!");

    this.watch(manoeuvre.getFlag(1));
    System.out.println("Move C is passing by!");

    this.watch(manoeuvre.getFlag(2));
    System.out.println("Move C is finished!");
    this.signs[0].broadcast();
    this.signs[1].broadcast();
    try {
      entryTickets[0].free();
    } catch (Exception e) {
       System.out.println("Exception in MoveC.moveScript(): "+e.getMessage());
       e.printStackTrace();
    }
  }
}
