package net.agileframes.forces.space;
import net.agileframes.core.forces.Transform;
import net.agileframes.forces.space.POS;
import net.agileframes.core.forces.State;
import net.agileframes.forces.space.Position;

/**
 * This object introduces coordinate axes relative to the absolute coordinate axes.
 *
 * @author Wierenga
 * @version 0.0.1
 */

public class POSTransform extends Transform {
  private Position translation;
  private double rotation;
  private Position scale;


  /////////////Constructors//////////////////////////////////////

  /**
   * @param translation Position indicating translation in x, y and z direction
   *                    of the relative axes with respect to absolute axes
   * @param rotation    double indicating rotation of relative axes
   * @param scale       Position indicating scale of x, y and z componenets
   *                    of the relative axes with respect to absolute axes
   */
  public POSTransform(Position translation, double rotation, Position scale) {
    this.translation = new Position(translation.x,translation.y,translation.z);
    this.rotation = rotation;
    this.scale = new Position(scale.x,scale.y,scale.z);
  }

  /**
   * @param x         float indicating translation in x direction of the relative axes
   *                  with respect to absolute axes
   * @param y         float indicating translation in y direction of the relative axes
   *                  with respect to absolute axes
   * @param rotation  double indicating rotation of relative axes
   */
  public POSTransform(float x, float y, double rotation){
    this(new Position(x,y),rotation,new Position(1,1));
  }

  /**
   * @param x         float indicating translation in x direction of the relative axes
   *                  with respect to absolute axes
   * @param y         float indicating translation in y direction of the relative axes
   *                  with respect to absolute axes
   * @param rotation  double indicating rotation of relative axes
   * @param scale     float indicating scale of all components of relative axes
   */
  public POSTransform(float x, float y, double rotation, float scale){
    this(new Position(x,y),rotation,new Position(scale,scale));
  }

  ////////////////////////////////////////////////////////////////


  /**
   * Glues a transform at the end of this transform.
   * @param t   Transform that will be appended at the end of this transform
   *
   * @return    Transform that is the result of adding t to this transform
   */
  public Transform add(Transform t){
    POSTransform pt = (POSTransform) t;

    float x = (float)(this.scale.x * (pt.translation.x * Math.cos(this.rotation) - pt.translation.y * Math.sin(this.rotation)) + this.translation.x);
    float y = (float)(this.scale.y * (pt.translation.x * Math.sin(this.rotation) + pt.translation.y * Math.cos(this.rotation)) + this.translation.y);
    float z = 0;//currently code is only written for 2D
    double rot = this.rotation + pt.rotation;

    if (rot>2*Math.PI) {rot-=2*Math.PI;}
    if (rot<-2*Math.PI) {rot+=2*Math.PI;}

    //another implementation for the scales is needed...
    float scalex = pt.scale.x * this.scale.x;
    float scaley = pt.scale.y * this.scale.y;
    float scalez = pt.scale.z * this.scale.z;

    return (new POSTransform(new Position(x,y,z), rot, new Position(scalex,scaley,scalez)));
  }

  private POS oldPos = null;
  private float x,y;
  private double alpha;

  /**
   * Transforms a relative State-object to an absolute State-object.
   *
   * @param state State that will be transformed
   * @return transformed State-object
   */
  public State transform (State state){
    oldPos = (POS)state;

    x = (float)( scale.x * ( oldPos.x*Math.cos(rotation) - oldPos.y*Math.sin(rotation)) + translation.x);
    y = (float)( scale.y * ( oldPos.x*Math.sin(rotation) + oldPos.y*Math.cos(rotation)) + translation.y);
    alpha = oldPos.alpha + rotation;

    return (new POS(x,y,alpha));
  }

  /**
   * Gives the most basic attributes of this POSTransform. Used for output of info on the screen.
   * @return String with attributes of this POSTransform
   */
  public String toString(){
    return ("POSTransform: translation.x,y,z= ("+translation.x+","+translation.y+","+translation.z+
                                  ")  Rotation="+rotation+
                                  "  Scale.x,y,z= ("+scale.x+","+scale.y+","+scale.z+")");
  }
}