package net.agileframes.brief;
import net.agileframes.core.brief.Brief;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Trajectory;
import net.agileframes.core.forces.Rule;
import net.agileframes.core.forces.Constraint;

/*
srcID = the move's acquired from the scene
dstID = the driver's id, the actor was uploaded with it.
*/

public class MoveBrief extends Brief {

  public ServiceID actorID = null;
  public Long actorIDleastSignificantBits = null;
  public Long actorIDmostSignificantBits = null;

  public void setActorID(ServiceID actorID) {
    if (actorID != null) {
      this.actorID = actorID;
      this.actorIDleastSignificantBits = new Long(actorID.getLeastSignificantBits());
      this.actorIDmostSignificantBits  = new Long(actorID.getMostSignificantBits());
    }
    else {
      this.actorID = null;
      this.actorIDleastSignificantBits = null;
      this.actorIDmostSignificantBits  = null;
    }
  }

  public ServiceID getActorID() {
    if (this.actorID != null) {
      this.actorID = new ServiceID(actorIDleastSignificantBits.longValue(),actorIDmostSignificantBits.longValue());
    }
    return this.actorID;
  }

  //////////////////////////////////////////////////////////////////////////

  public Trajectory trajectory = null;
  public Rule[] rules = null;  // how does this work with serialization and entries?
  public Constraint[] constraints = null;

  public MoveBrief() {
    //System.out.println("re-serializing MoveBrief");
  }

  public MoveBrief(ServiceID moveID,ServiceID driverID,ServiceID actorID,
                   Trajectory trajectory,Rule[] rules) {
    super(moveID,driverID);
    this.setActorID(actorID);
    this.trajectory = trajectory;
    // this.rules = new Rules(rules);
    // this.rules = rules;
    // this.constraints = constraints
  }

  public MoveBrief(ServiceID moveID,ServiceID driverID,ServiceID actorID,
                   Trajectory trajectory,Rule[] rules,Constraint[] constraints) {
    super(moveID,driverID);
    this.setActorID(actorID);
    this.trajectory = trajectory;
    this.rules = rules;
    this.constraints = constraints;
  }


  /////////////////////////////////////////////////////////////////////////

  public String toString() {
    String x = this.getClass().toString() + "\n";
    if (trajectory==null) { x = x + "  trajectory=null\n"; }
    else {                  x = x + "  trajectory="  + trajectory.toString() + "\n"; }
    if (actorID==null) { x = x + "  actorID=null\n";  }
    else {               x = x + "  actorID=" + actorID.toString() + "\n  ";    }
    x = x + super.toString();
    return x;
  }







}


