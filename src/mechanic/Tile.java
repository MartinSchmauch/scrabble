package mechanic;

import game.GameSettings;

// ** @author ldreyer
public class Tile {

  final boolean IS_JOKER;

  private Letter letter;
  private Field field;

  boolean onGameBoard;
  boolean onRack;


  public Tile(Letter letter, Field field) {
    this.letter = letter;
    this.field = field;
    this.IS_JOKER = (letter.getLetter() == '*');
  }

  public Field getField() {
    return this.field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public int getValue() {
    if (!IS_JOKER) {
      return this.letter.getLetterValue();
    } else {
      return GameSettings.getLetters().get('*').getLetterValue();
    }
  }

  public int getLetter() {
    return this.letter.getLetter();
  }

  public void setLetter(Letter letter) {
    this.letter = letter;
  }

}
