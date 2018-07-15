package net.agileframes.traces;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.forces.MachineRemote;
import net.agileframes.core.services.Job;
import net.agileframes.core.traces.Action;
import net.agileframes.core.traces.Actor;
import net.agileframes.core.traces.LogisticPosition;
import net.agileframes.core.traces.NotTrustedException;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.traces.SceneAction;
import net.agileframes.core.traces.Ticket;
import net.agileframes.server.AgileSystem;
import net.agileframes.server.ServerIB;
import net.agileframes.services.ActionJob;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceTemplate;
/**
 * <b>Implementation of Actor.</b>
 * <p>
 * This implementation has two threads: <br>
 * 1) The job-interpreter-thread: takes care of receiving jobs and translating them into actions<br>
 * 2) The actor-run-thread: takes care of executing scene-actions.<br>
 * @see     net.agileframes.core.traces.Actor
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class ActorIB extends ServerIB implements Actor, Runnable {
  //------------------------------- Attributes -----------------------------
  private MachineRemote machine = null;
  private Action currentSceneAction = null;
  private SceneAction nextSceneAction = null;
  private Job currentJob = null;
  private Job nextJob = null;
  private boolean shutDown = false;
  private Scene scene = null;
  /**
   * Parameter to be used to debug this class.<p>
   * Set to <b><code>true</b></code> to receive print-statements during execution.
   * Default is <b><code>false</b></code>.
   */
  public static boolean DEBUG = true;
  private Ticket exitTicket = null;
  private LogisticPosition lp = null;
  //------------------------------- Constructors ---------------------------
  /**
   * Default Constructor.<p>
   * Calls super. Creates and starts the job-interpreter-thread.
   * Runs another thread in this object's run-method.
   * @see   #jobInterpreterCycle()
   * @see   #run()
   * @see   net.agileframes.server.ServerIB#ServerIB(String,ServiceID)
   * @param actorID the unique service-id of this actor, may be null: then one will be created
   * @param machine the machine which belongs to this actor, may or may not be remote
   * @param name    the name of this actor
   */
  public ActorIB(ServiceID actorID, MachineRemote machine, String name) throws RemoteException {
    super(name, actorID);
    this.machine = machine;
    Thread jobInterpreterThread = new Thread("JobInterpreterThread@"+name) {
      public void run() { jobInterpreterCycle(); }
    };
    jobInterpreterThread.start();
    this.run();
  }
  //------------------------------- Methods --------------------------------
  /**
   * Accepts jobs.<p>
   * Currently not used, because job-interpreter-thread takes care of this.
   * @see     #jobInterpreterCycle()
   * @param   basicServerID the service id of the job's Server: not used
   * @param   job           the job to be accepted
   * @return  <code><b>true</code></b>  iff the job is accepted<br>
   *          <code><b>false</code></b>  iff the job is not accepted
   */
  public boolean acceptJob(ServiceID basicServerID, Job job) throws RemoteException, NotTrustedException {
    if (nextJob != null) { return false; }
    if (currentJob == null) { currentJob = job; }
    else { nextJob = job; }
    if (DEBUG) System.out.println("*D* ActorIB: "+name+" Accepted a job!");
    synchronized (this) { this.notifyAll(); }
    return true;
  }

  private void downloadScene() {//Navigator must be called SceneProxy
    Class[] sceneClass = { net.agileframes.core.traces.Scene.class };
    ServiceTemplate sceneTemplate = new ServiceTemplate(null, sceneClass, null);
    scene = (Scene)AgileSystem.lookup(sceneTemplate);// any scene
    if (scene == null) {
      System.out.println("ActorIB.downloadScene: Actor cannot find Scene. ");
      System.out.println("ActorIB.downloadScene: The Scene should have been made available for the JLS.");
    } else {
      System.out.println("ActorIB.downloadScene: Scene has been found.");
      try {
        /** TEMP - HW**/
        FuSpace p = machine.getState();
        while (p==null) {
          System.out.println("ActorIB.downloadScene: waiting for the machine's position.");
          synchronized(this) { this.wait(1000); }
          p = machine.getState();
        }//wait for the machine to be seen by the camera
        System.out.println("ActorIB.downloadScene: p ="+p.toString());
        lp = scene.whereAmI(p);
        System.out.println("ActorIB.downloadScene: logistic position = "+lp);
        this.getProperties();//to be sure props is not null
        props.destination = lp.getName();
        props.origin = "start-up";

        Actor actorRemote = (Actor)UnicastRemoteObject.toStub(this);
        Action sa = lp.scene.join(actorRemote, lp);
        System.out.println("ActorIB.downloadScene: join scene-action="+sa);
        sa.setActor(this);
        sa.execute();
        System.out.println("ActorIB.downloadScene:  FINISHED!!!!");
        exitTicket = sa.getExitTicket();

        props.origin = props.destination;
        props.destination = "n/a";
        //end - temp
      } catch (Exception e) { e.printStackTrace();}
    }
  }

  /**
   * Method in which the jobInterpreter-thread cycles. <p>
   * Selects right sceneAction (available under sceneAction and nextSceneAction).
   * jobInterpreterThread is started in constructor.
   * @see #ActorIB(ServiceID,MachineRemote,String)
   */
  protected void jobInterpreterCycle() {//overload this method!
    if (DEBUG) System.out.println("*D* ActorIB *** jobInterpreterCycle ***");

    /** TEMP - HW **/
    downloadScene();
    // end temp

    while (!shutDown) {
      while (currentJob == null ) {
        if (DEBUG) System.out.println("*D* ActorIB: jobInterpreter about to wait for a job");
        try{ synchronized(this) { this.wait(); } }
        catch (Exception e) {
          System.out.println("Exception while waiting in ActorIB.jobInterpreterCycle(): "+e.getMessage());
          e.printStackTrace();
        }
      }
      if (DEBUG) System.out.println("*D* ActorIB: ## jobInterpreter is getting busy!");
      ActionJob actionJob;
      synchronized(this) {
        actionJob = (ActionJob)currentJob;
        currentJob = nextJob;
      }

      if (scene == null) { downloadScene(); }
      if (scene == null) {
        System.out.println("Actor cannot execute SceneAction, because no Scene was found");
        break;
      }
      // now let's check if this navigator is still alive or that we should find a new one
      boolean connectable = false;
      while (!connectable) {
        try { connectable = scene.getLifeSign(); } catch (RemoteException e) {
          // ok, this scene clearly is not available
          System.out.println ("Downloaded Scene is no longer available. Actor needs to download new one.");
          System.out.println ("If  this problem continues to occur, please check your network or restart the Scene.");
          //e.printStackTrace();
          try { synchronized(this) { this.wait(15000); } } catch (Exception ex) { ex.printStackTrace(); }
          connectable = false;
        }
      }
      if (DEBUG) System.out.println("*D* ActorIB: The scene is responding...");

      try {
        //nxt insert by HW 3 july 2001
        scene = scene.getTopScene().find(actionJob.getDescription());
        currentSceneAction = scene.getSceneAction(actionJob.getDescription(), this);
      } catch (RemoteException re) { re.printStackTrace(); }

      if (currentSceneAction != null) {
        currentSceneAction.setActor(this);
/**/    currentSceneAction.setBeginPosition(lp);
        System.out.println("Downloaded sceneAction :"+currentSceneAction.toString()+"  downloaded by:"+this.toString());
        System.out.println("From scene : "+currentSceneAction.getScene().toString());
      } else {System.out.println("Downloaded sceneAction = null");}

      synchronized(this) { this.notify(); }// to wake up run()
    }
    if (DEBUG) System.out.println("*D* ActorIB *** jobInterpreterCycle *** ENDED");
  }

  /**
   * The run-method for the thread that executes scene-actions.<p>
   * Started in constructor. Cycles in actorRunCycle.
   * @see #ActorIB(ServiceID,MachineRemote,String)
   * @see #actorRunCycle()
   */
  public void run() {
    Thread actorRunCycleThread = new Thread("actorRunCycleThread@"+name){
      public void run() { actorRunCycle(); }
    };
    actorRunCycleThread.start();
  }
  /**
   * Method in which the actor-run-thread cycles.<p>
   * Executes selected scene-actions. The scene-actions are selected by the
   * job-interpreter-thread.<br>
   * @see #run()
   * @see #jobInterpreterCycle()
   * @see net.agileframes.core.traces.SceneAction#execute(Ticket[])
   */
  protected void actorRunCycle() {
    if (DEBUG) System.out.println("*D* ActorIB: *** actorRunCycle ***");
    while (!shutDown) {
      while (currentSceneAction == null) {
        try { synchronized(this) { this.wait(); }}
        catch (Exception e) {
          System.out.println("Exception in ActorIB.actorRunCycle: "+e.getMessage());
          e.printStackTrace();
        }
      }
      if (DEBUG) System.out.println("*D* ActorIB: actorRuncycle about to run scene-action");
      currentSceneAction.execute(new Ticket[] {exitTicket});
      exitTicket = currentSceneAction.getExitTicket();
      if (DEBUG) System.out.println("*D* ActorIB: scene-action started running");
      currentSceneAction = null;
    }
  }
  /**
   * Returns the name of this Actor.<p>
   * @return  the name of this actor.
   */
  public String getName() { return name; }
  //inherited from Actor
  
  public MachineRemote getMachine() { 
		return this.machine; }
  
  public MachineRemote getMachineRemote() { try {
	return (MachineRemote)UnicastRemoteObject.exportObject(machine,0);
} catch (RemoteException e) {
	e.printStackTrace();
	return null;
} }
  
  private ActorProperties props = null;
  //inherited from Actor
  public Properties getProperties() {
    if (props == null) {
      props = new ActorProperties();
      props.name = name;
      props.origin = "N/A";
      props.destination = "N/A";
      props.nextDestination = "N/A";
      props.busy = 0;
      props.idle = 100;
      props.capacity = 0;
    }
    return props;
  }
  /**
   * Disposes this actor.<p>
   * Calls the super.<br>
   * Disposes the current scene-action of this actor. The scene to which the
   * scene-action belongs is obtained by asking the top scene action, who will
   * forward the call to its sub-scenes.
   * @see   net.agileframes.server.ServerIB#dispose()
   * @see   net.agileframes.core.traces.Scene#getTopScene()
   * @see   net.agileframes.core.traces.Scene#destroySceneAction(Actor)
   */
  public void dispose() {
    super.dispose();
    if (scene != null) {
      try { scene.getTopScene().destroySceneAction(this); }
      catch (Exception e) {
        System.out.println("Connection to scene lost while destroying scene-action.");
        System.out.println("Exception ignored.");
      }
    }
    System.out.println(System.currentTimeMillis()+": Actor disposed");
  }

  /**
   * Checks if the two instances are equal.<p>
   * @param   obj the object to be checked
   * @return  <code><b>true </code></b>  iff the two objects are equal<br>
   *          <code><b>false</code></b>  iff the two objects are not equal
   */
  public boolean equals(Object obj) {
    if ((obj == null) || !(obj instanceof Actor)) return false;
    Actor actor = (Actor)obj;
    try { return this.serviceID.equals(actor.getServiceID()); }
    catch (RemoteException e) { e.printStackTrace(); }
    return false;
  }


  //-------------------- Inherited from Service --------------------------
  /**
   * <b>Implementation of Properties.</b>
   * <p>
   * It is recommended to extend this class if you want to implement your own functionality.
   * Currently this data-object only allows pre-defined properties. In future this Properties-
   * object should be similar to the MachineProperties.
   * @see     net.agileframes.forces.MachineIB.MachineProperties
   * @author  H.J. Wierenga
   * @version 0.1
   */
  public class ActorProperties implements Properties, java.io.Serializable {
    /** The name of this Actor. */
    public String name;
    /** The origin of this Actor (wrt SceneAction) */
    public String origin;
    /** The destination of this Actor (wrt SceneAction) */
    public String destination;
    /** The next destination of this Actor (wrt SceneAction) */
    public String nextDestination;
    /** The busy time (rate) of this Actor, between 0-1 */
    public double busy;
    /** The idle time (rate) of this Actor, between 0-1 */
    public double idle;
    /** The free capacity of this Actor */
    public double capacity;
    /** Empty -default- Constructor */
    public ActorProperties() {}
  }
}
