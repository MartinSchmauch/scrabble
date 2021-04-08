package mechanic;

import java.util.List;

/**
 * The word class is a help class for the turn. It is used to represent a word and consists of a
 * tile list.
 * 
 * @author lurny
 */

public class Word {
  private List<Tile> tiles;

  public Word(List<Tile> tiles) {
    this.tiles = tiles;
  }

  public List<Tile> getTiles() {
    return tiles;
  }

  /** The to String method returns the Word as a String */
  public String toString() {
    String wordString = "";
    for (Tile t : this.tiles) {
      wordString = wordString + t.getLetter().getChar();
    }
    return wordString;
  }
}
