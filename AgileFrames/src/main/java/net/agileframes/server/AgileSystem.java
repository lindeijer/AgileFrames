package net.agileframes.server;

//import net.agileframes.activation.*;

//import java.rmi.activation.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import net.agileframes.core.brief.Brief;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
//import java.util.Hashtable;
//import java.util.Collection;
//import java.util.Iterator;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.lease.LeaseRenewalManager; //com.sun.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;  // com.sun.jini.lookup.*;
import net.jini.lookup.entry.Name;
//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.JFrame;
import net.jini.space.JavaSpace;


/**
The Core of the local system. Must comply with java 1.1
*/

public class AgileSystem {

  public static final DecimalFormat dotTwo = new DecimalFormat("0.00");
  public static final  DecimalFormat dotThree = new DecimalFormat("0.000");
  public static final  DecimalFormat lenFive   = new DecimalFormat("00000");

  ////////////////////////////////////////////////////////////////////////

  /**
  Will be null in a client context. The client may choose later.
  */
  protected static String agileframesLoginbaseName = null;

  /**
  @return loginbase-name of this AgileSystem
  */
  public static String getLoginbaseName() {
    return agileframesLoginbaseName;
  }

  ////////////////////////////////////////////////////////////////////////

  // protected static String agileframesLoginbaseHostname = null;

  /////////////////////////////////////////////////////////////////////////

  /**
  @return milliseconds passed since 1970
  */
  public static long getTime() { return System.currentTimeMillis(); }

  ///////////////////////////////////////////////////////////////////////

  /**
  the service registrar of JLS found by lookupLocator0
  */
  protected static ServiceRegistrar serviceRegistrar0 = null;

  /**
  @return the service-registrars of all known JLS
  */
  public static ServiceRegistrar[] getServiceRegistrars() {
    if (agilesystemMute == "TRUE") { return null; }
    // the AgileServerRoot may have discovered local JLS !!
    if (serviceRegistrar0 == null) {
/*      try {
        serviceRegistrar0 = lookupLocator0.getRegistrar();
      }
      catch (java.io.IOException e) {
        System.out.println("IOException in AgileSystem.getServiceRegistrars = " + e.getMessage());
      }
      catch (java.lang.ClassNotFoundException e) {
        System.out.println("ClassNotFoundException in AgileSystem.getServiceRegistrars = " + e.getMessage());
      }*/
    }
    ServiceRegistrar[] serviceRegistrars = { serviceRegistrar0 };
    return serviceRegistrars;
  }

  /////////////////////////////////////////////////////////////////////////

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

  public static TransactionManager transactionManager = null;

  public static TransactionManager getTransactionManager() {
    if (transactionManager == null) {
      downloadJiniTransactionManager();
    }
    return transactionManager;
  }

  public static TransactionManager downloadJiniTransactionManager() {
    System.out.println("AgileSystem.downloadJiniTransactionManager");
    if (lookupLocator0 == null) {
      downloadJiniLookupService();
      // serviceRegistrar0 is set
    }
    /*try {
      transactionManager = (TransactionManager)serviceRegistrar0.lookup(
        new ServiceTemplate(null,new Class[]{TransactionManager.class},null)
      );
    } catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.downloadTransactionManager=" + e.getMessage());
      System.exit(1);
    } */
    if (transactionManager == null) {
      System.out.println("AgileSystem.downloadTransactionManager quitting due to missing TM");
      System.exit(1);
    }
    return transactionManager;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
  LookupLocator of the ever-present agileframes JLS.
  */
  protected static LookupLocator lookupLocator0 = null;

  /**
  @return the lookup-locators of all known JLS
  */
  public static LookupLocator[] getLookupLocators() {
    // the AgileServerRoot may have discovered local JLS !!
    LookupLocator[] lookupLocators = { lookupLocator0 };
    return lookupLocators;
  }

  ///////////////////////////////////////////////////////////////////////

  protected static LeaseRenewalManager leaseRenewalManager =
    new LeaseRenewalManager();

  public static LeaseRenewalManager getLeaseRenewalManager() {
    return leaseRenewalManager;
  }

  /////////////////////////////////////////////////////////////////////

  public static JavaSpace getSpace(String name) {
    ServiceTemplate spaceTemplate = new ServiceTemplate(null,
      new Class[]{JavaSpace.class},
      new Entry[]{new Name(name)}
    );
    JavaSpace theNamedSpace = null;
    /*try {
      // the service registrar hosts this loginbase group!
      theNamedSpace = (JavaSpace)getServiceRegistrars()[0].lookup(spaceTemplate);
    }
    catch (RemoteException e) {
      System.out.println("RemoteException in AgileSystem.getSpace(): " + e.getMessage());
      System.out.println("RemoteException in AgileSystem.getSpace() ignored");
    } */
    return theNamedSpace;
  }

  ///////////////////////////////////////////////////////////////////////

  /**
  register with all known JLS & keep lease renewed
  @param server
  @param serviceID for the uploaded service, a unique servideID is created iff this parameter is null
  @param service
  @param attributeSets
  */
  public static ServiceID registerService(
    ServerImplBase server,
    ServiceID serviceID,
    Object service,
    Entry[] attributeSets
  ) { /*
    if (server == null) { return null; }
    if (service == null) { return null; }
    if (serviceID == null) { serviceID = AgileSystem.getServiceID(); }
    JoinManager joinManager = null;
    try {
      joinManager = new JoinManager(
        serviceID,service,attributeSets,
        new String[] { agileframesLoginbaseName },
        getLookupLocators(),
        leaseRenewalManager
      );
    }
    catch (IOException e) {
      System.out.println("IOException in AgileSystem.registerService()=" + e.getMessage());
      System.exit(2);
    }
    Hashtable joinManagerMap = (Hashtable)server2service.get(server);
    joinManagerMap.put(serviceID,joinManager);    */
    return serviceID;
  }

  /**
  look up in the list, kill the lease, drop the registration
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
        return;
      }
      else {
        System.out.println("no service for serviceID=" + serviceID.toString());
      }
    }
  }


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

  // creates a map for remembering uploaded services.
  public static ServiceID registerServer(ServerImplBase server) {
    ServiceID serviceID = AgileSystem.getServiceID();
    server2service.put(server,new Hashtable());
    return serviceID;
  }

  public static void registerServer(ServerImplBase server,ServiceID serviceID) {
    server2service.put(server,new Hashtable());
  }

  // destroy all uploaded services.
  public static void unregisterServer(ServerImplBase server) {
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
  Calls dispose on all registered servers, the call bach with unregisterserver
  */
  public static void dispose() {
    Enumeration serverSet = server2service.keys();
    while(serverSet.hasMoreElements()) {
      ServerImplBase server = (ServerImplBase)serverSet.nextElement();
      server.dispose();   // server calls back with unregisterServer
    }
    System.out.println("AgileSystem and servers disposed");
  }

  ////////////////////////////////////////////////////////////////////////

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

  public static ServiceID getServiceID() {
    ServiceItem serviceItem = new ServiceItem(null,new Brief(),null);
    ServiceRegistration serviceRegistration = null;
    ServiceID serviceID = null;
    if (serviceRegistrar0 == null) {
      //System.out.println("serviceRegistrar0 in AgileSystem.getServiceID() is null, is AgileSystem mute? Anyway, returning a new ServiceID(Long.MAX_VALUE,Long.MIN_VALUE)");
      serviceID = new ServiceID(Long.MAX_VALUE,Long.MIN_VALUE);
      return serviceID;
    }
    if (agilesystemMute == "TRUE") {
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

  public static String javaSecurityPolicy = null;
  public static String javaRmiServerCodebase = null;
  public static String javaRmiServerHostname = null;
  public static String agileframesLoginbaseHostname = null;
  public static String agilesystemVisible = null;
  public static String agilesystemMute = null;
  public static String agilesystemQuit = null;
  public static boolean isVisible = true;
  public static boolean isMute = true;
  public static boolean isQuit = true;

  public static Properties systemProperties = System.getProperties();

  private static void getAndSetJVMSystemProperties() {
    javaSecurityPolicy = systemProperties.getProperty("java.security.policy","null");
    // properties.put("java.security.policy",javaSecurityPolicy);
    System.out.println("java.security.policy=" + javaSecurityPolicy);
    //
    // javaRmiServerCodebase = systemProperties.getProperty("java.rmi.server.codebase","http://www.agileframes.net/codebase/");
    // properties.put("java.rmi.server.codebase",javaRmiServerCodebase);
    // System.out.println("java.rmi.server.codebase=" + javaRmiServerCodebase);
    //
    javaRmiServerHostname = systemProperties.getProperty("java.rmi.server.hostname","huh");
    // properties.put("java.rmi.server.hostname",javaRmiServerHostname);
    System.out.println("java.rmi.server.hostname=" + javaRmiServerHostname);
    //
    agileframesLoginbaseName = systemProperties.getProperty("agileframes.loginbase.name","public");
    //properties.put("agileframes.loginbase.name",agileframesLoginbaseName);
    System.out.println("agileframes.loginbase.name=" + agileframesLoginbaseName);
    //
    agileframesLoginbaseHostname = systemProperties.getProperty("agileframes.loginbase.hostname","www.agileframes.net");
    //properties.put("agileframes.loginbase.host",agileframesLoginbaseHostname);
    System.out.println("agileframes.loginbase.hostname=" + agileframesLoginbaseHostname);
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

  public static void downloadJiniLookupService() {
    try {
      lookupLocator0 = new LookupLocator("jini://" + agileframesLoginbaseHostname);
      System.out.println("lookupLocator0 created (this does not mean the corresponding JLS has also been found)");
      serviceRegistrar0 = lookupLocator0.getRegistrar();
    }
    catch (java.net.MalformedURLException e) {
      System.out.println("MalformedURLException is AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    catch (java.lang.ClassNotFoundException e) {
      System.out.println("ClassNotFoundException is AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
    }
    catch (java.io.IOException e) {
      System.out.println("IOException in AgileSystem.getJiniLookupServiceAndSpaces = " + e.getMessage());
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

  public static void downloadJiniSpaces() {
    net.agileframes.brief.BriefSpace.getBriefSpace();
    net.agileframes.brief.SignalSpace.getSignalSpace();
    net.agileframes.brief.MoveSpace.getMoveSpace();
    net.agileframes.brief.JobSpace.getJobSpace();
  }

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

  public static void main(String[] args) {
    //Transaction transaction = getTransaction();
    //System.out.println("transaction=" + transaction.toString());
    //Properties properties = System.getProperties();
    //System.out.println("ALL SYSTEM PROPERTIES ARE: "+ properties.toString());
  }


}
