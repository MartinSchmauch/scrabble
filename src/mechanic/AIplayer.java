package mechanic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

  /**
   * 
   * @param gb
   * @param wordlength
   * @return
   */
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

    // System.out.println("\n ACTUAL");
    // for (int i = 0; i < results.size(); i++) {
    // System.out.println("\n" + results.get(i));
    // for (Field f : results.get(i)) {
    // System.out.println(f.toString());
    // }
    //
    // }
    return results;

  }

  /**
   * 
   * @param index, 0: row, 1: col
   * @param axis
   * @return list of tiles
   */
  public List<Tile> generateLayedDownFields(GameBoard gb) {
    ArrayList<Field[]> possibleLocations;
    ArrayList<Field[]> currentLocations;
    HashSet<WordOnList> currentDictionary;
    List<Tile> layedDownTileList;
    Turn currentTurn;
    int maximumScore = 0;
    List<Tile> layedDownTileListWithMaximumScore = null;
    Field[] layedDownFieldsWithMaximumScore = null;
    // List<Tile> rack = new ArrayList<Tile>();
    for (int k = 2; k <= this.maximumWordlength; k++) {
      possibleLocations = getValidWordPositionsForWordLength(gb, k);
      for (int j = 1; j <= gb.getFields().length; j++) {
        currentLocations = getRowLocations(possibleLocations, j);
        currentDictionary = gb.getWordlist().getWordsWithLength(k);
        for (Field[] location : currentLocations) {
          // System.arraycopy(rack, 0, this.getRackTiles().toArray(rack), 0, 7);
          // rack = this.getRackTiles();
          // rack = this.getRackTiles().toArray(rack);
          layedDownTileList = generateWord(gb, location, currentDictionary);
          System.out.println("############## Neuer Turn ###############");
          if (layedDownTileList != null) {
            currentTurn = new Turn(this.getNickname());
            currentTurn.setLaydDownTiles(layedDownTileList);
            if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
              maximumScore = currentTurn.getTurnScore();
              layedDownTileListWithMaximumScore = layedDownTileList;
              layedDownFieldsWithMaximumScore = location;
            }
            if (layedDownTileList != null) {
              this.cleanupGameboard(layedDownTileList);
            }
          }
        }
      }
      for (int i = 1; i <= gb.getFields().length; i++) {
        currentLocations = getColLocations(possibleLocations, i);
        currentDictionary = gb.getWordlist().getWordsWithLength(k);
        for (Field[] location : currentLocations) {
          // System.arraycopy(rack, 0, this.getRackTiles().toArray(rack), 0, 7);
          // rack = this.getRackTiles();
          // rack = this.getRackTiles().toArray(rack);
          layedDownTileList = generateWord(gb, location, currentDictionary);
          currentTurn = new Turn(this.getNickname());
          if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
            maximumScore = currentTurn.getTurnScore();
            layedDownTileListWithMaximumScore = layedDownTileList;
            layedDownFieldsWithMaximumScore = location;
          }
          if (layedDownTileList != null) {
            this.cleanupGameboard(layedDownTileList);
          }
        }
      }
    }
    System.out.println("Maximum score: " + maximumScore);
    for (Field f : layedDownFieldsWithMaximumScore) {
      System.out.println("Field newly layed down: " + f);
    }
    return layedDownTileListWithMaximumScore;

  }

  private void cleanupGameboard(List<Tile> layedDownTileList) {
    for (Tile t : layedDownTileList) {
      t.getField().setTileOneDirection(null);;
      this.getFreeRackField().setTile(t);
      // t.setFieldOneDirection(null);
    }

  }

  // private List<Tile> generateWord(GameBoard gb, Field[] currentLocation,
  // HashSet<WordOnList> currentDictionary, List<Tile> rack) {
  // List<Tile> result = new ArrayList<Tile>();
  // boolean isValid;
  // boolean isValidForTilesOnRack_oneChar;
  // boolean atLeastOneRackTileUsed;
  // Tile t;
  // for (WordOnList word : currentDictionary) {
  // isValid = true;
  // atLeastOneRackTileUsed = false;
  // for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
  // isValidForTilesOnRack_oneChar = false;
  // if (currentLocation[wordIndex].getTile() != null && word.getWordString()
  // .charAt(wordIndex) == currentLocation[wordIndex].getTile().getLetter().getChar()) {
  // isValid = false;
  // break;
  // }
  // for (int i = 0; i < rack.size(); i++) {
  // t = rack.get(i);
  // if (rack.get(i) != null
  // && t.getLetter().getChar() == word.getWordString().charAt(wordIndex)) {
  // isValidForTilesOnRack_oneChar = true;
  // atLeastOneRackTileUsed = true;
  // rack.remove(i);
  // break;
  // }
  // }
  // if (!isValidForTilesOnRack_oneChar) {
  // isValid = false;
  // break;
  // }
  // }
  // if (isValid && atLeastOneRackTileUsed) {
  // for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
  // if (currentLocation[wordIndex].getTile() == null) {
  // currentLocation[wordIndex].setTile(new Tile(new Letter(word.getWordString().charAt(wordIndex),
  // 1, 1), currentLocation[wordIndex]));
  // }
  // result.add(currentLocation[wordIndex].getTile());
  // }
  // }
  // }
  // return result;
  //
  // }
  /**
   * 
   * @param gb
   * @param currentLocation
   * @param currentDictionary
   * @param rack
   * @return
   */
  private List<Tile> generateWord(GameBoard gb, Field[] currentLocation,
      HashSet<WordOnList> currentDictionary) {
    List<Tile> result = new ArrayList<Tile>();
    boolean isValid;
    boolean atLeastOneRackTileNeeded = false;
    Tile t;

    // check if all fields already have tiles on gameboard
    for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
      if (currentLocation[wordIndex].getTile() == null) {
        atLeastOneRackTileNeeded = true;
      }
    }
    if (!atLeastOneRackTileNeeded) {
      return null;
    }

    for (WordOnList word : currentDictionary) {
      isValid = true;
      for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
        if ((t = currentLocation[wordIndex].getTile()) != null) {
          if (!(t.getLetter().getCharacter() == word.getWordString().charAt(wordIndex))) {
            isValid = false;
            break;
          }
        } else {
          isValid = false; // will be set to true by for-loop if char is found in rack
          for (int i = 0; i < this.getRackTiles().size(); i++) {
            if (this.getRackTile(i) != null && word.getWordString().charAt(wordIndex) == this
                .getRackTile(i).getLetter().getCharacter()) {
              isValid = true;
              break;
            }
          }
          if (!isValid) {
            break;
          }
        }
      }
      if (isValid) {
        for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
          if (currentLocation[wordIndex].getTile() == null) {
            for (int i = 0; i < this.getRackTiles().size(); i++) {
              if (this.getRackTile(i) != null && word.getWordString().charAt(wordIndex) == this
                  .getRackTile(i).getLetter().getCharacter()) {
                // currentLocation[wordIndex].setOnlyTile(rack[i]);
                // currentLocation[wordIndex].getTile().setOnlyField(currentLocation[wordIndex]);
                // currentLocation[wordIndex].getTile().setOnGameBoard(true);
                // currentLocation[wordIndex].getTile().setOnRack(false);
                // currentLocation[wordIndex].getTile().setPlayed(true);
                // t = new Tile(new Letter(word.getWordString().charAt(wordIndex),
                // rack[i].getLetter().getLetterValue(), rack[i].getLetter().getCount()), )
                // result.add(new Tile())
                currentLocation[wordIndex].setTile(this.getRackTile(i));
                result.add(currentLocation[wordIndex].getTile());

                System.out.println("Neuer Buchstabe");
                System.out.println(this.getRackTile(i));
                System.out.println(currentLocation[wordIndex]);
                System.out.println("------------");

                this.setRackTileToNone(i);
                break;
              }
            }
          } else {
            // result.add(currentLocation[wordIndex].getTile());
          }
        }
        return result;
      }
    }
    return null;

  }

  private ArrayList<Field[]> getColLocations(ArrayList<Field[]> possibleLocations, int i) {
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    boolean inRow;
    for (Field[] possibleLocation : possibleLocations) {
      inRow = true;
      for (Field field : possibleLocation) {
        if (field.getxCoordinate() != i - 1) {
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
        if (field.getyCoordinate() != j - 1) {
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
