package net.agileframes.core.traces;
/**
 * <b>Thrown when a ticket performs an illegal action while BLOCKING.</b><p>
 * @see net.agileframes.traces.ticket.TicketIB
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class BlockException extends Exception {
  /**
   * Constructor creates BlockException with a message.
   * @param msg the message that forms the content of this BlockException.
   */
  public BlockException(String msg) {
    super(msg);
  }
}