package net.agileframes.vr.space3d;
import javax.media.j3d.*;
import java.awt.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.*;
import javax.vecmath.*;
import javax.swing.*;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import java.rmi.server.UnicastRemoteObject;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceID;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;
import java.rmi.RemoteException;
import net.agileframes.server.AgileSystem;
import net.agileframes.core.vr.AvatarFactory;
import net.agileframes.core.vr.Body;
import net.agileframes.core.vr.Avatar;
import net.agileframes.core.vr.Virtuality;
/**
 * <b>Implementation of Virtuality in a 3D environment.</b>
 * <p>
 * Virtuality3D sets up the main 3D screen, and its associated behavior.
 * These behaviors are:
 * <ul>
 * <li>KeyNavigator: responds to arrow keys in combination with alt and ctrl keys
 * <li>MouseRotate: responds to the left mouse-button
 * <li>MouseTranslate: responds to the right mouse-button
 * <li>MouseZoom: responds to the alt-key in combination with the left mouse-button
 * </ul><br>
 * The Virtuality3D is a RemoteEventListener and listens for Body's with an AvatarFactory
 * to come up in the system. If such a Body comes up, its Avatar3D will be asked. If available,
 * the Avatar3D will be shown.<br>
 * If a shown Body disappears, it will be deleted from the Virtuality.<br>
 * <p>
 * This Virtuality3D has its own MAIN method. You can run this Virtuality3D stand-alone
 * without any problems.
 * <p>
 * <b>WARNING</b><br>
 * Using Virtuality3D can slow down your system's performances significantly due to the
 * use of Java3D objects. It is recommended to have at least 256 MB RAM and 600 MHz
 * available on your platform. Best is to run on a platform with multiple processors.
 * @see     Avatar3D
 * @see     net.agileframes.core.vr.Body
 * @see     net.agileframes.core.vr.AvatarFactory
 * @author  F.A. van Dijk, D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class Virtuality3D implements RemoteEventListener, Virtuality {
  private Entry factory = new AvatarFactory();
  private Name name = new Name();
  private ServiceTemplate bodyTemplate = new ServiceTemplate(null, new Class[] {net.agileframes.core.vr.Body.class}, new Entry[] {name, factory});
  private Canvas3D canvas3d=null;
  private SimpleUniverse simpleuniverse=null;
  private BranchGroup sceneBG=null;
  private TransformGroup objTransform=null;
  private BranchGroup mountBG=null;
  private TransformGroup vpTrans=null;
  private JFrame frame=null;
  private Transform3D t3d=null;
  private static final int MAX_AVATARS = 100;
  /**
   * Empty Constructor. Not Implemented.
   * @param parentFrame frame in which this Virtuality is shown - not implemented.
   */
  public Virtuality3D(JFrame parentFrame) throws RemoteException {
  }
  /**
   * Default Constructor.<p>
   * Creates Virtuality3D that listens for Body's to come up.
   * Exports this object with UnicastRemoteObject.exportObject.
   * Registers as a ServiceListener with AgileSystem.
   * @see   net.agileframes.server.AgileSystem#registerServiceListener(ServiceTemplate,RemoteEventListener)
   */
  public Virtuality3D() throws RemoteException {
    UnicastRemoteObject.exportObject(this);//needed for RemoteEventListener
    AgileSystem.registerServiceListener(bodyTemplate, this);

    //GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    //this.canvas3d = new Canvas3D(config);
    GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
    GraphicsConfiguration cfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
    this.canvas3d = new Canvas3D(cfg);

    simpleuniverse = new SimpleUniverse(canvas3d);

    this.sceneBG = new BranchGroup();
    this.objTransform = new TransformGroup();
    this.mountBG = new BranchGroup();
    this.mountBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
    this.mountBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

    this.sceneBG.addChild(this.objTransform);

    this.objTransform.addChild(this.mountBG);
    this.objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    this.objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    this.sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

    /*this.vpTrans = simpleuniverse.getViewingPlatform().getViewPlatformTransform();
    Vector3f    vp_Vector3f    = new Vector3f(-30.0f, 0.0f, 100.0f); // 10 m boven platform //1700
    Transform3D vp_Transform3D = new Transform3D();
    vp_Transform3D.setTranslation(vp_Vector3f);
    vp_Transform3D.setScale(20);//50
    vpTrans.setTransform(vp_Transform3D);
    */
    this.vpTrans = simpleuniverse.getViewingPlatform().getViewPlatformTransform();
    this.t3d = new Transform3D();
//    t3d.lookAt(new Point3d(-35.0, -35.0, 5.0), new Point3d(-35.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));
 //   t3d.lookAt(new Point3d(55.0, -10.0, 280.0), new Point3d(55.0, 70.0, 20.0), new Vector3d(0.0, 1.0, 0.0));
 //   t3d.invert();
 //   t3d.setScale(20);//was: 20
 //   this.vpTrans.setTransform(t3d);

//Key Navigator Behavior
    KeyNavigatorBehavior KNB = new KeyNavigatorBehavior(this.vpTrans);
    KNB.setSchedulingBounds( new BoundingSphere( new Point3d(), 2000.0) );
    sceneBG.addChild(KNB);

// MouseRotate
    MouseRotate myMouseRotate = new MouseRotate();
    myMouseRotate.setTransformGroup(objTransform);
    myMouseRotate.setSchedulingBounds(new BoundingSphere());
    sceneBG.addChild(myMouseRotate);

//MouseTranslate
    MouseTranslate myMouseTranslate = new MouseTranslate();
    myMouseTranslate.setTransformGroup(objTransform);
    myMouseTranslate.setSchedulingBounds(new BoundingSphere());
    sceneBG.addChild(myMouseTranslate);

//MouseZoom
    MouseZoom myMouseZoom = new MouseZoom();
    myMouseZoom.setTransformGroup(objTransform);
    myMouseZoom.setSchedulingBounds(new BoundingSphere());
    sceneBG.addChild(myMouseZoom);

    BoundingSphere lightBounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
    AmbientLight ambLight = new AmbientLight(true, new Color3f(1.0f, 1.0f, 1.0f));
    ambLight.setInfluencingBounds(lightBounds);
    ambLight.setCapability(Light.ALLOW_STATE_WRITE);
    this.sceneBG.addChild(ambLight);
    DirectionalLight headLight = new DirectionalLight();
    headLight.setCapability(Light.ALLOW_STATE_WRITE);
    headLight.setInfluencingBounds(lightBounds);
    this.sceneBG.addChild(headLight);

    this.frame = new JFrame();
    this.frame.getContentPane().add(canvas3d);
    // these are the settings for semMain:
    //this.frame.setSize(850, 640);// 1000,750
    this.frame.setVisible(true);


    this.setView(0, 0, 200, 20);

    //text..should be in a seperate method
    Text3D text3d = new Text3D(new Font3D(new Font("Arial",Font.BOLD,8),
                                          new FontExtrusion()),
                               "AgileFrames",
                               new Point3f(-40,-40,0)
                               );
    Shape3D shape3d = new Shape3D(text3d);
    this.add(shape3d);

    this.notify(null);  // lookup already existing services
  }

  /**
   * Adds an object to the virtual universe. <p>
   * @param node the object to be added
   */
  public void add(Node node) {
    this.mountBG.addChild(node);
  }

  /**
   * Saves the current viewpoint.<p>
   */
  public void saveViewPoint() {
    TransformGroup vp = this.simpleuniverse.getViewingPlatform().getViewPlatformTransform();
    Transform3D vpt3d = new Transform3D();
    vp.getTransform(vpt3d);
  }

  /**
   * Sets a new view point.<p>
   * Not implemented.
   * @param eye the position of the eye
   * @param center the oint at which the eye is looking
   * @param up the direction where the view will move if the up-key is pressed
   */
  public void setViewPoint(Point3d eye, Point3d center, Vector3d up) {

  }
  /**
   * Sets size and location of the frame in which the Virtuality3D shows.<p>
   * @param x       the x-coordinate of the frame
   * @param y       the y-coordinate of the frame
   * @param width   the width of the frame
   * @param height  the height of the frame
   */
  public void setSizeAndLocation(int x, int y, int width, int height) {
    this.frame.setSize(width, height);
    this.frame.setLocation(x,y);
  }
  /**
   * Sets the view.<p>
   * See Transform3D.lookAt()
   * @param x the x-coordinate of the view
   * @param y the y-coordinate of the view
   * @param z the z-coordinate of the view
   */
  public void setView(float x, float y, float z) {
    Transform3D t3d = new Transform3D();
    t3d.lookAt(new Point3d(x, y, z), new Point3d(x, y, 0), new Vector3d(0.0, 1.0, 0.0));
    t3d.invert();
    this.vpTrans.setTransform(t3d);
  }
  /**
   * Sets the view.<p>
   * See Transform3D.lookAt() and Transform3D.setScale()
   * @param x     the x-coordinate of the view
   * @param y     the y-coordinate of the view
   * @param z     the z-coordinate of the view
   * @param scale the scale of the view (zooming)
   */
  public void setView(float x, float y, float z, double scale) {
    Transform3D t3d = new Transform3D();
    t3d.lookAt(new Point3d(x, y, z), new Point3d(x, y, 0), new Vector3d(0.0, 1.0, 0.0));
    t3d.invert();
    t3d.setScale(scale);
    this.vpTrans.setTransform(t3d);
  }

  /**
   * Sets the viewing point.<p>
   * See Transform3D.lookAt()
   * @param eye     the viewing-eye
   * @param center  the viewing-center
   * @param up      the viewing-height
   */
  public void lookAt(Point3d eye, Point3d center, Vector3d up) {
    Transform3D t3d=new Transform3D();
    this.vpTrans.getTransform(t3d);
    t3d.lookAt(eye, center, up);
    t3d.invert();
    this.vpTrans.setTransform(t3d);
  }
  /**
   * Sets the scale of the view (zooming).<p>
   * See Transform3D.setScale()
   * @param scale   the viewing-scale
   */
  public void setScale(double scale) {
    Transform3D t3d=new Transform3D();
    this.vpTrans.getTransform(t3d);
    t3d.setScale(scale);
    this.vpTrans.setTransform(t3d);
  }


  //HW- I have the feeling the can be done in a better way. It must be possible to make changes to the
  //    sceneBG and still compile only once (for example when an avatar is added/ removed).
  //    It should made sure that during rendering, nothing has to be compiled...

  /**
   * End is the method that causes the 3D world to become 'alive'.<p>
   * Without this method nothing will be shown/rendered.
   * After this method only certain objects with certain capabilities may be added
   */
  public void end() {
    this.sceneBG.compile();
    simpleuniverse.addBranchGraph(this.sceneBG);
  }

  // from RemoteEventListener
  /**
   * Called when a Body with AvatarFactory (dis)appears or changes.<p>
   * Inherited from RemoteEventListener.
   * @param e the remote-event - not used.
   */
  public void notify(RemoteEvent e) {
    System.out.println("*** Virtuality notified");
    ServiceItem[] serviceItems = AgileSystem.lookup(bodyTemplate, MAX_AVATARS);
    this.evaluateItems(serviceItems);
  }
  private Avatar3D[] avatars = new Avatar3D[MAX_AVATARS];
  private int registeredAvatars = 0;
  private void addAvatar(ServiceItem serviceItem) {
    System.out.println("New service found, avatar will be added");
    AvatarFactory factory = (AvatarFactory)serviceItem.attributeSets[1];
    // we are virtuality3D, so we need avatar3D
    Avatar3D avatar = (Avatar3D)factory.getAvatar(getClass(), (Body)serviceItem.service);
    avatar.setServiceID(serviceItem.serviceID);

    avatars[registeredAvatars] = avatar;
    registeredAvatars++;
    add(avatar.getBG());
  }
  private void removeAvatar(int index) {
    System.out.println("Service is disappeared, avatar will be removed");
    avatars[index].getBG().detach();
    for (int i = index; i < registeredAvatars; i++) { avatars[i] = avatars[i+1]; }
    registeredAvatars--;
  }
  private void evaluateItems(ServiceItem[] serviceItems) {
    // check if new item is added
    for (int i = 0; i < serviceItems.length; i++) {
      boolean registered = false;
      for (int j = 0; j < registeredAvatars; j++) {
        if (avatars[j].getServiceID().equals(serviceItems[i].serviceID)) { registered = true; break; }
      }
      if (!registered) { addAvatar(serviceItems[i]); }//!!!!
    }
    // check if item is gone
    for (int i = 0; i < registeredAvatars; i++) {
      boolean available = false;
      for (int j = 0; j < serviceItems.length; j++) {
        if (serviceItems[j].serviceID.equals(avatars[i].getServiceID())) { available = true; break; }
      }
      if (!available) { removeAvatar(i); }
    }
  }

  //-------------------------- Main ----------------------------------
  /**
   * The Main method.<p>
   * Called when runned stand-alone.
   * Creates Virtuality3D.
   * @param args  run-time arguments - not used
   */
  public static void main(String[] args) {
    try {
      Virtuality3D virtuality = new Virtuality3D();
      virtuality.setSizeAndLocation(550,0,300,310);
      virtuality.setView(0, 0, 200, 20);
      virtuality.end();
    } catch (RemoteException e) { e.printStackTrace(); }
  }

}



  // THESE methods are not used at the moment, maybe they should be in virtuality anyway. Who knows?

  // used to draw a grid in the 3d-world
  /*
  public Shape3D createGrid() {
    LineArray grid = new LineArray( 800, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    float l = -100.0f;
    for (int c = 0; c < 800; c+=4) {
      grid.setCoordinate( c+0, new Point3f( -100.0f, l, 0.0f) );
      grid.setCoordinate( c+1, new Point3f(  100.0f, l, 0.0f) );
      grid.setCoordinate( c+2, new Point3f( l, -100.0f, 0.0f) );
      grid.setCoordinate( c+3, new Point3f( l,  100.0f, 0.0f) );
      l+= 1.0f;
    } // for
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    for (int i = 0; i < 800; i++) {
      grid.setColor( i, Color3D.white );
    }
    return new Shape3D(grid);
  }  // createGrid
  */

  /*
  private Shape3D createLine(float x, float y) {
    LineArray line = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    line.setCoordinate(0, new Point3f(x, y, -5.0f));
    line.setCoordinate(1, new Point3f(x, y,  5.0f));
    line.setColor(0, Color3D.red);
    line.setColor(1, Color3D.red);
    return new Shape3D(line);
  }
  */



