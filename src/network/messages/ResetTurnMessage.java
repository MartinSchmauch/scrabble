package network.messages;

import java.util.List;
import mechanic.Tile;

/**
 * The ResetTurn Messsage resets the current turn.
 *
 * @author lurny
 */

public class ResetTurnMessage extends Message {
  private static final long serialVersionUID = 1L;
  private List<Tile> tiles;

  /**
   * This method creates an instance of the class.
   */
  public ResetTurnMessage(String nickname, List<Tile> tiles) {
    super(MessageType.RESET_TURN, nickname);
    this.tiles = tiles;
  }

  /**
   * gets the variable tiles of the current instance.
   */
  public List<Tile> getTiles() {
    return tiles;
  }

}
