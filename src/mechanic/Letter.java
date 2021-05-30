package mechanic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

/**
 * This class is used for storing all available letters and how many tiles of the respective letter
 * are generated for the TileBag. The letter object is an attribute of every tile, defining its
 * identity. All attributes are final. Letters are generated from the GameSettings.
 *
 * @author ldreyer
 */

public class Letter implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonIgnore
  private char character;

  private int value;
  private int jokerValue;
  private int count;

  /** Creates letter instance. */
  public Letter(char letter, int letterValue, int count) {
    this.character = letter;
    this.value = letterValue;
    this.count = count;
  }

  /**
   * gets the variable character of the current instance.
   */
  public char getCharacter() {
    return this.character;
  }

  /**
   * gets the variable letterValue of the current instance.
   */
  public int getLetterValue() {
    return this.value;
  }

  /**
   * gets the variable count of the current instance.
   */
  public int getCount() {
    return this.count;
  }
  
  /**
   * gets the variable jokerValue of the current instance.
   */
  public int getJokerValue() {
    return this.jokerValue;
  }

  /**
   * sets the variable jokerValue of the current instance.
   */
  public void setJokerValue(int jokerValue) {
    this.jokerValue = jokerValue;
  }

}
