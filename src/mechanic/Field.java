package mechanic;

// ** @author ldreyer

public class Field {
  Letter coveredBy;
  int letterMultiplier;
  int wordMultiplier;
  int xCoordinate;
  int yCoordinate;

  public Field(int letterMultiplier, int wordMultiplier, int xCoordinate, int yCoordinate) {
    this.letterMultiplier = letterMultiplier;
    this.wordMultiplier = wordMultiplier;
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
  }

  public Field(int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
  }

  public Letter getCoveredBy() {
    return coveredBy;
  }

  public void setCoveredBy(Letter coveredBy) {
    this.coveredBy = coveredBy;
  }

  public int getLetterMultiplier() {
    return letterMultiplier;
  }

  public int setLetterMultiplier() {
    return letterMultiplier;
  }

  public int getWordMultiplier() {
    return wordMultiplier;
  }

  public int setWordMultiplier() {
    return wordMultiplier;
  }

  public int getxCoordinate() {
    return xCoordinate;
  }

  public int getyCoordinate() {
    return yCoordinate;
  }
}


