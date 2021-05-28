package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mechanic.Field;
import mechanic.Tile;
import mechanic.TileBag;
import mechanic.Turn;


/**
 * The GameController is responsible to evaluate and verify all steps taken in the game and respond
 * accordingly.
 *
 * @author ldreyer
 * @author lurny
 */

public class GameController {
  private GameState gameState;
  private TileBag tileBag;
  private List<Turn> turns;
  private List<Turn> scoredTurns;
  private Turn turn;
  private int currentPlayerIndex;
  private HashSet<String> dictionary;

  /**
   * creates an instance of the class.
   */
  public GameController(GameState gameState) {
    fillDictionary();
    this.gameState = gameState;
    this.gameState.setUpGameboard();
    this.tileBag = new TileBag();
    this.turns = new ArrayList<Turn>();
    this.scoredTurns = new ArrayList<Turn>();
  }

  /**
   * This method fills the dictionary with words.
   *
   * @author lurny
   * 
   */
  public void fillDictionary() {
    this.dictionary = new HashSet<String>();

    try (BufferedReader br = new BufferedReader(new FileReader(GameSettings.getDictionary()))) {
      Pattern p = Pattern.compile("[^\\n\\s]*");
      String line;
      while ((line = br.readLine()) != null) {
        Matcher m = p.matcher(line);
        if (!line.isEmpty()) {
          m.find();
          this.dictionary.add(line.substring(m.regionStart(), m.end()).toUpperCase());
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void newTurn() {
    this.turn = new Turn(gameState.getCurrentPlayer(), this);
  }

  public HashSet<String> getDictionary() {
    return dictionary;
  }

  public Turn getTurn() {
    return this.turn;
  }

  public void setTurn(Turn turn) {
    this.turn = turn;
  }

  public List<Turn> getTurns() {
    return turns;
  }

  public List<Tile> drawTutorial(char[] chars) {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < chars.length; i++) {
      if (!tileBag.isEmpty()) {
        tiles.add(tileBag.drawTile(chars[i]));
      }
    }
    return tiles;
  }

  /**
   * This method gets the selected amount of tiles for the current player.
   *
   * @author lurny
   */
  public List<Tile> drawTiles(int amount) {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < amount; i++) {
      if (!tileBag.isEmpty()) {
        tiles.add(tileBag.drawTile());
      }
    }
    return tiles;
  }
  
  /**
   * This method gets the new tiles for the current player, determining the amount by checking the
   * placed tiles of the current turn. If the bag is empty no more tiles are returned.
   *
   * @return ArrayList of tiles from tile bag
   */

  public List<Tile> drawTiles() {
    List<Tile> tiles = new ArrayList<Tile>();

    for (int i = 0; i < this.turn.getLaydDownTiles().size(); i++) {
      if (!tileBag.isEmpty()) {
        tiles.add(tileBag.drawTile());
      }
    }

    return tiles;
  }

  /**
   * This method adds Tiles from the parameter tileList to the TileBag.
   *
   * @author lurny
   */
  public void addTilesToTileBag(List<Tile> tileList) {
    for (Tile t : tileList) {
      this.tileBag.addTile(t);
    }
  }


  /**
   * This method validates the move of a tile from a player's rack to the game board. If allowed,
   * the method updates the game board.
   */

  public boolean addTileToGameBoard(String player, Tile tile, int x, int y) {
    System.out.println(gameState.getGameBoard());
    if (!gameState.getCurrentPlayer().equals(player)
        || gameState.getGameBoard().getField(x, y).getTile() != null) {
      return false;
    }

    tile.setField(gameState.getGameBoard().getField(x, y));
    tile.setOnRack(false);
    tile.setOnGameBoard(true);

    this.turn.addTileToTurn(tile);

    return true;
  }

  /**
   * This method validates the move of a tile from a field on the game board to another field on the
   * game board. If allowed, the method updates the game board.
   *
   * @param x1 coordinate before
   * @param y1 coordinate before
   * @param x2 coordinate after
   * @param y2 coordinate after
   */

  public boolean moveTileOnGameBoard(String player, int x1, int y1, int x2, int y2) {
    Field beforeField = gameState.getGameBoard().getField(x1, y1);
    Field afterField = gameState.getGameBoard().getField(x2, y2);

    if (!gameState.getCurrentPlayer().equals(player) || beforeField == null
        || beforeField.getTile() == null || beforeField.getTile().isPlayed()
        || afterField.getTile() != null) {
      return false;
    }

    beforeField.getTile().setField(afterField);
    beforeField.setTileOneDirection(null);

    return true;
  }

  /**
   * This method validates the move of a tile from a field on the game board back to the player's
   * rack. If allowed, the method updates the game board.
   */

  public boolean removeTileFromGameBoard(String player, int x, int y) {
    Field beforeField = gameState.getGameBoard().getField(x, y);
    if (!gameState.getCurrentPlayer().equals(player) || beforeField == null
        || beforeField.getTile().isPlayed()) {
      return false;
    }

    this.turn.removeTileFromTurn(beforeField.getTile());
    beforeField.getTile().setOnRack(true);
    beforeField.getTile().setOnGameBoard(false);
    beforeField.setTile(null);

    return true;
  }

  /**
   * This method checks, if you can remove a Tile from the Gameboard of the player.
   */
  public boolean checkRemoveTileFromGameBoard(String player, int x, int y) {
    Field beforeField = gameState.getGameBoard().getField(x, y);
    if (!gameState.getCurrentPlayer().equals(player) || beforeField == null
        || beforeField.getTile() == null || beforeField.getTile().isPlayed()) {
      return false;
    }

    return true;
  }

  /**
   * This method checks the end game criteria.
   *
   * @author ldreyer
   * @returns true if endGame criteria have been met
   */

  public boolean checkEndGame(boolean tilesLeftOnRack) {
    boolean sixScorelessRounds = false;

    if (this.turns.size() > 6) {
      sixScorelessRounds = true;
      for (int i = 0; i < 6; i++) {
        if (this.turns.get(i).getTurnScore() > 0) {
          sixScorelessRounds = false;
          break;
        }
      }
    }

    if (!tilesLeftOnRack && this.tileBag.isEmpty() || sixScorelessRounds
        || (GameSettings.getMaxScore() > -1 && this.gameState
            .getScore(this.gameState.getCurrentPlayer()) > GameSettings.getMaxScore())) {
      return true;
    }

    return false;
  }

  /**
   * This method is called to get the next player after a player finished his turn.
   *
   * @return String nextPlayer
   */
  public String getNextPlayer() {
    this.currentPlayerIndex++;
    if (this.currentPlayerIndex >= this.gameState.getAllPlayers().size()) {
      this.currentPlayerIndex = 0;
    }
    String nextPlayer = this.gameState.getAllPlayers().get(currentPlayerIndex).getNickname();
    return nextPlayer;
  }

  public GameState getGameState() {
    return gameState;
  }

  public TileBag getTileBag() {
    return tileBag;
  }

  public List<Turn> getScoredTurns() {
    return scoredTurns;
  }

  public void addScoredTurn(Turn t) {
    this.scoredTurns.add(0, t);
  }

  public void addTurn(Turn t) {
    this.turns.add(0, t);
  }

}
