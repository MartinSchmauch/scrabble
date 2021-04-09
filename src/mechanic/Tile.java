package mechanic;

import java.io.Serializable;
import game.GameSettings;

/**
 * The Tile class is essential part of the domain model. It has a Field attribute that refers to the
 * field that is covered by the tile or is null if the tile is not covering any field. The other way
 * around, every field has an attribute knowing which tile covers it. This double-linked relation is
 * automatically established when setting a field for a tile. If the Tile is a joker Tile it has the
 * boolean flag set true.
 * 
 * @author ldreyer
 */

public class Tile implements Serializable {
  private static final long serialVersionUID = 1L;
  final boolean IS_JOKER;
  private Letter letter;
  private Field field;
  private boolean isPlayed;
  private boolean onGameBoard;
  private boolean onRack;


  public Tile(Letter letter, Field field) {
    this.letter = letter;
    this.field = field;
    this.IS_JOKER = (letter.getChar() == '*');
  }

  public Field getField() {
    return this.field;
  }

  /**
   * This method and the method setTileOneDirection are used to automatically set the double linked
   * object connection
   * 
   * @author lurny
   * @param tile
   */
  public void setField(Field field) {
    this.field = field;
    field.setTileOneDirection(this);
  }

  /** @author lurny */
  public void setFieldOneDirection(Field field) {
    this.field = field;
  }

  /** If the tile is a joker tile, the value for joker tiles is returned from the GameSettings. */

  public int getValue() {
    if (!IS_JOKER) {
      return this.letter.getLetterValue();
    } else {
      return GameSettings.getLetters().get('*').getLetterValue();
    }
  }

  public Letter getLetter() {
    return this.letter;
  }

  public void setLetter(Letter letter) {
    this.letter = letter;
  }

  public boolean isPlayed() {
    return isPlayed;
  }

  public void setPlayed(boolean played) {
    this.isPlayed = played;
  }

  // ** @author lurny
  public boolean isOnGameBoard() {
    return onGameBoard;
  }

  // ** @author lurny
  public void setOnGameBoard(boolean onGameBoard) {
    this.onGameBoard = onGameBoard;
  }

  // ** @author lurny
  public boolean isOnRack() {
    return onRack;
  }

  // ** @author lurny
  public void setOnRack(boolean onRack) {
    this.onRack = onRack;
  }

  /**
   * This method returns the top tile, which lies next to the current tile. If the top field or top
   * tile does not exists the method returns null.
   * 
   * @author lurny
   */
  public Tile getTopTile() {
    Field f = this.getField().getTop();
    if (f == null) {
      return null;
    } else {
      Tile t = f.getTile();
      if (t == null) {
        return null;
      } else {
        return t;
      }
    }
  }

  /**
   * This method returns the top bottom, which lies next to the current tile. If the bottom field or
   * bottom tile does not exists the method returns null.
   * 
   * @author lurny
   */
  public Tile getBottomTile() {
    Field f = this.getField().getBottom();
    if (f == null) {
      return null;
    } else {
      Tile t = f.getTile();
      if (t == null) {
        return null;
      } else {
        return t;
      }
    }
  }

  /**
   * This method returns the left tile, which lies next to the current tile. If the left field or
   * left tile does not exists the method returns null.
   * 
   * @author lurny
   */
  public Tile getLeftTile() {
    Field f = this.getField().getLeft();
    if (f == null) {
      return null;
    } else {
      Tile t = f.getTile();
      if (t == null) {
        return null;
      } else {
        return t;
      }
    }
  }

  /**
   * This method returns the right tile, which lies next to the current tile. If the right field or
   * right tile does not exists the method returns null.
   * 
   * @author lurny
   */
  public Tile getRightTile() {
    Field f = this.getField().getRight();
    if (f == null) {
      return null;
    } else {
      Tile t = f.getTile();
      if (t == null) {
        return null;
      } else {
        return t;
      }
    }
  }

}
