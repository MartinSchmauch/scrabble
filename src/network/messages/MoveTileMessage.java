package network.messages;

/**
 * This message is send, when a user moves a tile from one field to another field.
 * 
 * @author lurny
 */

public class MoveTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  // private Tile tile;
  private int newXCoordinate, newYCoordinate, oldXCoordinate, oldYCoordinate;

  public MoveTileMessage(String from, int oldXCoordinate, int oldYCoordinate, int newXCoordinate,
      int newYCoordinate) {
    super(MessageType.MOVE_TILE, from);
    this.newXCoordinate = newXCoordinate;
    this.newYCoordinate = newYCoordinate;
    this.oldXCoordinate = oldXCoordinate;
    this.oldYCoordinate = oldYCoordinate;
  }


  public int getNewXCoordinate() {
    return newXCoordinate;
  }

  public int getNewYCoordinate() {
    return newYCoordinate;
  }

  public int getOldXCoordinate() {
    return oldXCoordinate;
  }

  public int getOldYCoordinate() {
    return oldYCoordinate;
  }

}
