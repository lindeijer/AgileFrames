package net.agileframes.server;
import net.agileframes.server.AgileSystemView;

/**
 * <b> AgileSystemRoot is an instance of AgileSystem needed for discovery of lookup services.</b>
 * <p>
 * Currently almost empty.
 * @author  D.G. Lindeijer
 * @version 0.1
 */
public class AgileSystemRoot extends AgileSystem {

  /** The AgileSystem-Frame. */
  protected static AgileSystemView agileSystemView = null;

  /**
   * Constructor.<p>
   * If this AgileSystem is visible, a new AgileSystemView will be created.
   * @see   AgileSystemView
   * @see   AgileSystem#isVisible
   * @param visible indicates if a frame is made for this AgileSystem.
   */
  public AgileSystemRoot(boolean visible) {
    if (visible) {
      agileSystemView = new AgileSystemView("AgileSystem for " + AgileSystem.getLoginbaseName());
    }
  }

  /**
   * Main method.<p>
   * Not implemented.
   * @param args  run-parameters: not used
   */
  public static void main(String[] args) {
  }


}




/*
extends AgileSystem implements
      DiscoveryListener, AgileSystemRemote {

  public AgileSystemRoot() {
    if (AgileSystem.root == null) {
      AgileSystem.root = this;
      try {
        // note that initial discovery is disabled

      }
      lookupDiscovery.addDiscoveryListener(this);
      //
      if (AgileSystem.loginbaseName != null) {
        AgileSystem.setLoginBase(loginbaseName);
        System.out.println("AgileSystemRoot() running for " + loginbaseName);
      } else {
        System.out.println("AgileSystemRoot() running anonymously");
      }
    }
  }

  ///////////////////////////////////////////////////////////////////

  public static void dispose() {
    AgileSystem.dispose();
  }


  //////////////// implementation of DiscoveryListener //////////////

  /**
  Only new ls are discoverd, renew by discarding first.
  Called by LookupDiscovery
  This method changes serviceRegistrars
  /
  public void discovered(DiscoveryEvent ev) {
    ServiceRegistrar[] newServiceRegistrars = ev.getRegistrars();
    for (int i=0;i<newServiceRegistrars.length;i++) {
      System.out.print("Newly discovered LS, ID=" + newServiceRegistrars[i].getServiceID());
    }
    if (AgileSystem.serviceRegistrars == null) {
      AgileSystem.serviceRegistrars = newServiceRegistrars;
    }
    //
    AgileSystem.lookupLocators = new LookupLocator[serviceRegistrars.length];
    for (int i=0;i<AgileSystem.serviceRegistrars.length;i++) {
      try {
        AgileSystem.lookupLocators[i] = serviceRegistrars[i].getLocator();
      } catch (RemoteException e) {}
    }
  }

  /**
  called by lookupDiscovery after I call discard() on it
  /
  public void discarded(DiscoveryEvent ev) {
  }

  ///////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////

  public ServiceID getProcessID(String name) { return null; }

  /////////////////////////////////////////////////////////////////////

  static {
    System.out.println("AgileSystemRoot loaded");
  }

  public static void main(String[] args) {
    // AgileSystem is loaded first
    new AgileSystemRoot();
  }

  //////////////////////////////////////////////////////////////////////

  /**
  Discovers JLS services on the local network hosting specified groups.
  The initial groups-specification is the empty-set and JLS discovery passivates.
  JLS discovery reactivates when lookupDiscovery.setGroups(groupNames) is called
  in startDiscovery.
  /
  protected static LookupDiscovery lookupDiscovery =
    new LookupDiscovery(new String[]{});

  /**
  Iff loginbaseName is null, then groupNames.length==0 and discovery passivates
  until setGroups is called.
  /
  private static void startDiscovery() {
    String[] groupNames = { loginbaseName };
    try {
      serviceRegistrars =  null;
      lookupDiscovery.setGroups(groupNames);
    } catch (IOException e) {
      System.out.println("Exception in startDiscovery(); " + e.getMessage());
    }
  }


  /** the serviceRegistrars[i] of JLS discoverd by lookupDiscovery /
  protected static ServiceRegistrar[] serviceRegistrars = null;

    /**
  LookupLocators of JLSs dicoverd on the local network.
  /
  protected static LookupLocator[] lookupLocators = null;

}

*/
