package network.messages;

/** @author lurny */

public class TileRequestMessage extends Message {
  private static final long serialVersionUID = 1L;

  public TileRequestMessage(String from) {
    super(MessageType.TILE_REQUEST, from);
  }

}
