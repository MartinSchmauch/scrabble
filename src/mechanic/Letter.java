package mechanic;

// ** @author lurny

public class Letter {
  private final char LETTER;
  private final int VALUE;
  private final int COUNT;
  private int remaining;

  public Letter(char letter, int letterValue, int count) {
    this.LETTER = letter;
    this.VALUE = letterValue;
    this.COUNT = count;
    this.remaining = count;
  }

  public char getLetter() {
    return LETTER;
  }

  public int getLetterValue() {
    return VALUE;
  }

  // ** @author ldreyer
  public int getCount() {
    return COUNT;
  }

  // ** @author ldreyer
  public int getRemaining() {
    return this.remaining;
  }

  // ** @author ldreyer
  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }
}
