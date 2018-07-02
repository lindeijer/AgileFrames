package net.agileframes.forces.space;
import java.io.Serializable;

/**
 * Created: Wed Jan 12 14:56:39 2000
 * @author Lindeijer, Evers
 * @version 0.0.1
 */


public class Position implements Cloneable, Serializable {

  public Position(float x,float y,float z) { this.x=x; this.y=y; this.z=z; }
  public Position(float x,float y) { this.x=x; this.y=y; this.z=0; }
  public float x;
  public float y;
  public float z;

}







