package network.messages;

import java.util.List;
import mechanic.Tile;

/**
 * The ResetTurn Messsage resets the current turn
 * 
 * @author lurny
 */

public class ResetTurnMessage extends Message {
  private static final long serialVersionUID = 1L;
  private List<Tile> tiles;


  public ResetTurnMessage(String nickname, List<Tile> tiles) {
    super(MessageType.RESET_TURN, nickname);
    this.tiles = tiles;
  }


  public List<Tile> getTiles() {
    return tiles;
  }

}
