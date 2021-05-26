package mechanic;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import game.GameController;
import game.GameSettings;
import game.GameState;
import mechanic.AIplayer.AIcombination;
import util.JsonHandler;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Stopwatch;
import com.opencsv.CSVWriter;

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
  // @Before
  public void setUp() throws Exception {
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", gc1, AIplayer.AiLevel.UNBEATABLE);

    aiplayer.generateTileCombinations();
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
    aiplayer = new AIplayer("test", 5, 20, null);
    gb = new GameBoard(15);



    assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, -1));
    assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, 0));
    assertEquals(null, aiplayer.getValidTilePositionsForNumOfTiles(gb, 1));

    Tile a = new Tile(new Letter('A', 1, 1), gb.getField(8, 8));
    gb.getField(8, 8).setTile(a);
    System.out.println("Current layed Down tile: " + a);
    System.out.println("######################## NUMOFTILES == 2 ########################");
    for (Field[] f_list : aiplayer.getValidTilePositionsForNumOfTiles(gb, 2)) {
      System.out.println("# GENERATED POSITION #");
      for (Field f : f_list) {
        System.out.println(f);
      }
      System.out.println();
    }
    System.out.println("######################## NUMOFTILES == 3 ########################");
    for (Field[] f_list : aiplayer.getValidTilePositionsForNumOfTiles(gb, 3)) {
      System.out.println("# GENERATED POSITION #");
      for (Field f : f_list) {
        System.out.println(f);
      }
      System.out.println();
    }
  }

  @Test
  public void testRunAI_dynamic() {
    int iterations = 10;
    int maxNumberOfCombinationsToUse;


    // DOCUMENTATION
    Stopwatch timeOverall = Stopwatch.createUnstarted();
    File file = null;
    file = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
        + "resources" + System.getProperty("file.separator") + "csv"
        + System.getProperty("file.separator")
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm_ss")) + ".csv");
    try {
      file.createNewFile();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    String[] currentTurnWithParam = new String[] {"BoardSetup", "maxNumOfTiles",
        "numOfCombinationsToUse", "goodTurnScore", "RacktilesAtBeginOfTurn", "Duration(millisec)",
        "StringRepresentation", "layedDownFields", "turnScore", "containesStarTiles", "StarTiles"};
    String[] temp;
    List<String[]> toCsv = new ArrayList<String[]>();
    toCsv.add(currentTurnWithParam);

    char newRackChar;
    Turn idealTurn;

    System.out.println("\n-------------------------------------------------------------");
    System.out.println();
    System.out.println("######### AI TEST WITH RACK VERSION 1 #########");
    System.out.println();
    System.out.println("-------------------------------------------------------------\n");

    for (int i = 0; i < iterations; i++) {

      PlayerData pd1 = new PlayerData("test-" + i);
      GameState gs1 = new GameState(pd1, null);
      GameController gc1 = new GameController(gs1);
      gs1.setUpGameboard();
      gb = gs1.getGameBoard();
      setUpGameBoard1(gb);

      aiplayer = new AIplayer("test-1-" + i, 0, 0, gc1);
      aiplayer.setTestmode(true);

      for (int ii = 0; ii < 7; ii++) {
        newRackChar = (char) ((Math.random() * (92 - 'A')) + 'A');
        if (newRackChar == (char) 91) {
          newRackChar = '*';
        }
        aiplayer
            .addTileToRack(new Tile(GameSettings.getLetterForChar(newRackChar), new Field(0, 0)));
      }

      maxNumberOfCombinationsToUse = 0;
      int helper = 0;

      for (int maxNumOfTiles = 2; maxNumOfTiles <= 7; maxNumOfTiles++) {

        helper = 1;
        for (int h = 0; h < maxNumOfTiles; h++) {
          helper *= 7 - h;
        }

        maxNumberOfCombinationsToUse += helper;

        for (int numberOfCombinationsToUse =
            15; numberOfCombinationsToUse <= maxNumberOfCombinationsToUse; numberOfCombinationsToUse *=
                2) { // TODO
          for (int goodScore = 5; goodScore < 200; goodScore *= 2) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println(
                "PARAMETER: maxNumOfTiles = " + maxNumOfTiles + ", numberOfCombinationsToUse = "
                    + numberOfCombinationsToUse + ", goodScore = " + goodScore);
            System.out.println("-------------------------------------------------------------");

            aiplayer.setMaxNumOfTiles(maxNumOfTiles);
            aiplayer.generateTileCombinations();
            aiplayer.setNumberOfCombinationsToUse(numberOfCombinationsToUse);
            aiplayer.setGoodScore(goodScore);

            /*
             * TIMED AREA BEGIN
             */
            timeOverall.start();
            idealTurn = aiplayer.runAi(gb);
            timeOverall.stop();

            /*
             * TIMED AREA END
             */
            if (idealTurn == null) {
              timeOverall.reset();
              break;
            }

            currentTurnWithParam[0] = 1 + ""; // current Board Setup
            currentTurnWithParam[1] = maxNumOfTiles + "";
            currentTurnWithParam[2] = numberOfCombinationsToUse + "";
            currentTurnWithParam[3] = goodScore + "";
            currentTurnWithParam[4] = aiplayer.getRackTile(0).getLetter().getCharacter() + "";
            for (int k = 1; k < GameSettings.getTilesOnRack(); k++) {
              currentTurnWithParam[4] = currentTurnWithParam[4] + ", "
                  + aiplayer.getRackTile(k).getLetter().getCharacter();
            }
            currentTurnWithParam[5] = timeOverall.elapsed(TimeUnit.MILLISECONDS) + "";
            timeOverall.reset();

            temp = idealTurn.toStringArray();

            for (int ii = 0; ii < temp.length; ii++) {
              currentTurnWithParam[ii + 5] = temp[ii];
            }
            for (String s : currentTurnWithParam) {
              System.out.println(s);
            }
            toCsv.add(currentTurnWithParam);
            try {
              csvWriterAll(toCsv, file);
              toCsv.clear();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  // @Test
  public void testgenerateTileCombinations() {
    PlayerData pd1 = new PlayerData("test1");
    GameState gs1 = new GameState(pd1, null);
    GameController gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    aiplayer = new AIplayer("test", gc1, AIplayer.AiLevel.UNBEATABLE);

    aiplayer.generateTileCombinations();
    int i = aiplayer.getTwoTilesCombinations().size();
    for (AIcombination c : aiplayer.getTwoTilesCombinations()) {
      System.out.println("#" + i + " " + c);
      i--;
    }
  }

  // @Test
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
    setUpGameBoard1(gb);



    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    aiplayer = new AIplayer("test", 7, 15, gc1);
    char newRackChar;
    for (int ii = 0; ii < 7; ii++) {
      newRackChar = (char) ((Math.random() * (92 - 'A')) + 'A');
      if (newRackChar == (char) 92) {
        newRackChar = '*';
      }
      aiplayer.addTileToRack(new Tile(GameSettings.getLetterForChar(newRackChar), new Field(0, 0)));
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");

    aiplayer.generateTileCombinations();
    Turn idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 15, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(15);
    aiplayer.setMaxNumOfTiles(6);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 15, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(15);
    aiplayer.setMaxNumOfTiles(4);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }
  }

  public static void csvWriterAll(List<String[]> stringArray, File file) throws Exception {
    CSVWriter writer = new CSVWriter(new FileWriter(file, true));
    writer.writeAll(stringArray);
    writer.close();
  }

  // @Test
  public void testtRunAI_static() {
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
    setUpGameBoard1(gb);



    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    aiplayer = new AIplayer("test", 7, 150, gc1);
    for (int ii = 0; ii < 7; ii++) {
      aiplayer.addTileToRack(new Tile(
          new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1), new Field(0, 0)));
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");

    Turn idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 200, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(200);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(350);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 500");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(500);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 1000");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(1000);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 150, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(150);
    aiplayer.setMaxNumOfTiles(6);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 200, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(200);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(350);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 500");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(500);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 1000");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(1000);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 150");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 150, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(150);
    aiplayer.setMaxNumOfTiles(4);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 200");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 200, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(200);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 350");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(350);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 500");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(500);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 1000");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 350, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(1000);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard1(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }
  }

  public static void setUpGameBoard1(GameBoard gb) {
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
  }

  public static void setUpGameBoard2(GameBoard gb) {
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
  }

  // @Test
  public void testRunAI_static2() {
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
    setUpGameBoard2(gb);


    // for (char cOnRack = 'A'; cOnRack <= 'G'; cOnRack++) {
    // aiplayer.addTileToRack(new Tile(new Letter(cOnRack, 1, 1), aiplayer.getFreeRackField()));
    // }
    aiplayer = new AIplayer("test2", 7, 15, gc1);
    for (int ii = 0; ii < 7; ii++) {
      aiplayer.addTileToRack(new Tile(
          new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1), new Field(0, 0)));
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");

    Turn idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 7, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 15, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(15);
    aiplayer.setMaxNumOfTiles(6);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 6, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 6, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 15");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 15, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(15);
    aiplayer.setMaxNumOfTiles(4);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 20");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 20, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(20);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 35");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 4, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(35);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 42");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(42);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

    System.out.println("\n-------------------------------------------------------------");
    System.out.println("PARAMETER: maxNumOfTilew = 4, numberOfCombinationsToUse = 49");
    System.out.println("-------------------------------------------------------------");
    // aiplayer = new AIplayer("test", 7, 35, gc1);
    // for (int ii = 0; ii < 7; ii++) {
    // aiplayer
    // .addTileToRack(new Tile(new Letter((char) ((Math.random() * ('[' - 'A')) + 'A'), 1, 1),
    // aiplayer.getFreeRackField()));
    // }
    aiplayer.generateTileCombinations();
    aiplayer.setNumberOfCombinationsToUse(49);
    gc1 = new GameController(gs1);
    gs1.setUpGameboard();
    gb = gs1.getGameBoard();
    setUpGameBoard2(gb);

    idealTurn = aiplayer.runAi(gb);
    if (idealTurn != null) {
      for (Tile result : idealTurn.getLaydDownTiles()) {
        System.out.println(result.toString());
      }
    }

  }
}
