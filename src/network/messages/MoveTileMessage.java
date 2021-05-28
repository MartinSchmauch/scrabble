package network.messages;

/**
 * This message is send, when a user moves a tile from one field to another field.
 *
 * @author lurny
 */

public class MoveTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  // private Tile tile;
  private int newX;
  private int newY;
  private int oldX;
  private int oldY;

  /**
   * Constructor for the MoveTileMessage that is send when a user moves a tile from one field to
   * another field.
   */
  public MoveTileMessage(String from, int oldX, int oldY, int newX,
      int newY) {
    super(MessageType.MOVE_TILE, from);
    this.newX = newX;
    this.newY = newY;
    this.oldX = oldX;
    this.oldY = oldY;
  }


  public int getNewX() {
    return newX;
  }

  public int getNewY() {
    return newY;
  }

  public int getOldX() {
    return oldX;
  }

  public int getOldY() {
    return oldY;
  }

}
