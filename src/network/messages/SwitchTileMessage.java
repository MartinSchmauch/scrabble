package network.messages;

/**
 * The SwitchTileMessage
 * 
 * @author lurny
 */

public class SwitchTileMessage extends Message {
  private static final long serialVersionUID = 1L;
  private int countdown;
  private int remainingTilesInTileBag;

  public SwitchTileMessage(String from) {
    super(MessageType.START_GAME, from);
  }

}

// TODO do we need this
