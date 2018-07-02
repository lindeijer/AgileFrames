package net.agileframes.traces;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.brief.Brief;
import net.agileframes.core.traces.Action;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public abstract class ActionImplBase /* extends UnicastRemoteObject */ implements Action {

  public ActionImplBase() {}

  public Actor actor;
  public SceneImplBase scene;

  //////////////////////////////////////////////////////////////

  public static final int CREATED     = 0; // after creation
  public static final int INITIALIZED = 1;
  public static final int ACTIVE      = 2; // after activity.
  public static final int EXECUTING   = 3; // execute called , script started
  public static final int POST_EXEC   = 4; // script completed
  public static final int POST_ACTIVE = 5; // after last activity

  private int state = 0;
  protected void setState(int s) { state = s; }

  //////////////////////////////////////////////////////////////

  public abstract void exec(int[] indexes,Ticket[] tickets,Brief[] briefs);
  public abstract void execute();

  //////////////////// INDEXES ////////////////////////////////


  public void exec(int[] indexes) {
    exec(indexes,null,null);
  }

  public synchronized void exec(int i1,int i2) {
    exec(new int[]{i1,i2},null,null);
  }

  public synchronized void exec(int i1,int i2,int i3) {
    exec(new int[]{i1,i2,i3},null,null);
  }

  public synchronized void exec(int i1,int i2,int i3,int i4) {
    exec(new int[]{i1,i2,i3,i4},null,null);
  }

  public synchronized void exec(int i) {
    exec(new int[]{i},null,null);
  }

  //////////////////////// TICKETS //////////////////////////////////////

  public synchronized void exec(Ticket[] tickets) {
    exec(null,tickets,null);
  }

  public synchronized void exec(Ticket t1) {
    exec(null,new Ticket[]{t1},null);
  }

  public synchronized void exec(Ticket t1,Ticket t2) {
    exec(null,new Ticket[]{t1,t2},null);
  }

  public synchronized void exec(Ticket t1,Ticket t2,Ticket t3) {
    exec(null,new Ticket[]{t1,t2,t3},null);
  }

  public synchronized void exec(Ticket t1,Ticket t2,Ticket t3,Ticket t4) {
    exec(null,new Ticket[]{t1,t2,t3,t4},null);
  }

  ////////////////////////////////// BRIEFS //////////////////////////

  public synchronized void exec(Brief[] briefs) {
    exec(null,null,briefs);
  }

  public synchronized void exec(Brief b1) {
    exec(null,null,new Brief[]{b1});
  }

  public synchronized void exec(Brief b1,Brief b2) {
    exec(null,null,new Brief[]{b1,b2});
  }

  public synchronized void exec(Brief b1,Brief b2,Brief b3) {
    exec(null,null,new Brief[]{b1,b2,b3});
  }

  public synchronized void exec(Brief b1,Brief b2,Brief b3,Brief b4) {
    exec(null,null,new Brief[]{b1,b2,b3,b4});
  }

  ////////////////////// COMBIS ///////////////////////////////////////


  public synchronized void exec(int i1,Ticket t1,Brief b1) {
    exec(new int[]{i1},new Ticket[]{t1},new Brief[]{b1});
  }

  public synchronized void exec(int i1,Ticket t1,Ticket t2,Ticket t3) {
    exec(new int[]{i1},new Ticket[]{t1,t2,t3},null);
  }

  public synchronized void exec(int i1,int i2,Ticket t1,Ticket t2) {
    exec(new int[]{i1,i2},new Ticket[]{t1,t2},null);
  }

  public synchronized void exec(int i1,int i2,int i3,Ticket t1) {
    exec(new int[]{i1,i2,i3},new Ticket[]{t1},null);
  }

}