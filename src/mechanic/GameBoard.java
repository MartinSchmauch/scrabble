package mechanic;

/** @author ldreyer */

public class GameBoard {
  private Field[][] fields;

  public GameBoard(int size) {
    this.fields = new Field[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.fields[i][j] = new Field(i, j);
        this.fields[i][j].setGameBoard(this);
      }
    }
  }

  public Field getField(int xCoordinate, int yCoordinate) {
    return fields[xCoordinate - 1][yCoordinate - 1];
  }

}
