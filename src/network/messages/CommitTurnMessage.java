/** @author lurny */
package network.messages;

public class CommitTurnMessage extends Message {
  private static final long serialVersionUID = 1L;

  public CommitTurnMessage(String from) {
    super(MessageType.COMMIT_TURN, from);
  }

}
