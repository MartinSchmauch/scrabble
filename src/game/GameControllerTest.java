// ** @author lurny
package game;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.JSONHandler;

public class GameControllerTest {
	GameController gc;
	@Before
	public void before() {
		 gc =  new GameController();
		 JSONHandler jH = new JSONHandler();
		 jH.loadGameSettings("resources/defaultGameSettings.json");
	}

	@Test
	public void setUpGameBoardTest() {
		gc.setUpGameboard();
		assertEquals(gc.getGameBoard().getField(7-1, 7-1).getLetterMultiplier(), 2);
	}

}
