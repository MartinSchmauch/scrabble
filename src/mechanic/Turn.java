package mechanic;

import game.GameController;
import game.GameSettings;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");
  private static File file = new File(baseDir + "CollinsScrabbleWords.txt");
  private boolean containedStarTiles;
  private List<Tile> starTiles;


  public Turn(String player, GameController gamecontroller) {
    this.gameController = gamecontroller;
    this.player = player;
    this.words = new ArrayList<Word>();
    this.laydDownTiles = new ArrayList<Tile>();
    this.turnScore = 0;
    this.stringRepresentation = "not calculated";
    this.laydDownFields = new ArrayList<Field>();
    this.containedStarTiles = false;
    this.starTiles = new ArrayList<Tile>();
}

  public boolean addTileToTurn(Tile t) {
    if (!this.laydDownTiles.contains(t)) {
      this.laydDownTiles.add(t);
      return true;
    }
    return false;
  };

  public boolean removeTileFromTurn(Tile t) {
    return this.laydDownTiles.remove(t);
  };

  public boolean moveTileInTurn(Tile t, Field newField) {
    this.laydDownTiles.remove(t);
    t.setField(newField);
    return this.laydDownTiles.add(t);
  };


  /**
   * The calculateWords() method is used to find all words (via calculateWordsHelper(), that emerge from the layd down tiles
   * after a turn is commited. After all words are found, every word is verified with Collins
   * Scrabble Words. If one word does not exists the method returns false.
   */
  public boolean calculateWords() {
    for (Tile t : this.laydDownTiles) {
      this.laydDownFields.add(t.getField());
    }
    // skip turn
    if (this.laydDownTiles.isEmpty()) {
      stringRepresentation = "Turn skipped.";
      return true;
    }

    Field starField = this.getGameController().getGameState().getGameBoard().getField(8, 8);

    // central star field must be covered
    if (starField.getTile() == null) {
      stringRepresentation = "Invalid: Star field not covered.";
      return false;
    }
    for (Tile t : this.laydDownTiles) {
      if (t.getLetter().getCharacter() == '*') {
        this.containedStarTiles = true;
        this.starTiles.add(t);
      }
    }
    if (this.containedStarTiles) {
      int maxScore = 0;
      int maxIndex = -1;
      for (int i = 0; i < Math.pow(26, this.starTiles.size()); i++) {
        for (int k = 0; k < starTiles.size(); k++) {
          if (k == 0) {
            this.starTiles.get(starTiles.size() - k - 1).setLetter(GameSettings.getLetterForChar((char) ('A' + ((i / 1) % 26))));
          }
          else {
            this.starTiles.get(starTiles.size() - k - 1).setLetter(GameSettings.getLetterForChar((char) ('A' + ((i / (k*26)) % 26))));
          }
        }
        if (calculateWordsHelper()) {
          if (maxScore < calculateTurnScore()) {
            maxScore = getTurnScore();
            maxIndex = i;
          }
        }
      }
      if (maxIndex == -1) {
        return false;
      }
      else {
        for (int k = 0; k < this.starTiles.size(); k++) {
          if (k == 0) {
            this.starTiles.get(this.starTiles.size() - k - 1).setLetter(GameSettings.getLetterForChar((char) ('A' + ((maxIndex / 1) % 26))));
          }
          else {
            this.starTiles.get(this.starTiles.size() - k - 1).setLetter(GameSettings.getLetterForChar((char) ('A' + ((maxIndex / (k*26)) % 26))));
          }
        }
        calculateWordsHelper();
        return true;
      }
    }
    else {
      return calculateWordsHelper();
    }
  }

  /**
   * The calculateWords() method is used to find all words, that emerge from the layd down tiles
   * after a turn is commited. After all words are found, every word is verified with Collins
   * Scrabble Words. If one word does not exists the method returns false.
   */
  public boolean calculateWordsHelper() {
    Field starField = this.getGameController().getGameState().getGameBoard().getField(8, 8);
    // word list describes the Tiles that build the word
    List<Tile> word = new ArrayList<Tile>();
    this.words = new ArrayList<Word>();

    int playedTiles = 0;
    int scoredTiles = 0;
    boolean horizontal = false;
    boolean vertical = false;
    CALCULATE_MAIN_WORD: {
      Tile t = this.laydDownTiles.get(0);
      Tile pivotTile = t;
      word.add(pivotTile);

      // check horizontal
      while (t.getRightTile() != null) {
        t = t.getRightTile();
        word.add(t);
        if (t.isPlayed()) {
          playedTiles++;
        }
      }
      t = pivotTile;
      while (t.getLeftTile() != null) {
        t = t.getLeftTile();
        word.add(0, t);
        if (t.isPlayed()) {
          playedTiles++;
        }
      }

      if (word.size() > 1) {
        // System.out.println("horizontal " + word.toString());
        horizontal = true;
        break CALCULATE_MAIN_WORD;
      }

      // check vertical
      t = pivotTile;
      while (t.getTopTile() != null) {
        t = t.getTopTile();
        word.add(0, t);
        if (t.isPlayed()) {
          playedTiles++;
        }
      }
      t = pivotTile;
      while (t.getBottomTile() != null) {
        t = t.getBottomTile();
        word.add(t);
        if (t.isPlayed()) {
          playedTiles++;
        }
      }

      if (word.size() > 1) {
        // System.out.println("vertical " + word.toString());
        vertical = true;
        break CALCULATE_MAIN_WORD;
      }

      // System.out.println("isolated");
      stringRepresentation = "Invalid: Single letter is not a word.";
      return false;
    }

    // add main word to word list
    this.words.add(new Word(word));
    scoredTiles += word.size();
    word.clear();

    // find additional words
    if (vertical) {
      for (Tile t : this.laydDownTiles) {
        Tile pivotTile = t;
        word.add(pivotTile);
        while (t.getRightTile() != null) {
          t = t.getRightTile();
          if (!t.isPlayed()) {
            stringRepresentation = "Invalid: No continous word played.";
            return false;
          }
          playedTiles++;
          word.add(t);
        }
        t = pivotTile;
        while (t.getLeftTile() != null) {
          t = t.getLeftTile();
          if (!t.isPlayed()) {
            stringRepresentation = "Invalid: No continous word played.";
            return false;
          }
          playedTiles++;
          word.add(0, t);
        }

        if (word.size() > 1) {
          this.words.add(new Word(word));
          scoredTiles += word.size();
        }

        word.clear();
      }
    } else if (horizontal) {
      for (Tile t : this.laydDownTiles) {
        Tile pivotTile = t;
        word.add(pivotTile);
        while (t.getBottomTile() != null) {
          t = t.getBottomTile();
          if (!t.isPlayed()) {
            stringRepresentation = "Invalid: No continous word played.";
            return false;
          }
          playedTiles++;
          word.add(t);
        }
        t = pivotTile;
        while (t.getTopTile() != null) {
          t = t.getTopTile();
          if (!t.isPlayed()) {
            stringRepresentation = "Invalid: No continous word played.";
            return false;
          }
          playedTiles++;
          word.add(0, t);
        }

        if (word.size() > 1) {
          this.words.add(new Word(word));
          scoredTiles += word.size();
        }

        word.clear();
      }
    }

    // checks if there are unconnected tiles or words
    if (scoredTiles - playedTiles < this.laydDownTiles.size()
        || playedTiles == 0 && starField.getTile().isPlayed()) {
      stringRepresentation = "Invalid: Separate words.";
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
   * @return
   */
  public int calculateTurnScore() {
    this.turnScore = 0;
    // calculate word score
    if (this.laydDownTiles.size() == 7) {
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
      this.stringRepresentation += ("* " + localWordMultiplier + " = " + singleWordScore);
      this.turnScore = this.turnScore + singleWordScore;
    }

//    // set all multipliers to 1
//    for (Word w : words) {
//      for (Tile t : w.getTiles()) {
//        t.getField().setLetterMultiplier(1);
//        t.getField().setWordMultiplier(1);
//      }
//    }
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
  }

  public void setLaydDownTiles(List<Tile> laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
  }

  public List<Tile> getLaydDownTiles() {
    return laydDownTiles;
  }

  public List<Word> getWords() {
    return words;
  }

  public int getTurnScore() {
    return turnScore;
  }

  public void setTurnScore(int turnScore) {
    this.turnScore = turnScore;
  }

  public String getPlayer() {
    return player;
  }

  public boolean isValid() {
    return isValid;
  }

  /**
   * @author pkoenig
   * @return deep copy of current instance (tiles won't be "deep copied")
   */
  public Turn getDeepCopy() {
    Turn res = new Turn(this.getPlayer(), this.gameController);
    res.player = this.player;
    res.isValid = this.isValid;
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
    res.laydDownFields = this.laydDownFields;
    res.stringRepresentation = this.stringRepresentation;
    return res;
  }

  public GameController getGameController() {
    return gameController;
  }

  public String toString() {
    return stringRepresentation;
  }

  /**
   * @return the containedStarTiles
   */
  public boolean isContainedStarTiles() {
    return containedStarTiles;
  }

  /**
   * @param containedStarTiles the containedStarTiles to set
   */
  public void setContainedStarTiles(boolean containedStarTiles) {
    this.containedStarTiles = containedStarTiles;
  }

  /**
   * @return the starTiles
   */
  public List<Tile> getStarTiles() {
    return starTiles;
  }

  /**
   * @param starTiles the starTiles to set
   */
  public void setStarTiles(List<Tile> starTiles) {
    this.starTiles = starTiles;
  }


}

