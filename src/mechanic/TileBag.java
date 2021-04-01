package mechanic;

import java.util.Iterator;
import java.util.List;
import game.GameSettings;

/**
 * The TileBag creates Tile objects for all letters specified in the GameSettings. If the TileBag is
 * empty the isEmpty flag is set to true.
 * 
 * @author ldreyer
 */

public class TileBag {
  private List<Tile> tiles;
  private boolean isEmpty;
  private int remaining;

  public TileBag() {
    List<Letter> letters = (List<Letter>) GameSettings.getLetters().values();
    Iterator<Letter> it = letters.iterator();

    while (it.hasNext()) {
      Letter l = it.next();
      for (int i = 0; i < l.getCount(); i++) {
        tiles.add(new Tile(l, null));
      }
    }

    this.remaining = tiles.size();
    this.isEmpty = tiles.isEmpty();
  }

  /** @return a random Tile from the TileBag */

  public Tile drawTile() {
    Tile tile = tiles.remove((int) (remaining * Math.random()) - 1);
    isEmpty = tiles.isEmpty();
    remaining = tiles.size();
    return tile;
  }

  public boolean getIsEmpty() {
    return isEmpty;
  }

  public int getRemaining() {
    return remaining;
  }

}
