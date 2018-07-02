package net.agileframes.core.forces;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Trajectory;
import java.io.Serializable;

// import java.rmi.MarshalledObject;

/**
 Defines a method to be executed when the flag is raised or lowered.
 * Created: Wed Jan 12 12:59:43 2000
 * @author Lindeijer, Evers
 * @version 0.0.1
*/

public interface Rule extends Flag {

  //////////////////////////////////////////////////////////////////////

  /**
  Default evaluation method for the rule, this method may only call methods defined in the machines type.
  Called by the evaluator iff it does not possess a custom evaluation method for this rule,
  iff the rule is unknown to the evaluator. (security?)
  @param functional state of the machine, access state.machine if you must.
  */
  public boolean evaluate(State state);

  //////////////////////////////////////////////////////////////////////

  /**
  Called every time the rule's evaluation changes value and upon activation.
  Overload this method in your implementation of Rule.
  @param result of the evaluation. (security?)
  @see accepted()
   */
  public void execute(boolean val);

  //////////////////////////////////////////////////////////////////////

  public boolean isActive();

}


