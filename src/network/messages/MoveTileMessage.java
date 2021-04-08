package network.messages;

import mechanic.Field;
import mechanic.Tile;

/**
 * This message is send, when a user moves a tile from one field to another field.
 * 
 * @author lurny
 */

public class MoveTileMessage extends Message {

  private static final long serialVersionUID = 1L;

  private Tile tile;
  private Field newField;

  public MoveTileMessage(String from, Tile tile, Field newField) {
    super(MessageType.MOVE_TILE, from);
    this.tile = tile;
    this.newField = newField;
  }

  public Tile getTile() {
    return this.tile;
  }

  public Field getNewField() {
    return newField;
  }

}
