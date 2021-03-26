package mechanic;

// ** @author lurny

public class TileBag {
  private Tile[] tiles;
  private boolean isEmpty;
  private int remaining;

  public TileBag(Tile[] tiles, boolean isEmpty) {
    this.tiles = tiles;
    this.isEmpty = isEmpty;
  }

  public Tile[] getTiles() {
    return tiles;
  }

  public void setLetters(Tile[] tiles) {
    this.tiles = tiles;
  }

  public boolean getIsEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  public int getRemaining() {
    return remaining;
  }

  public void setRemaining(int remaining) {
    this.remaining = remaining;
  }

}
