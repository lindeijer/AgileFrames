package net.agileframes.traces.ticket;

import net.agileframes.traces.SceneAction;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;

// import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.CannotCommitException;
//import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.TimeoutExpiredException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.Transaction;
import java.rmi.*;

public abstract class SetTicket extends TicketImplBase {

  Ticket[] sub_tickets = null;
  boolean[] assigned_sub_tickets = null;

  protected void setSubTickets(Ticket[] tickets) {
    if (tickets == null) return;
    else {
      sub_tickets = tickets;
      assigned_sub_tickets = new boolean[sub_tickets.length];
    }
  }

  public SetTicket(String name,SceneAction sa,Ticket[] tickets) {
    super(name,sa);
    setSubTickets(tickets);
  }

  /**
  Called by collect-tickets and select-tickets on themselves.
  This is the root-method of ant transaction.
  If anything goes wrong a ReserveDeniedException is thrown.
  */
  protected void reserve(Ticket[] tickets) throws BlockException {
    boolean reserved = false;
    while (!reserved) {
      try {
        transaction = this.scene_action.scene.getTransaction(tickets);
        for(int i=0;i<sub_tickets.length;i++) {
          sub_tickets[i].reserve(transaction);
          reserved = true;
        }
      }
      catch (RemoteException e) {
        System.out.println("what code did I delete here?");
      }
      catch (ReserveDeniedException e) {
        // any semaphore exception.
        // any remote exception.
        // System.out.println(getName() +
        // " failed to reserve under transaction: due to ReserveDeniedException: " + e.getMessage());
        abort(transaction);
        reserved = false; // we must retry the reserve operation.
      }
      catch (BlockException block_exception) {
        System.out.println(toString() +
          " failed to reserve under transaction due to BlockException.");
        abort(transaction);
        throw block_exception;
      }
      if (reserved) {
        reserved = commit(transaction);
        transaction = null;
      }
    }
    //System.out.println(toString() + " committed");
  }

  /**
  Commits the transaction, exits the system if the transaction is unknown.
  @return false iff the transaction could not commit.
  */
  private boolean commit(Transaction transaction) {
    try {
      transaction.commit(60000);  // one minute
    }
    catch (RemoteException e) {
      System.out.println(toString() +
      "could not tx.commit(): RemoteException" );
      return false;
    }
    catch (UnknownTransactionException e) {
      System.out.println(toString() +
      "could not tx.commit(): UnknownTransactionException" );
      e.printStackTrace();
      System.exit(1);
    }
    catch (CannotCommitException e) {
      System.out.println(toString() +
      "could not tx.commit(): CannotCommitException" );
      e.printStackTrace();
      return false;
    }
    catch (TimeoutExpiredException e) {
      if (!e.committed) {
        System.out.println(toString() +
          "commit(tnx) timed out but transaction did commit: continue");
      }
      else {
        System.out.println(toString() +
          "commit(tnx) timed out, transaction did not commit: fail");
        System.exit(1);
      }
    }
    return true;
  }

  /**
  Aborts the transaction, exits the system if that generates an exception.
  */
  private void abort(Transaction transaction) {
    try {
      transaction.abort(60000);
      Object o = new Object();
      synchronized (o) {
        try { o.wait(1000); }
        catch (InterruptedException e) {}
      }
    }
    catch (CannotAbortException e) {
      System.out.println(toString() +
      "could not tx.abort(): CannotAbortException" );
      e.printStackTrace();
      System.exit(1);
    }
    catch (UnknownTransactionException e) {
      System.out.println(toString() +
      "could not tx.abort(): UnknownTransactionException" );
      e.printStackTrace();
      System.exit(1);
    }
    catch (RemoteException e) {
      System.out.println(toString() +
      "could not tx.abort(): RemoteException (Am I no longer connected?)" );
      e.printStackTrace();
      System.exit(1);
    }
    catch (TimeoutExpiredException e) {
      if (!e.committed) {
        System.out.println(toString() +
          "abort(tnx) timed out, strangely transaction did commit: fail");
        System.exit(1);
      }
      else {
        System.out.println(toString() +
          "commit(tnx) timed out, transaction did not commit: continue");
      }
    }
  }

}