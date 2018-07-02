package net.agileframes.brief;

import net.jini.core.lookup.ServiceID;
import net.agileframes.core.brief.Signal;
import java.util.Hashtable;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.space.JavaSpace;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.brief.SignalOffice;

/**
Listens in the space for signals destined for objects registered here.
It takes such signals out of the space and gets the invocation going.
*/

public class SignalSpace implements RemoteEventListener, SignalOffice {

  protected static JavaSpace signalSpace = null;
  protected static String signalSpaceName = null;
  public static JavaSpace getSignalSpace() {
    if (signalSpace == null) {
      signalSpace = AgileSystem.getSpace(signalSpaceName); }
    return signalSpace;
  }

  protected SignalSpace() {}

  /////////////////////////////////////////////////////////

  public void notify(RemoteEvent re) {
    // re brief in Signal space with dstID==some serviceID
    // find the associated object
    // get the signal from the space
    // pass the object to the signal
  }

  /////////////////////////////////////////////////////////

  // is hashtable thread-safe?
  private static Hashtable registrations = new Hashtable();

  /**
  Register an object for signals. The system may invoke methods specified by
  signals on the object iff they arrive. The signal is discarded after invocation.
  If a signal arrives before te destination object is registered the signal is lost.
  */
  public static void register(Object object,ServiceID serviceID) {
    // listen for signals for dstID==serviceID
    registrations.put(serviceID,object);
  }

  /**
  Un-register an object for signals.
  */
  public static void unregister(Object object,ServiceID serviceID) {
    // cancel the signal notifications for this object.
    registrations.remove(object);
  }

  /**
  Send a signal to another object in the system. The system invoke the method
  specified by the signal on the destination object as soon as possible.
  @param signal to be sent, a copy is forwarded.
  */
  public static void writeSignal(Signal signal) {
    // throw the signal in the signal space iff you dont know the destination
  }

  static {
    signalSpaceName = System.getProperty("agileframes.signalspace.name");
    if (signalSpaceName == null) { signalSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.signalspace.name=" + signalSpaceName);
    if (getSignalSpace() == null) { System.out.println("signalSpace not found"); }
  }

}



