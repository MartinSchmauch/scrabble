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


public class TurnTest {
  private Field l1, l2, l3, l4, l5;
  private Tile tile1, tile2, tile3, tile4, tile5;
  private Turn turn, turn2;

  private Tile f, a, r, m, s, h, o, n, o2, b, p, a2, t1, e;

  @Before
  public void before() {
    // Test 1
    tile1 = new Tile(new Letter('C', 1, 1), l1);
    tile2 = new Tile(new Letter('L', 1, 1), l2);
    tile3 = new Tile(new Letter('O', 1, 1), l3);
    tile4 = new Tile(new Letter('S', 1, 1), l4);
    tile5 = new Tile(new Letter('E', 1, 1), l5);

    GameBoard gb = new GameBoard(15);
    gb.getField(1, 1).setTile(tile1);
    gb.getField(1, 1).setLetterMultiplier(1);
    gb.getField(1, 1).setWordMultiplier(2);

    gb.getField(2, 1).setTile(tile2);
    gb.getField(2, 1).setLetterMultiplier(2);
    gb.getField(2, 1).setWordMultiplier(1);

    gb.getField(3, 1).setTile(tile3);
    gb.getField(3, 1).setLetterMultiplier(1);
    gb.getField(3, 1).setWordMultiplier(1);

    gb.getField(4, 1).setTile(tile4);
    gb.getField(4, 1).setLetterMultiplier(1);
    gb.getField(4, 1).setWordMultiplier(1);

    gb.getField(5, 1).setTile(tile5);
    gb.getField(5, 1).setLetterMultiplier(1);
    gb.getField(5, 1).setWordMultiplier(1);

    tile1.setField(gb.getField(1, 1));
    tile2.setField(gb.getField(2, 1));
    tile3.setField(gb.getField(3, 1));
    tile4.setField(gb.getField(4, 1));
    tile5.setField(gb.getField(5, 1));

    turn = new Turn("TestPlayer");

    turn.addTileToTurn(tile1);
    turn.addTileToTurn(tile2);
    turn.addTileToTurn(tile3);
    turn.addTileToTurn(tile4);
    turn.addTileToTurn(tile5);

    // Test 2
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

    GameBoard gb2 = new GameBoard(15);
    gb2.getField(1, 3).setTile(h);
    gb2.getField(2, 3).setTile(o);
    gb2.getField(3, 3).setTile(r);
    gb2.getField(4, 3).setTile(n);
    gb2.getField(3, 1).setTile(f);
    gb2.getField(3, 2).setTile(a);
    gb2.getField(3, 4).setTile(m);
    gb2.getField(4, 4).setTile(o2);
    gb2.getField(5, 4).setTile(b);
    gb2.getField(1, 5).setTile(p);
    gb2.getField(2, 5).setTile(a2);
    gb2.getField(3, 5).setTile(s);
    gb2.getField(4, 5).setTile(t1);
    gb2.getField(5, 5).setTile(e);

    turn2 = new Turn("TestPlayer2");
    turn2.addTileToTurn(o2);
    turn2.addTileToTurn(b);

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
  }

  /**
   * calculateWordsTest2() and calculateWordScoreTest2() test the algorithm for turn 4 of
   * https://boardgames.stackexchange.com/questions/44841/limits-on-using-new-words-in-scrabble
   * assuming every letter has the value one and special fields do not exist.
   */
  @Test
  public void calculateWordsTest2() {
    assertEquals(true, turn2.calculateWords());
  }

  @Test
  public void calculateWordScoreTest2() {
    turn2.calculateWords();
    turn2.calculateTurnScore();
    assertEquals(8, turn2.getTurnScore());
  }


}
