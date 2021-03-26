package mechanic;

// ** @author ldreyer

public class Field {

  private Tile tile;
  private int xCoordinate;
  private int yCoordinate;
  private int letterMultiplier;
  private int wordMultiplier;

  public Field(int letterMultiplier, int wordMultiplier, int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.letterMultiplier = letterMultiplier;
    this.wordMultiplier = wordMultiplier;
  }

  public Field(int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.letterMultiplier = 1;
    this.wordMultiplier = 1;
  }

  public Tile getTile() {
    return tile;
  }

  public void setTile(Tile tile) {
    this.tile = tile;
  }

  public int getxCoordinate() {
    return xCoordinate;
  }

  public void setxCoordinate(int xCoordinate) {
    this.xCoordinate = xCoordinate;
  }

  public int getyCoordinate() {
    return yCoordinate;
  }

  public void setyCoordinate(int yCoordinate) {
    this.yCoordinate = yCoordinate;
  }

  public int getLetterMultiplier() {
    return letterMultiplier;
  }

  public void setLetterMultiplier(int letterMultiplier) {
    this.letterMultiplier = letterMultiplier;
  }

  public int getWordMultiplier() {
    return wordMultiplier;
  }

  public void setWordMultiplier(int wordMultiplier) {
    this.wordMultiplier = wordMultiplier;
  }
}


