package game;

import java.io.BufferedReader;
import java.io.File;
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
  private Turn turn;
  private int currentPlayerIndex;
  private HashSet<String> dictionary;


  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");
  private static File file = new File(baseDir + "CollinsScrabbleWords.txt");

  public GameController(GameState gameState) {
    fillDictionary();
    this.gameState = gameState;
    this.tileBag = new TileBag();
  }

  /**
   * This method fills the dictionary with words.
   * 
   * @author lurny
   * 
   */
  public void fillDictionary() {
    this.dictionary = new HashSet<String>();

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      Pattern p = Pattern.compile("\\w*");
      String line;
      while ((line = br.readLine()) != null) {
        Matcher m = p.matcher(line);
        if (!line.isEmpty()) {
          m.find();
          this.dictionary.add(line.substring(m.regionStart(), m.end()));
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

  /**
   * This method gets the 7 initial tiles for the current player.
   * 
   * @author lurny
   * @return ArrayList, that contains 7 tiles from tile bag
   */
  public List<Tile> drawInitialTiles() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < 7; i++) {
      tiles.add(tileBag.drawTile());
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
   * 
   * @param liste
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

  public boolean addTileToGameBoard(String player, Tile t, int x, int y) {
    if (!gameState.getCurrentPlayer().equals(player)
        || gameState.getGameBoard().getField(x, y).getTile() != null) {
      return false;
    }

    t.setField(gameState.getGameBoard().getField(x, y));
    t.setOnRack(false);
    t.setOnGameBoard(true);
    this.turn.addTileToTurn(t);

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
        || beforeField.getTile().isPlayed() || afterField.getTile() != null) {
      return false;
    }

    this.turn.moveTileInTurn(beforeField.getTile(), afterField);
    beforeField.getTile().setField(afterField);
    beforeField.setTile(null);

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
    beforeField.setTile(null);

    return true;
  }

  public boolean checkRemoveTileFromGameBoard(String player, int x, int y) {
    Field beforeField = gameState.getGameBoard().getField(x, y);
    if (!gameState.getCurrentPlayer().equals(player) || beforeField == null
        || beforeField.getTile().isPlayed()) {
      return false;
    }

    return true;
  }

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

}
