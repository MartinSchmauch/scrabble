package network.messages;

/** @author lurny */

public class IDConfirmMessage extends Message {

  private static final long serialVersionUID = 1L;
  private int id;

  public IDConfirmMessage(String from, int id) {
    super(MessageType.ID_CONFIRMED, from);
    this.id = id;
  }

  public int getID() {
    return this.id;
  }

}
