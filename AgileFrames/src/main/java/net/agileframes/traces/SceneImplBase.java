package net.agileframes.traces;

import net.jini.core.transaction.Transaction;
import net.agileframes.core.traces.Ticket;
import net.agileframes.server.ServerImplBase;
import net.agileframes.server.AgileSystem;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.traces.Navigator;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;
import java.rmi.RemoteException;
/**
The implementation base behind core.traces.Scene
*/

public abstract class SceneImplBase extends ServerImplBase implements net.agileframes.core.traces.Scene  {

  public SceneImplBase(String name) throws java.rmi.RemoteException {
    super(name);

  }

  ////////////////////////////////////////////////////////////////////////////

  public Transaction getTransaction(Ticket[] tickets) {
    return AgileSystem.getTransaction();
  }

  ////////////////////////////////////////////////////////////////////////

  protected void add(Move[] moves) {
    // moves[i].setScene(this);
  }
  protected void add(Semaphore[] semaphores) {
    // semaphores[i].setScene(this);
  }

  protected void add(Move[][] moves) {
    // moves[i][j].setScene(this);
  }
  protected void add(Semaphore[][] semaphores) {
    // semaphores[i][k].setScene(this);
  }

  /////////////////////////////////////////////////////////////////////



}

///////////////////////////////////////////////////////////////////

