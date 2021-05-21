package network.messages;

/**
 * This message indicates a server shutdown.
 *
 * @author lurny
 */

public class InvalidMoveMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String reason;

  /**
   * This method creates an instance of the class.
   */
  public InvalidMoveMessage(String from, String reason) {
    super(MessageType.INVALID, from);
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }

}
