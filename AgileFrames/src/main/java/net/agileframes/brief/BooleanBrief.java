package net.agileframes.brief;
import net.agileframes.core.brief.Brief;
import net.jini.core.lookup.ServiceID;

public class BooleanBrief extends Brief {

  public Boolean value = null;
  public String reason = null;

  public BooleanBrief() {}

  public BooleanBrief(ServiceID srcID,ServiceID dstID) {
    super(srcID,dstID);
  }

  public BooleanBrief(ServiceID srcID,ServiceID dstID,boolean val) {
    super(srcID,dstID);
    this.value = new Boolean(val);
  }

  public BooleanBrief(ServiceID srcID,ServiceID dstID,boolean val,String reason) {
    super(srcID,dstID);
    this.value = new Boolean(val);
    this.reason = reason;
  }
}