package mechanic;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
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
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", 2, gc1, AIplayer.AiLevel.Unbeatable);

    aiplayer.generateTwoTilesCombinations();
    int i = aiplayer.getTwoTilesCombinations().size();
    for (AIcombination c : aiplayer.getTwoTilesCombinations()) {
      System.out.println("#" + i + " " + c);
      i--;
    }
  }

  /**
   * Test method for
   * {@link mechanic.AIplayer_firstGeneration#getValidPositionsForWordLength(mechanic.Field[][], int)}.
   * Code is not fully dynamic with size
   */
     // @Test
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

  //@Test
  public void testNextTiles() {
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", 2, 700, gc1);
    aiplayer.generateTwoTilesCombinations();

    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    // Random random = new Random();
    // IntStream cOnRack = random.ints('A', 'Z');
    // cOnRack.findFirst();
    for (int i = 0; i < 7; i++) {
      aiplayer
          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
              aiplayer.getFreeRackField()));
    }

    ArrayList<Tile> current = null;
    ArrayList<Integer> indicesOnRack = new ArrayList<Integer>();
    int i = 1;
    while ((current = aiplayer.nextTiles(current, indicesOnRack, 7)) != null) {
      System.out.print("# " + i + " " + "nextTiles: ");
      for (Tile t : current) {
        System.out.print(" " + t.getLetter().getCharacter());
      }
      System.out.println();
      i++;
    }
  }

  //@Test
  public void testgenerateTwoTilesCombinations() {
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", 2, gc1, AIplayer.AiLevel.Unbeatable);

    aiplayer.generateTwoTilesCombinations();
    int i = aiplayer.getTwoTilesCombinations().size();
    for (AIcombination c : aiplayer.getTwoTilesCombinations()) {
      System.out.println("#" + i + " " + c);
      i--;
    }
  }

  @Test
  public void testgenerateLayedDownTiles() {
    System.out.println("\n-------------------------------------------------------------");
    System.out.println();
    System.out.println("######### AI TEST WITH RACK VERSION 1 #########");
    System.out.println();
    System.out.println("-------------------------------------------------------------\n");
    // JsonHandler jh = new JsonHandler();
    // jh.loadGameSettings("resources/defaultGameSettings.json");
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    

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

    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    aiplayer = new AIplayer("test", 7, 150, gc1);
    for (int ii = 0; ii < 7; ii++) {
      aiplayer
          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
              aiplayer.getFreeRackField()));
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");

    Turn idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 7, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 7, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 150, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(150);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 150, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(150);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }
  }

  @Test
  public void testgenerateLayedDownTiles2() {
    System.out.println("\n-------------------------------------------------------------");
    System.out.println();
    System.out.println("######### AI TEST WITH RACK VERSION 2 #########");
    System.out.println();
    System.out.println("-------------------------------------------------------------\n");
    // JsonHandler jh = new JsonHandler();
    // jh.loadGameSettings("resources/defaultGameSettings.json");
    PlayerData pd1 = new PlayerData("test2");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();


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


    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    aiplayer = new AIplayer("test2", 7, 150, gc1);
    for (int ii = 0; ii < 7; ii++) {
      aiplayer
          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
              aiplayer.getFreeRackField()));
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");

    Turn idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 7, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 7, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 150, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(150);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 6, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 150, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(150);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 200, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(200);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
//    aiplayer = new AIplayer("test", 4, 350, gc1);
//    for (int ii = 0; ii < 7; ii++) {
//      aiplayer
//          .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
//              aiplayer.getFreeRackField()));
//    }
    aiplayer.setNumberOfCombinationsToUse(350);

    idealTurn = aiplayer.generateIdealTurn(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

  }

}
