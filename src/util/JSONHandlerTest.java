package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import game.GameSettings;
import mechanic.Player;

public class JSONHandlerTest {

  private JSONHandler jH;
  private Player p;
  private GameSettings settings;

  @Before
  public void setUp() throws Exception {
    this.jH = new JSONHandler();
  }

  @Test
  public void test() {
    this.p = jH.loadPlayerProfile("resources/playerProfile.json");
    assertNotNull(p.getID());
    assertNotNull(p.getAvatar());
    assertEquals(p.getNickname(), "ScrabbleGamer");
    assertNotNull(p.getVolume());

    jH.savePlayerProfile("resources/playerProfileTest.json", p);
    this.p = jH.loadPlayerProfile("resources/playerProfileTest.json");

    assertNotNull(p.getID());
    assertNotNull(p.getAvatar());
    assertEquals(p.getNickname(), "ScrabbleGamer");
    assertNotNull(p.getVolume());

    this.settings = jH.loadGameSettings("resources/defaultGameSettings.json");
    assertEquals(settings.getTimePerPlayer(), 1500);
    assertEquals(settings.getLetters().get('A').getLetterValue(), 1);
    assertEquals(settings.getSpecialFields().get(0).getWordMultiplier(), 2);
  }

}
