package network.messages;

import java.util.List;
import mechanic.Tile;

/**
 * The Disconnect Messsage is used to disconnect one client from the Server. If client disconnects
 * from running game, disconnect message can contain tiles that have to be removed from the
 * gameboard.
 *
 * @author lurny
 */

public class DisconnectMessage extends Message {
  private static final long serialVersionUID = 1L;
  private List<Tile> tiles;

  public DisconnectMessage(String nickname, List<Tile> tiles) {
    super(MessageType.DISCONNECT, nickname);
    this.tiles = tiles;
  }

  public List<Tile> getTiles() {
    return tiles;
  }

  public void setTiles(List<Tile> tiles) {
    this.tiles = tiles;
  }

}
