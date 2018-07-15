package net.agileframes.forces;
import net.agileframes.server.AgileSystem;
import net.agileframes.server.ServerIB;
import net.agileframes.core.forces.MachineRemote;
import net.agileframes.forces.mfd.*;
import net.agileframes.core.server.Server;
import java.rmi.RemoteException;
import net.jini.core.lookup.ServiceID;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.Manoeuvre;
import net.agileframes.core.vr.BodyRemote;
import net.agileframes.core.vr.Body;
import net.agileframes.core.vr.Avatar;
/**
 * <b>Basic Implementation of MachineRemote Interface.</b>
 * <p>
 * It is recommended to extend this class if you want to implement your own
 * functionality.
 * @see net.agileframes.core.forces.MachineRemote
 * @author  D.G.Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class MachineIB extends ServerIB implements MachineRemote {
  //------------------------------ Attributes -----------------------------------------------------
  /** A reference to the manoeuvreDriver-object of this MFD-Thread. */
  protected ManoeuvreDriver manoeuvreDriver = null;
  /** A reference to the stateFinder-object of this MFD-Thread. */
  protected StateFinder stateFinder = null;
  /** A reference to the instructor-object of this MFD-Thread. */
  protected Instructor instructor = null;
  /** A reference to the physicalDriver-object of this MFD-Thread. */
  protected PhysicalDriver physicalDriver = null;
  /** A reference to the mechatronics-object. */
  protected Mechatronics mechatronics = null;
  /** A reference to the MFD-Thread that will be created in this object. */
  protected Thread mfdThread = null;

  //------------------------------ Constructors ---------------------------------------------------
  /** Empty Constructor, should not be used. */
  public MachineIB() throws RemoteException {}
  /**
   * Basic Constructor, only calls super.<p>
   * If the ServiceID in this constructor is null, then a ServiceID will be
   * created in the super.
   * @see   net.agileframes.server.ServerIB#ServerIB(String, ServiceID)
   * @param name      the name of this Machine
   * @param serviceID the unique ServiceID of this machine
   */
  public MachineIB(String name, ServiceID serviceID) throws RemoteException {
    super(name,serviceID);// machineID is set to serviceID or to a new ServiceID iff it was null
  }
  // overrides getName in ServerIB
  public String getName() { return name; }
  //------------------------------ Methods --------------------------------------------------------
  /**
   * Creates and starts the MFD-Thread.<p>
   * The MFD-Thread will run in ManoeuvreDriver.
   * The name of the MFD-Thread will be "mfdThread@<i>machineName</i>"
   * @see ManoeuvreDriverIB#run()
   */
  public void start() {
    mfdThread = new Thread("mfdThread@"+getName()) {
      public void run() {  manoeuvreDriver.run();  }
    };
    mfdThread.start();
  }
  /**
   * Returns a reference to the ManoeuvreDriver MFD-Object.<p>
   * Notice that the manoeuvre-object is not serializable, so this method cannot
   * be called in a remote context.
   * @return   the manoeuvreDriver-object
   */
  public ManoeuvreDriver getManoeuvreDriver(){ return manoeuvreDriver; }
  //------------------------------ Methods inherited from Machine ---------------------------------
  public int getMachineNumber() throws RemoteException { return -1; }// must be overloaded
  public void prepare(Manoeuvre m) { manoeuvreDriver.prepare(m); }
  public void begin(Manoeuvre m) { manoeuvreDriver.begin(m); }

  //------------------------------ Methods inherited from Body ------------------------------------
  public void addAvatar(Avatar avatar) throws RemoteException {}
  public void removeAvatar(Avatar avatar) throws RemoteException {}
  public FuSpace getState() throws RemoteException { 
	  return stateFinder.getObservedState();
  }
  public int getGeometryID() throws RemoteException { return 0; }
  public int getAppearanceID() throws RemoteException { return 0; }
  public FuSpace removeChild(BodyRemote child) throws RemoteException { return null; }
  public Body.StateAndAvatar addChild(BodyRemote child, FuSpace state) throws RemoteException { return null; }
  public void setParent(BodyRemote parent) throws RemoteException {}

  //----------------------------- Properties -----------------------
  /** Not implemented. */
  public MachineRemote.Properties getProperties() throws RemoteException {
    System.out.println("MachineIB.getProperties() called but not implemented.");
    return null;
  }
  /**
   * To be called when the Machine and MFD-Thread are to die. <p>
   * Calls super.dispose.
   * Shuts down the ManoeuvreDriver.
   * @see   ManoeuvreDriverIB#shutDown()
   */
  public void dispose() {
    super.dispose();
    this.manoeuvreDriver.shutDown();
    System.out.println(System.currentTimeMillis()+": Machine disposed.");
  }
  /**
   * Checks if the two instances are equal.<p>
   * @param   obj the object to be checked
   * @return  <code><b>true </code></b>  iff the two objects are equal<br>
   *          <code><b>false</code></b>  iff the two objects are not equal
   */
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof MachineRemote)) return false;
    MachineRemote machine = (MachineRemote)obj;
    try { return this.serviceID.equals(machine.getServiceID()); }
    catch (RemoteException e) { e.printStackTrace(); }
    return false;
  }

  //--------------------------- Inner-class ---------------------
  /**
   * <b>General Useful Implementation of MachineRemote.Properties Interface.</b>
   * <p>
   * It is recommended to extend this class if you want to implement your own
   * functionality.
   * <p>
   * This class provides the user with the possibility to create its own
   * properties. In this basic implementation, the properties can be of 4
   * different kinds: double, boolean, integer, percent. To be generic, these
   * properties are all expressed in Doubles:<ul>
   * <li> <code>DOUBLE </code>: as usual
   * <li> <code>BOOLEAN</code>: 0.0 = false, other = true
   * <li> <code>INTEGER</code>: type-cast to double
   * <li> <code>PERCENT</code>: 0.0 = 0% --> 1.0 = 100%
   * </ul> <br>
   * In addition to this, two generic properties are available: name and
   * activeTime.
   * @see Property
   * @see net.agileframes.core.forces.MachineRemote.Properties
   * @author  H.J. Wierenga
   * @version 0.1
   */
  public class MachineProperties implements MachineRemote.Properties, java.io.Serializable {
    //--- Constants ---
    /** Constant indicating a the property is a double. */
    public static final int DOUBLE = 1;
    /** Constant indicating a the property is a boolean. */
    public static final int BOOLEAN = 2;
    /** Constant indicating a the property is a integer. */
    public static final int INTEGER = 3;
    /** Constant indicating a the property is a percentage. */
    public static final int PERCENT = 4;
    //--- Attributes ---
    /** Generic property: name of the Machine. */
    public String name;
    /** Generic property: time the Machine is active. */
    public long activeTime;
    /** Array with specific Properties. */
    public Property[] specProps;
    //--- Constructor ---
    /** Empty Constructor. */
    public MachineProperties() {}
    //--- Property ---
  /**
   * <b>The Property Class.</b>
   * <p>
   * A property has 3 fields:<ul>
   * <li> Description
   * <li> Value
   * <li> Type: one of the Property-Constants:
   * {@link  net.agileframes.forces.MachineIB.MachineProperties#DOUBLE  DOUBLE},
   * {@link  net.agileframes.forces.MachineIB.MachineProperties#BOOLEAN BOOLEAN},
   * {@link  net.agileframes.forces.MachineIB.MachineProperties#INTEGER INTEGER} or
   * {@link  net.agileframes.forces.MachineIB.MachineProperties#PERCENT PERCENT}.
   * </ul>
   * @see     net.agileframes.forces.MachineIB.MachineProperties
   * @author  H.J. Wierenga
   * @version 0.1
   */
    public class Property implements java.io.Serializable {
      /** Description of this property. */
      public String description;
      /** Value of this property. */
      public double value;
      /** Type of this property. */
      public int type;
      /** Empty Constructor/ */
      public Property() {}
    }
  }
}
