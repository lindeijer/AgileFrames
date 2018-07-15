package net.agileframes.forces.flags;

import net.agileframes.core.forces.Flag;
import net.agileframes.core.forces.Manoeuvre;
/**
 * <b>The ToDo Flag. </b>
 * <p>
 * The ToDo-Flag will be raised if and only if the remaining evolution
 * is smaller than or as small as the prescribed distance.
 * @author  H.J. Wierenga, D.G. Lindeijer
 * @version 0.1
 */

public class ToDoFlag extends Flag {
  private double toDoLength = Double.NaN;
  /**
   * Default Constructor.<p>
   * Only sets these two parameters.
   * @param manoeuvre   the manoeuvre on which this Flag is defined
   * @param toDoLength  the prescribed distance to the end of the manoeuvre
   */
  public ToDoFlag(Manoeuvre manoeuvre, double toDoLength) {
    this.toDoLength = toDoLength;
    this.manoeuvre = manoeuvre;
  }
  /**
   * Evaluates this Flag.<p>
   * Will raise the Flag iff:
   * <code><blockquote>
   * current remaining evolution  <=  prescribed evolution-distance
   * </blockquote></code>
   */
  public void evaluate() {
    if (manoeuvre.getTrajectory().getEvolutionEnd() - manoeuvre.getCalcEvolution() <= toDoLength) { raise(); }
  }
}


