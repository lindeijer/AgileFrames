package net.agileframes.forces.flags;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Started Flag. </b>
 * <p>
 * The Started-Flag will be raised if and only if the physical evolution
 * is larger than zero.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */
public class StartedFlag extends Flag {
  /**
   * Default Constructor.<p>
   * Only sets these one parameters.
   * @param manoeuvre the manoeuvre on which this Flag is defined
   */
  public StartedFlag(Manoeuvre manoeuvre) {
    this.manoeuvre = manoeuvre;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * current evolution  > 0
   * </blockquote></code>
   */
  public void evaluate() {
    if (manoeuvre.getCalcEvolution() > 0) {
      raise();
    }
  }

  public String toString() {
    return "StartedFlag raised="+isRaised()+", current evolution = "+manoeuvre.getCalcEvolution();
  }

}
