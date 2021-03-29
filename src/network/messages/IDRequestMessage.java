package network.messages;

public class IDRequestMessage extends Message {

  private static final long serialVersionUID = 1L;

  public IDRequestMessage(MessageType type, String from, String nickname) {
    super(type, from);
  }

}
