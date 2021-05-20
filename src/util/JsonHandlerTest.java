package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import game.GameSettings;
import java.io.File;
import mechanic.Player;

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
    this.player = jsonHandler.loadPlayerProfile(new File("resources/defaultPlayerProfile.json"));
    assertNotNull(player.getAvatar());
    assertEquals(player.getNickname(), "Guest");
    assertNotNull(player.getVolume());

    jsonHandler.savePlayerProfile(new File("resources/playerProfileTest.json"), player);
    this.player = jsonHandler.loadPlayerProfile(new File("resources/playerProfileTest.json"));

    assertNotNull(player.getAvatar());
    assertEquals(player.getNickname(), "Guest");
    assertNotNull(player.getVolume());

    jsonHandler.loadGameSettings(new File("resources/defaultGameSettings.json"));
    assertEquals(GameSettings.getTimePerPlayer(), 1500);
    assertEquals(GameSettings.getLetters().get('A').getLetterValue(), 1);
    assertEquals(GameSettings.getSpecialFields().get(0).getWordMultiplier(), 2);

    jsonHandler.saveGameSettings(new File("resources/customGameSettings.json"));
  }

}

