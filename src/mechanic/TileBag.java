package mechanic;

import java.util.ArrayList;
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


  /**
   * Constructor fills tile bag according to the game settings configuration.
   */

  public TileBag() {
    List<Letter> letters = new ArrayList<Letter>(GameSettings.getLetters().values());
    Iterator<Letter> it = letters.iterator();
    this.tiles = new ArrayList<Tile>();
    while (it.hasNext()) {
      Letter l = it.next();
      for (int i = 0; i < l.getCount(); i++) {
        tiles.add(new Tile(l));
      }
    }

    this.remaining = tiles.size();
    this.isEmpty = tiles.isEmpty();
  }

  /**
   * This method chooses a random tile from the tile bag and removes it.
   *
   * @return a random Tile from the TileBag
   */

  public Tile drawTile() {
    Tile tile = tiles.remove((int) ((remaining - 1) * Math.random()));
    isEmpty = tiles.isEmpty();
    remaining = tiles.size();
    return tile;
  }

  /**
   * This method returns a specific tile, without removing it fromthe lost
   * 
   * @author nilbecke
   * @param input
   * @return
   */

  public Tile drawTile(Character input) {
    for (int i = 0; i < tiles.size(); i++) {
      if (tiles.get(i).getLetter().getCharacter() == input) {
        return tiles.get(i);
      }
    }
    return null;
  }



  /**
   * This method adds a (tile in case of a tile swap) to the tileBag.
   * 
   * @author lurny
   * 
   * @param tile
   */
  public void addTile(Tile tile) {
    this.tiles.add(tile);
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public int getRemaining() {
    return remaining;
  }

}
