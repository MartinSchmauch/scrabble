package mechanic;

/**
 * This class test the Turn class which is used to find verify and words to calculate the turn
 * score.
 * 
 * @author lurny
 */

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import game.GameController;
import game.GameState;


public class TurnTest {
  private Field l1, l2, l3, l4, l5;
  private Tile tile1, tile2, tile3, tile4, tile5;
  private Turn turn, turn2, turn3;

  private Tile f, a, r, m, s, h, o, n, o2, b, p, a2, t1, e;

  @Before
  public void before() {
    // Test 1
    // JsonHandler jh = new JsonHandler();
    // jh.loadGameSettings("resources/defaultGameSettings.json");
    PlayerData pd1 = new PlayerData("Tom");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);

    tile1 = new Tile(new Letter('C', 1, 1));
    tile2 = new Tile(new Letter('L', 1, 1));
    tile3 = new Tile(new Letter('O', 1, 1));
    tile4 = new Tile(new Letter('S', 1, 1));
    tile5 = new Tile(new Letter('E', 1, 1));

    GameBoard gb = gs1.getGameBoard();

    tile1.setField(gb.getField(4, 8));
    tile2.setField(gb.getField(5, 8));
    tile3.setField(gb.getField(6, 8));
    tile4.setField(gb.getField(7, 8));
    tile5.setField(gb.getField(8, 8));

    turn = new Turn("TestPlayer", gc1);

    turn.addTileToTurn(tile1);
    turn.addTileToTurn(tile2);
    turn.addTileToTurn(tile3);
    turn.addTileToTurn(tile4);
    turn.addTileToTurn(tile5);

    // Test 2
    PlayerData pd2 = new PlayerData("Tom");
    GameState gs2 = new GameState(pd2, null);
    GameController gc2 = new GameController(gs2);

    f = new Tile(new Letter('F', 1, 1));
    a = new Tile(new Letter('A', 1, 1));
    r = new Tile(new Letter('R', 1, 1));
    m = new Tile(new Letter('M', 1, 1));
    s = new Tile(new Letter('S', 1, 1));
    h = new Tile(new Letter('H', 1, 1));
    o = new Tile(new Letter('O', 1, 1));
    n = new Tile(new Letter('N', 1, 1));
    o2 = new Tile(new Letter('O', 1, 1));
    b = new Tile(new Letter('B', 1, 1));
    p = new Tile(new Letter('P', 1, 1));
    a2 = new Tile(new Letter('A', 1, 1));
    t1 = new Tile(new Letter('T', 1, 1));
    e = new Tile(new Letter('E', 1, 1));

    f.setPlayed(true);
    a.setPlayed(true);
    r.setPlayed(true);
    m.setPlayed(true);
    s.setPlayed(true);
    h.setPlayed(true);
    o.setPlayed(true);
    n.setPlayed(true);
    p.setPlayed(true);
    a2.setPlayed(true);
    t1.setPlayed(true);
    e.setPlayed(true);

    GameBoard gb2 = gs2.getGameBoard();
    gb2.getField(4, 7).setTile(h);
    gb2.getField(5, 7).setTile(o);
    gb2.getField(6, 7).setTile(r);
    gb2.getField(7, 7).setTile(n);
    gb2.getField(6, 5).setTile(f);
    gb2.getField(6, 6).setTile(a);
    gb2.getField(6, 8).setTile(m);
    gb2.getField(7, 8).setTile(o2);
    gb2.getField(8, 8).setTile(b);
    gb2.getField(4, 9).setTile(p);
    gb2.getField(5, 9).setTile(a2);
    gb2.getField(6, 9).setTile(s);
    gb2.getField(7, 9).setTile(t1);
    gb2.getField(8, 9).setTile(e);

    turn2 = new Turn("TestPlayer2", gc2);
    turn2.addTileToTurn(o2);
    turn2.addTileToTurn(b);
    
    // Test 3 // test with Stars
    PlayerData pd3 = new PlayerData("Tom3");
    GameState gs3 = new GameState(pd3, null);
    GameController gc3 = new GameController(gs3);

    f = new Tile(new Letter('F', 1, 1));
    a = new Tile(new Letter('A', 1, 1));
    r = new Tile(new Letter('R', 1, 1));
    m = new Tile(new Letter('M', 1, 1));
    s = new Tile(new Letter('s', 1, 1));
    h = new Tile(new Letter('H', 1, 1));
    o = new Tile(new Letter('O', 1, 1));
    n = new Tile(new Letter('N', 1, 1));
    o2 = new Tile(new Letter('*', 1, 1));
    b = new Tile(new Letter('*', 1, 1));
    p = new Tile(new Letter('P', 1, 1));
    a2 = new Tile(new Letter('a', 1, 1));
    t1 = new Tile(new Letter('T', 1, 1));
    e = new Tile(new Letter('E', 1, 1));

    f.setPlayed(true);
    a.setPlayed(true);
    r.setPlayed(true);
    m.setPlayed(true);
    s.setPlayed(true);
    h.setPlayed(true);
    o.setPlayed(true);
    n.setPlayed(true);
    p.setPlayed(true);
    a2.setPlayed(true);
    t1.setPlayed(true);
    e.setPlayed(true);

    GameBoard gb3 = gs3.getGameBoard();
    gb3.getField(4, 7).setTile(h);
    gb3.getField(5, 7).setTile(o);
    gb3.getField(6, 7).setTile(r);
    gb3.getField(7, 7).setTile(n);
    gb3.getField(6, 5).setTile(f);
    gb3.getField(6, 6).setTile(a);
    gb3.getField(6, 8).setTile(m);
    gb3.getField(7, 8).setTile(o2);
    gb3.getField(8, 8).setTile(b);
    gb3.getField(4, 9).setTile(p);
    gb3.getField(5, 9).setTile(a2);
    gb3.getField(6, 9).setTile(s);
    gb3.getField(7, 9).setTile(t1);
    gb3.getField(8, 9).setTile(e);

    turn3 = new Turn("TestPlayer3", gc3);
    turn3.addTileToTurn(o2);
    turn3.addTileToTurn(b);

  }

  /** The first two methods are testing the word "close". */
  @Test
  public void calculateWordsTest() {
    assertEquals(true, turn.calculateWords());
  }

  @Test
  public void calculateWordScoreTest() {
    turn.calculateWords();
    turn.calculateTurnScore();
    assertEquals(12, turn.getTurnScore());
    System.out.println(turn);
  }

  /**
   * calculateWordsTest2() and calculateWordScoreTest2() test the algorithm for turn 4 of
   * https://boardgames.stackexchange.com/questions/44841/limits-on-using-new-words-in-scrabble
   * assuming every letter has the value one and special fields do not exist.
   */
  @Test
  public void calculateWordsTest2() {
    assertEquals(true, turn2.calculateWords());
    turn2.calculateTurnScore();
    assertEquals(15, turn2.getTurnScore());
    // mob 6 Punkte (Double Word), not 5 Punkte (2x Double Letter), be 4 Punkte (Double Word)
    System.out.println(turn2);
  }
  
  @Test
  public void calculateWordsTest3() {
    assertEquals(true, turn3.calculateWords());
    turn3.calculateTurnScore();
    assertEquals(8, turn3.getTurnScore());
    // mob 6 Punkte (Double Word), not 5 Punkte (2x Double Letter), be 4 Punkte (Double Word)
    System.out.println(turn3);
  }

}
