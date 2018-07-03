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
 * <b>The implementation of MoveSpace.</b>
 * <p>
 * Listens in the space for signals destined for objects registered here.
 * It takes such signals out of the space and gets the invocation going.
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class SignalSpace implements RemoteEventListener, SignalOffice {
  /** The JavaSpace SignalSpace */
  protected static JavaSpace signalSpace = null;
  /** The name of this SignalSpace */
  protected static String signalSpaceName = null;
  /**
   * Returns a reference to the SignalSpace.<p>
   * A SignalSpace will be created when it doesnot exist already.
   * @see     net.agileframes.server.AgileSystem#getSpace(String)
   * @return  the SignalSpace
   */
  public static JavaSpace getSignalSpace() {
    if (signalSpace == null) {
      signalSpace = AgileSystem.getSpace(signalSpaceName); }
    return signalSpace;
  }
  /** Empty Constructor. Not used. */
  protected SignalSpace() {}

  /////////////////////////////////////////////////////////
  /** Not implemented. Called by JLS. */
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
   * Registers an object for signals.<p>
   * The system may invoke methods specified by
   * signals on the object iff they arrive. The signal is discarded after invocation.
   * If a signal arrives before te destination object is registered the signal is lost.
   * @param object    the object to register
   * @param serviceID the unique service-id of the object
   */
  public static void register(Object object,ServiceID serviceID) {
    // listen for signals for dstID==serviceID
    registrations.put(serviceID,object);
  }

  /**
   * Un-register an object for signals.<p>
   * @param object    the object to unregister
   * @param serviceID the unique service-id of the object
   */
  public static void unregister(Object object,ServiceID serviceID) {
    // cancel the signal notifications for this object.
    registrations.remove(object);
  }

  /**
   * Sends a signal to another object in the system. <p>
   * The system invoke the method specified by the signal on the destination
   * object as soon as possible.<br>
   * Not implemented.
   * @param signal the signal to be sent, a copy is forwarded.
   */
  public static void writeSignal(Signal signal) {
    // throw the signal in the signal space iff you dont know the destination
  }


  /**
   * Static Initializer.<p>
   * Reads system-property agileframes.signalspace.name.
   * Gets SignalSpace.
   * @see #getSignalSpace()
   */
  static {
    signalSpaceName = System.getProperty("agileframes.signalspace.name");
    if (signalSpaceName == null) { signalSpaceName = "JavaSpaces"; }
    System.out.println("agileframes.signalspace.name=" + signalSpaceName);
    if (getSignalSpace() == null) { System.out.println("signalSpace not found"); }
  }

}



