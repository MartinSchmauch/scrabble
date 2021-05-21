package network.messages;

import java.util.ArrayList;
import java.util.List;
import mechanic.Tile;

/**
 * The server sends a tile to to client who requested a tile via the TileRequestMessage.
 *
 * @author lurny
 */
public class TileMessage extends Message {
  private static final long serialVersionUID = 1L;
  private List<Tile> tiles;

  /**
   * This method creates a TileMessage instance.
   */
  public TileMessage(String from, List<Tile> tiles) {
    super(MessageType.TILE, from);
    this.tiles = new ArrayList<Tile>(tiles);
  }

  public List<Tile> getTiles() {
    return tiles;
  }

}
