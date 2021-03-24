package mechanic;

// ** @author ldreyer

public class Field {

  private Letter letter;
  private int letterMultiplier;
  private int wordMultiplier;
  private int xCoordinate;
  private int yCoordinate;

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

  public Letter getLetter() {
    return letter;
  }

  public void setLetter(Letter letter) {
    this.letter = letter;
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
}


