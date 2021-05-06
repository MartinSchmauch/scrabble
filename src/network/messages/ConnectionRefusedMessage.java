package network.messages;

/**
 * This message is used to indicate that the clients connection request is refused by the server.
 * 
 * @author lurny
 */

public class ConnectionRefusedMessage extends Message {

  private static final long serialVersionUID = 1L;

  private String reason;
  

  public ConnectionRefusedMessage(String from, String reason) {
    super(MessageType.CONNECTION_REFUSED, from);
    this.reason = reason;
  }

  public String getReason() {
    return this.reason;
  }  


}
