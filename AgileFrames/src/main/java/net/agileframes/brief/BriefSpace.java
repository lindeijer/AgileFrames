package net.agileframes.brief;
import net.agileframes.core.brief.Brief;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;
import net.jini.core.transaction.TransactionException;
import net.jini.core.entry.UnusableEntryException;
import java.rmi.RemoteException;
import net.jini.core.lease.Lease;
import net.agileframes.core.brief.BriefOffice;
/**
 * <b>The implementation of BriefSpace.</b>
 * <p>
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class BriefSpace implements BriefOffice {
  /** The JavaSpace BriefSpace */
  protected static JavaSpace briefSpace = null;
  /** The name of this BriefSpace */
  protected static String briefSpaceName = null;
  /**
   * Returns a reference to the BriefSpace.<p>
   * A BriefSpace will be created when it doesnot exist already.
   * @see     net.agileframes.server.AgileSystem#getSpace(String)
   * @return  the BriefSpace
   */
  public static JavaSpace getBriefSpace() {
    if (briefSpace == null) {
      briefSpace = AgileSystem.getSpace(briefSpaceName); }
    return briefSpace;
  }

  ////////////////////////////////////////////////////////////////////////
  /**
   * Writes a Brief in a Space.<p>
   * @param space the space to write in
   * @param brief the brief to write
   */
  public static void write(JavaSpace space,Brief brief) {
    try {
      Lease lease = space.write(brief,null,10*60*1000);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in write=" + e.getMessage());
    }
    catch (TransactionException e) {
      System.out.println("RemoteException in write=" + e.getMessage());
    }
  }
  /**
   * Writes a Brief in the BriefSpace.<p>
   * @param brief the brief to write
   */
  public static void write(Brief brief) {
    write(briefSpace,brief);
  }

  ///////////////////////////////////////////////////////////////////////
  /**
   * Checks if a Brief is in a Space.<p>
   * @param space     the space to check
   * @param template  the brief-template to look for
   */
  public static boolean isBrief(JavaSpace space,Brief template) {
    Brief brief = null;
    try {
      brief = (Brief)space.readIfExists(template,null,Long.MAX_VALUE);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in isBrief()=" + e.getMessage());
    }
    catch (InterruptedException e) {
      System.out.println("InterruptedException in isBrief()=" + e.getMessage());
    }
    catch (TransactionException e) {
      System.out.println("TransactionException in isBrief()=" + e.getMessage());
    }
    catch (UnusableEntryException e) {
      System.out.println("UnusableEntryException in isBrief()=" + e.getMessage());
    }
    if (brief == null) { return false; }
    return true;
  }
  /**
   * Checks if a Brief is in the BriefSpace.<p>
   * @param template  the brief-template to look for
   */
  public static boolean isBrief(Brief template) {
    return isBrief(briefSpace,template);
  }

  //////////////////////////////////////////////////////////////////////////
  /**
   * Reads a Brief from a Space.<p>
   * @param   space     the space to read from
   * @param   template  the brief-template to read
   * @return  a copy of the brief matching the template
   */
  public static Brief read(JavaSpace space,Brief template) {
    Brief brief = null;
    try {
      brief = (Brief)space.read(template,null,Long.MAX_VALUE);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in read()=" + e.getMessage());
    }
    catch (InterruptedException e) {
      System.out.println("InterruptedException in read()=" + e.getMessage());
    }
    catch (TransactionException e) {
      System.out.println("TransactionException in read()=" + e.getMessage());
    }
    catch (UnusableEntryException e) {
      System.out.println("UnusableEntryException in read()=" + e.getMessage());
    }
    return brief;
  }

  /**
   * Reads a Brief from the BriefSpace. <p>
   * Retrieves a copy of a brief described by a template. If there is no such brief
   * then the method blocks untill the described brief arrives.
   * @param  template  the brief-template of the brief expected to have arrived.
   * @return a copy of the brief matching the template, the brief is available in the system.
  */
  public static Brief read(Brief template) {
    return read(briefSpace,template);
  }

  ///////////////////////////////////////////////////////////////////////

  /**
   * Takes a Brief from a Space.<p>
   * Retrieves a brief described by a template. If there is no such brief
   * then the method blocks untill the described brief arrives.
   * @param   template  the brief-template of the brief expected to have arrived.
   * @param   space     the space to take the brief from
   * @return  the brief matching the template, the brief is no longer available in the system.
   */
  public static Brief take(JavaSpace space,Brief template) {
    Brief brief = null;
    try {
      brief = (Brief)space.take(template,null,Long.MAX_VALUE);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in take()=" + e.getMessage());
    }
    catch (InterruptedException e) {
      System.out.println("InterruptedException in take()=" + e.getMessage());
    }
    catch (TransactionException e) {
      System.out.println("TransactionException in take()=" + e.getMessage());
    }
    catch (UnusableEntryException e) {
      System.out.println("UnusableEntryException in take()=");
      System.out.println("  partialEntry=" + e.partialEntry.toString());
      System.out.println("  unusableFields=" + e.unusableFields.toString());
      System.out.println("  nestedExceptions=" + e.nestedExceptions.toString());
    }
    return brief;
  }
  /**
   * Takes a Brief from the BriefSpace.<p>
   * Retrieves a brief described by a template. If there is no such brief
   * then the method blocks untill the described brief arrives.
   * @param   template  the brief-template of the brief expected to have arrived.
   * @return  the brief matching the template, the brief is no longer available in the system.
   */
  public static Brief take(Brief template) {
    return take(briefSpace,template);
  }

  /////////////////////////////////////////////////////////////////////
  /**
   * Static Initializer.<p>
   * Reads system-property agileframes.briefspace.name.
   * Gets BriefSpace.
   * @see #getBriefSpace()
   */
  static {
    briefSpaceName = System.getProperty("agileframes.briefspace.name");
    if (briefSpaceName == null) { briefSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.briefspace.name=" + briefSpaceName);
    if (getBriefSpace() == null) { System.out.println("briefSpace not found"); }
  }
}