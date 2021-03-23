package mechanic;

// ** @author lurny

public class Word {
  private Letter[] letters;
  private int wordScore;
  private boolean isValid;

  public Word(Letter[] letters) {
    this.letters = letters;
    this.wordScore = 0;
    this.isValid = false;
  }

  public Letter[] getLetters() {
    return letters;
  }

  public void setLetters(Letter[] letters) {
    this.letters = letters;
  }

  public int getWordScore() {
    return wordScore;
  }

  public void setWordScore(int wordScore) {
    this.wordScore = wordScore;
  }

  public boolean getIsValid() {
    return isValid;
  }

  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }
}
