package mechanic;

// ** @author lurny

public class Letter {
  private char letter;
  private int letterValue;
  private int count;
  private Field field;

  public Letter(char letter, int letterValue, int count, Field field) {
    this.letter = letter;
    this.letterValue = letterValue;
    this.count = count;
    this.field = field;
  }

  public char getLetter() {
    return letter;
  }

  public void setLetter(char letter) {
    this.letter = letter;
  }

  public int getLetterValue() {
    return letterValue;
  }

  public void setLetterValue(int letterValue) {
    this.letterValue = letterValue;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  // ** @author ldreyer
  public int getCount() {
    return count;
  }

  // ** @author ldreyer
  public void setCount(int count) {
    this.count = count;
  }
}
