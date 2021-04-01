package network.messages;

/** @author lurny */

public class ShutdownMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String reason;

  public ShutdownMessage(String from, String reason) {
    super(MessageType.SHUTDOWN, from);
    this.reason = reason;
  }

public String getReason() {
	return reason;
}

}
