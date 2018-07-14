package net.agileframes.forces.flags;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Passed Flag. </b>
 * <p>
 * The Passed-Flag will be raised if and only if the prescribed evolution
 * is larger than or as large as the physical evolution.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */

public class PassedFlag extends Flag {
  private double realisedEvolutionPoint = Double.NaN;
  /**
   * Default Constructor.<p>
   * Only sets these two parameters.
   * @param manoeuvre               the manoeuvre on which this Flag is defined
   * @param realisedEvolutionPoint  the prescribed evolution
   */
  public PassedFlag(Manoeuvre manoeuvre, double realisedEvolutionPoint) {
    this.realisedEvolutionPoint = realisedEvolutionPoint;
    this.manoeuvre = manoeuvre;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * cuurent evolution  >=  prescribed evolution
   * </blockquote></code>
   */
  public void evaluate() {
    if (manoeuvre.getCalcEvolution() >= realisedEvolutionPoint) { raise(); }
  }

  public String toString() {
    return "PassedFlag("+realisedEvolutionPoint+") raised="+isRaised()+", current evolution = "+manoeuvre.getCalcEvolution();
  }
}

