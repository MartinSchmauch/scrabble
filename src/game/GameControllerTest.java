package game;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import mechanic.Player;


/**
 * @author lurny
 * @author ldreyer
 */

public class GameControllerTest {
  GameController gc;
  GameState gS;
  Player host;

  @Before
  public void before() {
    host = new Player("Host");
    gS = new GameState(host.getPlayerInfo(), null);
    gc = new GameController(gS);
  }

  @Test
  public void setUpGameBoardTest() {
    assertEquals(gS.getGameBoard().getField(7, 7).getLetterMultiplier(), 2);
  }

}
