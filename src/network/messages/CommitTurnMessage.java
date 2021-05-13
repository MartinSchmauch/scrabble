package network.messages;

/**
 * This Message is send, when a player has layed down the tiles to the Gameboard and wants to commit
 * the turn.
 * 
 * @author lurny
 */
public class CommitTurnMessage extends Message {
  private static final long serialVersionUID = 1L;
  private boolean tilesLeftOnRack;

  public CommitTurnMessage(String from, boolean tilesLeftOnRack) {
    super(MessageType.COMMIT_TURN, from);
    this.tilesLeftOnRack = tilesLeftOnRack;
  }

  public boolean getTilesLeftOnRack() {
    return tilesLeftOnRack;
  }

}
