package network.messages;

/** @author lurny */

import mechanic.Tile;

public class TileResponseMessage extends Message {
  private static final long serialVersionUID = 1L;
  private Tile tile;

  public TileResponseMessage(String from, Tile tile) {
    super(MessageType.TILE_RESPONSE, from);
  }

  public Tile getTile() {
    return tile;
  }

}
