package net.agileframes.brief;
import net.agileframes.core.brief.Brief;
import net.jini.core.lookup.ServiceID;
/**
 * <b>The implementation of BooleanBrief.</b>
 * <p>
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class BooleanBrief extends Brief {
  /** The boolean value of this Brief */
  public Boolean value = null;
  /** The reason of this Brief */
  public String reason = null;
  /** Empty Constructor. Not Used. */
  public BooleanBrief() {}
  /**
   * Constructor without value and reason.<p>
   * Calls super.
   * @see   net.agileframes.core.brief.Brief#Brief(ServiceID,ServiceID)
   * @param srcID the unique service-id of the sender of this brief
   * @param dstID the unique service-id of the destination of this brief
   */
  public BooleanBrief(ServiceID srcID,ServiceID dstID) {
    super(srcID,dstID);
  }
  /**
   * Constructor with value.<p>
   * Calls super. Sets value.
   * @see   net.agileframes.core.brief.Brief#Brief(ServiceID,ServiceID)
   * @param srcID the unique service-id of the sender of this brief
   * @param dstID the unique service-id of the destination of this brief
   * @param val   the (boolean) value of this Brief
   */
  public BooleanBrief(ServiceID srcID,ServiceID dstID,boolean val) {
    super(srcID,dstID);
    this.value = new Boolean(val);
  }
  /**
   * Constructor with value and reason.<p>
   * Calls super. Sets value and reason.
   * @see   net.agileframes.core.brief.Brief#Brief(ServiceID,ServiceID)
   * @param srcID   the unique service-id of the sender of this brief
   * @param dstID   the unique service-id of the destination of this brief
   * @param val     the (boolean) value of this Brief
   * @param reason  a description of the reason of this brief
   */
  public BooleanBrief(ServiceID srcID,ServiceID dstID,boolean val,String reason) {
    super(srcID,dstID);
    this.value = new Boolean(val);
    this.reason = reason;
  }
}