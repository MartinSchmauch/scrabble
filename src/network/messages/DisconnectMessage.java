/** @author lurny */

package network.messages;

public class DisconnectMessage extends Message {
  private static final long serialVersionUID = 1L;

  public DisconnectMessage(String from) {
    super(MessageType.DISCONNECT, from);
  }

}
