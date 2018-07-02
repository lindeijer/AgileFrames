package net.agileframes.vr;

import java.awt.GraphicsConfiguration;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Enumeration;
// import algemeen.Axis;

import com.agileways.forces.infrastructure.jumbo.avatar.Pillar;

public class Viewer {

  private JFrame f = null;
  private Container contentPane = null;
  private BranchGroup addBG = new BranchGroup();

  public BranchGroup createSceneGraph(SimpleUniverse su) {
	  // Create the root of the branch graph
	  BranchGroup objRoot = new BranchGroup();
    this.addBG = new BranchGroup();

    TransformGroup objTransform = new TransformGroup();
    objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    objRoot.addChild(objTransform);
    //   objTransform.addChild(new ColorCube(0.4));
    //   objTransform.addChild(new Axis());
    System.exit(0);

    MouseRotate myMouseRotate = new MouseRotate();
    myMouseRotate.setTransformGroup(objTransform);
    myMouseRotate.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseRotate);

    MouseTranslate myMouseTranslate = new MouseTranslate();
    myMouseTranslate.setTransformGroup(objTransform);
    myMouseTranslate.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseTranslate);

    MouseZoom myMouseZoom = new MouseZoom();
    myMouseZoom.setTransformGroup(objTransform);
    myMouseZoom.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseZoom);
    objTransform.addChild(this.addBG);


	  BoundingSphere lightBounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
    AmbientLight ambLight = new AmbientLight(true, new Color3f(1.0f, 1.0f, 1.0f));
    ambLight.setInfluencingBounds(lightBounds);
    ambLight.setCapability(Light.ALLOW_STATE_WRITE);
    objRoot.addChild(ambLight);
    DirectionalLight headLight = new DirectionalLight();
    headLight.setCapability(Light.ALLOW_STATE_WRITE);
    headLight.setInfluencingBounds(lightBounds);
    objRoot.addChild(headLight);

	  // Let Java 3D perform optimizations on this scene graph.
    //objRoot.compile();

	  return objRoot;
  } // end of CreateSceneGraph method of MouseNavigatorApp

  // Create a simple scene and attach it to the virtual universe

  public Viewer(Node avatar) {


    this.f = new JFrame();

    this.f.addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.out.println("Exiting application");
        System.exit(0);
      }
    });

    this.contentPane = f.getContentPane();
    this.contentPane.setLayout(new BorderLayout());
    this.f.setSize(new Dimension(300, 300));

    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);

    contentPane.add(canvas3D, BorderLayout.CENTER);
    // SimpleUniverse is a Convenience Utility class
    SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

    BranchGroup scene = createSceneGraph(simpleU);
    this.addBG.addChild(avatar);
    scene.compile();
	  // This will move the ViewPlatform back a bit so the
	  // objects in the scene can be viewed.
    simpleU.getViewingPlatform().setNominalViewingTransform();

    simpleU.addBranchGraph(scene);
    f.setVisible(true);
  } // end of MouseNavigatorApp (constructor)


  private void user() {
    JPanel userPanel = new JPanel();
  }

  //  The following allows this to be run as an application
  //  as well as an applet
  public static void main(String[] args) {
    // BranchGroup ding = VrmlAvatarFactory.getAvatar("C:\\vrmlfiles\\ols1.wrl");
    //BranchGroup ding = new Pillar(0.0f, 0.0f, 1.0f, Color3D.cyan, 1.0f).getBG();
    // Viewer app = new Viewer(ding);
  } // end of main (method of MouseNavigatorApp)

}
