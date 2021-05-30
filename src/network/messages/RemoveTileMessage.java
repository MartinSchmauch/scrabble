package network.messages;

/**
 * This message is send, when a player removes a tile (e.g. puts it from the Gameboard back on his
 * tile rack).
 *
 * @author lurny
 */

public class RemoveTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  private int row;
  private int collumn;

  /**
   * creates an instance of the class.
   */
  public RemoveTileMessage(String from, int row, int collumn) {
    super(MessageType.REMOVE_TILE, from);
    this.row = row;
    this.collumn = collumn;
  }

  /**
   * gets the variable row of the current instance.
   */
  public int getX() {
    return row;
  }

  /**
   * gets the variable collumn of the current instance.
   */
  public int getY() {
    return collumn;
  }

}
