package mechanic;

// ** @author lurny

public class Letter {
  private final char CHAR;
  private final int VALUE;
  private final int COUNT;
  private int remaining;

  public Letter(char letter, int letterValue, int count) {
    this.CHAR = letter;
    this.VALUE = letterValue;
    this.COUNT = count;
    this.remaining = count;
  }

  public char getChar() {
    return this.CHAR;
  }

  public int getLetterValue() {
    return this.VALUE;
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
