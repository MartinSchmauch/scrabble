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
  private int xCoordinate, yCoordinate;

  public RemoveTileMessage(String from, Tile tile) {
    super(MessageType.REMOVE_TILE, from);
    this.tile = tile;
    this.xCoordinate = this.tile.getField().getxCoordinate();
    this.yCoordinate = this.tile.getField().getyCoordinate();
    this.tile.setField(null);
  }

  public Tile getTile() {
    return this.tile;
  }

  public int getxCoordinate() {
    return xCoordinate;
  }

  public int getyCoordinate() {
    return yCoordinate;
  }

}
