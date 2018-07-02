package net.agileframes.traces;

import net.agileframes.core.forces.Actor;
import net.agileframes.core.traces.Ticket;
import net.agileframes.core.brief.Brief;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.Action;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import com.objectspace.jgl.Array;


public abstract class SceneAction extends ActionImplBase {

  public SceneAction(SceneImplBase scene)  {
    this.scene = scene;
  }

  public Action superSceneAction;

  public void assimilate(Actor actor) {
    this.assimilate(actor,null);
  }

  public void assimilate(Actor actor,Action superSceneAction) {
    this.actor = actor;
    this.superSceneAction = superSceneAction;
    setState(INITIALIZED);
  }

  ///////////////// user methods ///////////////////////////////////////


  protected abstract void script() throws BlockException,RemoteException;

  ////////////////////////////////////////////////////////////////////

  protected int[] accessIndexes = null;
  protected Ticket[] accessTickets = null;
  protected Brief[] accessBriefs = null;

  protected int[] entryIndexes = null;
  protected Ticket[] entryTickets = null;
  protected Brief[] entryBriefs = null;

  protected int[] exitIndexes = null;
  protected Ticket[] exitTickets = null;
  protected Brief[] exitBriefs = null;

  ////////////////////////////////////////////////////////////////////

  /** note that the array-indexes are not adjusted */
  public void exec(int[] indexes,Ticket[] tickets,Brief[] briefs) {
    entryIndexes = indexes;
    entryTickets = tickets;
    entryBriefs = briefs;
    execute();
  }

  public synchronized void execute() {
    setState(EXECUTING);
    try { script(); }
    catch (BlockException e) { // thrown by tickets
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(2);
    }
    catch (RemoteException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(2);
    }
    setState(POST_EXEC);
    // if (sa_painter != null) { sa_painter.quit(); }
  }

  //////////// sub-action callback methods /////////////////////

  /** */
  public Array ticket_list = new Array();  // run-time list.

  /**
  called by a prime-ticket upon creation
  iff it has a reference to its scene-action.
  */
  public void addTicket(Ticket ticket) {
    ticket_list.pushBack(ticket);
    modelChanged();
  }

  /**
  called when a prime-ticket finalizes.
  */
  public void removeTicket(Ticket ticket) {
    ticket_list.remove(ticket);   // does the array pack?
    ticket_list.trimToSize();
    modelChanged();
  }

  ////////////////////////////////////////////////////////////////

  protected void modelChanged() {}

}

  /*
  ////////////////////////////////////////////////////////////////

  public String getName() {
    return this.getClass().toString();
  }

  public String getIdentity() {
    String identity = getName();
    if (actor != null) {
      try {
        identity = identity +" for "+ actor.getIdentity();
      }
      catch (Exception e) {
        identity = identity + " for some actor";
      }
    }
    return identity;
  }


  //////////// CONSTRUCTORS ///////////////////////////////////////////



  protected abstract void initialize(); // overload with your stuff!!


  //////////////////////////////////////////////////////////////



  ///////// implementation of SceneAction /////////////////////////////






  ////////////////////////////////////////////////////////////////////////

  /**
  public Array basic_action_list = new Array();  // run-time list.

  /**
  called by a basic-action upon creation

  public void addBasicAction(BasicAction_ImplBase basic_action) {
    basic_action_list.pushBack(basic_action);
    modelNew();
  }

  /**
  called when a basic-action finalizes.

  public void removeBasicAction(BasicAction_ImplBase basic_action) {
    basic_action_list.remove(basic_action);   // does the array pack?
    basic_action_list.trimToSize();
    modelNew();
  }

  //////////// VIEW RELATED METHODS /////////////////////////////////////

  /*
  SceneActionPainter sa_painter = null;

  public SceneActionPainter getSceneActionPainter() {
    if (sa_painter == null) { sa_painter = _getSceneActionPainter(); }
    return sa_painter;
  }

  /**
  overload this method if you define a coustom scene-action painter.

  protected SceneActionPainter _getSceneActionPainter() {
    return new SceneActionPainter(this);
  }

  public void setSceneActionPainter(SceneActionPainter sap) {
    if (sa_painter != sap) { sa_painter = sap; }
  }

  protected void modelChanged() {
    if (sa_painter != null) {
      sa_painter.modelNew();
    }
    else {
      System.out.println("Damn SA PAINTER IS NULL");
    }
  }
  protected void modelChanged(int property_index) {}
  protected void modelNew() {}

}             */
