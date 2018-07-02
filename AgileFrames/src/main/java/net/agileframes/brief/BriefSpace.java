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
*/

public class BriefSpace implements BriefOffice {

  protected static JavaSpace briefSpace = null;
  protected static String briefSpaceName = null;
  public static JavaSpace getBriefSpace() {
    if (briefSpace == null) {
      briefSpace = AgileSystem.getSpace(briefSpaceName); }
    return briefSpace;
  }

  ////////////////////////////////////////////////////////////////////////

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

  public static void write(Brief brief) {
    write(briefSpace,brief);
  }

  ///////////////////////////////////////////////////////////////////////

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

  public static boolean isBrief(Brief template) {
    return isBrief(briefSpace,template);
  }

  //////////////////////////////////////////////////////////////////////////

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
  Retrieve a copy of a brief described by a template. If there is no such brief
  then the method blocks untill the described brief arrives.
  @param template of the brief expected to have arrived.
  @return a copy of the brief matching the template,
          the brief is available in the system.
  */
  public static Brief read(Brief template) {
    return read(briefSpace,template);
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  Retrieve a brief described by a template. If there is no such brief
  then the method blocks untill the described brief arrives.
  @param template of the brief expected to have arrived.
  @return the brief matching the template,
          the brief is no longer available in the system.
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

  public static Brief take(Brief template) {
    return take(briefSpace,template);
  }

  /////////////////////////////////////////////////////////////////////

  static {
    briefSpaceName = System.getProperty("agileframes.briefspace.name");
    if (briefSpaceName == null) { briefSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.briefspace.name=" + briefSpaceName);
    if (getBriefSpace() == null) { System.out.println("briefSpace not found"); }
  }

}


