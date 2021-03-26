package mechanic;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;


public class TurnTest {
  private Field l1, l2, l3, l4, l5;
  private Tile tile1, tile2, tile3, tile4, tile5;
  private Turn t;

  @Before
  public void before() {
    l1 = new Field(1, 2, 1, 1);
    l2 = new Field(2, 1, 1, 1);
    l3 = new Field(1, 1, 1, 1);
    l4 = new Field(1, 1, 1, 1);
    l5 = new Field(1, 1, 1, 1);

    tile1 = new Tile(new Letter('K', 1, 1), l1);
    tile2 = new Tile(new Letter('A', 1, 1), l2);
    tile3 = new Tile(new Letter('T', 1, 1), l3);
    tile4 = new Tile(new Letter('Z', 1, 1), l4);
    tile5 = new Tile(new Letter('E', 1, 1), l5);


    Tile[] laydDownTileList = {tile1, tile2, tile3, tile4, tile5};

    t = new Turn(laydDownTileList);
    Word[] word = {new Word(laydDownTileList)};
    t.setWords(word);
  }



  @Test
  public void calculateWordScoreTest() {
    t.calculateTurnScore();
    assertEquals(12, t.getTurnScore());
  }

  @Test
  public void print() {
    t.calculateWords();
  }

}
