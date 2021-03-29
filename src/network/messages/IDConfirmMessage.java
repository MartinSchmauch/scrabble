package network.messages;

public class IDConfirmMessage extends Message {

  private static final long serialVersionUID = 1L;
  private int id;

  public IDConfirmMessage(MessageType type, String from, int id) {
    super(type, from);
    this.id = id;
  }

  public int getID() {
    return this.id;
  }

}
