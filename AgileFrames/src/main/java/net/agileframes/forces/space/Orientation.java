package net.agileframes.forces.space;

/**
 * Created: Wed Jan 12 14:56:39 2000
 * @author Lindeijer, Evers
 * @version 0.0.1
 */


public class Orientation {

  public double alpha; // angle in x-y pane
  public double beta;  // angle in xy-z pane
  public double gamma;

  public Orientation(double alpha,double beta, double gamma) {
    this.alpha=alpha; this.beta=beta; this.gamma=gamma;
  }

  public Position getOrientator(Position p,float distance) {
    // from position p, go in direction or this orientation
    // for distance, and return that point in space
    return null;
  }

}