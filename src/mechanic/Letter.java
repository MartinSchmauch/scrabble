package mechanic;

// ** @author ldreyer

public class Letter {
  private final char CHAR;
  private final int VALUE;
  private final int COUNT;

  public Letter(char letter, int letterValue, int count) {
    this.CHAR = letter;
    this.VALUE = letterValue;
    this.COUNT = count;
  }

  public char getChar() {
    return this.CHAR;
  }

  public int getLetterValue() {
    return this.VALUE;
  }

  public int getCount() {
    return COUNT;
  }
}
