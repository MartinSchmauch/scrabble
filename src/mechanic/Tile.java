package mechanic;

import java.io.Serializable;

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
  final boolean isJoker;
  private Letter letter;
  private Field field;
  private boolean isPlayed;
  private boolean onGameBoard;
  private boolean onRack;


  /**
   * This method creates an instance of the class.
   */
  public Tile(Letter letter, Field field) {
    this.letter = letter;
    setField(field);
    this.isJoker = (letter.getCharacter() == '*');
  }

  public Tile(Letter letter) {
    this.letter = letter;
    this.isJoker = (letter.getCharacter() == '*');
  }

  public Field getField() {
    return this.field;
  }

  /**
   * This method and the method setTileOneDirection are used to automatically set the double linked.
   * object connection
   *
   * @author lurny
   */
  public void setField(Field field) {
    if (field == null) {
      this.field.setTileOneDirection(null);
      this.field = null;
    } else {
      this.field = field;
      field.setTileOneDirection(this);
    }
  }

  /**
   * This method is used if you don´t want to set the automatically generated double link between
   * Tile and Field.
   */
  public void setOnlyField(Field field) {
    if (field.getTile() != this) {
      field.setTile(this);
    }
  }

  /**
   * This method sets only one direction of the double linked Tile - Field.
   *
   * @author lurny
   */
  public void setFieldOneDirection(Field field) {
    this.field = field;
  }

  /** If the tile is a joker tile, the value for joker tiles is returned from the GameSettings. */

  public int getValue() {
    if (!isJoker) {
      return this.letter.getLetterValue();
    } else {
      return this.letter.getJokerValue();
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

  /**
   * This method overrides the equal method to compare two tiles.
   *
   * @author lurny
   */
  @Override
  public boolean equals(Object other) {
    Tile t;

    if (other == null || other.getClass() != getClass()) {
      return false;
    } else {
      t = (Tile) other;
      if (t.isPlayed == this.isPlayed && t.onGameBoard == this.onGameBoard
          && t.onRack == this.onRack) {
        if (t.field != null && this.field != null && t.field.equals(this.field)
            || t.field == null && this.field == null) {
          if (t.letter != null && this.letter != null && t.letter.equals(this.letter)
              || t.letter == null && this.letter == null) {
            return true;
          }
        }
      }

      return false;
    }
  }

  // ** @author lurny
  public boolean isOnGameBoard() {
    return onGameBoard;
  }

  // ** @author lurny
  public void setOnGameBoard(boolean onGameBoard) {
    this.onGameBoard = onGameBoard;
    this.onRack = !onGameBoard; // TODO: check that this does not create errors
  }

  // ** @author lurny
  public boolean isOnRack() {
    return onRack;
  }

  // ** @author lurny
  public void setOnRack(boolean onRack) {
    this.onRack = onRack;
    this.onGameBoard = !onRack; // TODO: check that this does not create errors
  }

  /**
   * This method provides the top tile, which lies next to the current tile. If the top field or top
   * tile does not exists the method gives back null.
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
   * This method provides the top bottom, which lies next to the current tile. If the bottom field
   * or bottom tile does not exists the method gives back null.
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
   * This method provides the left tile, which lies next to the current tile. If the left field or
   * left tile does not exists the method gives back null.
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
   * This method provides the right tile, which lies next to the current tile. If the right field or
   * right tile does not exists the method gives back null.
   *
   * @author lurny
   */
  public Tile getRightTile() {

    if (this.getField() == null) {
      System.out.println("######### PROBLEM IN TILE LINE 213");
    }

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

  @Override
  public String toString() {
    if (this.field == null) {
      return "Tile (currently Field=null) with Char " + this.letter.getCharacter();
    }
    return "Tile at Field " + this.field.toString() + " with Char " + this.letter.getCharacter();
  }

}
