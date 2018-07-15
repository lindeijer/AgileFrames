package net.agileframes.traces.ticket;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.TimeoutExpiredException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.Transaction;
import java.rmi.*;
/**
 * <b>The SetTicket makes it possible to do an atomic reserve.</b>
 * <p>
 * The API-documentation is not optimal.
 * @see     SelectTicket
 * @see     CollectTicket
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public abstract class SetTicket extends TicketIB {
  Ticket[] sub_tickets = null;
  boolean[] assigned_sub_tickets = null;
  /**
   * Parameter to be used to debug this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = false;
  /**
   * Sets the sub-tickets of this set-ticket.<p>
   * @param tickets the tickets to set
   */
  protected void setSubTickets(Ticket[] tickets) {
    if (tickets == null) return;
    else {
      sub_tickets = tickets;
      assigned_sub_tickets = new boolean[sub_tickets.length];
    }
  }
  /**
   * Default Constructor.<p>
   * Calls super and sets sub-tickets.
   * @see   #setSubTickets(Ticket[])
   * @see   TicketIB#TicketIB(String,SceneAction)
   * @param name    the name of this ticket
   * @param sa      the scene-action in which this ticket is created
   * @param tickets the sub-tickets of this ticket
   */
  public SetTicket(String name,SceneAction sa,Ticket[] tickets) {
    super(name,sa);
    setSubTickets(tickets);
  }

  /**
  Called by collect-tickets and select-tickets on themselves.<p>
  This is the root-method of a transaction.
  If anything goes wrong a ReserveDeniedException is thrown.
  <p>
  This method is called by SelectTic._reserve() [or Collect]<br>
  1) subtickets are put in pending-lists of its semaphores<br>
  2) committing (calls this.commit)<br>

  @param  tickets the tickets to reserve on
  */
  protected void reserve(Ticket[] tickets) throws BlockException {
    if (DEBUG) System.out.println("*D* SetTicket.reserve(Ticket[]) called");
    boolean reserved = false;
    while (!reserved) {
      try {
        if (DEBUG) System.out.println("*D* SetTicket.reserve(tickets), number of sub-tickets = "+sub_tickets.length);
        transaction = this.scene_action.scene.getTransaction(tickets);
        for(int i=0;i<sub_tickets.length;i++) {
          sub_tickets[i].reserve(transaction);//put the subtickets in pending list
          if (DEBUG) System.out.println("*D* SetTicket.reserve: sub_tickets[i].reserve(transaction) completed");
          reserved = true;
        }
      }
      catch (RemoteException e) {
        System.out.println("RemoteException: Are you sure the Transaction Manager is started?");
        e.printStackTrace();
      }
      catch (ReserveDeniedException e) {
        // any semaphore exception.
        // any remote exception.
        System.out.println(getName() +
         " failed to reserve under transaction: due to ReserveDeniedException: " + e.getMessage());
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
    if (DEBUG) System.out.println("*D* SetTicket: "+toString() + " committed");
  }

  /*
  Commits the transaction, exits the system if the transaction is unknown.<p>
  <p>
  Transaction.commit will call commit on transactionparticipants, that is: Semaphores.
  @see  net.agileframes.core.traces.Semaphore#commit(TransactionManager,long)
  @return false iff the transaction could not commit.
  */
  private boolean commit(Transaction transaction) {
    if (DEBUG) System.out.println("*D* SetTicket.commit(txn)");
    try {
      transaction.commit(60000);  // one minute
    }
    catch (RemoteException e) {
      System.out.println("EXCEPTION "+ toString() +
      "could not tx.commit(): RemoteException" );
      return false;
    }
    catch (UnknownTransactionException e) {
      System.out.println("EXCEPTION "+toString() +
      "could not tx.commit(): UnknownTransactionException" );
      e.printStackTrace();
      System.exit(1);
    }
    catch (CannotCommitException e) {
      System.out.println("EXCEPTION "+toString() +
      "could not tx.commit(): CannotCommitException, message=" + e.getMessage());
      e.printStackTrace();
      return false;
    }
    catch (TimeoutExpiredException e) {
      if (!e.committed) {
        System.out.println("EXCEPTION "+toString() +
          "commit(tnx) timed out but transaction did commit: continue");
      }
      else {
        System.out.println("EXCEPTION "+toString() +
          "commit(tnx) timed out, transaction did not commit: SYSTEM WILL QUIT");
        System.exit(1);
      }
    }
    return true;
  }

  /*
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
        if (DEBUG) System.out.println("*D* "+toString() +
          "commit(tnx) timed out, transaction did not commit: continue");
      }
    }
  }

}