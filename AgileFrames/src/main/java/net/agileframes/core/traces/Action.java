package net.agileframes.core.traces;

// import net.agileframes.core.brief.Brief;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Action extends Remote {

  public void execute() throws RemoteException;

  /*
  public void exec(int[] indexes,Ticket[] tickets,Brief[] briefs) throws RemoteException;

  public void exec(int i1,Ticket t1,Brief b1)                     throws RemoteException;
  public void exec(int i1,Ticket t1,Ticket t2,Ticket t3)          throws RemoteException;
  public void exec(int i1,int i2,Ticket t1,Ticket t2)             throws RemoteException;
  public void exec(int i1,int i2,int i3,Ticket t1)                throws RemoteException;
  //
  public void exec(int[] indexes)               throws RemoteException;
  public void exec(int i1)                      throws RemoteException;
  public void exec(int i1,int i2)               throws RemoteException;
  public void exec(int i1,int i2,int i3)        throws RemoteException;
  public void exec(int i1,int i2,int i3,int i4) throws RemoteException;
  //
  public void exec(Ticket[] tickets)                        throws RemoteException;
  public void exec(Ticket t1)                               throws RemoteException;
  public void exec(Ticket t1,Ticket t2)                     throws RemoteException;
  public void exec(Ticket t1,Ticket t2,Ticket t3)           throws RemoteException;
  public void exec(Ticket t1,Ticket t2,Ticket t3,Ticket t4) throws RemoteException;
  //
  public void exec(Brief[] briefs)                      throws RemoteException;
  public void exec(Brief b1)                            throws RemoteException;
  public void exec(Brief b1,Brief b2)                   throws RemoteException;
  public void exec(Brief b1,Brief b2,Brief b3)          throws RemoteException;
  public void exec(Brief b1,Brief b2,Brief b3,Brief b4) throws RemoteException;

  */
}
