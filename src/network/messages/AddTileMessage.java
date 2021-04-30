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
  private int newXCoordinate, newYCoordinate;

  public AddTileMessage(String from, Tile tile, int newXCoordinate, int newYCoordinate) {
    super(MessageType.ADD_TILE, from);
    this.tile = tile;
    this.newXCoordinate = newXCoordinate;
    this.newYCoordinate = newYCoordinate;
  }

  public Tile getTile() {
    return this.tile;
  }

  public int getNewXCoordinate() {
    return newXCoordinate;
  }

  public int getNewYCoordinate() {
    return newYCoordinate;
  }

}
