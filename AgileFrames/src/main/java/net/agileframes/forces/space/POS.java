package net.agileframes.forces.space;
import net.agileframes.core.forces.State;
import java.lang.Cloneable;
import java.text.DecimalFormat;
import net.agileframes.server.AgileSystem;

/**
 * Created: Wed Jan 12 14:56:39 2000
 * @author Lindeijer, Evers
 * @version 0.0.1

 Position and Orientation in Space

 */


public class POS extends State implements Cloneable {

  public float x;
  public float y;
  public float z;
  public Position position;
  public Position getPosition() { return new Position(x,y,z); }

  public double alpha;  // angle in the x-y pane, counter-clockwize from the x-axis
  public double getAlpha() { return this.alpha; }
  public double beta;   // angle in the xy-z pane
  public double getBeta()  { return this.beta;  }
  public double gamma;
  public double getGamma() { return this.gamma; }
  public Orientation orientation;
  public Orientation getOrientation() { return new Orientation(alpha, beta, gamma); }

  ////////////////////////////////////////////////////////////////////////

  public POS(float x,float y,float z,double alpha,double beta, double gamma) {
    this.x=x;
	  this.y=y;
	  this.z=z;
    this.alpha=alpha;
	  this.beta=beta;
	  this.gamma=gamma;
  }

  public POS(float x, float y, double alpha) {
    this(x, y, 0, alpha, 0, 0);
  }


  public POS(){}

  ////////////////////////////////////////////////////////////////////////

  public void clear()
  {
    x            = 0.0f;
    y            = 0.0f;
    z            = 0.0f;
    alpha        = 0.0f;
    beta         = 0.0f;
	  gamma        = 0.0f;
    u            = 0.0f;
    t            = 0;
  }

  public float distance(State state) {
    if (state == null) {
      System.out.println("POS distance to null is NaN");
      return Float.NaN;
    }
    return distance((POS)state);
  }

  public float distance(POS pos) {
    double _x = x - pos.x; _x = _x * _x;
    double _y = y - pos.y; _y = _y * _y;
    double _z = z - pos.z; _z = _z * _z;
    float d = (float)Math.sqrt( _x + _y + _z );
    // System.out.println("POS.this=" + toString() + "    POS.that=" + pos.toString() + " distance=" +d);
    return d;
  }

  /**
   * fills this POS with the difference in x,y,z and alpha,beta between 2 POS objects
   */
  public void difference (POS state1, POS state2)
  {
    this.x     = state1.x     - state2.x;
    this.y     = state1.y     - state2.y;
    this.z     = state1.z     - state2.z;
    //this.alpha = state1.alpha - state2.alpha;
    //this.beta  = state1.beta  - state2.beta;
  }

  ///////////////////////////////////////////////////////////////////////

  public State add(State state) {
    return add((POS)state);
  }

  public POS add(POS space) {
    POS s = new POS();
    s.x = this.x + space.x;
    s.y = this.y + space.y;
    s.z = this.z + space.z;
    s.alpha = this.alpha + space.alpha;
    s.beta = this.beta + space.beta;
    return s;
  }

  /**
   * add without creating a new object (add to this object)
   */
  public void add2this (POS toAdd)
  {
    this.x     += toAdd.x;
    this.y     += toAdd.y;
    this.z     += toAdd.z;
    this.alpha += toAdd.alpha;
    this.beta  += toAdd.beta;
  }

  public State subtract(State state) {
    return subtract((POS)state);
  }

  /**
  Am I subtracting the alpha and beta in the wrong oreder?
  */
  public POS subtract(POS space) {
    POS s   = new POS();
    s.x     = this.x - space.x;
    s.y     = this.y - space.y;
    s.z     = this.z - space.z;
    s.alpha = this.alpha - space.alpha;
    s.beta  = this.beta - space.beta;
    return s;
  }

  //////////////////////////////////////////////////////////////////////

  /** state = Maneuver.F(u), state.velocity = Maneuver.F'(u). */
  //public float velocity = 0;   // dit moet weg hier

  //public float accelleration = 0; // dit moet weg hier

  ///////////////////////////////////////////////////////////////////////

  public Object clone() {
    POS clone = new POS(x, y, z, alpha, beta, gamma);
    clone.t = t;
    clone.u = u;
    //clone.accelleration = accelleration;
    //clone.velocity = velocity;
    return clone;
  }

  /**
   * Copy without creating a new object (= clone without creating a new object)
   */
  public void copy (POS pos)
  {
    this.x              = pos.x;
    this.y              = pos.y;
    this.z              = pos.z;
    this.alpha          = pos.alpha;
    this.beta           = pos.beta;
    this.gamma          =pos.gamma;
    //this.accelleration  = pos.accelleration;
    //this.velocity       = pos.velocity;
    this.u              = pos.u;
    this.t              = pos.t;
  }

  public boolean equals(POS pos) {
    if (pos.x == x) {
      if (pos.y == y) {
        if (pos.z == z) {
          if (pos.alpha == alpha) {
            if (pos.beta == beta) {
              if (pos.gamma == gamma) {
                return true;
    } } } } } }
    return false;
  }

  // DecimalFormat-declaraties zijn naar binnen de methode verschoven.
  // DecimalFormats blijken veel rekentijd te kosten (40% van de rekentijd
  // bij opstarten werd gebruikt voor het maken van DecimalFormats):
  // ze zouden in een aparte DecimalFormat-klasse moeten zitten, zodat ze maar
  // 1 keer aangemaakt worden.

  public String toString() {
    return "[" + toString2D() + "]" ;
  }

  public String toString2D() {
    return "x="  + x +   " y=" + y +
           " a=" + alpha +
           " u=" + u +     " t=" + t;
  }

  public String toString2DFormatted() {
    DecimalFormat dotTwo = AgileSystem.dotTwo;
    DecimalFormat dotThree = AgileSystem.dotThree;
    DecimalFormat lenFive   = AgileSystem.lenFive;
    return "x="  + dotTwo.format(x) +   " y=" + dotTwo.format(y) +
           " a=" + dotThree.format(alpha) +
           " u=" + dotTwo.format(u) +     " t=" + (t%100000000);
  }

  public String toString3D() {
    return toString3DFormatted();
  }

  public String toString3DFormatted() {
    DecimalFormat dotTwo = AgileSystem.dotTwo;
    DecimalFormat dotThree = AgileSystem.dotThree;
    DecimalFormat lenFive   = AgileSystem.lenFive;
    return "x="  + dotTwo.format(x) +   " y=" + dotTwo.format(y) +   " z=" + dotTwo.format(z) +
           " a=" + dotThree.format(alpha) + " beta=" + dotThree.format(beta) + " gamma=" + dotThree.format(gamma) +
           " u=" + dotTwo.format(u) +     " t=" + (t%100000000);
  }




}
