package net.agileframes.core.brief;
import java.io.*;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;

/**
Brief is the base-class for sending messages asynchronously to objects within
the same AgileFrames-system. A destination-object retrieves a brief by presenting
the AgileFrames-system a template describing the brief. The template may match
any number of briefs sent by source-objects, the AgileFrames-system will select one for
retrieval. Depending on the request the brief itself or a copy of the brief will
the retrieved for the destination-object.

The template describing the brief to be retreived must be of the same class as
the brief or a super-class. The template must set values to its public non-primitive
fields, these value must be identical to the values set on the brief.
A null value implies that the template will match any value on the brief for that field.

The Brief class provides serveral public non-primitive fields which are usefull for
retrieval. The field dstID is intended to contain the unique ServiceID of the
destination-object. The destination-object may easily retrieve any brief sent to it
using its personal ServiceID. Assuming the srcID field is set by the source-object, the
destination-object may easily retrieve briefs sent to it by a specific source-object.

Objects may acquire a unique ServiceID from the AgileFrames-system and then use
that ServiceID in brief-communication. This impliest that a source-object must
acquire the destination-objects servicIF


*/

public class Brief implements Entry {

  public String srcServerName;
  public String srcServerClassName;
  public String srcProcessName;
  public String srcProcessClassName;
  public String srcMark;

  public String dstServerName;
  public String dstServerClassName;
  public String dstProcessName;
  public String dstProcessClassName;
  public String dstMark;

  ////////////////////////////////////////////////////////////////////////

  public ServiceID srcID = null;
  /*
  public Long srcIDleastSignificantBits = null;
  public Long srcIDmostSignificantBits = null;
  */
  public void setSrcID(ServiceID srcID) { this.srcID = srcID; }
  /*
    if (srcID != null) {
      this.srcID = srcID;
      this.srcIDleastSignificantBits = new Long(srcID.getLeastSignificantBits());
      this.srcIDmostSignificantBits  = new Long(srcID.getMostSignificantBits());
    }
    else {
      this.srcID = null;
      this.srcIDleastSignificantBits = null;
      this.srcIDmostSignificantBits  = null;
    }
  }
  */

  public ServiceID getSrcID() { return  srcID; }
    /*
    if (this.srcID == null) {
      if (

      this.srcID = new ServiceID(srcIDleastSignificantBits.longValue(),srcIDmostSignificantBits.longValue());
    }
    return this.srcID;
  }
  */

  ////////////////////////////////////////////////////////////////////////

  public ServiceID dstID = null;
  /*
  public Long dstIDleastSignificantBits = null;
  public Long dstIDmostSignificantBits = null;
  */
  public void setDstID(ServiceID dstID) { this.dstID = dstID; }
    /*
     if (dstID != null) {
      this.dstID = dstID;
      this.dstIDleastSignificantBits = new Long(dstID.getLeastSignificantBits());
      this.dstIDmostSignificantBits  = new Long(dstID.getMostSignificantBits());
    }
    else {
      this.dstID = null;
      this.dstIDleastSignificantBits = null;
      this.dstIDmostSignificantBits  = null;
    }
  }
  */

  public ServiceID getDstID() { return dstID; }
    /*
    if (this.dstID != null) {
      this.dstID = new ServiceID(dstIDleastSignificantBits.longValue(),dstIDmostSignificantBits.longValue());
    }
    return this.dstID;
  }
  */

  ////////////////////////////////////////////////////////////////////////

  public Brief(ServiceID srcID,ServiceID dstID) {
    setSrcID(srcID);
    setDstID(dstID);
  }

  public Brief() {}

  ////////////////////////////////////////////////////////////////////////

  public String toString() {
    String x = "net.agileframes.core.brief.Brief extended to be a " + this.getClass().toString() + "\n";
    if (srcID == null) {
      x = x + "  srcID=null\n";
    } else {
      ServiceID srcID = getSrcID();
      x = x + "  srcID=" + srcID.toString() + "\n";
    }
    if (dstID == null) {
      x = x + "  dstID=null";
    } else {
      ServiceID dstID = getDstID();
      x = x + "  dstID=" + dstID.toString();
    }
    return x;
  }

}