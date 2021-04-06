package mechanic;

/**
 * This class is used for storing all available letters and how many tiles of the respective letter
 * are generated for the TileBag. The letter object is an attribute of every tile, defining its
 * identity. All attributes are final. Letters are generated from the GameSettings.
 * 
 * @author ldreyer
 */

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
