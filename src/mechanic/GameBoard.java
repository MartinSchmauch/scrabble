package mechanic;

// ** @author ldreyer

public class GameBoard {
  Field[][] fields;

  public GameBoard(int size) {
    this.fields = new Field[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.fields[i][j] = new Field(i, j);
      }
    }
  }

  public Field getField(int xCoordinate, int yCoordinate) {
    return fields[xCoordinate][yCoordinate];
  }

}
