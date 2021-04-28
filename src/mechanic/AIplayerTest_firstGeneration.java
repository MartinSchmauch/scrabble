package mechanic;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
// *******************************
// DEPRECATED !!!!
// *******************************

/**
 * @author pkoenig
 *
 */
public class AIplayerTest_firstGeneration {
  AIplayer aiplayer;
  ArrayList<Field[]> results = new ArrayList<Field[]>();
  GameBoard gb;


  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

  }

  /**
   * Test method for
   * {@link mechanic.AIplayer_firstGeneration#getValidPositionsForWordLength(mechanic.Field[][], int)}.
   * Code is not fully dynamic with size
   */
  @Test
  public void testGetValidPositionsForWordLength() {
    // fields = new Field[size][size];
    // for (int i = 0; i < size; i++) {
    // for (int j = 0; j < size; j++) {
    // this.fields[i][j] = new Field(i, j);
    // }
    // }
    //
    aiplayer = new AIplayer("test", 10);
    // int[][] res = new int[15][15];
    //
    // for (int j = 0; j < res.length; j++) {
    // for (int i = 0; i < res.length; i++) {
    // res[i][j] = 0;
    // }
    // }
    gb = new GameBoard(15);



    assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, -1));
    assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, 0));

    // System.out.println(res);

    assertEquals(results, aiplayer.getValidTilePositionsForNumOfTiles(gb, 1));
    assertEquals(results, aiplayer.getValidTilePositionsForNumOfTiles(gb, 5));

    Tile a = new Tile(new Letter('a', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(a);

    HashSet<Field[]> results = new HashSet<Field[]>();

    // generate words for "Top Field"
    Field[] fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(7, 7);
    fields[1] = gb.getField(8, 7);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 7);
    fields[1] = gb.getField(9, 7);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 6);
    fields[1] = gb.getField(8, 7);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 7);
    fields[1] = gb.getField(8, 8);
    results.add(fields);


    // generate words for "Left Field"
    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(6, 8);
    fields[1] = gb.getField(7, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(7, 8);
    fields[1] = gb.getField(8, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(7, 7);
    fields[1] = gb.getField(7, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(7, 8);
    fields[1] = gb.getField(7, 9);
    results.add(fields);


    // generate words for "Right Field"
    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 8);
    fields[1] = gb.getField(9, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(9, 8);
    fields[1] = gb.getField(10, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(9, 7);
    fields[1] = gb.getField(9, 8);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(9, 8);
    fields[1] = gb.getField(9, 9);
    results.add(fields);


    // generate words for "bottom Field"
    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(7, 9);
    fields[1] = gb.getField(8, 9);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 9);
    fields[1] = gb.getField(9, 9);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 8);
    fields[1] = gb.getField(8, 9);
    results.add(fields);

    fields = new Field[2]; // wordlength 2
    fields[0] = gb.getField(8, 9);
    fields[1] = gb.getField(8, 10);
    results.add(fields);

    // System.out.println("\n EXPECTED");
    // for (int i = 0; i < results.size(); i++) {
    // System.out.println("\n" + results.get(i));
    // for (Field f : results.get(i)) {
    // System.out.println(f.toString());
    // }
    //
    // }

    assertArrayEquals(results.toArray(),
        aiplayer.getValidTilePositionsForNumOfTiles(gb, 2).toArray());

    // aiplayer.getValidWordPositionsForWordLength(gb, 5);
    //
    // Tile b = new Tile(new Letter('b', 1, 1), gb.getField(8, 8));
    // gb.getField(8, 8).setTile(b);
    //
    // aiplayer.getValidWordPositionsForWordLength(gb, 5);

    // res[6][7] = 1;
    // res[7][5] = 1;
    // res[9][7] = 1;
    // res[7][10] = 1;
    //
    // res[6][6] = 1;
    // res[8][8] = 1;
    // res[6][8] = 1;
    // res[8][6] = 1;


    // assertArrayEquals(res, aiplayer.getValidPositionsForWordLength(fields, 2));

  }

  @Test
  public void testgenerateLayedDownTiles() {
    gb = new GameBoard(15);
    aiplayer = new AIplayer("test", 10);

    // BIRTHDAY
    Tile b = new Tile(new Letter('B', 1, 1), gb.getField(4, 8));
    gb.getField(4, 8).setTile(b);
    Tile i = new Tile(new Letter('I', 1, 1), gb.getField(5, 8));
    gb.getField(5, 8).setTile(i);
    Tile r = new Tile(new Letter('R', 1, 1), gb.getField(6, 8));
    gb.getField(6, 8).setTile(r);
    Tile t = new Tile(new Letter('T', 1, 1), gb.getField(7, 8));
    gb.getField(7, 8).setTile(t);
    Tile h = new Tile(new Letter('H', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(h);
    Tile d = new Tile(new Letter('D', 1, 1), gb.getField(9, 8));
    gb.getField(9, 8).setTile(d);
    Tile a = new Tile(new Letter('A', 1, 1), gb.getField(10, 8));
    gb.getField(10, 8).setTile(a);
    Tile y = new Tile(new Letter('Y', 1, 1), gb.getField(11, 8));
    gb.getField(11, 8).setTile(y);


    // TIGER
    t = new Tile(new Letter('T', 1, 1), gb.getField(5, 7));
    gb.getField(5, 7).setTile(t);
    i = new Tile(new Letter('I', 1, 1), gb.getField(5, 8));
    gb.getField(5, 8).setTile(i);
    Tile g = new Tile(new Letter('G', 1, 1), gb.getField(5, 9));
    gb.getField(5, 9).setTile(g);
    Tile e = new Tile(new Letter('E', 1, 1), gb.getField(5, 10));
    gb.getField(5, 10).setTile(e);
    r = new Tile(new Letter('R', 1, 1), gb.getField(5, 11));
    gb.getField(5, 11).setTile(r);

    // TEACHER
    t = new Tile(new Letter('T', 1, 1), gb.getField(7, 8));
    gb.getField(7, 8).setTile(t);
    e = new Tile(new Letter('E', 1, 1), gb.getField(7, 9));
    gb.getField(7, 9).setTile(e);
    a = new Tile(new Letter('A', 1, 1), gb.getField(7, 10));
    gb.getField(7, 10).setTile(a);
    Tile c = new Tile(new Letter('C', 1, 1), gb.getField(7, 11));
    gb.getField(7, 11).setTile(c);
    h = new Tile(new Letter('H', 1, 1), gb.getField(7, 12));
    gb.getField(7, 12).setTile(h);
    e = new Tile(new Letter('E', 1, 1), gb.getField(7, 13));
    gb.getField(7, 13).setTile(e);
    r = new Tile(new Letter('R', 1, 1), gb.getField(7, 14));
    gb.getField(7, 14).setTile(r);

    // RACK
    r = new Tile(new Letter('R', 1, 1), gb.getField(5, 11));
    gb.getField(5, 11).setTile(r);
    a = new Tile(new Letter('A', 1, 1), gb.getField(6, 11));
    gb.getField(6, 11).setTile(a);
    c = new Tile(new Letter('C', 1, 1), gb.getField(7, 11));
    gb.getField(7, 11).setTile(c);
    Tile k = new Tile(new Letter('K', 1, 1), gb.getField(8, 11));
    gb.getField(8, 11).setTile(k);

    for (char cOnRack = 'A'; cOnRack != 'G'; cOnRack++) {
      aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    }

    Turn idealTurn = aiplayer.generateIdealTurn(gb);
    for (Tile result : idealTurn.getLaydDownTiles()) {
      System.out.println(result.toString());
    }

  }

}
