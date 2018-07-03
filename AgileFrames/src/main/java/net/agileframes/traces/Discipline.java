package net.agileframes.traces;
import net.agileframes.core.traces.Semaphore;
import net.agileframes.traces.ticket.PrimeTicket;

import com.objectspace.jgl.Array;

/**
 * <b>The Discipline (of a Semaphore).</b>
 * <p>
 * Is a FIFO discipline. (First In First Out).
 * To be extended for other disciplines.
 * @see     net.agileframes.core.traces.Semaphore
 * @author  D.G. Lindeijer
 * @version 0.1
 */

public class Discipline {
  /**
   * The Semaphore to which this discipline belongs.<p>
   * Set in Semaphore.setDiscipline.
   * @see   net.agileframes.core.traces.Semaphore#setDiscipline(Discipline)
   */
  public Semaphore semaphore = null; // set via Semaphore.setDiscipline()
  /**
   * The reserved-list of the Semaphore to which this discipline belongs.<p>
   * Set in Semaphore.setDiscipline.
   * @see   net.agileframes.core.traces.Semaphore#setDiscipline(Discipline)
   */
  public Array reserved_list = null; // set via Semaphore.setDiscipline()

  /** Empty Constructor. */
  public Discipline(){}

  /**
   * Select a prime-ticket, put it at the front of the reserved-list.<p>
   * Called in semaphore.
   * @return  the selected ticket
   */
  public PrimeTicket select() {// called in Semaphore.assign
    if (!reserved_list.isEmpty()) {
      PrimeTicket candidate = (PrimeTicket)reserved_list.front();
      if (candidate.threshold <= semaphore.getCapacity()) {
        return candidate;
      }
    }
    else {
      //System.out.println("discipline: reserved list was empty: " + reserved_list.toString());
    }
    return null;
  }

}