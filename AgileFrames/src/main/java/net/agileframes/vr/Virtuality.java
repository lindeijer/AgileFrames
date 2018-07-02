package net.agileframes.vr;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Virtuality {

  private Canvas3D canvas3d;
  private SimpleUniverse simpleuniverse;
  private BranchGroup sceneBG;
  private TransformGroup objTransform;
  private BranchGroup mountBG;
  private TransformGroup vpTrans;
  private JFrame frame;

  public Virtuality() {

    //GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    //this.canvas3d = new Canvas3D(config);
    GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
    GraphicsConfiguration cfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(template);
    this.canvas3d = new Canvas3D(cfg);

    simpleuniverse = new SimpleUniverse(canvas3d);

    this.sceneBG = new BranchGroup();
    this.objTransform = new TransformGroup();
    this.mountBG = new BranchGroup();

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
    Transform3D t3d = new Transform3D();
//    t3d.lookAt(new Point3d(-35.0, -35.0, 5.0), new Point3d(-35.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));
    t3d.lookAt(new Point3d(55.0, -10.0, 280.0), new Point3d(55.0, 70.0, 20.0), new Vector3d(0.0, 1.0, 0.0));
    t3d.invert();
    t3d.setScale(20);//was: 20
    this.vpTrans.setTransform(t3d);

//Key Navigator Behavior - causes extreme performance problems.
    //KeyNavigatorBehavior KNB = new KeyNavigatorBehavior(this.vpTrans);
    //KNB.setSchedulingBounds( new BoundingSphere( new Point3d(), 2000.0) );
    //sceneBG.addChild(KNB);
    //System.out.println("Just added Key Navigator Berhavior");

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
    this.frame.setSize(850, 640);// 1000,750
    this.frame.setVisible(true);
  }

  /**
   * Add an object to the virtual universe
   * @param node the object to be added
   */
  public void add(Node node) {
    this.mountBG.addChild(node);
  }

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


  private Shape3D createLine(float x, float y) {
    LineArray line = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    line.setCoordinate(0, new Point3f(x, y, -5.0f));
    line.setCoordinate(1, new Point3f(x, y,  5.0f));
    line.setColor(0, Color3D.red);
    line.setColor(1, Color3D.red);
    return new Shape3D(line);
  }

  /**
   * ????????
   */
  public void end() {
    this.sceneBG.compile();
    simpleuniverse.addBranchGraph(this.sceneBG);
  }
}
