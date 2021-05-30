package mechanic;

/**
 * The GameBoard class contains all GameBoard fields in a two-dimensional array. The arrays start at
 * zero while the general coordinate system is implemented starting at one. This is respected in the
 * getter-method.
 *
 * @author ldreyer
 */

public class GameBoard {
  private Field[][] fields;

  /**
   * This method creates an instance of the class.
   */
  public GameBoard(int size) {
    this.fields = new Field[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.fields[i][j] = new Field(i + 1, j + 1);
        this.fields[i][j].setGameBoard(this);
      }
    }
  }

  /**
   * gives back the Gameboard Field at x1Coordinate and y1Coordinate.
   */
  public Field getField(int x1Coordinate, int y1Coordinate) {
    if (x1Coordinate < 1 || y1Coordinate < 1 || x1Coordinate > this.fields.length
        || y1Coordinate > this.fields.length) {
      return null;
    }
    return fields[x1Coordinate - 1][y1Coordinate - 1];
  }

  /**
   * This method gives back the field array.
   *
   * @author pkoenig
   */
  public Field[][] getFields() {
    return fields;
  }

}
