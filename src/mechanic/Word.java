package mechanic;

import java.util.List;

// ** @author lurny

public class Word {
  private List<Tile> tiles;

  public Word(List<Tile> tiles) {
    this.tiles = tiles;
  }

  public List<Tile> getTiles() {
    return tiles;
  }
  
  public String toString() {
		String wordString = "";
		for(Tile t: this.tiles) {
			wordString = wordString + t.getLetter().getChar();
		}
		return wordString;
  }
}
