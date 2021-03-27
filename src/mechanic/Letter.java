package mechanic;

// ** @author ldreyer

public class Letter {
  private final char LETTER;
  private final int VALUE;
  private final int COUNT;

  public Letter(char letter, int letterValue, int count) {
    this.LETTER = letter;
    this.VALUE = letterValue;
    this.COUNT = count;
  }

  public char getLetter() {
    return LETTER;
  }

  public int getLetterValue() {
    return VALUE;
  }

  public int getCount() {
    return COUNT;
  }
}
