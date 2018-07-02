package net.agileframes.traces.ticket;

import net.agileframes.traces.SceneAction;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;
import net.agileframes.traces.Semaphore;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.TransactionManager;
import java.rmi.*;
// import net.agileframes.TRACES.VIEW.TicketPainter;

public class CollectTicket implements Ticket  { //  extends SetTicket

  private static Semaphore cTsem;
  private static PrimeTicket cTt;

  static {
    try {
      cTsem = new Semaphore("CT-static-Sem");
      cTt = new PrimeTicket(null,cTsem);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  PrimeTicket[] subTickets;

  public CollectTicket(PrimeTicket[] tickets,PrimeTicket ticket) {
    subTickets = new PrimeTicket[tickets.length+1];
    for (int i=0;i<tickets.length;i++)  {
      subTickets[i] =  tickets[i];
    }
    subTickets[tickets.length] = ticket;
  }

  public CollectTicket(PrimeTicket[] tickets) {
    subTickets = tickets;
  }

  public void insist() throws BlockException {
    cTt.insist();
    for (int i=0;i<subTickets.length;i++)  {
      if (subTickets[i] != null) { subTickets[i].reserve(); }
    }
    cTt.free();
    for (int i=0;i<subTickets.length;i++)  {
      if (subTickets[i] != null) { subTickets[i].insist(); }
    }
  }

  //////////////////////////////////////////////

  public void reserve(){
    System.out.println("CT.reserve not implmented, will exit");
    System.exit(1);
  }

  public boolean attempt() {
    System.out.println("CT.reserve not implmented, will exit");
    System.exit(1);
    return false;
  }

  public void free() {
    System.out.println("CT.free not implmented, will exit");
    System.exit(1);
  }

  ///////////////////////////////////////////

  public int snip(){ return -1; }
  public void reserve(Transaction txn){}
  public boolean reserve(Ticket super_ticket,int i){ return false; }
  public void setAssigned(Ticket super_ticket,int i){}

}

/*

  private CollectTicket(Ticket[] tickets) {
    this("Anonymous",null,tickets);
  }

  private CollectTicket(String name,Ticket[] tickets) {
    this(name,null,tickets);
  }

  public CollectTicket(String name,
                       SceneAction scene_action,
                       Ticket[] tickets) {
    super(name,scene_action,tickets);
  }

  /// implementation of Ticket

  /**
  The collect-ticket must attempt to reserve its claim with all the semaphores
  involved if possible, it does so in a sequential manner.
  A remote-exception is considered a failed attempt.
  /
  public boolean _attempt() throws BlockException { // state == INITIAL
    boolean isassigned = true;
    int i=0;
    try {
      for(i=0;i<sub_tickets.length;i++) {
        isassigned = isassigned && sub_tickets[i].attempt();
        if (!isassigned) { continue; }
      }
    }
    catch (/*Remote/Exception re) { /* what did i delete ? / }
    return isassigned;
  }

  /**
  top-level transaction starts, this is the client.
  the transaction is initially null.
  The state is INITIAL.
  /
  public boolean _reserve() throws BlockException {
    // do the atomic reserve or fail or BlockException.
    reserve(sub_tickets);
    // state is RESERVING.
    // create the callback structure.
    try {
      for(int i=0;i<sub_tickets.length;i++) {
        this.assigned_sub_tickets[i] = sub_tickets[i].reserve(this,i);
      }
    }
    catch (/*Remote/Exception re) {
      System.out.println(toString() +
        "could not create callback structure: RemoteException " +
        "(Am I no longer connected?)" );
      re.printStackTrace();
      System.exit(1);
    }
    boolean isassigned = true;
    for(int i=0;i<sub_tickets.length;i++) {
      isassigned = isassigned && assigned_sub_tickets[i];
    }
    return isassigned;
  }

  /**
  /
  public void  _insist() { // state == RESERVING
    int i=0;
    //System.out.println(getIdentity() + " is starting to insist");
    try {
      for(i=0;i<sub_tickets.length;i++) { sub_tickets[i].insist(); }
    }
    catch (BlockException e) {
      System.out.println(toString() +
      " is bogying on sub-ticket " + i + " during _insist(). ");
      e.printStackTrace();
      System.exit(1);
    }
    catch (RemoteException re) {
      System.out.println(toString() +
      " got a RemoteException on sub-ticket " + i + " during _insist(). ");
      re.printStackTrace();
      System.exit(1);
    }
    //System.out.println(getIdentity() + " is done insisting");
  }

  /**
  /
  public void _free() throws BlockException { // state == ASSIGNED
    int i=0;
    try {
      for(i=0;i<sub_tickets.length;i++) { sub_tickets[i].free(); }
    }
    catch (BlockException block_exception) {
      System.out.println(toString() +
      " is bogying on sub-ticket " + i + " during _free(). ");
      throw block_exception;
    }
    catch (RemoteException re) {
      System.out.println(toString() +
      " got a RemoteException on sub-ticket " + i + " during _free(). ");
      re.printStackTrace();
      System.exit(1);
    }
  }

  /**
  /
  public int _snip() {
    int i=0;
    try {
      for(i=0;i<sub_tickets.length;i++) {
        if (sub_tickets[i].snip() == 0) { return 0; }
      }
    }
    catch (BlockException e) {
      System.out.println(toString() +
      " is bogying on sub-ticket " + i + " during _snip(). ");
      e.printStackTrace();
      System.exit(1);
    }
    catch (RemoteException re) {
      System.out.println(toString() +
      " got a RemoteException on sub-ticket " + i + " during _snip(). ");
      re.printStackTrace();
      System.exit(1);
    }
    return 1;
  }

  ///////////////////////////////////////////////////////////////////

  /**
  The collect-tickets must reserve its claim with all the semaphores involved,
  the operation is governed by the transaction.
  This operation preceeds reserve(Ticket,int).
  ReserveDeniedException for any exception, this will trigger a retry
  at the source of the transaction.
  /
  protected void _reserve(Transaction tx)
    throws ReserveDeniedException,BlockException {
    // this is in some way a sub-ticket of a collect ticket.
    int i = -1;
    try {
      for (i=0;i<sub_tickets.length;i++) {
        sub_tickets[i].reserve(tx);
      }
    }
    catch (RemoteException re) {
      System.out.println(toString() +
      " got a RemoteException on sub-ticket " + i + " during _reserve(). ");
      re.printStackTrace();
      throw new ReserveDeniedException("RemoteException");
    }
  }

  /**
  RemoteExceptions must be deals with. RE-RE-RE TRY;
  /
  protected boolean _reserve(net.agileframes.core.traces.Ticket super_ticket,int index)
      throws BlockException {
    // parameters are irrelevant.
    int i=0;
    boolean isassigned = true;
    boolean reserved = false;
    for(i=0;i<sub_tickets.length;i++) {
      while (!reserved) {
        try {
          reserved = sub_tickets[i].reserve(this,i);
        }
        catch (BlockException block_exception) {
          System.out.println(toString() +
          " is bogying on sub-ticket " + i +
          " during _reserve(Ticket,int).");
          throw block_exception;
        }
        catch (RemoteException re) {
          System.out.println(toString() +
          " got a RemoteException on sub-ticket " + i +
          " during _reserve(Ticket,int).");
          reserved = false;
          try { this.wait(2000); }
          catch (InterruptedException e) {}
        }
      };
      isassigned = isassigned && reserved;
    }
    return isassigned;
  }

  //////////////////////////////////////////////////////////////////////////


  /**
  /
  public void setAssigned(net.agileframes.core.traces.Ticket sub_ticket,int i) {
    if (sub_tickets[i] != sub_ticket) {
      System.out.println(toString() +
      " got a callback by sub-ticket " + i + " who is spoofing");
      System.exit(1);
    }
    assigned_sub_tickets[i] = true;
    boolean isassigned = true;
    for (int j=0;j<sub_tickets.length;j++) {
      isassigned = isassigned && assigned_sub_tickets[j];
    }
    if (isassigned) setState(ASSIGNED);
    // System.out.println(getIdentity() +
    //  " got a callback by sub-ticket " + i);
  }

  ////////////////////////////////////////////////////////////////////////
  /*
  protected TicketPainter _getTicketPainter() {
    return new TicketPainter(this);
  }
  */


