package net.agileframes.forces.flags;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Deviation Flag. </b>
 * <p>
 * The Deviation-Flag will be raised if and only if the physical deviation
 * is larger than or as large as the prescribed deviation.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class DeviationFlag extends Flag {
  private double deviation = Double.NaN;
  /**
   * Default Constructor.<p>
   * Only sets these two parameters.
   * @param manoeuvre the manoeuvre on which this Flag is defined
   * @param deviation the prescribed deviation
   */
  public DeviationFlag(Manoeuvre manoeuvre, double deviation) {
    this.manoeuvre = manoeuvre;
    this.deviation = deviation;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * prescribed deviation  <=  current deviation
   * </blockquote></code>
   */
  public void evaluate() {
    if (deviation <= manoeuvre.getCalcDeviation()) { raise(); }
  }
}

