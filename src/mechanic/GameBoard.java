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

  /**
   * @author pkoenig
   * @return fields
   */
  public Field[][] getFields() {
    return fields;
  }

  /**
   * @author pkoenig
   * @return rowStrings
   */
  public String[] getRowsAsStrings() {
    String[] res = new String[15];
    for (int j = 0; j < this.fields.length; j++) {
      res[j] = "";
      for (int i = 0; i < this.fields.length; i++) {
        res[j] = res[j] + this.fields[i][j].getTile().getLetter().getChar();
      }
    }
    return res;
  }

}
