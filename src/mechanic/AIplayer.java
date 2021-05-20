package mechanic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Stopwatch;
import game.GameController;

/**
 * 
 * @author pkoenig
 *
 */
public class AIplayer extends Player {

  private int maxNumOfTiles;
  private GameController gc;
  private TreeSet<AIcombination> twoTilesCombinations;
  private TreeSet<AIcombination> currentTwoTilesCombinations; // filtered by racktiles
  private AiLevel ailevel;
  private int numberOfCombinationsToUse; // use only the top x AIcombination (top in
                                         // regards to
  // count)
  private final int numberOfCombinationSize = 2; // currently only 2 is supported

  public enum AiLevel {
    EASY, MEDIUM, HARD, Unbeatable
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
    this.twoTilesCombinations = new TreeSet<AIcombination>();
    this.generateTwoTilesCombinations();
    switch (ailevel) {
      case EASY:
        this.maxNumOfTiles = 3;
        this.numberOfCombinationsToUse = 350;
        break;
      case MEDIUM:
        this.maxNumOfTiles = 5;
        this.numberOfCombinationsToUse = 350;
        break;
      case HARD:
        this.maxNumOfTiles = 6;
        this.numberOfCombinationsToUse = 450;
        break;
      case Unbeatable:
        this.maxNumOfTiles = 7;
        this.numberOfCombinationsToUse = 500;
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
    this.twoTilesCombinations = new TreeSet<AIcombination>();
    this.numberOfCombinationsToUse = numberOfCombinationsToUse;
    this.generateTwoTilesCombinations();
  }

  @JsonCreator
  public AIplayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume, 0, 0, 0, "", 0, 0, 0);
  }

  public void generateTwoTilesCombinations() { // Version 2.0 (runtime about 5 sec compared to 2
                                               // hours with Version 1.0)
    AIcombination c;
    HashMap<String, AIcombination> temp = new HashMap<String, AIcombination>();
    String cChars;
    for (String w : gc.getDictionary()) {
      for (int i = 0; i < w.length() - 1; i++) {
        cChars = w.substring(i, i + 2);
        if ((c = temp.get(cChars)) != null) {
          c.incCount();
        } else {
          c = new AIcombination(new char[] {cChars.charAt(0), cChars.charAt(1)});
          temp.put(cChars, c);
        }
      }
    }
    twoTilesCombinations.addAll(temp.values());
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
    return results;
  }

  public Turn generateIdealTurn(GameBoard gb) {
    System.out.println("AI is running...");
    // Stopwatch timeOverall = Stopwatch.createStarted();
    HashSet<Field[]> possibleLocations;
    Field[] maximumLocation = null;
    ArrayList<Integer> maximumIndicesOnRack = null;
    Turn currentTurn;
    int maximumScore = 0;
    // Field[] layedDownFieldsWithMaximumScore = null;
    ArrayList<Tile> currentLayedDownTiles;
    ArrayList<Integer> indicesOnRack;
    Turn turnWithMaximumScore = null;

    this.updateFilteredCombinationList();

    for (int k = 2; k <= this.maxNumOfTiles; k++) {
      // System.out
      // // .println("### NEW POSSIBLE LOCATIONS ARE GENERATED FOR TILENUMBER" + k + " ... ###");
      // Stopwatch sw = Stopwatch.createStarted();
      possibleLocations = getValidTilePositionsForNumOfTiles(gb, k);
      // sw.stop();
      // System.out.println("----- possible locations generated in "
      // + sw.elapsed(TimeUnit.MILLISECONDS) + " milliseconds -----\n");

      for (Field[] currentLocation : possibleLocations) {
        // System.out.println("### HANDLE CURRENT-LOCATION AT ");
        // for (Field f : currentLocation) {
        // System.out.print(f);
        // }
        // System.out.println(
        // " with index " + locationIndex + " out of " + possibleLocations.size() + " ... ###");
        // sw.reset();
        // sw.start();

        currentLayedDownTiles = null;
        indicesOnRack = new ArrayList<Integer>();
        while ((currentLayedDownTiles =
            nextTiles(currentLayedDownTiles, indicesOnRack, k)) != null) {
          // System.out.println(currentLayedDownTiles);
          for (int i = 0; i < currentLayedDownTiles.size(); i++) {
            currentLocation[i].setTile(currentLayedDownTiles.get(i));
            this.setRackTileToNone(indicesOnRack.get(i));
            // System.out.println("##### TILE MOVED FROM RACK TO GB #####");
            // System.out.println(currentLayedDownTiles.get(i));
          }
          currentTurn = new Turn(this.getNickname(), gc);
          currentTurn.setLaydDownTiles(currentLayedDownTiles);
          if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
            maximumScore = currentTurn.getTurnScore();
            maximumLocation = currentLocation;
            maximumIndicesOnRack = indicesOnRack;
            // layedDownTileListWithMaximumScore = currentLayedDownTiles;
            // layedDownFieldsWithMaximumScore = currentLocation;

            turnWithMaximumScore = currentTurn.getDeepCopy();

            // System.out.println("NEW MAXIMUM SCORE: " + maximumScore);
            // for (Tile t : turnWithMaximumScore.getLaydDownTiles()) {
            // System.out.println("Tile newly layed down: " + t);
            // }
          }
          // if (currentLayedDownTiles != null) {
          this.cleanupGameboardAndRack(currentLayedDownTiles, indicesOnRack);
          // }
        }
        // sw.stop();
        // System.out.println("----- current location handled in " +
        // sw.elapsed(TimeUnit.MILLISECONDS)
        // + " milliseconds -----\n");
      }
    }
    if (turnWithMaximumScore == null) {
      return null;
    }
    System.out.println();
    System.out.println();
    System.out.println("Maximum score: " + maximumScore);
    for (int i = 0; i < turnWithMaximumScore.getLaydDownTiles().size(); i++) {
      maximumLocation[i].setTile(turnWithMaximumScore.getLaydDownTiles().get(i));
      this.setRackTileToNone(maximumIndicesOnRack.get(i));
      System.out
          .println("Tile newly layed down: " + turnWithMaximumScore.getLaydDownTiles().get(i));
    }
    System.out.println("----------------------");
    for (Word w : turnWithMaximumScore.getWords()) {
      System.out.println(w.toString());
    }
    System.out.println("----------------------");
    // for (Field f : layedDownFieldsWithMaximumScore) {
    // System.out.println(f);
    // }
    // timeOverall.stop();
    // System.out.println("\n AI finished in " + timeOverall.elapsed(TimeUnit.SECONDS) + " seconds
    // ("+ timeOverall.elapsed(TimeUnit.MINUTES) + " minutes)\n");
    System.out.println("RACKTILES (AT BEGINN OF TURN)");
    for (Tile t : this.getRackTiles()) {
      System.out.println(t.getLetter().getCharacter());
    }
//    for (int i = 0; i < .size(); i++) {
//      maximumLocation[i].setTile(currentLayedDownTiles.get(i));
//      this.setRackTileToNone(indicesOnRack.get(i));
//      // System.out.println("##### TILE MOVED FROM RACK TO GB #####");
//      // System.out.println(currentLayedDownTiles.get(i));
//    }
    // TODO clientProtocoll (update ui), und im Server (updateServerUI)
    
    return turnWithMaximumScore;

  }

  @SuppressWarnings("unchecked")
  public void updateFilteredCombinationList() {
    ArrayList<Character> charsOnRack = new ArrayList<Character>();
    this.currentTwoTilesCombinations = (TreeSet<AIcombination>) this.twoTilesCombinations.clone();
    for (int i = 0; i < TILE_COUNT_PER_PLAY; i++) {
      charsOnRack.add(this.getRackTile(i).getLetter().getCharacter());
    }

    for (AIcombination a : this.twoTilesCombinations) {
      for (char c : a.getChars()) {
        if (!charsOnRack.contains(c)) {
          currentTwoTilesCombinations.remove(a);
          break;
        }
      }
    }
    System.out.println("finished getFilteredCombinationList");
  }

  private void cleanupGameboardAndRack(List<Tile> layedDownTileList, List<Integer> indicesOnRack) {
    Tile t;
    for (int i = 0; i < layedDownTileList.size(); i++) {
      t = layedDownTileList.get(i);
      t.getField().setTileOneDirection(null);
      this.setRackTile(indicesOnRack.get(i), t);
    }

  }

  public ArrayList<Tile> nextTiles(ArrayList<Tile> current, ArrayList<Integer> indicesOnRack,
      int numOfTiles) {
    boolean isOnCombinationList;
    TreeSet<Integer> changedIndicesInCurrent = new TreeSet<Integer>();
    int numberOfCOmbinationsAlreadyCovered;
    if (current == null) {
      current = new ArrayList<Tile>();
      for (int i = 0; i < numOfTiles; i++) {
        current.add(this.getRackTile(i));
        indicesOnRack.add(i);
        changedIndicesInCurrent.add(i);
        changedIndicesInCurrent.add(i - 1);
      }
      isOnCombinationList = false;
      for (Integer k : changedIndicesInCurrent) {
        if (k < 0) {
          continue;
        }
        if (k + this.numberOfCombinationSize - 1 > numOfTiles - 1) {
          break;
        }
        isOnCombinationList = false;
        for (AIcombination a : this.currentTwoTilesCombinations.descendingSet()) {
          for (int acIndex = 0; acIndex <= this.numberOfCombinationSize; acIndex++) {
            if (acIndex >= this.numberOfCombinationSize) {
              isOnCombinationList = true;
              break;
            } else if (a.getChars()[acIndex] != current.get(acIndex + k).getLetter()
                .getCharacter()) {
              break;
            }
          }
          if (isOnCombinationList) {
            break;
          }
        }
        if (!isOnCombinationList) {
          break;
        }
      }
      if (isOnCombinationList) {
        return current;
      }
    }
    int currentRackIndex;
    for (int i = numOfTiles - 1; i >= 0; i--) {
      currentRackIndex = indicesOnRack.get(i);
      for (currentRackIndex++; currentRackIndex < 7; currentRackIndex++) {
        if (indicesOnRack.indexOf(currentRackIndex) == -1
            || indicesOnRack.indexOf(currentRackIndex) > i) {
          current.set(i, this.getRackTile(currentRackIndex));
          indicesOnRack.set(i, currentRackIndex);
          changedIndicesInCurrent.add(i);
          changedIndicesInCurrent.add(i - 1);
          for (int ii = i + 1; ii < numOfTiles; ii++) {
            for (int updateRackIndex = 0; updateRackIndex < 7; updateRackIndex++) {
              if (indicesOnRack.indexOf(updateRackIndex) == -1) {
                current.set(ii, this.getRackTile(updateRackIndex));
                indicesOnRack.set(ii, updateRackIndex);
                changedIndicesInCurrent.add(ii);
                changedIndicesInCurrent.add(ii - 1);
                break;
              }
            }
          }
          isOnCombinationList = false;
          for (Integer k : changedIndicesInCurrent) {
            if (k < 0) {
              continue;
            }
            if (k + this.numberOfCombinationSize - 1 > numOfTiles - 1) {
              break;
            }
            isOnCombinationList = false;
            numberOfCOmbinationsAlreadyCovered = 0;
            for (AIcombination a : this.currentTwoTilesCombinations.descendingSet()) {
              if (numberOfCOmbinationsAlreadyCovered >= this.numberOfCombinationsToUse) {
                break;
              }
              numberOfCOmbinationsAlreadyCovered++;
              for (int acIndex = 0; acIndex <= this.numberOfCombinationSize; acIndex++) {
                if (acIndex >= this.numberOfCombinationSize) {
                  isOnCombinationList = true;
                  break;
                } else if (a.getChars()[acIndex] != current.get(acIndex + k).getLetter()
                    .getCharacter()) {
                  break;
                }
              }
              if (isOnCombinationList) {
                break;
              }
            }
            if (!isOnCombinationList) {
              break;
            }
          }
          if (isOnCombinationList) {
            return current;
          }
        }
      }
    }
    return null;
  }

  // public ArrayList<Tile> nextTiles(ArrayList<Tile> current, ArrayList<Integer> indicesOnRack, //
  // Version 3.0, recursive
  // int numOfTiles) {
  //
  // if (current == null) {
  // current = new ArrayList<Tile>();
  // for (int i = 0; i < numOfTiles; i++) {
  // current.add(this.getRackTile(i));
  // indicesOnRack.add(i);
  // }
  // }
  // else {
  // boolean isOnCombinationList;
  // int numberOfCOmbinationsAlreadyCovered;
  // int currentRackIndex;
  // for (int i = numOfTiles - 1; i >= 0; i--) {
  // currentRackIndex = indicesOnRack.get(i);
  // for (currentRackIndex++; currentRackIndex < 7; currentRackIndex++) {
  // if (indicesOnRack.indexOf(currentRackIndex) == -1
  // || indicesOnRack.indexOf(currentRackIndex) > i) {
  // current.set(i, this.getRackTile(currentRackIndex));
  // indicesOnRack.set(i, currentRackIndex);
  // for (int ii = i + 1; ii < numOfTiles; ii++) {
  // for (int updateRackIndex = 0; updateRackIndex < 7; updateRackIndex++) {
  // if (indicesOnRack.indexOf(updateRackIndex) == -1) {
  // current.set(ii, this.getRackTile(updateRackIndex));
  // indicesOnRack.set(ii, updateRackIndex);
  // break;
  // }
  // }
  // }
  // }
  //
  // }
  // for (int i = numOfTiles - 1; i >= 0; i--) {
  // currentRackIndex = indicesOnRack.get(i);
  // for (currentRackIndex++; currentRackIndex < 7; currentRackIndex++) {
  // if (indicesOnRack.indexOf(currentRackIndex) == -1
  // || indicesOnRack.indexOf(currentRackIndex) > i) {
  // current.set(i, this.getRackTile(currentRackIndex));
  // indicesOnRack.set(i, currentRackIndex);
  // changedIndicesInCurrent.add(i);
  // changedIndicesInCurrent.add(i - 1);
  // for (int ii = i + 1; ii < numOfTiles; ii++) {
  // for (int updateRackIndex = 0; updateRackIndex < 7; updateRackIndex++) {
  // if (indicesOnRack.indexOf(updateRackIndex) == -1) {
  // current.set(ii, this.getRackTile(updateRackIndex));
  // indicesOnRack.set(ii, updateRackIndex);
  // changedIndicesInCurrent.add(ii);
  // changedIndicesInCurrent.add(ii - 1);
  // break;
  // }
  // }
  // }
  // isOnCombinationList = false;
  // for (Integer k : changedIndicesInCurrent) {
  // if (k < 0) {
  // continue;
  // }
  // if (k + this.numberOfCombinationSize - 1 > numOfTiles - 1) {
  // break;
  // }
  // isOnCombinationList = false;
  // numberOfCOmbinationsAlreadyCovered = 0;
  // for (AIcombination a : currentTwoTilesCombinations.descendingSet()) {
  // if (numberOfCOmbinationsAlreadyCovered >= this.numberOfCombinationsToUse) {
  // break;
  // }
  // numberOfCOmbinationsAlreadyCovered++;
  // for (int acIndex = 0; acIndex <= this.numberOfCombinationSize; acIndex++) {
  // if (acIndex >= this.numberOfCombinationSize) {
  // isOnCombinationList = true;
  // break;
  // } else if (a.getChars()[acIndex] != current.get(acIndex + k).getLetter()
  // .getCharacter()) {
  // break;
  // }
  // }
  // if (isOnCombinationList) {
  // break;
  // }
  // }
  // if (!isOnCombinationList) {
  // break;
  // }
  // }
  // if (isOnCombinationList) {
  // return current;
  // }
  // }
  // }
  // }
  // return null;
  // }


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
    return twoTilesCombinations;
  }

  /**
   * @param twoTilesCombinations the twoTilesCombinations to set
   */
  public void setTwoTilesCombinations(TreeSet<AIcombination> twoTilesCombinations) {
    this.twoTilesCombinations = twoTilesCombinations;
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
   * @return the numberOfCombinationSize
   */
  public int getNumberOfCombinationSize() {
    return numberOfCombinationSize;
  }

}
