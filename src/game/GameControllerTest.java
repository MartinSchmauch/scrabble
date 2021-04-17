package game;

import static org.junit.Assert.assertEquals;

import mechanic.Player;
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
  }

}
