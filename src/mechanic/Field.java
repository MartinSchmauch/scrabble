package mechanic;

import java.io.Serializable;

/**
 * The Field class is essential part of the domain model. It has a Tile attribute that refers to the
 * tile covering the field or is null if the field is free. The other way around, every tile has an
 * attribute knowing on which field it lies. This double-linked relation is automatically
 * established when setting a tile to a field. The word and letter multiplier values are one by
 * default and higher if the field is a special field.
 * 
 * @author ldreyer
 */

public class Field implements Serializable {

  private static final long serialVersionUID = 1L;
  private GameBoard gameBoard;
  private Tile tile;
  private int xCoordinate; // starting at 1
  private int yCoordinate; // starting at 1
  private int letterMultiplier;
  private int wordMultiplier;

  public Field(int letterMultiplier, int wordMultiplier, int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.letterMultiplier = letterMultiplier;
    this.wordMultiplier = wordMultiplier;
  }

  public Field(int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.letterMultiplier = 1;
    this.wordMultiplier = 1;
  }

  public Tile getTile() {
    return tile;
  }

  /**
   * This method and the method setFieldOneDirection are used to automatically set the double linked
   * object connection
   * 
   * @author lurny
   * @param tile
   */
  public void setTile(Tile tile) {
    this.tile = tile;
    tile.setFieldOneDirection(this);
  }

  public void setOnlyTile(Tile tile) {
    if (tile.getField() != this) {
      tile.setField(this);
    }
    this.tile = tile;
  }

  /** @author lurny */
  public void setTileOneDirection(Tile t) {
    this.tile = t;
  }

  public int getxCoordinate() {
    return xCoordinate;
  }

  public void setxCoordinate(int xCoordinate) {
    this.xCoordinate = xCoordinate;
  }

  public int getyCoordinate() {
    return yCoordinate;
  }

  public void setyCoordinate(int yCoordinate) {
    this.yCoordinate = yCoordinate;
  }

  public int getLetterMultiplier() {
    return letterMultiplier;
  }

  public void setLetterMultiplier(int letterMultiplier) {
    this.letterMultiplier = letterMultiplier;
  }

  public int getWordMultiplier() {
    return wordMultiplier;
  }

  public void setWordMultiplier(int wordMultiplier) {
    this.wordMultiplier = wordMultiplier;
  }

  public void setGameBoard(GameBoard gameBoard) {
    this.gameBoard = gameBoard;
  }

  /**
   * This method returns the left field, which lies next to the current field. If the field does not
   * exist, the method returns null.
   * 
   * @author lurny
   */
  public Field getLeft() {
    if (this.xCoordinate > 1) {
      return this.gameBoard.getField(this.xCoordinate - 1, this.yCoordinate);
    } else {
      return null;
    }
  }

  /**
   * This method returns the right field, which lies next to the current field. If the field does
   * not exist, the method returns null.
   * 
   * @author lurny
   */
  public Field getRight() {
    if (this.xCoordinate <= 14) {
      return this.gameBoard.getField(this.xCoordinate + 1, this.yCoordinate);
    } else {
      return null;
    }
  }

  /**
   * This method returns the top field, which lies next to the current field. If the field does not
   * exist, the method returns null.
   * 
   * @author lurny
   */
  public Field getTop() {
    if (this.yCoordinate > 1) {
      return this.gameBoard.getField(this.xCoordinate, this.yCoordinate - 1);
    } else {
      return null;
    }
  }

  /**
   * This method returns the bottom field, which lies next to the current field. If the field does
   * not exist, the method returns null.
   * 
   * @author lurny
   */
  public Field getBottom() {
    if (this.yCoordinate <= 14) {
      return this.gameBoard.getField(this.xCoordinate, this.yCoordinate + 1);
    } else {
      return null;
    }
  }

  /**
   * @author pkoenig
   */
  @Override
  public String toString() {
    return "(x, y): = (" + this.xCoordinate + ", " + this.yCoordinate + ")";
  }

  /**
   * @author lurny
   */
  @Override
  public boolean equals(Object other) {
    Field t;

    if (other == null || other.getClass() != getClass()) {
      return false;
    } else {
      t = (Field) other;
      if (t.gameBoard.equals(this.gameBoard) && t.letterMultiplier == this.letterMultiplier
          && t.wordMultiplier == this.wordMultiplier && t.xCoordinate == this.xCoordinate
          && t.yCoordinate == this.yCoordinate) {
        return true;
      } else {
        return false;
      }
    }
  }
}


