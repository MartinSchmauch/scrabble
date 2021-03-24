package mechanic;

// ** @author lurny

public class Letter {
  private char letter;
  private int letterValue;
  private int count;

  public Letter(char letter, int letterValue, int count) {
    this.letter = letter;
    this.letterValue = letterValue;
    this.count = count;
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

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
