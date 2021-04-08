package network.messages;

import mechanic.Tile;

/**
 * This message is send, when a player removes a tile (e.g. puts it from the Gameboard back on his
 * tile rack).
 * 
 * @author lurny
 */

public class RemoveTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  private Tile tile;

  public RemoveTileMessage(String from, Tile tile) {
    super(MessageType.REMOVE_TILE, from);
    this.tile = tile;
  }

  public Tile getTile() {
    return this.tile;
  }

}
