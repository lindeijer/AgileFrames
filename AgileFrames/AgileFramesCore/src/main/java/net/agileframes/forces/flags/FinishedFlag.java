package net.agileframes.forces.flags;
import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The Finished Flag. </b>
 * <p>
 * The Finished-Flag will be raised if and only if the physical evolution
 * is larger than or as large as the prescribed evolution end.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */

public class FinishedFlag extends Flag {
  /**
   * Default Constructor.<p>
   * Only sets these one parameters.
   * @param manoeuvre the manoeuvre on which this Flag is defined
   */
  public FinishedFlag(Manoeuvre manoeuvre) {
    this.manoeuvre = manoeuvre;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * current evolution  >=  evolution end
   * </blockquote></code>
   */
  public void evaluate() {
	if (isRaised()) return;
    if (manoeuvre.getCalcEvolution() >= manoeuvre.getTrajectory().getEvolutionEnd()) { raise(); }
  }

}

