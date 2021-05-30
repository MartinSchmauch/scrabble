package mechanic;

import game.GameController;
import game.GameSettings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * An object of this class is used for each player turn. It is used to find all words that emerge
 * from the layd down tiles, to verify those words and to calculate the turn score.
 *
 * @author lurny
 */

public class Turn implements Serializable {
  private static final long serialVersionUID = 1L;
  String player;
  private List<Tile> laydDownTiles;
  private List<Field> laydDownFields;
  private List<Word> words; // Array, that contains all words, that result from the lay down letters
  private int turnScore;
  private boolean isValid;
  private GameController gameController;
  private String stringRepresentation;
  private boolean containedJokerTiles;
  private List<Tile> jokerTiles;


  /**
   * This method creates an instanc of the class.
   * 
   */
  public Turn(String player, GameController gamecontroller) {
    this.gameController = gamecontroller;
    this.player = player;
    this.words = new ArrayList<Word>();
    this.laydDownTiles = new ArrayList<Tile>();
    this.turnScore = 0;
    this.laydDownFields = new ArrayList<Field>();
    this.containedJokerTiles = false;
    this.jokerTiles = new ArrayList<Tile>();
    this.isValid = true;
  }

  /**
   * This method adds the Tile t to the Turn instance.
   */
  public boolean addTileToTurn(Tile t) {
    if (!this.laydDownTiles.contains(t)) {
      this.laydDownTiles.add(t);
      return true;
    }
    return false;
  }

  /**
   * This method removes the Tile t from the Turn instance.
   */
  public boolean removeTileFromTurn(Tile t) {
    return this.laydDownTiles.remove(t);
  }


  /**
   * The calculateWords() method is used to find all words (via calculateWordsHelper(), that emerge
   * from the layd down tiles after a turn is commited. After all words are found, every word is
   * verified with Collins Scrabble Words. If one word does not exists the method returns false.
   *
   * @author ldreyer
   * @author pkoenig
   * @return boolean validWord
   */
  public boolean calculateWords() {
    // skip turn
    if (this.laydDownTiles.isEmpty()) {
      stringRepresentation = "Turn skipped.";
      return true;
    }

    // central star field must be covered
    if (GameSettings.getStarField() != null && GameSettings.getStarField().getTile() == null) {
      stringRepresentation = "Invalid: Star field not covered.";
      return false;
    }
    for (Tile t : this.laydDownTiles) {
      if (t.getLetter().getCharacter() == '*') {
        this.containedJokerTiles = true;
        this.jokerTiles.add(t);
      }
    }
    if (this.containedJokerTiles) {
      int maxIndex = -1;
      for (int i = 0; i < Math.pow(26, this.jokerTiles.size()); i++) {
        for (int k = 0; k < jokerTiles.size(); k++) {
          if (k == 0) {
            this.jokerTiles.get(jokerTiles.size() - k - 1)
                .setLetter(GameSettings.getLetterForChar((char) ('A' + ((i / 1) % 26))));
          } else {
            this.jokerTiles.get(jokerTiles.size() - k - 1)
                .setLetter(GameSettings.getLetterForChar((char) ('A' + ((i / (k * 26)) % 26))));
          }
        }
        if (calculateWordsHelper()) {
          maxIndex = i;
          break;
        }
      }
      if (maxIndex == -1) {
        this.stringRepresentation = "Invalid: No word with joker tile found.";
        for (Tile t : jokerTiles) {
          t.setLetter(GameSettings.getLetters().get('*'));
        }
        this.jokerTiles.clear();
        return false;
      }
      calculateWordsHelper();
      return true;
    } else {
      return calculateWordsHelper();
    }
  }

  /**
   * The calculateWords() method is used to find all words, that emerge from the layd down tiles
   * after a turn is commited. After all words are found, every word is verified with Collins
   * Scrabble Words. If one word does not exists the method returns false.
   *
   * @author ldreyer, lurny
   */
  public boolean calculateWordsHelper() {
    // word list describes the Tiles that build the word
    List<Tile> word = new ArrayList<Tile>();
    List<Tile> newWordTiles = new ArrayList<Tile>();
    this.words = new ArrayList<Word>();

    boolean horizontal = false;
    boolean vertical = false;
    CALCULATE_MAIN_WORD: {
      Tile t = this.laydDownTiles.get(0);
      Tile pivotTile = t;
      word.add(pivotTile);
      newWordTiles.add(pivotTile);

      // check horizontal
      while (t.getRightTile() != null) {
        t = t.getRightTile();
        word.add(t);
        if (!t.isPlayed()) {
          newWordTiles.add(t);
        }
      }
      t = pivotTile;
      while (t.getLeftTile() != null) {
        t = t.getLeftTile();
        word.add(0, t);
        if (!t.isPlayed()) {
          newWordTiles.add(t);
        }
      }

      if (newWordTiles.size() > 1) {
        // System.out.println("horizontal " + word.toString());
        horizontal = true;
        this.words.add(new Word(word));
        break CALCULATE_MAIN_WORD;
      }

      word.clear();
      word.add(pivotTile);

      // check vertical
      t = pivotTile;
      while (t.getTopTile() != null) {
        t = t.getTopTile();
        word.add(0, t);
        if (!t.isPlayed()) {
          newWordTiles.add(t);
        }
      }
      t = pivotTile;
      while (t.getBottomTile() != null) {
        t = t.getBottomTile();
        word.add(t);
        if (!t.isPlayed()) {
          newWordTiles.add(t);
        }
      }

      if (newWordTiles.size() > 1) {
        // System.out.println("vertical " + word.toString());
        vertical = true;
        this.words.add(new Word(word));
        break CALCULATE_MAIN_WORD;
      }

      // System.out.println("single placed letter");
      vertical = true;
      horizontal = true;
    }

    if (newWordTiles.size() < this.laydDownTiles.size()) {
      stringRepresentation = "Invalid: No continous word played.";
      return false;
    }

    word.clear();

    // find additional words
    if (vertical) {
      for (Tile t : newWordTiles) {
        Tile pivotTile = t;
        word.add(pivotTile);
        while (t.getRightTile() != null) {
          t = t.getRightTile();
          word.add(t);
        }
        t = pivotTile;
        while (t.getLeftTile() != null) {
          t = t.getLeftTile();
          word.add(0, t);
        }

        if (word.size() > 1) {
          this.words.add(new Word(word));
        }

        word.clear();
      }
    }

    if (horizontal) {
      for (Tile t : newWordTiles) {
        Tile pivotTile = t;
        word.add(pivotTile);
        while (t.getBottomTile() != null) {
          t = t.getBottomTile();
          word.add(t);
        }
        t = pivotTile;
        while (t.getTopTile() != null) {
          t = t.getTopTile();
          word.add(0, t);
        }

        if (word.size() > 1) {
          this.words.add(new Word(word));
        }

        word.clear();
      }
    }

    // separate words
    if (words.size() == 1 && this.gameController.getScoredTurns().size() > 0
        && newWordTiles.size() == this.words.get(0).getTiles().size()) {
      stringRepresentation = "Invalid: Separate Words.";
      return false;
    }

    // isolated tile
    if (this.words.isEmpty()) {
      stringRepresentation = "Invalid: Single letter is not a valid word.";
      return false;
    }

    // verify words with dictionary
    boolean inDictionary = false;
    if (!this.words.isEmpty()) {
      stringRepresentation = this.words.toString();
      for (Word tileList : this.words) {

        // String representation of the ArrayList "tileList"
        String wordString = tileList.toString();

        for (String s : this.gameController.getDictionary()) {
          if (wordString.equalsIgnoreCase(s)) {
            inDictionary = true;
            break;
          }
        }

        if (!inDictionary) {
          stringRepresentation += "\n" + wordString + " not in dictionary.";
          return false;
        }
        inDictionary = false;

      }
      return true;
    }
    return false;
  }

  /**
   * This method is used to calculate the turn score resulting from all emerging Words. It is
   * called, if the method calculateWords returns true. After the score is calculated relevant
   * special fields WILL NOT BECOME normal fields with the multiplier 1.
   *
   * @author lurny
   * @return turnScore
   */
  public int calculateTurnScore() {
    this.turnScore = 0;
    // calculate word score
    if (this.laydDownTiles.size() == GameSettings.getTilesOnRack()) {
      this.turnScore = GameSettings.getBingo();
    }

    for (Word w : this.words) {
      int localWordMultiplier = 1;
      int singleWordScore = 0;
      this.stringRepresentation += "\n";

      for (Tile t : w.getTiles()) {
        singleWordScore += t.getValue() * t.getField().getLetterMultiplier();
        this.stringRepresentation += t.getLetter().getCharacter() + "["
            + t.getValue() * t.getField().getLetterMultiplier() + "] ";
        localWordMultiplier = localWordMultiplier * t.getField().getWordMultiplier();
      }
      singleWordScore = singleWordScore * localWordMultiplier;
      switch (localWordMultiplier) {
        case 1:
          this.stringRepresentation += ("\nScore " + singleWordScore);
          break;
        case 2:
          this.stringRepresentation += ("\nDouble word score\nScore " + singleWordScore);
          break;
        case 3:
          this.stringRepresentation += ("\nTriple word score\nScore" + singleWordScore);
          break;
        default:
          System.out.println("Error in Turn, calculateTurnScoe()");
          break;
      }
      this.stringRepresentation += "\n";
      this.turnScore = this.turnScore + singleWordScore;
    }

    return this.turnScore;
  }

  /**
   * Ends the current turn and calculates turn score.
   */
  public void endTurn() {
    this.isValid = calculateWords();
    if (this.isValid) {
      calculateTurnScore();

      // set all multipliers to 1
      for (Word w : words) {
        for (Tile t : w.getTiles()) {
          t.getField().setLetterMultiplier(1);
          t.getField().setWordMultiplier(1);
        }
      }
    } else {
      this.words.clear();
    }

    this.getGameController().addTurn(this);
  }

  /**
   * sets the variable laydDownTiles of the current instance.
   */
  public void setLaydDownTiles(List<Tile> laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
  }

  /**
   * gets the variable laydDownTiles of the current instance.
   */
  public List<Tile> getLaydDownTiles() {
    return laydDownTiles;
  }

  /**
   * gets the variable words of the current instance.
   */
  public List<Word> getWords() {
    return words;
  }

  /**
   * gets the variable turnScore of the current instance.
   */
  public int getTurnScore() {
    return turnScore;
  }

  /**
   * sets the variable turnScore of the current instance.
   */
  public void setTurnScore(int turnScore) {
    this.turnScore = turnScore;
  }

  /**
   * gets the variable player of the current instance.
   */
  public String getPlayer() {
    return player;
  }

  /**
   * gets the variable isValid of the current instance.
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * sets the variable isValid of the current instance.
   */
  public void setValid(boolean valid) {
    this.isValid = valid;
  }

  /**
   * This method is for getting a deep copy of a turn instance.
   *
   * @author pkoenig
   * @return deep copy of current instance (tiles won't be "deep copied")
   */
  public Turn getDeepCopy() {
    Turn res = new Turn(this.getPlayer(), this.gameController);
    for (Tile t : this.laydDownTiles) {
      res.laydDownFields.add(t.getField());
    }
    res.player = this.player;
    res.isValid = true; // TODO not so nice
    for (Tile t : this.laydDownTiles) {
      res.laydDownTiles.add(t);
    }
    res.turnScore = this.turnScore;
    ArrayList<Tile> temp = null;
    for (Word w : this.words) {
      temp = new ArrayList<Tile>();
      for (Tile t : w.getTiles()) {
        temp.add(t);
      }
      res.words.add(new Word(temp));
    }
    res.stringRepresentation = this.stringRepresentation;
    return res;
  }

  /**
   * gets the variable gameController of the current instance.
   */
  public GameController getGameController() {
    return gameController;
  }

  /**
   * gets a stringRepresentation.
   */
  public String toString() {
    return stringRepresentation;
  }

  /**
   * sets the variable stringRepresentation of the current instance.
   */
  public void setStringRepresentation(String s) {
    this.stringRepresentation = s;
  }

  /**
   * This method is a to String method, but returns an Array.
   */
  public String[] toStringArray() {
    String[] res = new String[5];
    res[0] = this.stringRepresentation;
    res[1] = "";
    for (Field f : this.laydDownFields) { // list of fields
      res[1] = res[1] + ", " + f;
    }
    res[2] = this.turnScore + "";
    res[3] = this.containedJokerTiles + "";
    for (Tile t : this.jokerTiles) {
      res[4] = res[4] + ", " + t;
    }
    return res;
  }

  /**
   * gets the variable containedStarTiles of the current instance.
   */
  public boolean isContainedStarTiles() {
    return containedJokerTiles;
  }

  /**
   * sets the variable containedStarTiles of the current instance.
   */
  public void setContainedStarTiles(boolean containedStarTiles) {
    this.containedJokerTiles = containedStarTiles;
  }

  /**
   * gets the variable starTiles of the current instance.
   */
  public List<Tile> getStarTiles() {
    return jokerTiles;
  }

  /**
   * sets the starTiles winner of the current instance.
   */
  public void setStarTiles(List<Tile> starTiles) {
    this.jokerTiles = starTiles;
  }

  /**
   * gets the variable laydDownFields of the current instance.
   */
  public List<Field> getLaydDownFields() {
    return laydDownFields;
  }

  /**
   * sets the variable laydDownFileds of the current instance.
   */
  public void setLaydDownFields(List<Field> laydDownFields) {
    this.laydDownFields = laydDownFields;
  }

  /**
   * sets the variable laydDownFields of the current instance.
   */
  public void setLaydDownFields(Field[] currentLocation) {
    this.laydDownFields = Arrays.asList(currentLocation);

  }


}

