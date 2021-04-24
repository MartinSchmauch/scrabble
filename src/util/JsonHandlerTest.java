package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import game.GameSettings;
import mechanic.Player;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the json handler implementation.
 *
 * @author ldreyer
 */

public class JsonHandlerTest {

  private JsonHandler jsonHandler;
  private Player player;

  @Before
  public void setUp() throws Exception {
    this.jsonHandler = new JsonHandler();
  }

  @Test
  public void test() {
    this.player = jsonHandler.loadPlayerProfile("resources/playerProfile.json");
    assertNotNull(player.getAvatar());
    assertEquals(player.getNickname(), "ScrabbleGamer");
    assertNotNull(player.getVolume());

    jsonHandler.savePlayerProfile("resources/playerProfileTest.json", player);
    this.player = jsonHandler.loadPlayerProfile("resources/playerProfileTest.json");

    assertNotNull(player.getAvatar());
    assertEquals(player.getNickname(), "ScrabbleGamer");
    assertNotNull(player.getVolume());

    jsonHandler.loadGameSettings("resources/defaultGameSettings.json");
    assertEquals(GameSettings.getTimePerPlayer(), 1500);
    assertEquals(GameSettings.getLetters().get('A').getLetterValue(), 1);
    assertEquals(GameSettings.getSpecialFields().get(0).getWordMultiplier(), 2);
  
    jsonHandler.saveGameSettings("resources/customGameSettings.json");
  }

}
