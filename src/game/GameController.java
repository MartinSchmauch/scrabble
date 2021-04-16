package game;

import java.util.ArrayList;
import java.util.List;
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

  public GameController(GameState gameState) {
    this.gameState = gameState;
    this.tileBag = new TileBag();
  }

  public void newTurn() {
    this.turn = new Turn(gameState.getCurrentPlayer());
  }

  public Turn getTurn() {
    return this.turn;
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

  public String getNextPlayer() {
    this.currentPlayerIndex++;
    if (this.currentPlayerIndex >= this.gameState.getAllPlayers().size()) {
      this.currentPlayerIndex = 0;
    }
    String nextPlayer = this.gameState.getAllPlayers().get(currentPlayerIndex).getNickname();
    return nextPlayer;
  }
}
