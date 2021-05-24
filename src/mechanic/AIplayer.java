package mechanic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Stopwatch;
import game.GameController;
import game.GameSettings;

/**
 * 
 * @author pkoenig
 *
 */
public class AIplayer extends Player {

  private int maxNumOfTiles;
  private GameController gc;
  private TreeSet<AIcombination> tileCombinations;
  private AiLevel ailevel;
  private int numberOfCombinationsToUse; // use only the top x AIcombination (top in
                                         // regards to
                                         // count)

  private int goodScore;
  private boolean testmode = false;

  public enum AiLevel {
    LOW, MEDIUM, HIGH, UNBEATABLE
  }

  class AIcombination implements Comparable<AIcombination> {

    private char[] chars;
    private int count;

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

    @Override
    public String toString() {
      String out = "AIcombination with count: " + this.count + " -> ";
      for (char t : this.chars) {
        out = out + " AND " + t;
      }
      return out;
    }

  }

  public AIplayer(String nickname, GameController gc, AiLevel level) {
    super(nickname);
    // this.gc = new GameController(new GameState(getPlayerInfo(), nickname));
    this.gc = gc;
    this.ailevel = level;
    this.tileCombinations = new TreeSet<AIcombination>();
    switch (ailevel) {
      case LOW:
        this.maxNumOfTiles = 4;
        this.numberOfCombinationsToUse = 20;
        this.goodScore = 5;
        break;
      case MEDIUM:
        this.maxNumOfTiles = 5;
        this.numberOfCombinationsToUse = 50;
        this.goodScore = 12;
        break;
      case HIGH:
        this.maxNumOfTiles = 6;
        this.numberOfCombinationsToUse = 100;
        this.goodScore = 20;
        break;
      case UNBEATABLE:
        this.maxNumOfTiles = 7;
        this.numberOfCombinationsToUse = 200;
        this.goodScore = 45;
        break;

      default:
        break;
    }
  }

  public AIplayer(String nickname, int maxNumOfTiles, int numberOfCombinationsToUse,
      GameController gc) {
    super(nickname);
    this.maxNumOfTiles = maxNumOfTiles;
    this.gc = gc;
    this.tileCombinations = new TreeSet<AIcombination>();
    this.numberOfCombinationsToUse = numberOfCombinationsToUse;
  }

  @JsonCreator
  public AIplayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume, null, 0, 0, 0, null, 0, 0, 0);
  }

  public void generateTileCombinations() {
    this.tileCombinations = new TreeSet<AIcombination>();
    AIcombination c;
    HashMap<String, AIcombination> temp = new HashMap<String, AIcombination>();
    String cChars;
    for (String w : gc.getDictionary()) {
      for (int currentNumOfTiles =
          2; currentNumOfTiles <= this.maxNumOfTiles; currentNumOfTiles++) {
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

  public Turn runAi(GameBoard gb) {
    System.out.println("\nAI is running with setting " + this.ailevel + "....");
    Stopwatch sw = Stopwatch.createStarted();

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

    for (int i = 2; i <= this.getMaxNumOfTiles(); i++) {
      possibleLocations.add(getValidTilePositionsForNumOfTiles(gb, i));
    }

    // run
    for (AIcombination currentAiCombination : currentAiCombinations) {

      // get Tiles from rack
      for (char c : currentAiCombination.getChars()) {
        for (Tile t : this.getRackTiles()) {
          if (t.getLetter().getCharacter() == c) {
            currentLayedDownTiles.add(t);
            this.setRackTileToNone(t.getField().getxCoordinate()); // !!!
            break;
          }
        }
      }
      // iterate over the possible locations
      for (Field[] currentLocation : possibleLocations.get(currentLayedDownTiles.size() - 2)) {

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
    sw.stop();

    System.out.println();
    System.out.println();
    System.out.println("Maximum score: " + maximumScore);
    System.out.println("----------------------");
    for (Word w : idealTurn.getWords()) {
      System.out.println(w.toString());
    }
    System.out.println("----------------------");
    System.out.println("AI finished in " + sw.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");

    if (!this.testmode) {
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

  @SuppressWarnings("unchecked")
  public TreeSet<AIcombination> getFilteredCombinationList() {
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



  public HashSet<Field[]> getValidTilePositionsForNumOfTiles(GameBoard gb, int numOfTiles) {
    if (numOfTiles <= 1) {
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
