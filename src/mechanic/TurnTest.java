package mechanic;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;


public class TurnTest {
  private Field l1, l2, l3, l4, l5;
  private Tile tile1, tile2, tile3, tile4, tile5;
  private Turn turn;

  private Field fF, fA, fR, fM, fS, fH, fO, fN, fO2, fB, fP, fA2, fT, fE;
  private Tile f, a, r, m, s, h, o, n, o2, b, p, a2, t1, e;

  @Before
  public void before() {

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


    List<Tile> laydDownTileList = new ArrayList<Tile>();
    laydDownTileList.add(tile1);
    laydDownTileList.add(tile2);
    laydDownTileList.add(tile3);
    laydDownTileList.add(tile4);
    laydDownTileList.add(tile5);


    turn = new Turn(laydDownTileList);



  }

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


}
