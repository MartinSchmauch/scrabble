package mechanic;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  private transient GameBoard gameBoard;
  @JsonIgnore
  private Tile tile;

  private int x1; // starting at 1
  private int y1; // starting at 1
  private int letterMultiplier;
  private int wordMultiplier;

  /**
   * This method creates an instance of the class.
   */
  public Field(int letterMultiplier, int wordMultiplier, int x, int y) {
    this.x1 = x;
    this.y1 = y;
    this.letterMultiplier = letterMultiplier;
    this.wordMultiplier = wordMultiplier;
  }

  /**
   * This method creates an instance of the class.
   */
  public Field(int x, int y) {
    this.x1 = x;
    this.y1 = y;
    this.letterMultiplier = 1;
    this.wordMultiplier = 1;
  }

  public Tile getTile() {
    return tile;
  }

  /**
   * This method and the method setFieldOneDirection are used to automatically set the double linked
   * object connection.
   *
   * @author lurny
   */
  public void setTile(Tile tile) {
    if (tile == null) {
      if (this.tile != null) {
        this.tile.setFieldOneDirection(null);
      }
      this.tile = null;
    } else {
      this.tile = tile;
      tile.setFieldOneDirection(this);
    }
  }

  /**
   * This method is called by the setField method to ensure the double link object connection.
   *
   * @author lurny
   */
  public void setTileOneDirection(Tile t) {
    this.tile = t;
  }

  /**
   * This method is used if you only want to set the Tile, without the automatically generated link
   * from Tile to Field.
   */
  public void setOnlyTile(Tile tile) {
    if (tile.getField() != this) {
      tile.setField(this);
    }
    this.tile = tile;
  }


  public int getxCoordinate() {
    return x1;
  }

  public int getyCoordinate() {
    return y1;
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
   * This method provides the left field, which lies next to the current field. If the field does
   * not exist, the method gives back null.
   *
   * @author lurny
   */
  @JsonIgnore
  public Field getLeft() {
    if (this.x1 > 1) {
      return this.gameBoard.getField(this.x1 - 1, this.y1);
    } else {
      return null;
    }
  }

  /**
   * This method provides the right field, which lies next to the current field. If the field does
   * not exist, the method gives back null.
   *
   * @author lurny
   */
  @JsonIgnore
  public Field getRight() {
    if (this.x1 <= 14) {
      return this.gameBoard.getField(this.x1 + 1, this.y1);
    } else {
      return null;
    }
  }

  /**
   * This method provides the top field, which lies next to the current field. If the field does not
   * exist, the method gives back null.
   *
   * @author lurny
   */
  @JsonIgnore
  public Field getTop() {
    if (this.y1 > 1) {
      return this.gameBoard.getField(this.x1, this.y1 - 1);
    } else {
      return null;
    }
  }

  /**
   * This method provides the bottom field, which lies next to the current field. If the field does
   * not exist, the method gives back null.
   *
   * @author lurny
   */
  @JsonIgnore
  public Field getBottom() {
    if (this.y1 <= 14) {
      return this.gameBoard.getField(this.x1, this.y1 + 1);
    } else {
      return null;
    }
  }

  /**
   * This method provides a String representation of the the Field coordinates.
   *
   * @author pkoenig
   */
  @Override
  public String toString() {
    return "(x, y): = (" + this.x1 + ", " + this.y1 + ")";
  }

  /**
   * This method is used to check if a field equals another field.
   *
   * @author lurny
   */
  @Override
  public boolean equals(Object other) {
    Field t;

    if (other == null || other.getClass() != getClass()) {
      return false;
    } else {
      t = (Field) other;
      if (t.letterMultiplier == this.letterMultiplier && t.wordMultiplier == this.wordMultiplier
          && t.x1 == this.x1 && t.y1 == this.y1) {
        return true;
      } else {
        return false;
      }
    }
  }
}


