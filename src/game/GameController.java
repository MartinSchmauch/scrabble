package game;

import mechanic.Field;
import mechanic.Tile;
import mechanic.Turn;


/**
 * The GameController is responsible to evaluate and verify all steps taken in the game and respond
 * accordingly.
 * 
 * @author ldreyer
 * @author lurny
 */

public class GameController {
  private GameState gS;
  private Turn turn;

  public GameController(GameState gameState) {
    this.gS = gameState;
  }

  public void newTurn() {
    this.turn = new Turn(gS.getCurrentPlayer());
  }

  public int endTurn() {
    if (!this.turn.calculateWords()) {
      return -1;
    }

    this.turn.calculateTurnScore();
    gS.addScore(this.turn.getPlayer(), this.turn.getTurnScore());

    return this.turn.getTurnScore();
  }


  /**
   * This method validates the move of a tile from a player's rack to the game board. If allowed,
   * the method updates the game board.
   */

  public boolean addTileToGameBoard(String player, Tile t, int xCoordinate, int yCoordinate) {
    if (!gS.getCurrentPlayer().equals(player)
        || gS.getGameBoard().getField(xCoordinate, yCoordinate).getTile() != null) {
      return false;
    }

    t.setField(gS.getGameBoard().getField(xCoordinate, yCoordinate));
    t.setOnRack(false);
    t.setOnGameBoard(true);
    this.turn.addTileToTurn(t);

    return true;
  }

  /**
   * This method validates the move of a tile from a field on the game board to another field on the
   * game board. If allowed, the method updates the game board.
   */

  public boolean moveTileOnGameBoard(String player, int xCoordinateBefore, int yCoordinateBefore,
      int xCoordinateAfter, int yCoordinateAfter) {
    Field beforeField = gS.getGameBoard().getField(xCoordinateBefore, yCoordinateBefore);
    Field afterField = gS.getGameBoard().getField(xCoordinateAfter, yCoordinateAfter);

    if (!gS.getCurrentPlayer().equals(player) || beforeField == null
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

  public boolean removeTileFromGameBoard(String player, int xCoordinate, int yCoordinate) {
    Field beforeField = gS.getGameBoard().getField(xCoordinate, yCoordinate);
    if (!gS.getCurrentPlayer().equals(player) || beforeField == null
        || beforeField.getTile().isPlayed()) {
      return false;
    }

    this.turn.removeTileFromTurn(beforeField.getTile());
    beforeField.setTile(null);

    return true;
  }
}
