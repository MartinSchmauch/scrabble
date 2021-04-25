package mechanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private List<Word> words; // Array, that contains all words, that result from the lay down letters
  private int turnScore;
  private boolean isValid;
  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");
  private static File file = new File(baseDir + "CollinsScrabbleWords.txt");


  public Turn(String player) {
    this.player = player;
    this.words = new ArrayList<Word>();
    this.laydDownTiles = new ArrayList<Tile>();
    this.turnScore = 0;
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
   * The calculateWords() method is used to find all words, that emerge from the layd down tiles
   * after a turn is commited. After all words are found, every word is verified with Collins
   * Scrabble Words. If one word does not exists the method returns false.
   */
  public boolean calculateWords() {
    // wordTiles describes the Tiles that build the word
    List<Tile> wordTiles = new ArrayList<Tile>();

    for (Tile t : this.laydDownTiles) {
      // find Top Letter
      while (t.getTopTile() != null) {
        t = t.getTopTile();
        //System.out.println("i am here");
      }
      // Go from Top to Bottom to build word
      wordTiles.add(t);
      while (t.getBottomTile() != null) {
        t = t.getBottomTile();
        wordTiles.add(t);
      }

      // Check if Word is larger than two characters
      if (wordTiles.size() >= 2) {
        if (this.checkIfWordAlreadyExists(wordTiles) == false) {
          // Make Deep Copy of ArrayList wordTiles
          List<Tile> helpList = new ArrayList<Tile>();
          for (Tile element : wordTiles) {
            helpList.add(element);
          }
          this.words.add(new Word(helpList));
        }
      }
      wordTiles.clear();
    }

    wordTiles.clear();
    for (Tile t : this.laydDownTiles) {

      // find leftest Letter
      while (t.getLeftTile() != null) {
        System.out.println("IN TURN:");
        System.out.println(t);

        t = t.getLeftTile();
        System.out.println(t);
        System.out.println("t.getField().getTile()");
        System.out.println(t.getField().getTile());
        System.out.println("------------");
      }
      // Go from left to right to build word
      wordTiles.add(t);
      while (t.getRightTile() != null) {
        t = t.getRightTile();
        wordTiles.add(t);
      }
      if (wordTiles.size() >= 2) {
        if (this.checkIfWordAlreadyExists(wordTiles) == false) {
          // Make Deep Copy of ArrayList wordTiles
          List<Tile> helpList = new ArrayList<Tile>();
          for (Tile element : wordTiles) {
            helpList.add(element);
          }
          this.words.add(new Word(helpList));
        }
      }
      wordTiles.clear();
    }
    // verify words
    boolean help2 = false;
    if (words != null) {
      for (Word tileList : this.words) {

        // String representation of the ArrayList "tileList"
        String wordString = tileList.toString();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          Pattern p = Pattern.compile("\\w*");
          String line;
          while ((line = br.readLine()) != null) {
            Matcher m = p.matcher(line);
            if (!line.isEmpty()) {
              m.find();
              String w = line.substring(m.regionStart(), m.end());
              if (wordString.equalsIgnoreCase(w)) {
                help2 = true;
              }
            }
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (help2 == false) {
          return help2;
        }

      }
      return true;
    }
    return false;
  }

  /**
   * This method is used to calculate the turn score resulting from all emerging Words. It is
   * called, if the method calculateWords returns true. After the score is calculated relevant
   * special fields become normal fields with the multiplier 1.
   * 
   * @return
   */
  public int calculateTurnScore() {
    // calculate word score
    for (Word w : this.words) {
      int localWordMultiplier = 1;
      int singleWordScore = 0;

      for (Tile t : w.getTiles()) {
        singleWordScore += t.getValue() * t.getField().getLetterMultiplier();
        localWordMultiplier = localWordMultiplier * t.getField().getWordMultiplier();
      }
      singleWordScore = singleWordScore * localWordMultiplier;
      this.turnScore = this.turnScore + singleWordScore;
    }

    // set all multipliers to 1
    for (Word w : words) {
      for (Tile t : w.getTiles()) {
        t.getField().setLetterMultiplier(1);
        t.getField().setWordMultiplier(1);
      }
    }
    return this.turnScore;
  }

  /** Methods checks, if a new Word already exists in the List Turn.words */
  public boolean checkIfWordAlreadyExists(List<Tile> wordTiles) {
    boolean check = false;
    for (Word w : this.words) {
      if (w.getTiles().size() == wordTiles.size()) {
        check = true;
        for (int i = 0; i < wordTiles.size(); i++) {
          if (w.getTiles().get(i).getField().getxCoordinate() == wordTiles.get(i).getField()
              .getxCoordinate()
              && w.getTiles().get(i).getField().getyCoordinate() == wordTiles.get(i).getField()
                  .getyCoordinate()) {
            check = check & true;
          } else {
            check = false;
          }
        }
        if (check) {
          return check;
        }
      }
    }
    return check;
  }

  /**
   * Ends the current turn and calculates turn score.
   */

  public void endTurn() {
    this.isValid = calculateWords();
    if (this.isValid) {
      calculateTurnScore();
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


}

