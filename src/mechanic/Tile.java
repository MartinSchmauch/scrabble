package mechanic;

import game.GameSettings;

/** @author ldreyer */

public class Tile {

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

  public void setField(Field field) {
    field.setTile(this);
    this.field = field;
  }

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

  // ** @author lurny
  public Tile getTopTile() {
    Field f = this.getField().getTop();
    if (f.equals(null)) {
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

  // ** @author lurny
  public Tile getBottomTile() {
    Field f = this.getField().getBottom();
    if (f.equals(null)) {
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

  // ** @author lurny
  public Tile getLeftTile() {
    Field f = this.getField().getLeft();
    if (f.equals(null)) {
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

  // ** @author lurny
  public Tile getRightTile() {
    Field f = this.getField().getRight();
    if (f.equals(null)) {
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
