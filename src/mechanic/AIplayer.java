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
  // private TreeSet<AIcombination> currentTwoTilesCombinations; // filtered by racktiles
  private AiLevel ailevel;
  private int numberOfCombinationsToUse; // use only the top x AIcombination (top in
                                         // regards to
  // count)
  // private final int numberOfCombinationSize = 2; // currently only 2 is supported

  private int minScore;
  private int maxTimeInSec;

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
        this.maxNumOfTiles = 3;
        this.numberOfCombinationsToUse = 10000;
        this.minScore = 5;
        this.maxTimeInSec = 15;
        break;
      case MEDIUM:
        this.maxNumOfTiles = 4;
        this.numberOfCombinationsToUse = 10000;
        this.minScore = 5;
        this.maxTimeInSec = 20;
        break;
      case HIGH:
        this.maxNumOfTiles = 6;
        this.numberOfCombinationsToUse = 10000;
        this.minScore = 5;
        this.maxTimeInSec = 30;
        break;
      case UNBEATABLE:
        this.maxNumOfTiles = 7;
        this.numberOfCombinationsToUse = 10000;
        this.minScore = -1; // no minScore
        this.maxTimeInSec = -1; // no maxTime
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
    System.out.println("AI is running....");
    // init
    TreeSet<AIcombination> currentAiCombinations =
        (TreeSet<AIcombination>) getFilteredCombinationList().descendingSet();
    Turn idealTurn = null;
    Turn currentTurn = null;
    ArrayList<HashSet<Field[]>> possibleLocations = new ArrayList<HashSet<Field[]>>();
    ArrayList<Tile> currentLayedDownTiles = new ArrayList<Tile>();
    int maximumScore = 0;

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

            // if (currentLayedDownTiles.get(currentLayedDownTiles.size() - 1).getField() == null) {
            // System.out.println("line 240");
            // }

            this.setRackTileToNone(t.getField().getxCoordinate()); // !!!
            break;
          }
        }
      }
      // if (currentLayedDownTiles.size() < 2) {
      // System.out.println("line 244");
      // }
      // if (currentLayedDownTiles.size() != currentAiCombination.getChars().length) {
      // System.out.println("line 247");
      // }
      int dummy = 0;
      // iterate over the possible locations
      for (Field[] currentLocation : possibleLocations.get(currentLayedDownTiles.size() - 2)) {

        // put Tiles on Gameboard
        for (int i = 0; i < currentLocation.length; i++) {


          // if (currentLayedDownTiles.get(i).getField() == null) {
          // System.out.println("line 244");
          // }
          // if (currentLayedDownTiles.get(i).getField().ge) {
          // System.out.println("line 247");
          // }

          if (currentLayedDownTiles.get(i).getField() != null) {
            currentLayedDownTiles.get(i).getField().setTileOneDirection(null); // TODO
          }
          
//          currentLocation[i].setTile(currentLayedDownTiles.get(i)); // TODO
          
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
          idealTurn.setValid(true);
        }
        
        // cleanup gameboard
        for (int i = 0; i < currentLocation.length; i++) {
          currentLocation[i].setTileOneDirection(null); // TODO
          currentLayedDownTiles.get(i).setFieldOneDirection(null);
        }

      }

      // put Tiles back to the rack
      for (int i = 0; i < currentLayedDownTiles.size(); i++) {
        if (currentLayedDownTiles.get(i).getField() != null) {
          currentLayedDownTiles.get(i).getField().setTile(null); // TODO
        }
        this.addTileToRack(currentLayedDownTiles.get(i));
      }
      // clean currentLayedDownTiles
      currentLayedDownTiles.clear();
    }

    if (idealTurn == null) {
      return null;
    }
    System.out.println();
    System.out.println();
    System.out.println("Maximum score: " + maximumScore);
    System.out.println("----------------------");
    for (Word w : idealTurn.getWords()) {
      System.out.println(w.toString());
    }
    System.out.println("----------------------");
    System.out.println("RACKTILES ()");
    for (Tile t : this.getRackTiles()) {
      System.out.println(t.getLetter().getCharacter());
    }
    System.out.println("AI finished");
    
    // add Tiles to gameboard
    Field[] maxLocation = new Field[idealTurn.getLaydDownFields().size()];
    int i = 0;
    for (Field f : idealTurn.getLaydDownFields()) {
      maxLocation[i] = f;
      i++;
    }
    ArrayList<Tile> maxTiles = (ArrayList<Tile>) idealTurn.getLaydDownTiles();
//    Field[] maxLocation = (Field[]) idealTurn.getLaydDownFields().toArray();
    for (int ii = 0; ii < maxTiles.size(); ii++) {
      if (maxTiles.get(ii).getField() != null) {
        maxTiles.get(ii).getField().setTile(null); // TODO
      }
      maxTiles.get(ii).setFieldOneDirection(maxLocation[ii]);
      maxLocation[ii].setTileOneDirection(maxTiles.get(ii));
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


  // public void generateTwoTilesCombinations() { // Version 2.0 (runtime about 5 sec compared to 2
  // // hours with Version 1.0)
  // AIcombination c;
  // HashMap<String, AIcombination> temp = new HashMap<String, AIcombination>();
  // String cChars;
  // for (String w : gc.getDictionary()) {
  // for (int i = 0; i < w.length() - 1; i++) {
  // cChars = w.substring(i, i + 2);
  // if ((c = temp.get(cChars)) != null) {
  // c.incCount();
  // } else {
  // c = new AIcombination(new char[] {cChars.charAt(0), cChars.charAt(1)});
  // temp.put(cChars, c);
  // }
  // }
  // }
  // tileCombinations.addAll(temp.values());
  // }

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

  // public Turn generateIdealTurn_V4(GameBoard gb) {
  // HashMap<Character, Tile> rackSet = new HashMap<Character, Tile>();
  // for (Tile t : this.getRackTiles()) {
  // rackSet.put(t.getLetter().getCharacter(), t);
  // }
  //
  // Turn idealTurn = null;
  //
  //
  //
  // return idealTurn;
  // }

  // public ArrayList<Tile> nextTiles(ArrayList<Tile> current, int numOfTiles) {
  // if (current == null) {
  // current = new ArrayList<Tile>();
  // current.add(this.getRackTile(0));
  // for (int i = 1; i < numOfTiles; i++) {
  // current.add(this.getRackTile(i));
  // if (checkTwoTiles(current.get(i - 1), current.get(i)))
  // ;
  // }
  // return current;
  // } else {
  // for (int i = numOfTiles - 1; i >= 0; i--) {
  // int currentRackIndex = indicesOnRack.get(i);
  // for (currentRackIndex++; currentRackIndex < 7; currentRackIndex++) {
  // if (indicesOnRack.indexOf(currentRackIndex) == -1
  // || indicesOnRack.indexOf(currentRackIndex) > i) {
  // current.set(i, this.getRackTile(currentRackIndex));
  // indicesOnRack.set(i, currentRackIndex);
  // for (i++; i < numOfTiles; i++) {
  // for (int updateRackIndex = 0; updateRackIndex < 7; updateRackIndex++) {
  // if (indicesOnRack.indexOf(updateRackIndex) == -1) {
  // current.set(i, this.getRackTile(updateRackIndex));
  // indicesOnRack.set(i, updateRackIndex);
  // break;
  // }
  // }
  // }
  // return current;
  // }
  // }
  // }
  // return null;
  // }
  // }

  // private boolean checkTwoTiles(Tile tile, Tile tile2) {
  // // TODO Auto-generated method stub
  // return false;
  // }


  // public Turn generateIdealTurn(GameBoard gb) {
  // HashSet<Field[]> possibleLocations;
  // long startTime = System.currentTimeMillis();
  // Tile[] currentLayedDownTiles;
  // TreeSet<AIcombination> currentTwoTilesCombinations =
  // (TreeSet<AIcombination>) this.updateFilteredCombinationList().descendingSet();
  // int cTTC_size = currentTwoTilesCombinations.size();
  // AIcombination currentAiCombination;
  // char[] currentChars;
  // boolean onAIcombinations;
  // Turn idealTurn = null;
  // int maximumScore = 0;
  // Turn potencialIdealTurn;
  // LinkedList<Tile> rack = (LinkedList<Tile>) this.getRackTiles();
  //
  // System.out.println("AI Version 3 is running...");
  //
  //
  // for (int k = maxNumOfTiles; k >= 2; k--) {
  // currentLayedDownTiles = new Tile[k];
  //
  // possibleLocations = getValidTilePositionsForNumOfTiles(gb, k);
  //
  //
  //
  // // for (int i = 0; i < Math.pow(GameSettings.getTilesOnRack(), k); i++) {
  // // onAIcombinations = false;
  // // for (int index = 0; index <= k; index++) {
  // // if (index == 0) {
  // // currentLayedDownTiles[index] = this.getRackTile(i % 26);
  // // } else {
  // // currentLayedDownTiles[index] = this.getRackTile((i / (index * 26)) % 26);
  // //
  // // // check if currentLayesDownTiles are on currentTwoTileCombinations
  // // Iterator<AIcombination> cTTC_iter = currentTwoTilesCombinations.iterator();
  // // while (cTTC_iter.hasNext()) {
  // // currentAiCombination = cTTC_iter.next();
  // // currentChars = currentAiCombination.getChars();
  // // if (currentChars[0] == currentLayedDownTiles[index - 1].getLetter().getCharacter()
  // // && currentChars[1] == currentLayedDownTiles[index].getLetter().getCharacter()) {
  // // onAIcombinations = true;
  // // break;
  // // }
  // // }
  // // if (!onAIcombinations) {
  // // break;
  // // }
  // // }
  // // }
  // // if (onAIcombinations) {
  // // potencialIdealTurn = generateTurnForTiles(currentLayedDownTiles, possibleLocations);
  // // if (potencialIdealTurn.getTurnScore() > maximumScore) {
  // // idealTurn = potencialIdealTurn;
  // // }
  // // }
  // // }
  //// for (int i = 0; i < k; i++) {
  //// Iterator<AIcombination> cTTC_iter = currentTwoTilesCombinations.iterator();
  //// while (cTTC_iter.hasNext()) {
  //// currentAiCombination = cTTC_iter.next();
  //// currentChars = currentAiCombination.getChars();
  //// if (currentChars[0] == currentLayedDownTiles[index - 1].getLetter().getCharacter()
  //// && currentChars[1] == currentLayedDownTiles[index].getLetter().getCharacter()) {
  //// onAIcombinations = true;
  //// break;
  //// }
  //// }
  //// }
  // }
  //
  // return null;
  // }


  // public Tile[] nextTiles_V4(List<Tile> rack, Tile[] currentLayedDownTiles) {
  // return null;
  // }
  //
  //
  // public Turn generateTurnForTiles(Tile[] currentLayedDownTiles,
  // HashSet<Field[]> possibleLocations) { // iterate over positions
  //
  // return null;
  // }

  // public Turn generateIdealTurn_V1(GameBoard gb) {
  // System.out.println("AI is running...");
  // // Stopwatch timeOverall = Stopwatch.createStarted();
  // HashSet<Field[]> possibleLocations;
  // Field[] maximumLocation = null;
  // ArrayList<Integer> maximumIndicesOnRack = null;
  // Turn currentTurn;
  // int maximumScore = 0;
  // // Field[] layedDownFieldsWithMaximumScore = null;
  // ArrayList<Tile> currentLayedDownTiles;
  // ArrayList<Integer> indicesOnRack;
  // Turn turnWithMaximumScore = null;
  // int locationIndex = 0;
  //
  // this.updateFilteredCombinationList();
  //
  // for (int k = 2; k <= this.maxNumOfTiles; k++) {
  // System.out
  // .println("### NEW POSSIBLE LOCATIONS ARE GENERATED FOR TILENUMBER" + k + " ... ###");
  // Stopwatch sw = Stopwatch.createStarted();
  // possibleLocations = getValidTilePositionsForNumOfTiles(gb, k);
  // // sw.stop();
  // // System.out.println("----- possible locations generated in "
  // // + sw.elapsed(TimeUnit.MILLISECONDS) + " milliseconds -----\n");
  //
  // for (Field[] currentLocation : possibleLocations) {
  // locationIndex++;
  // System.out.println("### HANDLE CURRENT-LOCATION AT ");
  // for (Field f : currentLocation) {
  // System.out.print(f);
  // }
  // System.out.println(
  // " with index " + locationIndex + " out of " + possibleLocations.size() + " ... ###");
  // // sw.reset();
  // // sw.start();
  //
  // currentLayedDownTiles = null;
  // indicesOnRack = new ArrayList<Integer>();
  // while ((currentLayedDownTiles =
  // nextTiles(currentLayedDownTiles, indicesOnRack, k)) != null) {
  // System.out.println(currentLayedDownTiles);
  // for (int i = 0; i < currentLayedDownTiles.size(); i++) {
  // // this.moveToGameBoard(indicesOnRack.get(i), currentLocation[i].getxCoordinate(),
  // // currentLocation[i].getyCoordinate());
  // currentLocation[i].setTile(currentLayedDownTiles.get(i));
  // this.setRackTileToNone(indicesOnRack.get(i));
  // System.out.println("##### TILE MOVED FROM RACK TO GB #####");
  // System.out.println(currentLayedDownTiles.get(i));
  // }
  // currentTurn = new Turn(this.getNickname(), gc);
  // currentTurn.setLaydDownTiles(currentLayedDownTiles);
  // for (Tile t : currentTurn.getLaydDownTiles()) {
  // System.out.println("Tile newly layed down: " + t + " with field " + t.getField());
  // }
  // if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
  // maximumScore = currentTurn.getTurnScore();
  // maximumLocation = currentLocation;
  // maximumIndicesOnRack = indicesOnRack;
  // // layedDownTileListWithMaximumScore = currentLayedDownTiles;
  // // layedDownFieldsWithMaximumScore = currentLocation;
  //
  // turnWithMaximumScore = currentTurn.getDeepCopy();
  //
  // System.out.println("############### NEW MAXIMUM SCORE: ##############" + maximumScore);
  // for (Tile t : turnWithMaximumScore.getLaydDownTiles()) {
  // System.out.println("Tile newly layed down: " + t);
  // }
  // }
  // // if (currentLayedDownTiles != null) {
  // this.cleanupGameboardAndRack(currentLayedDownTiles, indicesOnRack);
  // // }
  // }
  // // sw.stop();
  // // System.out.println("----- current location handled in " +
  // // sw.elapsed(TimeUnit.MILLISECONDS)
  // // + " milliseconds -----\n");
  // }
  // }
  // if (turnWithMaximumScore == null) {
  // return null;
  // }
  // System.out.println();
  // System.out.println();
  // System.out.println("Maximum score: " + maximumScore);
  // // for (int i = 0; i < turnWithMaximumScore.getLaydDownTiles().size(); i++) {
  // //// this.moveToGameBoard(maximumIndicesOnRack.get(i), maximumLocation[i].getxCoordinate(),
  // // maximumLocation[i].getyCoordinate());
  // // maximumLocation[i].setTile(turnWithMaximumScore.getLaydDownTiles().get(i));
  // // this.setRackTileToNone(maximumIndicesOnRack.get(i));
  // // System.out
  // // .println("Tile newly layed down: " + turnWithMaximumScore.getLaydDownTiles().get(i));
  // // }
  // System.out.println("----------------------");
  // for (Word w : turnWithMaximumScore.getWords()) {
  // System.out.println(w.toString());
  // }
  // System.out.println("----------------------");
  // // for (Field f : layedDownFieldsWithMaximumScore) {
  // // System.out.println(f);
  // // }
  // // timeOverall.stop();
  // // System.out.println("\n AI finished in " + timeOverall.elapsed(TimeUnit.SECONDS) + " seconds
  // // ("+ timeOverall.elapsed(TimeUnit.MINUTES) + " minutes)\n");
  // System.out.println("RACKTILES ()");
  // for (Tile t : this.getRackTiles()) {
  // System.out.println(t.getLetter().getCharacter());
  // }
  // // for (int i = 0; i < .size(); i++) {
  // // maximumLocation[i].setTile(currentLayedDownTiles.get(i));
  // // this.setRackTileToNone(indicesOnRack.get(i));
  // // // System.out.println("##### TILE MOVED FROM RACK TO GB #####");
  // // // System.out.println(currentLayedDownTiles.get(i));
  // // }
  // // TODO clientProtocoll (update ui), und im Server (updateServerUI)
  //
  // return turnWithMaximumScore;
  //
  // }



  private void cleanupGameboardAndRack(List<Tile> layedDownTileList, List<Integer> indicesOnRack) {
    Tile t;
    for (int i = 0; i < layedDownTileList.size(); i++) {
      t = layedDownTileList.get(i);
      t.getField().setTileOneDirection(null);
      this.setRackTile(indicesOnRack.get(i), t);
      // this.moveToRack(t, indicesOnRack.get(i));
    }

  }

  // public ArrayList<Tile> nextTiles(ArrayList<Tile> current, ArrayList<Integer> indicesOnRack,
  // int numOfTiles) {
  // boolean isOnCombinationList;
  // TreeSet<Integer> changedIndicesInCurrent = new TreeSet<Integer>();
  // int numberOfCOmbinationsAlreadyCovered;
  // if (current == null) {
  // current = new ArrayList<Tile>();
  // for (int i = 0; i < numOfTiles; i++) {
  // current.add(this.getRackTile(i));
  // indicesOnRack.add(i);
  // changedIndicesInCurrent.add(i);
  // changedIndicesInCurrent.add(i - 1);
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
  // for (AIcombination a : this.currentTwoTilesCombinations.descendingSet()) {
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
  // int currentRackIndex;
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
  // for (AIcombination a : this.currentTwoTilesCombinations.descendingSet()) {
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
   * @return the numberOfCombinationSize
   */
  // public int getNumberOfCombinationSize() {
  // return numberOfCombinationSize;
  // }

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

}
