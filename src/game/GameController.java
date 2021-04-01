package game;

import java.util.List;
import mechanic.Field;
import mechanic.GameBoard;
import mechanic.Player;
import mechanic.Tile;


/**
 * The GameController is responsible to evaluate and verify all steps taken in the game and respond
 * accordingly.
 * 
 * @author ldreyer
 * @author lurny
 */

public class GameController {
  private GameBoard gb;
  private GameState gS;

  public GameController(GameState gameState) {
    this.gS = gameState;
    setUpGameboard();
  }

  /**
   * setUp Gameboard with special Fields
   * 
   * @author lurny
   */
  public void setUpGameboard() {
    this.gb = new GameBoard(GameSettings.getGameBoardSize());
    List<Field> specialFields = GameSettings.getSpecialFields();
    for (Field f : specialFields) {
      this.gb.getField(f.getxCoordinate(), f.getyCoordinate())
          .setLetterMultiplier(f.getLetterMultiplier());
      this.gb.getField(f.getxCoordinate(), f.getyCoordinate())
          .setWordMultiplier(f.getWordMultiplier());
    }
  }

  public GameBoard getGameBoard() {
    return this.gb;
  }

  public GameState getGameState() {
    return this.gS;
  }


  /**
   * This method validates the move of a tile from a player's rack to the game board. If allowed,
   * the method updates the game board.
   */

  public boolean layDownLetterFromRack(Player player, int rackFieldIndex, int xCoordinate,
      int yCoordinate) {
    if (!gS.currentPlayer.equals(player.getNickname())
        || gb.getField(xCoordinate, yCoordinate).getTile() == null
        || player.getRackTile(rackFieldIndex) == null) {
      return false;
    }

    Tile t = player.removeRackTile(rackFieldIndex);
    t.setField(gb.getField(xCoordinate, yCoordinate));
    t.setOnRack(false);
    t.setOnGameBoard(true);

    return true;
  }

  /**
   * This method validates the move of a tile from a field on the game board to another field on the
   * game board. If allowed, the method updates the game board.
   */

  public boolean moveTileOnGameBoard(Player player, int xCoordinateBefore, int yCoordinateBefore,
      int xCoordinateAfter, int yCoordinateAfter) {
    Field beforeField = gb.getField(xCoordinateBefore, yCoordinateBefore);

    if (!gS.currentPlayer.equals(player.getNickname()) || beforeField == null
        || beforeField.getTile().isPlayed()) {
      return false;
    }

    Field afterField = gb.getField(xCoordinateAfter, yCoordinateAfter);
    beforeField.getTile().setField(afterField);

    return true;
  }

  /**
   * This method validates the move of a tile from a field on the game board back to the player's
   * rack. If allowed, the method updates the game board.
   */

  public boolean takeTileBackToRack(Player player, int rackFieldIndex, int xCoordinate,
      int yCoordinate) {
    Field beforeField = gb.getField(xCoordinate, yCoordinate);
    if (!gS.currentPlayer.equals(player.getNickname()) || beforeField == null
        || player.getRackTile(rackFieldIndex) == null || beforeField.getTile().isPlayed()) {
      return false;
    }

    Tile tile = beforeField.getTile();
    tile.setField(null);
    tile.setOnRack(true);
    tile.setOnGameBoard(false);
    player.setRackTile(rackFieldIndex, tile);

    return true;
  }
}
