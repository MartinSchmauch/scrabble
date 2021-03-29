package network.messages;

public class StartGameMessage extends Message {

  private int countdown;

  public StartGameMessage(MessageType type, String from, int countdown) {
    super(type, from);
    this.countdown = countdown;
  }

}
