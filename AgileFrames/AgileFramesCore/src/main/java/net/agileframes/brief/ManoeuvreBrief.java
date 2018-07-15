package net.agileframes.brief;
import net.agileframes.core.brief.Brief;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The implementation of ManoeuvreBrief.</b>
 * <p>
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class ManoeuvreBrief extends Brief {
  //------------------------- Attributes ---------------------------------
  private ServiceID actorID = null;
  private Long actorIDleastSignificantBits = null;
  private Long actorIDmostSignificantBits = null;
  private Manoeuvre manoeuvre = null;
  private boolean preparing = false;
  //------------------------- Constructor --------------------------------
  /** Empty Constructor. Not implemented. */
  public ManoeuvreBrief() {
  }
  /**
   * Default Constructor.<p>
   * Calls super.
   * @see   net.agileframes.core.brief.Brief#Brief(ServiceID,ServiceID)
   * @param manoeuvreID the unique service-id of the manoeuvre (=source-id)
   * @param driverID    the unique service-id of the (manoeuvre)driver (=destination-id)
   * @param actorID     the unique service-id of the actor
   * @param manoeuvre   the (serializable and cloned) manoeuvre
   * @param preparing   boolean indicating if the manoeuvre should be prepared
   */
  public ManoeuvreBrief(ServiceID manoeuvreID,ServiceID driverID,ServiceID actorID,
                   Manoeuvre manoeuvre, boolean preparing) {
    super(manoeuvreID,driverID);
    this.setActorID(actorID);
    this.manoeuvre = manoeuvre;
    this.preparing = preparing;
  }

  //------------------------- Methods ------------------------------------
  /**
   * Sets the unique service-id of the actor.<p>
   * @param actorID the actor-id to set
   */
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
  /**
   * Returns the unique service-id of the actor.<p>
   * @return the actor-id
   */
  public ServiceID getActorID() {
    if (this.actorID != null) {
      this.actorID = new ServiceID(actorIDleastSignificantBits.longValue(),actorIDmostSignificantBits.longValue());
    }
    return this.actorID;
  }
  /**
   * Returns information about this brief.<p>
   * Information contains the class-name, the manoeuvre and the actor-id.
   * @return  a string with information about this brief.
   */
  public String toString() {
    String string = this.getClass().toString() + "\n";
    if (manoeuvre == null) { string += "  manoeuvre = null\n"; }
    else { string += "  manoeuvre = "  + manoeuvre.toString() + "\n"; }
    if (actorID == null) { string += "  actorID = null\n";  }
    else { string += "  actorID = " + actorID.toString() + "\n  ";    }
    string += super.toString();
    return string;
  }
}
