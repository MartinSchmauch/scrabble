package game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import mechanic.Letter;
import mechanic.Player;
import mechanic.Tile;
import org.junit.Before;
import org.junit.Test;


/**
 * This class tests the game controller's functionality.
 *
 * @author lurny
 * @author ldreyer
 */

public class GameControllerTest {
  GameController gc;
  GameState gameState;
  Player host;


  /** Initializes needed attributes for the test. */
  @Before
  public void before() {
    host = new Player("Host");
    gameState = new GameState(host.getPlayerInfo(), null);
    gc = new GameController(gameState);
  }

  @Test
  public void setUpGameBoardTest() {
    assertEquals(gameState.getGameBoard().getField(7, 7).getLetterMultiplier(), 2);

    Tile t = new Tile(new Letter('A', 1, 2), gameState.getGameBoard().getField(1, 1));
    assertEquals(gameState.getGameBoard().getField(1, 1).getTile(), t);
    assertEquals(t.getField(), gameState.getGameBoard().getField(1, 1));

    gameState.getGameBoard().getField(1, 1).setTile(null);
    assertNull(t.getField());
    assertNull(gameState.getGameBoard().getField(1, 1).getTile());
  }

}
