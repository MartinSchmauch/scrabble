package network.messages;

public class ConnectionRefusedMessage extends Message {

  private static final long serialVersionUID = 1L;

  private String reason;

  public ConnectionRefusedMessage(MessageType type, String from, String reason) {
    super(type, from);
    this.reason = reason;
  }

  public String getReason() {
    return this.reason;
  }

}
