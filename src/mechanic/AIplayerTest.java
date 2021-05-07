package mechanic;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import game.GameController;
import game.GameState;
import mechanic.AIplayer.AIcombination;
import util.JsonHandler;

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
   * {@link mechanic.AIplayer_firstGeneration#getValidPositionsForWordLength(mechanic.Field[][], int)}.
   * Code is not fully dynamic with size
   */
  @Test
  public void testGetValidPositionsForWordLength() {
    // aiplayer = new AIplayer("test", 5);
    // gb = new GameBoard(15);
    //
    //
    //
    // assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, -1));
    // assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, 0));
    // assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, 1));
    //
    // Tile a = new Tile(new Letter('A', 1, 1), gb.getField(8, 8));
    // gb.getField(8, 8).setTile(a);
    // System.out.println("Current layed Down tile: " + a);
    // System.out.println("######################## NUMOFTILES == 2 ########################");
    // for (Field[] f_list : aiplayer.getValidTilePositionsForNumOfTiles(gb, 2)) {
    // System.out.println("# GENERATED POSITION #");
    // for (Field f : f_list) {
    // System.out.println(f);
    // }
    // System.out.println();
    // }
    // System.out.println("######################## NUMOFTILES == 3 ########################");
    // for (Field[] f_list : aiplayer.getValidTilePositionsForNumOfTiles(gb, 3)) {
    // System.out.println("# GENERATED POSITION #");
    // for (Field f : f_list) {
    // System.out.println(f);
    // }
    // System.out.println();
    // }
  }

  @Test
  public void testNextTiles() {
    // aiplayer = new AIplayer("test", 5);
    // gb = new GameBoard(15);
    //
    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    //
    // ArrayList<Tile> current = null;
    // ArrayList<Integer> indicesOnRack = new ArrayList<Integer>();
    // while ((current = aiplayer.nextTiles(current, indicesOnRack, 4)) != null) {
    // System.out.println(current);
    // }
  }
  
  @Test
  public void testgenerateTwoTilesCombinations() {
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", 2, gc1, AIplayer.AiLevel.Unbeatable);
    
    aiplayer.generateTwoTilesCombinations();
    for (AIcombination c : aiplayer.getTwoTilesCombinations()) {
      System.out.println(c);
    }
  }

  //@Test
  public void testgenerateLayedDownTiles() {
    // JsonHandler jh = new JsonHandler();
    // jh.loadGameSettings("resources/defaultGameSettings.json");
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", 2, gc1, AIplayer.AiLevel.Unbeatable);

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

    for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
      aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    }

    Turn idealTurn = aiplayer.generateIdealTurn(gb);
    for (Tile result : idealTurn.getLaydDownTiles()) {
      System.out.println(result.toString());
    }


  }
  
  //@Test
  public void testgenerateLayedDownTiles2() {
    // JsonHandler jh = new JsonHandler();
    // jh.loadGameSettings("resources/defaultGameSettings.json");
    PlayerData pd1 = new PlayerData("test2");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test2", 2, gc1, AIplayer.AiLevel.Unbeatable);

    // BIRTH
    Tile b = new Tile(new Letter('B', 1, 1), gb.getField(6, 8));
    gb.getField(6, 8).setTile(b);
    Tile i = new Tile(new Letter('I', 1, 1), gb.getField(7, 8));
    gb.getField(7, 8).setTile(i);
    Tile r = new Tile(new Letter('R', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(r);
    Tile t = new Tile(new Letter('T', 1, 1), gb.getField(9, 8));
    gb.getField(9, 8).setTile(t);
    Tile h = new Tile(new Letter('H', 1, 1), gb.getField(10, 8));
    gb.getField(10, 8).setTile(h);


    // THE
    h = new Tile(new Letter('H', 1, 1), gb.getField(9, 9));
    gb.getField(9, 9).setTile(h);
    Tile e = new Tile(new Letter('E', 1, 1), gb.getField(9, 10));
    gb.getField(9, 10).setTile(e);

    // BACK
    Tile a = new Tile(new Letter('A', 1, 1), gb.getField(6, 9));
    gb.getField(6, 9).setTile(a);
    Tile c = new Tile(new Letter('C', 1, 1), gb.getField(6, 10));
    gb.getField(6, 10).setTile(c);
    Tile k = new Tile(new Letter('K', 1, 1), gb.getField(6, 11));
    gb.getField(6, 11).setTile(k);


    for (char cOnRack = 'D'; cOnRack <= 'J'; cOnRack++) {
      aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    }

    Turn idealTurn = aiplayer.generateIdealTurn(gb);
    for (Tile result : idealTurn.getLaydDownTiles()) {
      System.out.println(result.toString());
    }


  }

}
