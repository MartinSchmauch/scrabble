package mechanic;

import static org.junit.Assert.assertEquals;

import game.GameController;
import game.GameState;
import org.junit.Before;
import org.junit.Test;


/**
 * This class test the Turn class which is used to find verify and words to calculate the turn
 * score.
 *
 * @author lurny
 */

public class TurnTest {
  private Tile tile1;
  private Tile tile2;
  private Tile tile3;
  private Tile tile4;
  private Tile tile5;
  private Turn turn;
  private Turn turn2;
  private Turn turn3;

  private Tile f1;
  private Tile a1;
  private Tile r1;
  private Tile m1;
  private Tile s1;
  private Tile h1;
  private Tile o1;
  private Tile n1;
  private Tile o2;
  private Tile b1;
  private Tile p1;
  private Tile a2;
  private Tile t1;
  private Tile e1;

  /**
   * This method sets up the Gameboard to later run the Turntest.
   */
  @Before
  public void before() {
    // Test 1
    final PlayerData pd1 = new PlayerData("Tom");
    final GameState gs1 = new GameState(pd1, null);
    final GameController gc1 = new GameController(gs1);

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
    final PlayerData pd2 = new PlayerData("Tom");
    final GameState gs2 = new GameState(pd2, null);
    final GameController gc2 = new GameController(gs2);

    f1 = new Tile(new Letter('F', 1, 1));
    a1 = new Tile(new Letter('A', 1, 1));
    r1 = new Tile(new Letter('R', 1, 1));
    m1 = new Tile(new Letter('M', 1, 1));
    s1 = new Tile(new Letter('S', 1, 1));
    h1 = new Tile(new Letter('H', 1, 1));
    o1 = new Tile(new Letter('O', 1, 1));
    n1 = new Tile(new Letter('N', 1, 1));
    o2 = new Tile(new Letter('O', 1, 1));
    b1 = new Tile(new Letter('B', 1, 1));
    p1 = new Tile(new Letter('P', 1, 1));
    a2 = new Tile(new Letter('A', 1, 1));
    t1 = new Tile(new Letter('T', 1, 1));
    e1 = new Tile(new Letter('E', 1, 1));

    f1.setPlayed(true);
    a1.setPlayed(true);
    r1.setPlayed(true);
    m1.setPlayed(true);
    s1.setPlayed(true);
    h1.setPlayed(true);
    o1.setPlayed(true);
    n1.setPlayed(true);
    p1.setPlayed(true);
    a2.setPlayed(true);
    t1.setPlayed(true);
    e1.setPlayed(true);

    GameBoard gb2 = gs2.getGameBoard();
    gb2.getField(4, 7).setTile(h1);
    gb2.getField(5, 7).setTile(o1);
    gb2.getField(6, 7).setTile(r1);
    gb2.getField(7, 7).setTile(n1);
    gb2.getField(6, 5).setTile(f1);
    gb2.getField(6, 6).setTile(a1);
    gb2.getField(6, 8).setTile(m1);
    gb2.getField(7, 8).setTile(o2);
    gb2.getField(8, 8).setTile(b1);
    gb2.getField(4, 9).setTile(p1);
    gb2.getField(5, 9).setTile(a2);
    gb2.getField(6, 9).setTile(s1);
    gb2.getField(7, 9).setTile(t1);
    gb2.getField(8, 9).setTile(e1);

    turn2 = new Turn("TestPlayer2", gc2);
    turn2.addTileToTurn(o2);
    turn2.addTileToTurn(b1);

    // Test 3 // test with Stars
    final PlayerData pd3 = new PlayerData("Tom3");
    final GameState gs3 = new GameState(pd3, null);
    final GameController gc3 = new GameController(gs3);

    f1 = new Tile(new Letter('F', 1, 1));
    a1 = new Tile(new Letter('A', 1, 1));
    r1 = new Tile(new Letter('R', 1, 1));
    m1 = new Tile(new Letter('M', 1, 1));
    s1 = new Tile(new Letter('s', 1, 1));
    h1 = new Tile(new Letter('H', 1, 1));
    o1 = new Tile(new Letter('O', 1, 1));
    n1 = new Tile(new Letter('N', 1, 1));
    o2 = new Tile(new Letter('*', 1, 1));
    b1 = new Tile(new Letter('*', 1, 1));
    p1 = new Tile(new Letter('P', 1, 1));
    a2 = new Tile(new Letter('a', 1, 1));
    t1 = new Tile(new Letter('T', 1, 1));
    e1 = new Tile(new Letter('E', 1, 1));

    f1.setPlayed(true);
    a1.setPlayed(true);
    r1.setPlayed(true);
    m1.setPlayed(true);
    s1.setPlayed(true);
    h1.setPlayed(true);
    o1.setPlayed(true);
    n1.setPlayed(true);
    p1.setPlayed(true);
    a2.setPlayed(true);
    t1.setPlayed(true);
    e1.setPlayed(true);

    GameBoard gb3 = gs3.getGameBoard();
    gb3.getField(4, 7).setTile(h1);
    gb3.getField(5, 7).setTile(o1);
    gb3.getField(6, 7).setTile(r1);
    gb3.getField(7, 7).setTile(n1);
    gb3.getField(6, 5).setTile(f1);
    gb3.getField(6, 6).setTile(a1);
    gb3.getField(6, 8).setTile(m1);
    gb3.getField(7, 8).setTile(o2);
    gb3.getField(8, 8).setTile(b1);
    gb3.getField(4, 9).setTile(p1);
    gb3.getField(5, 9).setTile(a2);
    gb3.getField(6, 9).setTile(s1);
    gb3.getField(7, 9).setTile(t1);
    gb3.getField(8, 9).setTile(e1);

    turn3 = new Turn("TestPlayer3", gc3);
    turn3.addTileToTurn(o2);
    turn3.addTileToTurn(b1);

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
