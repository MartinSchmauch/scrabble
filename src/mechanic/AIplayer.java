package mechanic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pkoenig
 *
 */
public class AIplayer extends Player {

  private int maximumWordlength;

  public AIplayer(String nickname, int maximumWordLength) {
    super(nickname);
    this.maximumWordlength = maximumWordLength;
  }

  @JsonCreator
  public AIplayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume);
  }

  public ArrayList<Field[]> getValidWordPositionsForWordLength(GameBoard gb, int wordlength) {
    if (wordlength <= 0) {
      return null;
    }
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    Field[] singleWordPosition = new Field[wordlength];
    for (int j = 1; j <= gb.getFields().length; j++) {
      for (int i = 1; i <= gb.getFields().length; i++) {
        if (gb.getField(i, j).getTop() != null && gb.getField(i, j).getTop().getTile() != null
            || gb.getField(i, j).getRight() != null
                && gb.getField(i, j).getRight().getTile() != null
            || gb.getField(i, j).getBottom() != null
                && gb.getField(i, j).getBottom().getTile() != null
            || gb.getField(i, j).getLeft() != null
                && gb.getField(i, j).getLeft().getTile() != null) {
          // horizontal
          for (int k = i - wordlength; k < i; k++) {
            if (!(k < 1) && !(k + wordlength > gb.getFields().length)) {
              for (int z = 0; z < wordlength; z++) {
                singleWordPosition[z] = gb.getField(k + z + 1, j);
              }
              results.add(singleWordPosition);
              singleWordPosition = new Field[wordlength];
            }
          }
          // Vertical
          for (int k = j - wordlength; k < j; k++) {
            if (!(k < 1) && !(k + wordlength > gb.getFields().length)) {
              for (int z = 0; z < wordlength; z++) {
                singleWordPosition[z] = gb.getField(i, k + z + 1);
              }
              results.add(singleWordPosition);
              singleWordPosition = new Field[wordlength];
            }
          }
        }
      }

    }

    System.out.println("\n ACTUAL");
    for (int i = 0; i < results.size(); i++) {
      System.out.println("\n" + results.get(i));
      for (Field f : results.get(i)) {
        System.out.println(f.toString());
      }

    }
    return results;

  }

  /**
   * 
   * @param index, 0: row, 1: col
   * @param axis
   * @return list of tiles
   */
  public List<Tile> generateLayedDownTiles(GameBoard gb) {
    ArrayList<Field[]> possibleLocations;
    ArrayList<Field[]> currentLocations;
    HashSet<WordOnList> currentDictionary;
    List<Tile> layedDownTileList;
    Turn currentTurn;
    int maximumScore = 0;
    List<Tile> layedDownTileListMaximumScore = null;
    for (int k = 2; k <= this.maximumWordlength; k++) {
      possibleLocations = getValidWordPositionsForWordLength(gb, k);
      for (int j = 1; j <= gb.getFields().length; j++) {
        currentLocations = getRowLocations(possibleLocations, j);
        currentDictionary = gb.getWordlist().getWordsWithLength(k);
        for (Field[] location : currentLocations) {
          layedDownTileList = generateWord(gb, location, currentDictionary);
          currentTurn = new Turn(layedDownTileList);
          if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
            maximumScore = currentTurn.getTurnScore();
            layedDownTileListMaximumScore = layedDownTileList;
          }
        }
      }
      for (int i = 1; i <= gb.getFields().length; i++) {
        currentLocations = getColLocations(possibleLocations, i);
        currentDictionary = gb.getWordlist().getWordsWithLength(k);
        for (Field[] location : currentLocations) {
          layedDownTileList = generateWord(gb, location, currentDictionary);
          currentTurn = new Turn(layedDownTileList);
          if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
            maximumScore = currentTurn.getTurnScore();
            layedDownTileListMaximumScore = layedDownTileList;
          }
        }
      }
    }
    return layedDownTileListMaximumScore;

  }

  private List<Tile> generateWord(GameBoard gb, Field[] currentLocation,
      HashSet<WordOnList> currentDictionary) {
    List<Tile> result = new ArrayList<Tile>();
    boolean isValid;
    for (WordOnList word : currentDictionary) {
      isValid = true;
      for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
        if (currentLocation[wordIndex].getTile() != null && word.getWordString()
            .charAt(wordIndex) == currentLocation[wordIndex].getTile().getLetter().getChar()) {
          isValid = false;
          break;
        }
      }
      if (isValid) {
        for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
          result.add(currentLocation[wordIndex].getTile());
        }
      }
      return result;
    }
    return null;

  }

  private ArrayList<Field[]> getColLocations(ArrayList<Field[]> possibleLocations, int i) {
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    boolean inRow;
    for (Field[] possibleLocation : possibleLocations) {
      inRow = true;
      for (Field field : possibleLocation) {
        if (field.getxCoordinate() != i) {
          inRow = false;
          break;
        }
      }
      if (inRow) {
        results.add(possibleLocation);
      }
    }
    return results;
  }

  private ArrayList<Field[]> getRowLocations(ArrayList<Field[]> possibleLocations, int j) {
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    boolean inRow;
    for (Field[] possibleLocation : possibleLocations) {
      inRow = true;
      for (Field field : possibleLocation) {
        if (field.getyCoordinate() != j) {
          inRow = false;
          break;
        }
      }
      if (inRow) {
        results.add(possibleLocation);
      }
    }
    return results;
  }

}
