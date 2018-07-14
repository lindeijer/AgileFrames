package net.agileframes.forces;
import net.agileframes.core.traces.Ticket;

public class MoveIB {//implements Runnable {

  public MoveIB() {
  }

  public void run(Ticket[] tickets) {
    Thread moveThread = new Thread("MoveThread@<ThisMove>") {
      public void run() {
        runMoveScript();
      }
    };
    moveThread.start();     // will start this.run()
  }

  public void run(){
    System.out.println("Yes, we are running; thread="+Thread.currentThread().toString());
  }

  private void runMoveScript() {
    System.out.println("Yes, we are running runMoveScript(); thread="+Thread.currentThread().toString());
    run();  
  }
}