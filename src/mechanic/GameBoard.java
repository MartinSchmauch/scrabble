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
  private Wordlist wordlist;

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
    // File file = new File(GameSettings.getDictionary());
    // this.setWordlist(new Wordlist(file));
  }

  public Field getField(int xCoordinate, int yCoordinate) {
    if (xCoordinate < 1 || yCoordinate < 1 || xCoordinate > this.fields.length
        || yCoordinate > this.fields.length) {
      return null;
    }
    return fields[xCoordinate - 1][yCoordinate - 1];
  }

  /**
   * This method gives back the field array.
   *
   * @author pkoenig
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
        res[j] = res[j] + this.fields[i][j].getTile().getLetter().getCharacter();
      }
    }
    return res;
  }

  public Wordlist getWordlist() {
    return wordlist;
  }


  public void setWordlist(Wordlist wordlist) {
    this.wordlist = wordlist;
  }

}
