package network.messages;

/** @author lurny */

public class ShutdownMessage extends Message {
  private static final long serialVersionUID = 1L;

  public ShutdownMessage(String from) {
    super(MessageType.SHUTDOWN, from);
  }

}
