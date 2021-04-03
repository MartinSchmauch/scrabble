/** @author lurny */
package network.messages;

import java.util.List;
import mechanic.Tile;

public class UpdateRequestGameBoardMessage extends Message {
  private static final long serialVersionUID = 1L;
  private int id;
  private List<Tile> tiles;

  public UpdateRequestGameBoardMessage(String from, int id, List<Tile> tiles) {
    super(MessageType.UPDATE_REQUEST_GAMEBOARD, from);
    this.id = id;
    this.tiles = tiles;
  }

  public int getID() {
    return this.id;
  }

  public List<Tile> getTiles() {
    return tiles;

  }
}
