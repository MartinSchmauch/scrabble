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

    jH.loadGameSettings("resources/defaultGameSettings.json");
    assertEquals(GameSettings.getTimePerPlayer(), 1500);
    assertEquals(GameSettings.getLetters().get('A').getLetterValue(), 1);
    assertEquals(GameSettings.getSpecialFields().get(0).getWordMultiplier(), 2);
  }

}
