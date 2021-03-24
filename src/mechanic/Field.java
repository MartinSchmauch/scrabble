package mechanic;

// ** @author ldreyer

public class Field {

  private Letter letter;
  private int letterMultiplierValue;
  private int wordMultiplierValue;
  private int xCoordinate;
  private int yCoordinate;

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
  
  public Letter getLetter() {
		return letter;
	}

	public void setLetter(Letter letter) {
		this.letter = letter;
	}

	public int getLetterMultiplierValue() {
		return letterMultiplierValue;
	}

	public void setLetterMultiplierValue(int letterMultiplierValue) {
		this.letterMultiplierValue = letterMultiplierValue;
	}

	public int getWordMultiplierValue() {
		return wordMultiplierValue;
	}

	public void setWordMultiplierValue(int wordMultiplierValue) {
		this.wordMultiplierValue = wordMultiplierValue;
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


