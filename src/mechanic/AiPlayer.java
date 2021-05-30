package mechanic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.GameController;
import game.GameSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;


/**
 * AI player can play scrabble. He searches for a valid Turn (see class turn) based on - the AiLevel
 * (mapped to different parameters) - the GameBoard - his rack - the dictionary
 * Depending on enum AiLevel the ai tries to create better or worse scores. Main Method is RunAi()
 *
 * @author pkoenig
 *
 */
public class AiPlayer extends Player {

  private int maxNumOfTiles;
  private GameController gc;
  private TreeSet<AIcombination> tileCombinations;
  private AiLevel ailevel;
  private int numberOfCombinationsToUse; // use only the top x AIcombination (in regards to count)

  private int goodScore;
  private boolean testmode = false;

  /**
   * Sets the difficulty
   *
   */
  public enum AiLevel {
    EASY, MEDIUM, HARD, UNBEATABLE
  }


  /**
   * 
   * Represent a combination of chars with a counter. The counter represents, how often this
   * combination of chars is present in the wordlist (i.e. dictionary)
   *
   * @author pkoenig
   *
   */
  class AIcombination implements Comparable<AIcombination> {

    private char[] chars;
    private int count;

    /**
     * Contructor
     * 
     * @param chars
     */
    public AIcombination(char[] chars) {
      this.chars = chars;
      this.count = 1;
    }

    /**
     * increase count
     */
    public void incCount() {
      this.count++;
    }

    /**
     * @return the tiles
     */
    public char[] getChars() {
      return this.chars;
    }

    /**
     * @param tiles the tiles to set
     */
    public void setChars(char[] chars) {
      this.chars = chars;
    }

    /**
     * @return the count
     */
    public int getCount() {
      return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
      this.count = count;
    }

    /**
     * Overrinds compareTo from Comparable interface to enable sorting of AIcombinations
     * 
     * @param AIcombination o
     */
    @Override
    public int compareTo(AIcombination o) {
      // equal chars
      if (this.chars.length == o.chars.length) {
        for (int i = 0; i <= this.chars.length; i++) {
          if (i == this.chars.length) {
            return 0;
          }
          if (this.chars[i] != o.chars[i]) {
            break;
          }
        }
      }

      // equal count
      if (Integer.valueOf(this.count).compareTo(Integer.valueOf(o.count)) == 0) {
        if (this.chars[0] > o.chars[0]) {
          return 1;
        }
        if (this.chars[0] == o.chars[0] && this.chars[1] > o.chars[1]) {
          return 1;
        }
        return -1;
      }

      // usual cases
      return Integer.valueOf(this.count).compareTo(Integer.valueOf(o.count));
    }

    /**
     * Overrides equals
     * 
     * @param Object o
     */
    @Override
    public boolean equals(Object o) {
      AIcombination oA = (AIcombination) o;
      if (this.chars.length == oA.chars.length) {
        for (int i = 0; i <= this.chars.length; i++) {
          if (i == this.chars.length) {
            return true;
          }
          if (this.chars[i] != oA.chars[i]) {
            break;
          }
        }
      }
      return false;
    }

    /**
     * Overrinds toString from Object. Returns a String-representation of current instance
     * 
     * @param none
     */
    @Override
    public String toString() {
      String out = "AIcombination with count: " + this.count + " -> ";
      for (char t : this.chars) {
        out = out + " AND " + t;
      }
      return out;
    }

  }

  /**
   * 
   * Main Contructor used in game.
   * 
   * @param nickname
   * @param gc
   * @param level
   */
  public AiPlayer(String nickname, GameController gc, AiLevel level) {
    super(nickname);
    // this.gc = new GameController(new GameState(getPlayerInfo(), nickname));
    this.gc = gc;
    this.ailevel = level;
    this.tileCombinations = new TreeSet<AIcombination>();
    switchAilevel(level);
  }

  /**
   * maps AiLevel to Parameters
   * 
   * @param ailevel
   */
  private void switchAilevel(AiLevel ailevel) {
    switch (ailevel) {
      case EASY:
        this.maxNumOfTiles = 2;
        this.numberOfCombinationsToUse = 8;
        this.goodScore = 1;
        break;
      case MEDIUM:
        this.maxNumOfTiles = 4;
        this.numberOfCombinationsToUse = 25;
        this.goodScore = 10;
        break;
      case HARD:
        this.maxNumOfTiles = 6;
        this.numberOfCombinationsToUse = 45;
        this.goodScore = 20;
        break;
      case UNBEATABLE:
        this.maxNumOfTiles = 7;
        this.numberOfCombinationsToUse = 125;
        this.goodScore = 45;
        break;

      default:
        break;
    }
  }

  /**
   * 
   * Contructor. Only used for testing-purposes.
   * 
   * @param nickname
   * @param maxNumOfTiles
   * @param numberOfCombinationsToUse
   * @param gc
   * 
   */
  public AiPlayer(String nickname, int maxNumOfTiles, int numberOfCombinationsToUse,
      GameController gc) {
    super(nickname);
    this.maxNumOfTiles = maxNumOfTiles;
    this.gc = gc;
    this.tileCombinations = new TreeSet<AIcombination>();
    this.numberOfCombinationsToUse = numberOfCombinationsToUse;
  }

  /**
   * 
   * Constructor used for profilemanagemnt with JSON
   * 
   * @param nickname
   * @param avatar
   * @param volume
   * 
   */
  @JsonCreator
  public AiPlayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume, null, 0, 0, 0, null, 0, 0, 0);
  }

  /**
   * Called once for each AIplayer at creation-time
   * 
   * Creates a instance-HashMap of AIcombinations used by Method runAi
   * 
   */
  public void generateTileCombinations() {
    this.tileCombinations = new TreeSet<AIcombination>();
    AIcombination c;
    HashMap<String, AIcombination> temp = new HashMap<String, AIcombination>();
    String cChars;
    for (String w : gc.getDictionary()) {
      for (int currentNumOfTiles =
          1; currentNumOfTiles <= this.maxNumOfTiles; currentNumOfTiles++) {
        for (int i = 0; i <= w.length() - currentNumOfTiles; i++) {
          cChars = w.substring(i, i + currentNumOfTiles);
          if ((c = temp.get(cChars)) != null) {
            c.incCount();
          } else {
            c = new AIcombination(cChars.toCharArray());
            temp.put(cChars, c);
          }
        }
      }
    }
    tileCombinations.addAll(temp.values());
  }

  /**
   * 
   * Main Method. Gets called from server, when AIplayer gets currentPlayer.
   * 
   * 
   * @param gb
   * @return Turn: ideal Turn (in Terms of AiLevel)
   * 
   */
  public Turn runAi(GameBoard gb) {
    if (this.testmode) {
      System.out.println("\nAI is running with setting " + this.ailevel + "....");
    }

    // init
    TreeSet<AIcombination> currentAiCombinations =
        (TreeSet<AIcombination>) getFilteredCombinationList().descendingSet();
    Turn idealTurn = null;
    Turn currentTurn = null;
    ArrayList<HashSet<Field[]>> possibleLocations = new ArrayList<HashSet<Field[]>>();
    ArrayList<Tile> currentLayedDownTiles = new ArrayList<Tile>();
    int maximumScore = 0;
    boolean finished = false;
    int combinationIndex = 0;

    for (int i = 1; i <= this.getMaxNumOfTiles(); i++) {
      possibleLocations.add(getValidTilePositionsForNumOfTiles(gb, i));
    }

    // run
    for (AIcombination currentAiCombination : currentAiCombinations) {

      // get Tiles from rack
      for (char c : currentAiCombination.getChars()) {
        for (Tile t : this.getRackTiles()) {
          if (t.getLetter().getCharacter() == c) {
            currentLayedDownTiles.add(t);
            this.removeRackTile(t.getField().getxCoordinate()); // !!!
            break;
          }
        }
      }
      // iterate over the possible locations
      for (Field[] currentLocation : possibleLocations.get(currentLayedDownTiles.size() - 1)) {

        // put Tiles on Gameboard
        for (int i = 0; i < currentLocation.length; i++) {
          if (currentLayedDownTiles.get(i).getField() != null) {
            currentLayedDownTiles.get(i).getField().setTileOneDirection(null);
          }
          currentLayedDownTiles.get(i).setFieldOneDirection(currentLocation[i]);
          currentLocation[i].setTileOneDirection(currentLayedDownTiles.get(i));

        }

        // manage Turn
        currentTurn = new Turn(this.getNickname(), this.gc);
        currentTurn.setLaydDownTiles(currentLayedDownTiles);
        if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
          maximumScore = currentTurn.getTurnScore();
          idealTurn = currentTurn.getDeepCopy();
          idealTurn.setLaydDownFields(currentLocation);

          // found turn with good score (for AiLevel
          if (maximumScore >= this.goodScore) {
            finished = true;
          }
        }

        // cleanup gameboard
        for (int i = 0; i < currentLocation.length; i++) {
          currentLocation[i].setTileOneDirection(null);
          currentLayedDownTiles.get(i).setFieldOneDirection(null);
        }
        if (finished) {
          break;
        }
      }

      // put Tiles back to the rack
      for (int i = 0; i < currentLayedDownTiles.size(); i++) {
        if (currentLayedDownTiles.get(i).getField() != null) {
          currentLayedDownTiles.get(i).getField().setTile(null);
        }
        this.addTileToRack(currentLayedDownTiles.get(i));
      }
      // clean currentLayedDownTiles
      currentLayedDownTiles.clear();
      if (finished) {
        break;
      }
      if (combinationIndex >= this.numberOfCombinationsToUse - 1) {
        break;
      }
      combinationIndex++;
    }

    if (idealTurn == null) {
      return null;
    }

    if (this.testmode) {
      System.out.println();
      System.out.println();
      System.out.println("Maximum score: " + maximumScore);
      System.out.println("----------------------");
      for (Word w : idealTurn.getWords()) {
        System.out.println(w.toString());
      }
      System.out.println("----------------------");
      System.out.println("AI finished");
    } else {
      // add Tiles to gameboard
      Field[] maxLocation = new Field[idealTurn.getLaydDownFields().size()];
      int i = 0;
      for (Field f : idealTurn.getLaydDownFields()) {
        maxLocation[i] = f;
        i++;
      }
      ArrayList<Tile> maxTiles = (ArrayList<Tile>) idealTurn.getLaydDownTiles();
      for (int ii = 0; ii < maxTiles.size(); ii++) {
        if (maxTiles.get(ii).getField() != null) {
          maxTiles.get(ii).getField().setTile(null);
        }
        maxTiles.get(ii).setFieldOneDirection(maxLocation[ii]);
        maxLocation[ii].setTileOneDirection(maxTiles.get(ii));
      }
    }

    return idealTurn;
  }

  /**
   * 
   * Will be called at the Beginn of everiy Turn (i.e. call of runAi() ) Filters the HashMap of
   * AIcombinations for the rack.
   * 
   * @return TreeSet<AIcombination> AIcombination, which can be used with current rack
   */
  @SuppressWarnings("unchecked")
  private TreeSet<AIcombination> getFilteredCombinationList() {
    ArrayList<Character> charsOnRack = new ArrayList<Character>();
    ArrayList<Character> currentCharsOnRack;
    TreeSet<AIcombination> currentTwoTilesCombinations = new TreeSet<AIcombination>();
    currentTwoTilesCombinations = (TreeSet<AIcombination>) this.tileCombinations.clone();
    for (int i = 0; i < GameSettings.getTilesOnRack(); i++) {
      charsOnRack.add(this.getRackTile(i).getLetter().getCharacter());
    }

    for (AIcombination a : this.tileCombinations) {
      currentCharsOnRack = (ArrayList<Character>) charsOnRack.clone();
      for (char c : a.getChars()) {
        if (!currentCharsOnRack.remove(Character.valueOf(c))) {
          currentTwoTilesCombinations.remove(a);
          break;
        }
      }
    }
    return currentTwoTilesCombinations;
  }

  /**
   * 
   * Used internally for getValidPositions
   * 
   * @param gb
   * @param i
   * @param j
   * @param numOfTiles
   * @param results
   */
  private static void addParallelTilesHorizontally_getValidTilePositionsForNumOfTilesHelper(
      GameBoard gb, int i, int j, int numOfTiles, HashSet<Field[]> results) {
    Field[] singleTilesPosition = new Field[numOfTiles];
    for (int l = i - numOfTiles + 1; l <= i; l++) {
      for (int w = 0; w <= numOfTiles; w++) {
        if (w == numOfTiles) {
          results.add(singleTilesPosition);
          singleTilesPosition = new Field[numOfTiles];
          break;
        } else if (gb.getField(l + w, j) != null && gb.getField(l + w, j).getTile() == null) {
          singleTilesPosition[w] = gb.getField(l + w, j);
        } else {
          singleTilesPosition = new Field[numOfTiles];
          break;
        }
      }
    }
  }

  /**
   * 
   * Used internally for getValidTilePositionsForNumOfTiles
   * 
   * @param gb
   * @param i
   * @param j
   * @param numOfTiles
   * @param results
   */
  private static void addParallelTilesVertically_getValidTilePositionsForNumOfTilesHelper(
      GameBoard gb, int i, int j, int numOfTiles, HashSet<Field[]> results) {
    Field[] singleTilesPosition = new Field[numOfTiles];
    for (int l = j - numOfTiles + 1; l <= j; l++) {
      for (int w = 0; w <= numOfTiles; w++) {
        if (w == numOfTiles) {
          results.add(singleTilesPosition);
          singleTilesPosition = new Field[numOfTiles];
          break;
        } else if (gb.getField(i, l + w) != null && gb.getField(i, l + w).getTile() == null) {
          singleTilesPosition[w] = gb.getField(i, l + w);
        } else {
          singleTilesPosition = new Field[numOfTiles];
          break;
        }
      }
    }
  }


  /**
   * 
   * Called at every runAi
   * 
   * 
   * @param gb
   * @param numOfTiles
   * @return HashSet<Field[]> list of validTilePositions
   */
  public HashSet<Field[]> getValidTilePositionsForNumOfTiles(GameBoard gb, int numOfTiles) {
    if (numOfTiles < 1) {
      return null;
    }
    HashSet<Field[]> results = new HashSet<Field[]>(); // no duplicates in results
    Field[] singleTilesPosition = new Field[numOfTiles];
    for (int j = 1; j <= gb.getFields().length; j++) {
      for (int i = 1; i <= gb.getFields().length; i++) {

        if (gb.getField(i, j).getTile() == null) {
          if (gb.getField(i, j).getTop() != null && gb.getField(i, j).getTop().getTile() != null) {
            // go down vertically
            for (int k = j; k <= j + numOfTiles; k++) {
              if (k == j + numOfTiles) {
                results.add(singleTilesPosition);
                singleTilesPosition = new Field[numOfTiles];
                break;
              } else if (gb.getField(i, k) != null && gb.getField(i, k).getTile() == null) {
                singleTilesPosition[k - j] = gb.getField(i, k);
              } else {
                singleTilesPosition = new Field[numOfTiles];
                break;
              }
            }
            // parallel tiles
            addParallelTilesVertically_getValidTilePositionsForNumOfTilesHelper(gb, i, j,
                numOfTiles, results);
          }


        }
        if (gb.getField(i, j).getRight() != null
            && gb.getField(i, j).getRight().getTile() != null) {
          // go left horizontally
          for (int l = i - numOfTiles + 1; l <= i + 1; l++) {
            if (l == i + 1) {
              results.add(singleTilesPosition);
              singleTilesPosition = new Field[numOfTiles];
              break;
            } else if (gb.getField(l, j) != null && gb.getField(l, j).getTile() == null) {
              singleTilesPosition[l - i + numOfTiles - 1] = gb.getField(l, j);
            } else {
              singleTilesPosition = new Field[numOfTiles];
              break;
            }
          }
          // parallel tiles
          addParallelTilesHorizontally_getValidTilePositionsForNumOfTilesHelper(gb, i, j,
              numOfTiles, results);

        }
        if (gb.getField(i, j).getBottom() != null
            && gb.getField(i, j).getBottom().getTile() != null) {
          // go up vertically
          for (int k = j - numOfTiles + 1; k <= j + 1; k++) {
            if (k == j + 1) {
              results.add(singleTilesPosition);
              singleTilesPosition = new Field[numOfTiles];
              break;
            } else if (gb.getField(i, k) != null && gb.getField(i, k).getTile() == null) {
              singleTilesPosition[k - j + numOfTiles - 1] = gb.getField(i, k);
            } else {
              singleTilesPosition = new Field[numOfTiles];
              break;
            }
          }
          // parallel tiles
          addParallelTilesVertically_getValidTilePositionsForNumOfTilesHelper(gb, i, j, numOfTiles,
              results);
        }
        if (gb.getField(i, j).getLeft() != null && gb.getField(i, j).getLeft().getTile() != null) {
          // go right horizontally
          for (int l = i; l <= i + numOfTiles; l++) {
            if (l == i + numOfTiles) {
              results.add(singleTilesPosition);
              singleTilesPosition = new Field[numOfTiles];
              break;
            } else if (gb.getField(l, j) != null && gb.getField(l, j).getTile() == null) {
              singleTilesPosition[l - i] = gb.getField(l, j);
            } else {
              singleTilesPosition = new Field[numOfTiles];
              break;
            }
          }
          // parallel tiles
          addParallelTilesHorizontally_getValidTilePositionsForNumOfTilesHelper(gb, i, j,
              numOfTiles, results);
        }
      }
    }
    // if first Move

    if (results.isEmpty()) {
      // horizontal
      for (int i = 8 - numOfTiles + 1; i <= 8; i++) {
        for (int k = i; k < i + numOfTiles; k++) {
          singleTilesPosition[k - i] = gb.getField(k, 8);
        }
        results.add(singleTilesPosition);
        singleTilesPosition = new Field[numOfTiles];
      }
      // vertical
      for (int i = 8 - numOfTiles + 1; i <= 8; i++) {
        for (int k = i; k < i + numOfTiles; k++) {
          singleTilesPosition[k - i] = gb.getField(8, k);
        }
        results.add(singleTilesPosition);
        singleTilesPosition = new Field[numOfTiles];
      }
    }


    return results;
  }


  /**
   * @return the ailevel
   */
  public AiLevel getAilevel() {
    return ailevel;
  }

  /**
   * @param ailevel the ailevel to set
   */
  public void setAilevel(AiLevel ailevel) {
    this.ailevel = ailevel;
    switchAilevel(ailevel);
  }

  /**
   * @return the twoTilesCombinations
   */
  public TreeSet<AIcombination> getTwoTilesCombinations() {
    return tileCombinations;
  }

  /**
   * @param twoTilesCombinations the twoTilesCombinations to set
   */
  public void setTwoTilesCombinations(TreeSet<AIcombination> twoTilesCombinations) {
    this.tileCombinations = twoTilesCombinations;
  }

  /**
   * @return the maxNumOfTiles
   */
  public int getMaxNumOfTiles() {
    return maxNumOfTiles;
  }

  /**
   * @param maxNumOfTiles the maxNumOfTiles to set
   */
  public void setMaxNumOfTiles(int maxNumOfTiles) {
    this.maxNumOfTiles = maxNumOfTiles;
  }

  /**
   * @return the numberOfCombinationsToUse
   */
  public int getNumberOfCombinationsToUse() {
    return numberOfCombinationsToUse;
  }

  /**
   * @param numberOfCombinationsToUse the numberOfCombinationsToUse to set
   */
  public void setNumberOfCombinationsToUse(int numberOfCombinationsToUse) {
    this.numberOfCombinationsToUse = numberOfCombinationsToUse;
  }

  /**
   * @return the gc
   */
  public GameController getGc() {
    return gc;
  }

  /**
   * @param gc the gc to set
   */
  public void setGc(GameController gc) {
    this.gc = gc;
  }

  /**
   * @return the goodScore
   */
  public int getGoodScore() {
    return goodScore;
  }

  /**
   * @param goodScore the goodScore to set
   */
  public void setGoodScore(int goodScore) {
    this.goodScore = goodScore;
  }

  /**
   * @return the testmode
   */
  public boolean isTestmode() {
    return testmode;
  }

  /**
   * @param testmode the testmode to set
   */
  public void setTestmode(boolean testmode) {
    this.testmode = testmode;
  }

}
