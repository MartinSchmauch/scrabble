package mechanic;

import java.util.ArrayList;
import java.util.Comparator;
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

  private int maxNumOfTiles;

  public AIplayer(String nickname, int maxNumOfTiles) {
    super(nickname);
    this.maxNumOfTiles = maxNumOfTiles;
  }

  @JsonCreator
  public AIplayer(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    super(nickname, avatar, volume);
  }

  /**
   * 
   * @param gb
   * @param numOfTiles
   * @return
   */
  public HashSet<Field[]> getValidTilePositionsForNumOfTiles(GameBoard gb, int numOfTiles) {
    if (numOfTiles <= 1) {
      return null;
    }
    HashSet<Field[]> results = new HashSet<Field[]>(); // no duplicates in results
    Field[] singleTilesPosition = new Field[numOfTiles];
    for (int j = 1; j <= gb.getFields().length; j++) {
      for (int i = 1; i <= gb.getFields().length; i++) {
        System.out.println("####################### NEXT FIELD: " + gb.getField(i, j)
            + " #######################");


        // #####################################################################################
        //
        // TODO !!!!!!!!!! parallel words, for each position (not just left, right (or up, down)
        //
        // #####################################################################################


        if (gb.getField(i, j).getTile() == null) {
          System.out.println("No Tile currently on this field.");
          if (gb.getField(i, j).getTop() != null && gb.getField(i, j).getTop().getTile() != null) {
            // go down vertically
            for (int k = j; k <= j + numOfTiles; k++) {
              if (k == j + numOfTiles) {
                System.out.println("# GENERATED POSITION FOR .TOP -> DOWN #");
                for (Field f : singleTilesPosition) {
                  System.out.println(f);
                }
                System.out.println();
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
            // parallel tiles going right
            if (gb.getField(i, j).getLeft() == null
                || (gb.getField(i, j).getLeft().getTile() == null)) {
              // above check for left tile prevents multiple work
              for (int l = i; l <= i + numOfTiles; l++) {
                if (l == i + numOfTiles) {
                  System.out.println("# GENERATED POSITION FOR .TOP -> RIGHT #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
            // parallel tiles going left
            if (gb.getField(i, j).getRight() == null
                || (gb.getField(i, j).getRight().getTile() == null)) {
              // above check for right tile prevents multiple work
              for (int l = i - numOfTiles + 1; l <= i + 1; l++) {
                if (l == i + 1) {
                  System.out.println("# GENERATED POSITION FOR .TOP -> LEFT #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
          }
          if (gb.getField(i, j).getRight() != null
              && gb.getField(i, j).getRight().getTile() != null) {
            // go left horizontally
            for (int l = i - numOfTiles + 1; l <= i + 1; l++) {
              if (l == i + 1) {
                System.out.println("# GENERATED POSITION FOR .RIGHT -> LEFT #");
                for (Field f : singleTilesPosition) {
                  System.out.println(f);
                }
                System.out.println();
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
            // parallel tiles going down
            if (gb.getField(i, j).getTop() == null
                || gb.getField(i, j).getTop().getTile() == null) {
              // above check for top tile prevents multiple work
              for (int k = j; k <= j + numOfTiles; k++) {
                if (k == j + numOfTiles) {
                  System.out.println("# GENERATED POSITION FOR .RIGHT -> DOWN #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
            // parallel tiles going up
            if (gb.getField(i, j).getBottom() == null
                || gb.getField(i, j).getBottom().getTile() == null) {
              // above check for bottom tile prevents multiple work
              for (int k = j - numOfTiles + 1; k <= j + 1; k++) {
                if (k == j + 1) {
                  System.out.println("# GENERATED POSITION FOR .RIGHT -> UP #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }

          }
          if (gb.getField(i, j).getBottom() != null
              && gb.getField(i, j).getBottom().getTile() != null) {
            // go up vertically
            for (int k = j - numOfTiles + 1; k <= j + 1; k++) {
              if (k == j + 1) {
                System.out.println("# GENERATED POSITION FOR .BOTTOM -> UP #");
                for (Field f : singleTilesPosition) {
                  System.out.println(f);
                }
                System.out.println();
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
            // parallel tiles going right
            if (gb.getField(i, j).getLeft() == null
                || (gb.getField(i, j).getLeft().getTile() == null)) {
              // above check for left tile prevents multiple work
              for (int l = i; l <= i + numOfTiles; l++) {
                if (l == i + numOfTiles) {
                  System.out.println("# GENERATED POSITION FOR .BOTTOM -> RIGHT #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
            // parallel tiles going left
            if (gb.getField(i, j).getRight() == null
                || (gb.getField(i, j).getRight().getTile() == null)) {
              // above check for right tile prevents multiple work
              for (int l = i - numOfTiles + 1; l <= i + 1; l++) {
                if (l == i + 1) {
                  System.out.println("# GENERATED POSITION FOR .BOTTOM -> LEFT #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
          }
          if (gb.getField(i, j).getLeft() != null
              && gb.getField(i, j).getLeft().getTile() != null) {
            // go right horizontally
            for (int l = i; l <= i + numOfTiles; l++) {
              if (l == i + numOfTiles) {
                System.out.println("# GENERATED POSITION FOR .LEFT -> RIGHT #");
                for (Field f : singleTilesPosition) {
                  System.out.println(f);
                }
                System.out.println();
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
            // parallel tiles going down
            if (gb.getField(i, j).getTop() == null
                || gb.getField(i, j).getTop().getTile() == null) {
              // above check for top tile prevents multiple work
              for (int k = j; k <= j + numOfTiles; k++) {
                if (k == j + numOfTiles) {
                  System.out.println("# GENERATED POSITION FOR .LEFT -> DOWN #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }
            // parallel tiles going up
            if (gb.getField(i, j).getBottom() == null
                || gb.getField(i, j).getBottom().getTile() == null) {
              // above check for bottom tile prevents multiple work
              for (int k = j - numOfTiles + 1; k <= j + 1; k++) {
                if (k == j + 1) {
                  System.out.println("# GENERATED POSITION FOR .LEFT -> UP #");
                  for (Field f : singleTilesPosition) {
                    System.out.println(f);
                  }
                  System.out.println();
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
            }

          }
          System.out.println();
        }
      }

    }
    return results;

  }

  public Turn generateIdealTurn(GameBoard gb) {
    HashSet<Field[]> possibleLocations;
    Turn currentTurn;
    int maximumScore = 0;
    //List<Tile> layedDownTileListWithMaximumScore = null;
    Field[] layedDownFieldsWithMaximumScore = null;
    ArrayList<Tile> currentLayedDownTiles;
    ArrayList<Integer> indicesOnRack;
    Turn turnWithMaximumScore = null;
    for (int k = 2; k <= this.maxNumOfTiles; k++) {
      possibleLocations = getValidTilePositionsForNumOfTiles(gb, k);

      for (Field[] currentLocation : possibleLocations) {
        currentLayedDownTiles = null;
        indicesOnRack = new ArrayList<Integer>();
        while ((currentLayedDownTiles =
            nextTiles(currentLayedDownTiles, indicesOnRack, k)) != null) {
          System.out.println(currentLayedDownTiles);
          for (int i = 0; i < currentLayedDownTiles.size(); i++) {
            currentLocation[i].setTile(currentLayedDownTiles.get(i));
            this.setRackTileToNone(indicesOnRack.get(i));
            System.out.println("##### TILE MOVED FROM RACK TO GB #####");
            System.out.println(currentLayedDownTiles.get(i));
          }
          currentTurn = new Turn(this.getNickname());
          currentTurn.setLaydDownTiles(currentLayedDownTiles);
          if (currentTurn.calculateWords() && maximumScore < currentTurn.calculateTurnScore()) {
            maximumScore = currentTurn.getTurnScore();
            //layedDownTileListWithMaximumScore = currentLayedDownTiles;
            layedDownFieldsWithMaximumScore = currentLocation;
            turnWithMaximumScore = currentTurn.getDeepCopy();
            System.out.println("NEW MAXIMUM SCORE: " + maximumScore);
            for (Tile t : turnWithMaximumScore.getLaydDownTiles()) {
              System.out.println("Tile newly layed down: " + t);
            }
          }
          // if (currentLayedDownTiles != null) {
          this.cleanupGameboardAndRack(currentLayedDownTiles, indicesOnRack);
          // }
        }
      }
    }
    System.out.println();
    System.out.println();
    System.out.println("Maximum score: " + maximumScore);
    for (Tile t : turnWithMaximumScore.getLaydDownTiles()) {
      System.out.println("Tile newly layed down: " + t);
    }
    System.out.println("----------------------");
    for (Word w : turnWithMaximumScore.getWords()) {
      System.out.println(w.toString());
    }
    System.out.println("----------------------");
    for (Field f : layedDownFieldsWithMaximumScore) {
      System.out.println(f);
    }
    return turnWithMaximumScore;

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
    if (current == null) {
      current = new ArrayList<Tile>();
      for (int i = 0; i < numOfTiles; i++) {
        current.add(this.getRackTile(i));
        indicesOnRack.add(i);
      }
      return current;
    } else {
      for (int i = numOfTiles - 1; i >= 0; i--) {
        int currentRackIndex = indicesOnRack.get(i);
        for (currentRackIndex++; currentRackIndex < 7; currentRackIndex++) {
          if (indicesOnRack.indexOf(currentRackIndex) == -1
              || indicesOnRack.indexOf(currentRackIndex) > i) {
            current.set(i, this.getRackTile(currentRackIndex));
            indicesOnRack.set(i, currentRackIndex);
            for (i++; i < numOfTiles; i++) {
              for (int updateRackIndex = 0; updateRackIndex < 7; updateRackIndex++) {
                if (indicesOnRack.indexOf(updateRackIndex) == -1) {
                  current.set(i, this.getRackTile(updateRackIndex));
                  indicesOnRack.set(i, updateRackIndex);
                  break;
                }
              }
            }
            return current;
          }
        }
      }
      return null;
    }
  }

  /**
   * 
   * @param gb
   * @param currentLocation
   * @param currentDictionary
   * @param rack
   * @return
   */
  // private List<Tile> generateWord(GameBoard gb, Field[] currentLocation,
  // HashSet<WordOnList> currentDictionary) {
  // List<Tile> result = new ArrayList<Tile>();
  // boolean isValid;
  // boolean atLeastOneRackTileNeeded = false;
  // Tile t;
  //
  // // check if all fields already have tiles on gameboard
  // for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
  // if (currentLocation[wordIndex].getTile() == null) {
  // atLeastOneRackTileNeeded = true;
  // }
  // }
  // if (!atLeastOneRackTileNeeded) {
  // return null;
  // }
  //
  // for (WordOnList word : currentDictionary) {
  // isValid = true;
  // for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
  // if ((t = currentLocation[wordIndex].getTile()) != null) {
  // if (!(t.getLetter().getChar() == word.getWordString().charAt(wordIndex))) {
  // isValid = false;
  // break;
  // }
  // } else {
  // isValid = false; // will be set to true by for-loop if char is found in rack
  // for (int i = 0; i < this.getRackTiles().size(); i++) {
  // if (this.getRackTile(i) != null && word.getWordString().charAt(wordIndex) == this
  // .getRackTile(i).getLetter().getChar()) {
  // isValid = true;
  // break;
  // }
  // }
  // if (!isValid) {
  // break;
  // }
  // }
  // }
  // if (isValid) {
  // for (int wordIndex = 0; wordIndex < currentLocation.length; wordIndex++) {
  // if (currentLocation[wordIndex].getTile() == null) {
  // for (int i = 0; i < this.getRackTiles().size(); i++) {
  // if (this.getRackTile(i) != null && word.getWordString().charAt(wordIndex) == this
  // .getRackTile(i).getLetter().getChar()) {
  // // currentLocation[wordIndex].setOnlyTile(rack[i]);
  // // currentLocation[wordIndex].getTile().setOnlyField(currentLocation[wordIndex]);
  // // currentLocation[wordIndex].getTile().setOnGameBoard(true);
  // // currentLocation[wordIndex].getTile().setOnRack(false);
  // // currentLocation[wordIndex].getTile().setPlayed(true);
  // // t = new Tile(new Letter(word.getWordString().charAt(wordIndex),
  // // rack[i].getLetter().getLetterValue(), rack[i].getLetter().getCount()), )
  // // result.add(new Tile())
  // currentLocation[wordIndex].setTile(this.getRackTile(i));
  // result.add(currentLocation[wordIndex].getTile());
  //
  // System.out.println("Neuer Buchstabe");
  // System.out.println(this.getRackTile(i));
  // System.out.println(currentLocation[wordIndex]);
  // System.out.println("------------");
  //
  // this.setRackTileToNone(i);
  // break;
  // }
  // }
  // } else {
  // // result.add(currentLocation[wordIndex].getTile());
  // }
  // }
  // return result;
  // }
  // }
  // return null;
  // }
}
