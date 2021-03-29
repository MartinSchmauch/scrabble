package game;

import java.util.List;
import mechanic.Field;
import mechanic.GameBoard;
import mechanic.Player;
import mechanic.Tile;


/**
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


  public boolean layDownLetterFromRack(Player player, int rackFieldIndex, int xCoordinate,
      int yCoordinate) {
    if (!gS.currentPlayer.equals(player) || gb.getField(xCoordinate, yCoordinate).getTile() == null
        || player.getRackTile(rackFieldIndex) == null) {
      return false;
    }

    Tile t = player.removeRackTile(rackFieldIndex);
    t.setField(gb.getField(xCoordinate, yCoordinate));
    t.setOnRack(false);
    t.setOnGameBoard(true);

    return true;
  }

  public boolean moveTileOnGameBoard(Player player, int xCoordinateBefore, int yCoordinateBefore,
      int xCoordinateAfter, int yCoordinateAfter) {
    Field beforeField = gb.getField(xCoordinateBefore, yCoordinateBefore);

    if (!gS.currentPlayer.equals(player) || beforeField == null
        || beforeField.getTile().isPlayed()) {
      return false;
    }

    Field afterField = gb.getField(xCoordinateAfter, yCoordinateAfter);
    beforeField.getTile().setField(afterField);

    return true;
  }

  public boolean takeTileBackToRack(Player player, int rackFieldIndex, int xCoordinate,
      int yCoordinate) {
    Field beforeField = gb.getField(xCoordinate, yCoordinate);
    if (!gS.currentPlayer.equals(player) || beforeField == null
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
