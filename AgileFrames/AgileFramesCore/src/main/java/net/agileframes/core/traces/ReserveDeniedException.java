package net.agileframes.core.traces;
/**
 * <b>Thrown when it is impossible to reserve a Semaphore.</b><p>
 * @author  D.G. Lindeijer, H.J. Wierenga
 * @version 0.1
 */
public class ReserveDeniedException extends Exception {
  /**
   * Constructor creates ReserveDeniedException with a message.
   * @param msg the message that forms the content of this ReserveDeniedException.
   */
  public ReserveDeniedException(String msg) {
    super(msg);
  }
}