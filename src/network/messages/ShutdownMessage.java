package network.messages;

/**
 * This message indicates a server shutdown.
 *
 * @author lurny
 */

public class ShutdownMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String reason;

  /**
   * This method creates an instance of the class.
   */
  public ShutdownMessage(String from, String reason) {
    super(MessageType.SHUTDOWN, from);
    this.reason = reason;
  }

  /**
   * gets the variable reason of the current instance.
   */
  public String getReason() {
    return reason;
  }

}
