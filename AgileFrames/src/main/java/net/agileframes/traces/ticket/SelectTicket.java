package net.agileframes.traces.ticket;

import java.rmi.*;
//import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.Transaction;
import net.agileframes.traces.SceneAction;
import net.agileframes.core.traces.BlockException;
import net.agileframes.core.traces.ReserveDeniedException;
import net.agileframes.core.traces.Ticket;
import net.agileframes.traces.ticket.PrimeTicket;

public class SelectTicket implements Ticket  { //  extends SetTicket implements Runnable

  Ticket[] subTickets;
  int selectedIndex = -1;

  public SelectTicket(PrimeTicket t0,
                      PrimeTicket t1,
                      PrimeTicket t2,
                      PrimeTicket t3,
                      PrimeTicket t4) {
    this(new PrimeTicket[]{t0,t1,t2,t3,t4});
  }

  public SelectTicket(PrimeTicket[] subTickets) {
    this.subTickets = subTickets;
  }

  public void insist(){
    boolean isSelected = false;
    try {
      for (selectedIndex=0;selectedIndex<subTickets.length;selectedIndex++) {
        if (subTickets[selectedIndex].snip() == 1) {
          isSelected = true;
          System.out.println("ST.insist: selectedIndex=" + selectedIndex + " according to snip");
          break;
      } }
      while (!isSelected) {
        selectedIndex = (int) Math.floor(subTickets.length * Math.random());  // 0 or 1
        if ( subTickets[selectedIndex].attempt() ) {
          isSelected = true;
          System.out.println("ST.insist: selectedIndex=" + selectedIndex + " according to random");
          break;
      } }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void reserve(){
    System.out.println("ST.reserve not implmented, will exit");
    System.exit(1);
  }

  public boolean attempt() {
    System.out.println("ST.reserve not implmented, will exit");
    System.exit(1);
    return false;
  }

  public void free() {
    System.out.println("ST.free not implmented, will exit");
    System.exit(1);
  }

  ///////////////////////////////////////////////////////////////////

  public int snip(){ return -1; }
  public void reserve(Transaction txn){}
  public boolean reserve(Ticket super_ticket,int i){ return false; }
  public void setAssigned(Ticket super_ticket,int i){}

}



  /*

  /**
  value is -1 iff state != ASSIGNED.
  Otherwise its value is the subTicket array-index of the selected ticket.
  /
  Integer assigned_ticket = new Integer(-1);

  private SelectTicket(Ticket[] tickets) { // test all kinds of extras.
    this("AnonymousST",null,tickets);
  }

  private SelectTicket(String name,Ticket[] tickets) {
    this(name,null,tickets);
  }

  public SelectTicket(Ticket t0,Ticket t1,Ticket t2,Ticket t3,Ticket t4) {
    super("ShortcutSelectTicket",null,new Ticket[]{t0,t1,t2,t3,t4});
  }

  public SelectTicket(SceneAction scene_action,
                      Ticket t1,Ticket t2) {
    this("anonymous",scene_action,null);
    Ticket[] tickets = new Ticket[2];
    tickets[0] = t1;
    tickets[1] = t2;
    setSubTickets(tickets);
  }

  public SelectTicket(String name,
                      SceneAction scene_action,
                      Ticket[] tickets) {
    super(name,scene_action,tickets);
  }

  // implementation of abstract methods inherited from Ticket_ImplBase //////

  public boolean _attempt() throws BlockException {
    try {
      for (int i=0;i<sub_tickets.length;i++) {
        if (sub_tickets[i].attempt()) {
          assigned_ticket = new Integer(i);
          return true;
        }
      }
    }
    catch (RemoteException e) {
      System.out.println(toString() + " RemoteException in _attempt()");
      System.exit(1);
    }
    return false;
  }


  /**
  state == INTIAL
  /
  public boolean _reserve() throws BlockException {

    /* implementation to be
    // do the atomic reserve or fail or BlockException.
    reserve(sub_tickets);
    // state is RESERVING.
    // create the callback structure.
    try {
      int i=0;
      for(i=0;i<sub_tickets.length;i++) {
        this.assigned_sub_tickets[i] = sub_tickets[i].reserve(this,i);
        if (assigned_sub_tickets[i] == true) {
          assigned_ticket = new Integer(i);
          break;
        }
      }
      int j=0;
      for(j=0;j<i;j++) {
        sub_tickets[j].free();
      }
      for(j=i+1;j<sub_tickets.length;j++) {
        sub_tickets[j].reserve(this,i);  // txn must become null !!
        sub_tickets[j].free();
      }
    }
    catch (RemoteException re) {
      System.out.println(toString() +
        "could not create callback structure: RemoteException " +
        "(Am I no longer connected?)" );
      re.printStackTrace();
      System.exit(1);
    }
    if (assigned_ticket.intValue() != -1) return true;
    // OLD CODE WAS if (assigned_ticket != null) return true;
    return false;
    /
  }

  /**
  state == BLOCKING.
  /
  public void _insist() {
    while (assigned_ticket.intValue() == -1) {
      synchronized (assigned_ticket) {
        try { wait(5000); }
        catch (InterruptedException e) {}
      }
    }
    if (getState() != ASSIGNED) {
      System.out.println("A ticket called back but state != ASSIGNED");
      System.exit(1);
    }
  }

  public void run() {
    for(;;) {
      try { this.wait(5000); }
      catch (InterruptedException e) {}
      if (assigned_ticket.intValue() != -1) {
        if (getState() != ASSIGNED) {
          int i = -1;
          try {
            for(i=0;i<assigned_ticket.intValue()-1;i++) {
              sub_tickets[i].free();
            }
            for(i=assigned_ticket.intValue()+1;i<sub_tickets.length;i++) {
              sub_tickets[i].free();
            }
          }
          catch (RemoteException e) {
            System.out.println("fjdhdjdjffj");
            System.exit(1);
          }
          catch (BlockException e) {
            System.out.println(toString() + " BlockException in run()." +
            " This implies you insisted on a sub-ticket you idiot.");
            System.exit(1);
          }
          setState(ASSIGNED);
        }
        else { /* I apparently allready did this / }
      }
      assigned_ticket.notify();
    }
  }

  /**
  /
  public void _free() throws BlockException {
    if (assigned_ticket.intValue() != -1) {
      try {
        sub_tickets[assigned_ticket.intValue()].free();
        assigned_ticket = new Integer(-1);
      }
      catch (BlockException block_exception) {
        System.out.println(toString() +
        " is bogying on sub-ticket " + assigned_ticket.intValue() +
        " during _free().");
        throw block_exception;
      }
      catch (RemoteException e) {
        System.out.println(toString() + " RemoteException in _free()");
        System.exit(1);
      }
    }
  }

  /**
  /
  public int _snip() {
    if (assigned_ticket.intValue() != -1) {
      return assigned_ticket.intValue() + 1;
    }
    else return 0;
  }

  ///////////////////////////////////////////////////////////

  /**
  Identical to collect-ticket.reserve(Transaction);
  /
  public void _reserve(Transaction tx)
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
  Identical to collect-ticket.reserve(Ticket,int);
  parameters are irrelevant.
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

  public synchronized void setAssigned(net.agileframes.core.traces.Ticket sub_ticket,int i) {
    if (sub_tickets[i] != sub_ticket) {
      System.out.println(toString() +
      " got a callback by sub-ticket " + i + " who is spoofing");
      System.exit(1);
    }
    if (assigned_ticket.intValue() == -1) {
      assigned_sub_tickets[i] = true;
      // all others must be false.
      synchronized(assigned_ticket) { assigned_ticket.notifyAll(); }
      assigned_ticket = new Integer(i);
      setState(ASSIGNED);
    }
  }

  ////////////////////////////////////////////////////////////////////////

  /*
  protected TicketPainter _getTicketPainter() {
    return new TicketPainter(this);
  }
  /


}

*/