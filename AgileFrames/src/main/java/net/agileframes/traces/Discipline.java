

package net.agileframes.traces;
import net.agileframes.traces.ticket.PrimeTicket;

import com.objectspace.jgl.Array;

/**
 * Is a FIFO discipline.
 *
 */

public class Discipline {
  public Semaphore semaphore = null; // set via Semaphore.setDiscipline()
  public Array reserved_list = null; // set via Semaphore.setDiscipline()

  public Discipline(){}

  /**
  select a prime-ticket, put it at the front of the list for popfront().
  */
  public PrimeTicket select() {
    if (!reserved_list.isEmpty()) {
      PrimeTicket candidate = (PrimeTicket) reserved_list.front();
      if (candidate.threshold <= semaphore.getCapacity()) {
        return candidate;
      }
    }
    else {
      // System.out.println("discipline: reserved list was empty: " + reserved_list.toString());
    }
    return null;
  }

}