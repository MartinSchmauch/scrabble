package network.messages;

import mechanic.Tile;

/**
 * This message is used, when a client adds a tile to the Gameboard.
 *
 * @author lurny
 */

public class AddTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  private Tile tile;
  private int newxCoordinate;
  private int newyCoordinate;

  /**
   * This method creates an instance of the class.
   */
  public AddTileMessage(String from, Tile tile, int newX, int newY) {
    super(MessageType.ADD_TILE, from);
    this.tile = tile;
    this.newxCoordinate = newX;
    this.newyCoordinate = newY;
  }

  public Tile getTile() {
    return this.tile;
  }

  public int getNewX() {
    return newxCoordinate;
  }

  public int getNewY() {
    return newyCoordinate;
  }

}
