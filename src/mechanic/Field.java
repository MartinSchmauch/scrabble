package mechanic;

// ** @author ldreyer

public class Field {
  Letter coveredBy;
  int letterMultiplierValue;
  int wordMultiplierValue;
  int xCoordinate;
  int yCoordinate;

  public Field(int letterMultiplierValue, int wordMultiplierValue, int xCoordinate,
      int yCoordinate) {
    this.letterMultiplierValue = letterMultiplierValue;
    this.wordMultiplierValue = wordMultiplierValue;
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

  public int getLetterMultiplierValue() {
    return letterMultiplierValue;
  }

  public int setLetterMultiplierValue() {
    return letterMultiplierValue;
  }

  public int getWordMultiplierValue() {
    return wordMultiplierValue;
  }

  public int setWordMultiplierValue() {
    return wordMultiplierValue;
  }

  public int getxCoordinate() {
    return xCoordinate;
  }

  public int getyCoordinate() {
    return yCoordinate;
  }
}


