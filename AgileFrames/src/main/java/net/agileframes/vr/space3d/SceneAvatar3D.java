package net.agileframes.vr.space3d;
import net.agileframes.core.vr.Body;
import net.agileframes.core.forces.FuSpace;
import net.agileframes.core.traces.Scene;
import net.agileframes.core.forces.FuTrajectory;
import net.agileframes.forces.xyaspace.XYASpace;
import net.agileframes.vr.BaseGeometry;
import net.agileframes.vr.Colors;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
/**
 * <b>Implementation of an Avatar of a Scene in a 3d environment.</b>
 * <p>
 * In order to view this Avatar in a remote virtuality, use an SceneAvatarFactory.
 * @see     net.agileframes.core.vr.SceneAvatarFactory
 * @author  H.J. wierenga, F.A. van Dijk, D.G. Lindeijer
 * @version 0.1
 */
public class SceneAvatar3D extends Avatar3D {
  //--- Attributes ---
  private double resolution = 0;
  private Color3f color = null;
  private Scene scene = null;

  //--- Constructor ---
  /**
   * Default Constructor.<p>
   * Creates avatar of a scene, represented by Body.
   * @param Body  the scene-body
   */
  public SceneAvatar3D(Body body) {
    super(body, -1);
    this.scene = (Scene)body;
    color = Colors.yellow;
    resolution = 3.0;// dots per meter

    addGeometry( new SceneLayout(scene, resolution, color) );
    addAppearance(Colors.yellow);
    addAppearance(Colors.green);
    addAppearance(Colors.blue);
    addAppearance(Colors.cyan);
    addAppearance(Colors.magenta);

    setGeometryAndAppearanceID(0, 0);
  }

  //--- Methods ---
  //inherited from Avatar
  public void setGeometryAndAppearanceID(int geometryID, int appearanceID) {
    this.currentGeometryID = geometryID;
    this.currentAppearanceID = appearanceID;
    this.color = (Color3f)this.appearanceArray.at(this.currentAppearanceID);

    try {
      if (scene.isChanged()) {
        BaseGeometry oldGeometry = (BaseGeometry)this.geometryArray.at(this.currentGeometryID);
        BaseGeometry newGeometry = new SceneLayout(scene, resolution, color);
        this.geometryArray.replace(oldGeometry, newGeometry);
        this.currentGeometry = newGeometry;
      } else {
        this.currentGeometry = (BaseGeometry)this.geometryArray.at(this.currentGeometryID);
      }
    } catch (Exception e) { e.printStackTrace();}

    this.geometrySwitchNode.setWhichChild(this.currentGeometryID);
  }
  //inherited from Avatar
  public void setState(FuSpace state) {
    XYASpace xyaSpace = (XYASpace)state;

    this.vectorTranslation.set(xyaSpace.getX(), xyaSpace.getY(), 0);
    this.t3dTranslation.set(this.vectorTranslation);
    this.translateTG.setTransform(this.t3dTranslation);
    this.vectorRotation.set(0, 0, xyaSpace.getAlpha());
    this.t3dRotation.setEuler(this.vectorRotation);
    this.rotateTG.setTransform(this.t3dRotation);

    try{
      if (scene.isChanged()) {
        BaseGeometry oldGeometry = (BaseGeometry)this.geometryArray.at(this.currentGeometryID);
        BaseGeometry newGeometry = new SceneLayout(scene, resolution, color);
        this.geometryArray.replace(oldGeometry, newGeometry);
        this.currentGeometry = newGeometry;
      } else {
        this.currentGeometry = (BaseGeometry)this.geometryArray.at(this.currentGeometryID);
      }
    } catch (Exception e) { e.printStackTrace();}

    this.geometrySwitchNode.setWhichChild(this.currentGeometryID);
  }
}
//---------- SceneLayout -------------
/**
 * <b>The SceneLayout is the shape of the Scene.</b>
 * <p>
 * @author  H.J. Wierenga
 * @version 0.1
 */
class SceneLayout extends BaseGeometry {
  //--- Attributes ---
  private BranchGroup sceneBG = null;
  private Scene scene = null;
  private Color3f color = null;
  private double resolution = 0;

  //--- Constructor ---
  /**
   * Default Constructor. <p>
   * @param scene       the scene to layout
   * @param resolution  the resolution of the avatar (pixels/meter)
   * @param color       the color of the main trajectories on the Avatar
   */
  public SceneLayout(Scene scene, double resolution, Color3f color) {
    this.scene = scene;
    this.resolution = resolution;
    this.color = color;

    sceneBG = new BranchGroup();
    sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

    sceneBG.addChild(this.getLayout());
  }

  //--- Methods ---
  /**
   * Returns the layout of the Scene as a Shape3D object.<p>
   * @return  the layout of the scene
   */
  public Shape3D getLayout() {
    double totalLength = 0;
    FuTrajectory[] trajects = null;
    try {  trajects = scene.getSceneTrajects(); } catch (Exception e) { e.printStackTrace(); }
    for (int i = 0; i < trajects.length; i++){
      totalLength += trajects[i].getEvolutionEnd();
    }
    int totalSize = (int)(totalLength * resolution + 1);
    int offset = 0;
    PointArray pArray = new PointArray(totalSize, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

    for (int i = 0; i < trajects.length; i++) {
      double trajectLength = trajects[i].getEvolutionEnd();
      int trajectSize = (int)(trajectLength * resolution + 1);
      for(int j = 0; j < trajectSize; j++) {
        double u = (double)(j * trajectLength / trajectSize);
        FuSpace state = trajects[i].getTrajectPoint(u);
        XYASpace xyaSpace = (XYASpace)state;

        pArray.setCoordinate(offset + j,new Point3d(xyaSpace.getX(),xyaSpace.getY(),0));
        if (j == 0) {pArray.setColor(offset + j, Colors.red); }
        else {pArray.setColor(offset + j, color);}
      }
      offset += trajectSize - 1;
    }
    return new Shape3D(pArray);
  }
  /**
   * Returns the branchgroup of the scene-layout.<p>
   * @return the branchgroup of the scene-layout
   */
  public BranchGroup getBG() {  return this.sceneBG; }
  /**
   * Sets the color of the scene-layout.<p>
   * @param color the color to be set
   */
  public void setColor(Color3f color) {  this.color = color; }
  /**
   * Sets the text that will appear in the layout.<p>
   * @param text  the text to appear
   */
  public void setText(String text) {
    Text3D text3d = new Text3D(new Font3D(new Font("Arial",Font.BOLD,3),
                                          new FontExtrusion()),
                               text,new Point3f(0,0, 3f) );
    this.sceneBG.addChild(new Shape3D(text3d));
  }
}

