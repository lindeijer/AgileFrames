package net.agileframes.traces;
import net.agileframes.core.traces.Action;
import java.rmi.RemoteException;
import java.rmi.Remote;
import net.agileframes.core.forces.Trajectory;

public interface Move extends Remote {

  public void event(int eventId,int seqNum) throws RemoteException;

  public final int EVENT = 0;
  public final int EVOLUTION_EVENT = 1;
  public final int TERMINATING_EVENT = 2;

  public class EvolutionRule extends net.agileframes.forces.rule.EvolutionRule {
    private Move move = null;
    private int seqNum = -1;
    public EvolutionRule(Move move,Trajectory trajectory,float evolution,int seqNum) {
      super(trajectory,evolution);
      this.move = move;
      this.seqNum = seqNum;
    }
    public void execute(boolean val) {
      if (val==true) {
        try {
          move.event(Move.EVOLUTION_EVENT,seqNum);
          super.setActive(false);
        } catch (RemoteException e) {
          System.out.println("RemoteException in execute of " + this.toString());
        }
      }
    }
  }  // end of Move.EvolutionRule

  public class TerminatingRule extends net.agileframes.forces.rule.TerminatingRule {
    private Move move = null;
    public TerminatingRule(Move move,Trajectory trajectory) {
      super(trajectory);
      this.move = move;
    }
    public void execute(boolean val) {
      if (val==true) {
        try {
          move.event(Move.TERMINATING_EVENT,0);
          super.setActive(false);
        } catch (RemoteException e) {
          System.out.println("RemoteException in execute of " + this.toString());
        }
      }
    }
  }  // end of Move.EvolutionRule


} // end of Move