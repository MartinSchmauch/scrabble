package mechanic;

// ** @author lurny

public class Letter {
  private char letter;
  private int letterValue;
  private Field field;

  public Letter(char letter, int letterValue) {
    this.letter = letter;
    this.letterValue = letterValue;
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
}
