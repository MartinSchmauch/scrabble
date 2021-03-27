package mechanic;

// ** @author ldreyer

public class Field {

  private GameBoard gameBoard;
  private Tile tile;
  private int xCoordinate;
  private int yCoordinate;
  private int letterMultiplier;
  private int wordMultiplier;

  public Field(int letterMultiplier, int wordMultiplier, int xCoordinate, int yCoordinate, GameBoard gameBoard) {
	this.gameBoard = gameBoard;
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.letterMultiplier = letterMultiplier;
    this.wordMultiplier = wordMultiplier;
  }

  public Field(int xCoordinate, int yCoordinate, GameBoard gameBoard) {
	this.gameBoard = gameBoard;
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
  
  // ** @author lurny
  public Field getLeft() {
	  if(this.xCoordinate>0) {
		  return this.gameBoard.getField(this.xCoordinate-1, this.yCoordinate);
	  }
	  else {
		  return null;
	  }
  }
  
  // ** @author lurny
  public Field getRight() {
	  if(this.xCoordinate<=13) {
		  return this.gameBoard.getField(this.xCoordinate+1, this.yCoordinate);
	  }
	  else {
		  return null;
	  }
  }
  
  // ** @author lurny
  public Field getTop() {
	  if(this.yCoordinate>0) {
		  return this.gameBoard.getField(this.xCoordinate, this.yCoordinate-1);
	  }
	  else {
		  return null;
	  }
  }
  
  // ** @author lurny
  public Field getBottom() {
	  if(this.yCoordinate<=13) {
		  return this.gameBoard.getField(this.xCoordinate, this.yCoordinate+1);
	  }
	  else {
		  return null;
	  }
  }
}


