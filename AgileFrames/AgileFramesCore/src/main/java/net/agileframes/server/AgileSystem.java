package net.agileframes.server;

import java.rmi.*;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistration;

import net.jini.lookup.entry.*;
import net.jini.core.entry.*;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.*;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;
import java.rmi.server.*;
import net.jini.space.JavaSpace;
import java.io.IOException;
import net.jini.core.lease.Lease;
import java.util.Properties;

import net.agileframes.core.brief.Brief;
import net.agileframes.core.brief.Signal;
import net.agileframes.core.server.Server;
import net.agileframes.server.AgileSystemRoot;

import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.Transaction.Created;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.EventRegistration;

import java.util.*;


/**
 * <b>The Core of the local system. </b>
 * <p>
 * Must comply with java 1.3.<br>
 * On every platform that is part of AgileFrames, the class AgileSystem is
 * available. AgileSystem is loaded in the memory of a computer as soon as one
 * of the static methods on AgileFrames is called.<br>
 * AgileSystem contains some custom-methods that make it easier to use Jini and
 * RMI in AgileFrames.
 * <p>
 * To have a full understanding of these methods, at least an introduction to
 * Jini and Java RMI is required!.<br>
 * Please read the appropriate manuals first!<br>
 * Be VERY careful with changing the contents of this class!<p>
 * If you are an average AgileFrames-user, then probably you won't use
 * any of these methods. If you are an expert AgileFrames-user, you will need
 * to know about Jini and Java RMI anyway.
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */

public class AgileSystem {

  ////////////////////////////////////////////////////////////////////////

  /**
   * Will be null in a client context. The client may choose later.
   */
  protected static String agileframesLoginbaseName = null;

  /**
   * Returns loginbase-name of this AgileSystem.
   * @return loginbase-name of this AgileSystem
   */
  public static String getLoginbaseName() {
    return agileframesLoginbaseName;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Returns the current time.
   * @return milliseconds passed since 1970
   */
  public static long getTime() { return System.currentTimeMillis(); }

  ///////////////////////////////////////////////////////////////////////

  /**
   * The service registrar of JLS found by lookupLocator0
   */
  protected static ServiceRegistrar serviceRegistrar0 = null;

  /**
   * Returns the service-registrars.
   * @return the service-registrars of all known JLS
   */
  public static ServiceRegistrar[] getServiceRegistrars() {
    if (agilesystemMute.equals("TRUE")) { return null; }
    // the AgileServerRoot may have discovered local JLS !!
    if (serviceRegistrar0 == null) {
      try {
        serviceRegistrar0 = lookupLocator0.getRegistrar();
      }
      catch (java.io.IOException e) {
        System.out.println("IOException in AgileSystem.getServiceRegistrars = " + e.getMessage());
      }
      catch (java.lang.ClassNotFoundException e) {
        System.out.println("ClassNotFoundException in AgileSystem.getServiceRegistrars = " + e.getMessage());
      }
    }
    ServiceRegistrar[] serviceRegistrars = { serviceRegistrar0 };
    return serviceRegistrars;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Returns a transaction.<p>
   * Transactions are used to do atomic reserve operations in semaphores for instance.
   * @return a transaction
   */
  public static Transaction getTransaction() {
    if (transactionManager == null) {
      transactionManager = getTransactionManager();
    }
    Transaction.Created transactionCreated = null;
    try {
      transactionCreated = TransactionFactory.create(transactionManager,10*60*1000);
    } catch (Exception e) {
      System.out.println("Exception in AgileSystem.getTransaction(), to quit, message="+  e.getMessage());
      System.exit(2);
    }
    Lease lease = transactionCreated.lease;
    // ignore the lease for now, we take the chance of letting it extipre before we are ready.
    return transactionCreated.transaction;
  }
  /** The transaction manager manages transactions. */
  public static TransactionManager transactionManager = null;
  /**
   * Returns the system's transaction-manager.
   * Will download the manager if not yet available.
   * @return the transaction-manager.
   */
  public static TransactionManager getTransactionManager() {
    if (transactionManager == null) {
      downloadJiniTransactionManager();
    }
    return transactionManager;
  }
  /**
   * Downloads a transaction-manager.<p>
   * Needed if you work in a remote context. Will be one of the basic procedures
   * when this AgileSystem is started.
   * @return  transaction-manager
   */
  public static TransactionManager downloadJiniTransactionManager() {
    System.out.println("AgileSystem.downloadJiniTransactionManager");
    if (lookupLocator0 == null) {
      downloadJiniLookupService();
      // serviceRegistrar0 is set
    }
    try {
      transactionManager = (TransactionManager)serviceRegistrar0.lookup(
        new ServiceTemplate(null,new Class[]{TransactionManager.class},null)
      );
    } catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.downloadTransactionManager=" + e.getMessage());
      System.exit(1);
    }
    if (transactionManager == null) {
      if (agilesystemQuit != "TRUE") {
        System.out.println("AgileSystem should quit due to missing TM, but it does not");
      } else {
        System.out.println("AgileSystem.downloadTransactionManager quitting due to missing TM");
        System.exit(1);
      }
    } else {
      System.out.println("transactionManager = "+transactionManager.toString());
    }
    return transactionManager;
  }

  /////////////////////////////////////////////////////////////////////////

  /** LookupLocator of the ever-present agileframes JLS. */
  protected static LookupLocator lookupLocator0 = null;

  /**
   * Returns the system's lookup-locators.<p>
   * Lookup locators are used to find remote services.<br>
   * JLS = Jini Lookup Services.<br>
   * The current implementation only accepts (and returns) ONE lookup-locator.
   * @return the lookup-locators of all known JLS
   */
  public static LookupLocator[] getLookupLocators() {
    // the AgileServerRoot may have discovered local JLS !!
    LookupLocator[] lookupLocators = { lookupLocator0 };
    return lookupLocators;
  }

  ///////////////////////////////////////////////////////////////////////

  /**
   * The LeaseRenewalManager. Takes care of maintaining leases.<br>
   * Services need leases to stay available. Once the lease is not maintained,
   * the service is not available anymore.
   */
  protected static LeaseRenewalManager leaseRenewalManager =
    new LeaseRenewalManager();
    /**
     * Returns this system's LeaseRenewalManager.
     * @return the lease-renewal-manager
     */
  public static LeaseRenewalManager getLeaseRenewalManager() {
    return leaseRenewalManager;
  }

  /////////////////////////////////////////////////////////////////////
  /**
   * Returns the JavSpace.
   * @return the javaSpace
   */
  public static JavaSpace getSpace(String name) {
    ServiceTemplate spaceTemplate = new ServiceTemplate(null,
      new Class[]{JavaSpace.class},
      new Entry[]{new Name(name)}
    );
    JavaSpace theNamedSpace = null;
    try {
      // the service registrar hosts this loginbase group!
      theNamedSpace = (JavaSpace)getServiceRegistrars()[0].lookup(spaceTemplate);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.getSpace(): " + e.getMessage());
      System.out.println("RemoteException in AgileSystem.getSpace() ignored");
    }
    return theNamedSpace;
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  Registers with all known JLS and keeps lease renewed
  @param server         the server to which this service belongs
  @param serviceID      the id for the uploaded service, a unique serviceID is created iff this parameter is null
  @param service        the service to be registered
  @param attributeSets  possible additional attributes
  */
  public static ServiceID registerService(
    Server server,
    ServiceID serviceID,
    Object service,
    Entry[] attributeSets
  ) {
    if (AgileSystem.isMute) { return null; }
    if (server == null) { return null; }
    if (service == null) { return null; }
    if (serviceID == null) { serviceID = AgileSystem.getServiceID(); }

    JoinManager joinManager = null;
    LookupDiscoveryManager lookupDiscoveryManager = null;
    try {
      lookupDiscoveryManager = new LookupDiscoveryManager(
        new String[] { agileframesLoginbaseName },
        getLookupLocators(),
        null
      );
      joinManager = new JoinManager(
        service,
        attributeSets,
        serviceID,
        lookupDiscoveryManager,
        leaseRenewalManager
      );
// Deprecated implementation of Jini 1.0: com.sun.jini.lookup.JoinManager
//      joinManager = new JoinManager(
//        serviceID,service,attributeSets,
//        new String[] { agileframesLoginbaseName },
//        getLookupLocators(),
//        leaseRenewalManager
//      );
    }
    catch (IOException e) {
      System.out.println("IOException in AgileSystem.registerService()=" + e.getMessage());
      //System.exit(2);
    }
    Hashtable joinManagerMap = (Hashtable)server2service.get(server);
    joinManagerMap.put(serviceID,joinManager);
    return serviceID;
  }

  /**
   * Unregisters a service.<p>
   * Look up in the list, kill the lease, drop the registration.
   * @param  serviceID the serviceID of the service to be unregistered
   */
  public static void unregisterService(ServiceID serviceID) {
    Enumeration serverSet = server2service.keys();
    while (serverSet.hasMoreElements()) {
      Object server = serverSet.nextElement();
      Hashtable service2manager = (Hashtable)server2service.get(server);
      if (service2manager.containsKey(serviceID)) {
        JoinManager joinManager = (JoinManager)service2manager.get(serviceID);
        joinManager.terminate();
        service2manager.remove(serviceID);
        System.out.println("AgileSystem unregistered a Service");
        return;
      }
      else {
        System.out.println("no service for serviceID=" + serviceID.toString());
      }
    }
    System.out.println("No services registered");
  }
  /**
   * Registers service-listener.<p>
   * Downloading a service is only useful if there exists a (new) service to
   * download. The object that wants to download a service needs to know when
   * services that it is interested in are added, removed or modified in the
   * Lookup Service. For this purpose, it is possible to register a (remote)
   * object as a ServiceListener with the Lookup Service. A ServiceListener is
   * a RemoteEventListener and listens to RemoteEvents. <br>
   * A service-listener registers with AgileSystem with a service-template that
   * describes the services it wants to listen to and it must contain the method
   * notify(RemoteEvent) which will be called when the specified services have
   * been added, removed or modified. <br>
   * @param   serviceTmpl the template describing the service to listen to
   * @param   listener    the listener-object that will be notified when the service(s) appears, disappears or changes.
   * @return  the event-registration, needed when unregistering the listener
   */
  public static EventRegistration registerServiceListener(ServiceTemplate serviceTmpl, RemoteEventListener listener) {
    EventRegistration er = null;
    int transitions = ServiceRegistrar.TRANSITION_MATCH_MATCH +
                      ServiceRegistrar.TRANSITION_MATCH_NOMATCH +
                      ServiceRegistrar.TRANSITION_NOMATCH_MATCH;
    try {
      er = serviceRegistrar0.notify(serviceTmpl, transitions, listener, null, Lease.FOREVER );
      // Watch out: The LeaseRenewalManager of Jini 1.0 has a wrong implementation
      // of renewFor(). Therefore, be sure to use Jini 1.1 or later!
      leaseRenewalManager.renewFor(er.getLease(), Lease.FOREVER, null);
    } catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.registerServiceListener(): " + e.getMessage());
      e.printStackTrace();
      System.out.println("RemoteException in AgileSystem.registerServiceListener() ignored");
    } catch (IOException e) {
      System.out.println("IOException in AgileSystem.registerServiceListener(): " + e.getMessage());
      e.printStackTrace();
      System.out.println("IOException in AgileSystem.registerServiceListener() ignored");
    }
    System.out.println("AgileSystem registered a RemoteEventListener");
    return er;
  }

  /**
   * Unregisters a service-listener.<p>
   * Currently not implemented.
   * @param er  the event-registration obtained when registering the service
   */
  public static void unregisterServiceListener(EventRegistration er) {
//    try { leaseRenewalManager.cancel(er.getLease()); }
//    catch (Exception e) {
//      System.out.println("Exception in AgileSystem.unregisterServiceListener(): " + e.getMessage());
//      e.printStackTrace();
//      System.out.println("Exception in AgileSystem.unregisterServiceListener() ignored");
//    }
//    System.out.println("AgileSystem unregistered a RemoteEventListener");
  }

  /**
   * Returns a join-manager.
   * @param serviceID the service-id
   * @return  the join-manager
   */
  public static JoinManager getJoinManager(ServiceID serviceID) {
    Enumeration serverSet = server2service.keys();
    while(serverSet.hasMoreElements()) {
      Object server = serverSet.nextElement();
      Hashtable service2manager = (Hashtable)server2service.get(server);
      if (service2manager.containsKey(serviceID)) {
        return (JoinManager)service2manager.get(serviceID);
      }
    }
    System.out.println("no joinManager for serviceID=" + serviceID.toString());
    return null;
  }

  /////////////////////////////////////////////////////////////////////

  // server -> ( map serviceID -> joinManager )
  private static Hashtable server2service = new Hashtable();

  /**
   * Creates a map for remembering uploaded services.<p>
   * @param   Server  the server
   * @return  the service-id
   */
  public static ServiceID registerServer(Server server) {
    ServiceID serviceID = AgileSystem.getServiceID();
    server2service.put(server,new Hashtable());
    return serviceID;
  }
  /**
   * Registers a server.
   * @param server    the server to register
   * @param serviceID the server's service-id
   */
  public static void registerServer(Server server,ServiceID serviceID) {
    server2service.put(server,new Hashtable());
  }

  /**
   * Destroys all uploaded services.
   * @param server  the server of which all services will be destroyed.
   */
  public static void unregisterServer(Server server) {
    Hashtable service2manager = (Hashtable)server2service.get(server);
    Enumeration serviceIDSet = service2manager.keys();
    while(serviceIDSet.hasMoreElements()) {
      ServiceID serviceID = (ServiceID)serviceIDSet.nextElement();
      JoinManager joinManager = (JoinManager)service2manager.remove(serviceID);
      joinManager.terminate();
    }
    server2service.remove(server);
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  Calls dispose on all registered servers, the call back with unregisterserver.<p>
  Called when system is (forced) to quit.
  */
  public static void dispose() {
    Enumeration serverSet = server2service.keys();
    while(serverSet.hasMoreElements()) {
      ServerIB server = (ServerIB)serverSet.nextElement();
      server.dispose();   // server calls back with unregisterServer
    }
    System.out.println(System.currentTimeMillis()+": AgileSystem and servers disposed");
  }

  ////////////////////////////////////////////////////////////////////////

  /**
   * Looks up a single service.<p>
   * If no service matching the template is available, null will be returned.<br>
   * If more than one service is available, one of these will be returned.
   * @param serviceTemplate the template that the service we are looking for should match.
   */
  public static Object lookup(ServiceTemplate serviceTemplate) {
    Object object = null;
    try {
      object = serviceRegistrar0.lookup(serviceTemplate);
    }
    catch (Exception e) {
      System.out.println("AgileSystem " + agileframesLoginbaseName + " lookup object method failed");
    }
    return object;
  }

  /**
   * Looks up services.<p>
   * If no service matching the template is available, null will be returned.<br>
   * @param serviceTemplate the template that the services we are looking for should match.
   * @param max             the maximal number of services to return
   */
  public static ServiceItem[] lookup(ServiceTemplate serviceTemplate,int max) {
    ServiceMatches serviceMatches = null;
    try {
      serviceMatches = serviceRegistrar0.lookup(serviceTemplate,max);
    }
    catch (Exception e) {
      System.out.println("AgileSystem " + agileframesLoginbaseName + " lookup items method failed");
    }
    return serviceMatches.items;
  }

  ///////////////////////////////////////////////////////////////////////////
  /**
   * Creates and returns a unique service-id.
   * @return  the unique service-id
   */
  public static ServiceID getServiceID() {
    ServiceItem serviceItem = new ServiceItem(null,new Brief(),null);
    ServiceRegistration serviceRegistration = null;
    ServiceID serviceID = null;
    if (serviceRegistrar0 == null) {
      //System.out.println("serviceRegistrar0 in AgileSystem.getServiceID() is null, is AgileSystem mute? Anyway, returning a new ServiceID(Long.MAX_VALUE,Long.MIN_VALUE)");
      serviceID = new ServiceID(Long.MAX_VALUE,Long.MIN_VALUE);
      return serviceID;
    }
    if (agilesystemMute.equals("TRUE")) {
      serviceID = new ServiceID(Long.MAX_VALUE,Long.MIN_VALUE);
      return serviceID;
    }
    try {
      serviceRegistration = serviceRegistrar0.register(serviceItem,10000);
      serviceID = serviceRegistration.getServiceID();
      serviceRegistration.getLease().cancel();
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.getServiceID()=" + e.getMessage());
    }
    catch (UnknownLeaseException e) {
      System.out.println("UnknownLeaseException in AgileSystem.getServiceID()=" + e.getMessage());
    }
    return serviceID;
  }

  //////////////////////////////////////////////////////////////////////

  /**
  The discovery listener
  */
  protected static AgileSystemRoot agileSystemRoot = null;

  //////////////////////////////////////////////////////////////////////

  /** The Java security policy */
  public static String javaSecurityPolicy = null;
  /** The codebase of the Java RMI server */
  public static String javaRmiServerCodebase = null;
  /** The hostname of the Java RMI server */
  public static String javaRmiServerHostname = null;
  /** The hostname of the AgileFrames loginbase */
  public static String agileframesLoginbaseHostname = null;
  /** The path for reading and writing data-files used in AgileFrames */
  public static String agileframesDataPath = null;
  /** A string with value "FALSE" or "TRUE" indicating whether or not this AgileSystem
   *  should run stand-alone.
   *  @see  #isMute */
  public static String agilesystemMute = null;
  /** A string with value "FALSE" or "TRUE" indicating whether or not a frame for
   *  this AgileSystem will be made. <p> Creating a frame, means loading all Java's
   *  AWT classes etc. which can be bad for the performance when located on a small
   *  (read: embedded) platform. If running on a normal computer, advised to be TRUE.
   *  @see  #isVisible */
  public static String agilesystemVisible = null;
  /** A string with value "FALSE" or "TRUE" indicating whether or not this AgileSystem
   *  should quit if some of the managers cannot be found.
   *  @see  #isQuit */
  public static String agilesystemQuit = null;
  /** Indicating if a frame for AgileSystem will be made.<p>
   *  @see  AgileSystemView
   *  @see  #agilesystemVisible */
  public static boolean isVisible = true;
  /** Indicating whether or not this AgileSystem should operate stand-alone.<p>
   *  If TRUE, no managers will be looked up.
   *  @see  #agilesystemMute */
  public static boolean isMute = true;
  /** Indicating whether or not this AgileSystem should quit if some of the managers cannot be found. <p>
   *  @see  #agilesystemQuit   */
  public static boolean isQuit = true;

  /** The system-properties. These are the run-parameters. */
  public static Properties systemProperties = System.getProperties();

  private static void getAndSetJVMSystemProperties() {
    javaSecurityPolicy = systemProperties.getProperty("java.security.policy","null");
    //properties.put("java.security.policy",javaSecurityPolicy);
    System.out.println("java.security.policy=" + javaSecurityPolicy);
    //
    javaRmiServerCodebase = systemProperties.getProperty("java.rmi.server.codebase","http://www.agileframes.net/codebase/");
    // properties.put("java.rmi.server.codebase",javaRmiServerCodebase);
    System.out.println("java.rmi.server.codebase=" + javaRmiServerCodebase);
    //
    //javaRmiServerHostname = properties.getProperty("java.rmi.server.hostname","www.agileframes.net");
    //properties.put("java.rmi.server.hostname",javaRmiServerHostname);
    //System.out.println("java.rmi.server.hostname=" + javaRmiServerHostname);
    //
    agileframesLoginbaseName = systemProperties.getProperty("agileframes.loginbase.name","public");
    //properties.put("agileframes.loginbase.name",agileframesLoginbaseName);
    System.out.println("agileframes.loginbase.name=" + agileframesLoginbaseName);
    //
    agileframesLoginbaseHostname = systemProperties.getProperty("agileframes.loginbase.hostname","www.agileframes.net");
    //properties.put("agileframes.loginbase.host",agileframesLoginbaseHostname);
    System.out.println("agileframes.loginbase.hostname=" + agileframesLoginbaseHostname);
    //
    agileframesDataPath = systemProperties.getProperty("agileframes.datapath","");
    //properties.put("agileframes.loginbase.host",agileframesLoginbaseHostname);
    System.out.println("agileframes.datapath=" + agileframesDataPath);
    //
    agilesystemVisible = systemProperties.getProperty("agilesystem.visible","TRUE");
    //properties.put("agilesystem.visible",agilesystemVisible);
    System.out.println("agilesystem.visible=" + agilesystemVisible);
    if (agilesystemVisible.equals("TRUE")) { isVisible = true; }
    else if (agilesystemVisible.equals("FALSE")) { isVisible = false; }
    else System.out.println("agileframes.loginbase.name must be TRUE or FALSE");
    //
    agilesystemMute = systemProperties.getProperty("agilesystem.mute","FALSE");
    //properties.put("agilesystem.mute",agilesystemMute);
    System.out.println("agilesystem.mute=" + agilesystemMute);
    if (agilesystemMute.equals("TRUE")) { isMute = true; }
    else if (agilesystemMute.equals("FALSE")) { isMute = false; }
    else System.out.println("agilesystem.mute must be TRUE or FALSE");
    //
    agilesystemQuit = systemProperties.getProperty("agilesystem.quit","TRUE");
    //properties.put("agilesystem.quit",agilesystemQuit);
    System.out.println("agilesystem.quit=" + agilesystemQuit);
    if (agilesystemQuit.equals("TRUE")) { isQuit = true; }
    else if (agilesystemQuit.equals("FALSE")) { isQuit = false; }
    else System.out.println("agilesystem.quit must be TRUE or FALSE");
    //
    //System.setProperties(properties);
  }

  /**
   * Downloads Jini Lookup Service (JLS).<p>
   * Actually only gets and sets the lookup-locator and registrar (both on loginbase).
   * @see #agileframesLoginbaseHostname
   * @see #lookupLocator0
   * @see #serviceRegistrar0
   */
  public static void downloadJiniLookupService() {
    try {
      System.out.println("searching for JLS on " + agileframesLoginbaseHostname);
      lookupLocator0 = new LookupLocator("jini://" + agileframesLoginbaseHostname);
      System.out.println("lookupLocator0 created (this does not mean the corresponding JLS has also been found)");
      serviceRegistrar0 = lookupLocator0.getRegistrar();
    }
    catch (java.net.MalformedURLException e) {
      System.out.println("MalformedURLException is AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    catch (java.lang.ClassNotFoundException e) {
      System.out.println("ClassNotFoundException in AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    catch (java.io.IOException e) {
      System.out.println("IOException in AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    catch (Exception e) {
      System.out.println("Exception in AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    if (serviceRegistrar0 == null) {
      System.out.println("serviceRegistrar0 is null, is the JLS on " + agileframesLoginbaseHostname + " down?");
      if (agilesystemQuit != "TRUE") {
        System.out.println("AgileSystem should quit, but it does not");
      }
      else {
        System.out.println("AgileSystem quitting due to missing JLS");
        System.exit(1);
      }
    }
    System.out.println("serviceRegistrar0 located: " + lookupLocator0.toString());
  }

  /**
   * Downloads the Jini Spaces.<p>
   * The BriefSpace, SignalSpace, MoveSpace and JobSpace.
   * @see  net.agileframes.brief.BriefSpace#getBriefSpace()
   * @see  net.agileframes.brief.SignalSpace#getSignalSpace()
   * @see  net.agileframes.brief.MoveSpace#getMoveSpace()
   * @see  net.agileframes.brief.JobSpace#getJobSpace()
   */
  public static void downloadJiniSpaces() {
    net.agileframes.brief.BriefSpace.getBriefSpace();
    net.agileframes.brief.SignalSpace.getSignalSpace();
    net.agileframes.brief.MoveSpace.getMoveSpace();
    net.agileframes.brief.JobSpace.getJobSpace();
  }

  /**
   * The static initializer.<p>
   * Does the following:<ul>
   * <li> Gets and Sets the Java Virtual Machine (JVM) System Properties
   * <li> Downloads Jini Lookup Service
   * <li> Downloads Jini Spaces
   * <li> Downloads Jini Transaction Manager
   * <li> Creates AgileSystemRoot
   * </ul>
   * @see #downloadJiniLookupService()
   * @see #downloadJiniSpaces()
   * @see #downloadJiniTransactionManager()
   * @see AgileSystemRoot
   */
  static {
    getAndSetJVMSystemProperties();
    //
    if (javaSecurityPolicy.equals("null")) {
      System.out.println("RMISecurityManager not set because there is no security policy");
    } else {
      if (true) {
        System.setSecurityManager(new RMISecurityManager());
        System.out.println("RMISecurityManager set");
      } else System.out.println("RMISecurityManager should be set, but it is not. JVM must use local classloading");
    }
    //
    if (agilesystemMute.equals("FALSE")) {
      System.out.println("AgileSystem is acute and reaching out to the world");
      downloadJiniLookupService();
      downloadJiniSpaces();
      downloadJiniTransactionManager();
    } else {
      System.out.println("AgileSystem is a mute, no jini lookup service, no spaces");
    }
    //
    if (agilesystemVisible.equals("TRUE")) {
      AgileSystem.agileSystemRoot = new AgileSystemRoot(true);
    } else {
      AgileSystem.agileSystemRoot = new AgileSystemRoot(false);
    }
    System.out.println("AgileSystem loaded");
  }

  //////////////////////////////////////////////////////////////////////////
  /**
   * Main method.<p>
   * If you want to run AgileSystem stand-alone.
   * Only to be used to test AgileSystem.
   * @param args  the run-parameters: will not be used
   */
  public static void main(String[] args) {
    Transaction transaction = getTransaction();
    System.out.println("transaction=" + transaction.toString());
    Properties properties = System.getProperties();
    System.out.println("ALL SYSTEM PROPERTIES ARE: "+ properties.toString());
  }


}
