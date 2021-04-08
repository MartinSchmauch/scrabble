package network.messages;

/**
 * The Disconnect Messsage is used to disconnect one client from the Server.
 * 
 * @author lurny
 */

public class DisconnectMessage extends Message {
  private static final long serialVersionUID = 1L;

  public DisconnectMessage(String nickname) {
    super(MessageType.DISCONNECT, nickname);
  }

}
