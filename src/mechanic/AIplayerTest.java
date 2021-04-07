package mechanic;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pkoenig
 *
 */
public class AIplayerTest {
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
   * {@link mechanic.AIplayer#getValidPositionsForWordLength(mechanic.Field[][], int)}. Code is not
   * fully dynamic with size
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



    assertEquals(null, aiplayer.getValidWordPositionsForWordLength(gb, -1));
    assertEquals(null, aiplayer.getValidWordPositionsForWordLength(gb, 0));

    // System.out.println(res);

    assertEquals(results, aiplayer.getValidWordPositionsForWordLength(gb, 1));
    assertEquals(results, aiplayer.getValidWordPositionsForWordLength(gb, 5));

    Tile a = new Tile(new Letter('a', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(a);
    
    ArrayList<Field[]> results = new ArrayList<Field[]>();
    
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
    
    System.out.println("\n EXPECTED");
    for (int i = 0; i < results.size(); i++) {
      System.out.println("\n" + results.get(i));
      for (Field f : results.get(i)) {
        System.out.println(f.toString());
      }
      
    }

    assertArrayEquals(results.toArray(), aiplayer.getValidWordPositionsForWordLength(gb, 2).toArray());
    
    aiplayer.getValidWordPositionsForWordLength(gb, 5);
    
    Tile b = new Tile(new Letter('b', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(b);
    
    aiplayer.getValidWordPositionsForWordLength(gb, 5);
    
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

}
