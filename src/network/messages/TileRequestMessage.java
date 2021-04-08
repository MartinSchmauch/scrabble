package network.messages;

/**
 * The TileRequestMessage is used, when a player finished his turn and needs new tiles for his tile
 * rack.
 * 
 * @author lurny
 */

public class TileRequestMessage extends Message {
  private static final long serialVersionUID = 1L;

  public TileRequestMessage(String from) {
    super(MessageType.TILE_REQUEST, from);
  }

}
