package net.agileframes.forces;
import net.agileframes.brief.BooleanBrief;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Constraint;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Machine.NotTrustedException;

public interface MoveManager {

  public BooleanBrief acceptMove(ServiceID serviceID,Trajectory trajectory,
      Rule[] rules,Constraint[] constraints) throws NotTrustedException;
  public BooleanBrief acceptTrajectory(ServiceID serviceID,Trajectory trajectory) throws NotTrustedException;
  public BooleanBrief acceptRule(ServiceID serviceID,Rule rule) throws NotTrustedException;
  public BooleanBrief acceptConstraint(ServiceID serviceID,Constraint constraint) throws NotTrustedException;
  public boolean isCoherentMove(Trajectory trajectory,Rule[] rules,Constraint[] constraints);
  public boolean isCoherentTrajectory(ServiceID serviceID,Trajectory trajectory);
  public boolean isCoherentRule(ServiceID serviceID,Rule rule);
  public boolean isCoherentConstraint(ServiceID serviceID,Constraint constraint);

} 