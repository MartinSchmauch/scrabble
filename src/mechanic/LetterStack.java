package mechanic;

// ** @author lurny

public class LetterStack {
  private Letter[] letters;
  private boolean isEmpty;

  public LetterStack(Letter[] letters, boolean isEmpty) {
    this.letters = letters;
    this.isEmpty = isEmpty;
  }

  public Letter[] getLetters() {
    return letters;
  }

  public void setLetters(Letter[] letters) {
    this.letters = letters;
  }

  public boolean getIsEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

}
