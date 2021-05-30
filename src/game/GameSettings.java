package game;

import java.util.HashMap;
import java.util.List;
import mechanic.Field;
import mechanic.Letter;

/**
 * This class is static and holds all current game settings. It can be updated with a Json file and
 * the loadGameSettings method of the JSONHandler class.
 *
 * @author ldreyer
 */

public class GameSettings {
  public static int port = 8421;
  private static int gameBoardSize = 15;
  private static int gameCountdown = 5;

  private static int timePerPlayer = 5;
  private static int maxOvertime;
  private static int maxScore;
  private static int tilesOnRack;
  private static String dictionary;
  private static int bingo;
  private static String ai;
  private static HashMap<Character, Letter> letters;
  private static List<Field> specialFields;
  private static Field starField;
  
  /**
   * get amount of tiles on rack.
   *
   * @return tilesOnRack as the current number.
   */

  public static int getTilesOnRack() {
    return tilesOnRack;
  }

  public static void setTilesOnRack(int tiles) {
    tilesOnRack = tiles;
  }
  /**
   * get current ai difficulty.
   *
   * @return ai as ai difficulty.
   */

  public static String getAiDifficulty() {
    return ai;
  }
  
  /**
   * set ai difficulty.
   */

  public static void setAiDifficulty(String newAi) {
    ai = newAi;
  }
  
  /**
   * get amount of time per player.
   *
   * @return timePerPlayer as amount.
   */

  public static int getTimePerPlayer() {
    return timePerPlayer;
  }

  /**
   * set player time amount.
   */

  public static void setTimePerPlayer(int timePerPlayer) {
    GameSettings.timePerPlayer = timePerPlayer;
  }
  
  /**
   * get amount of max overtime.
   *
   * @return maxOvertime as amount.
   */

  public static int getMaxOvertime() {
    return maxOvertime;
  }

  /**
   * set overtime amount.
   */

  public static void setMaxOvertime(int maxOvertime) {
    GameSettings.maxOvertime = maxOvertime;
  }

  /**
   * get amount of max score.
   *
   * @return maxScore as amount.
   */

  public static int getMaxScore() {
    return maxScore;
  }

  /**
   * set max score amount.
   */

  public static void setMaxScore(int maxScore) {
    GameSettings.maxScore = maxScore;
  }
  
  /**
   * get amount of game board size.
   *
   * @return gameBoardSize as amount.
   */

  public static int getGameBoardSize() {
    return gameBoardSize;
  }

  /**
   * set gameboard size amount amount.
   */

  public static void setGameBoardSize(int gameBoardSize) {
    GameSettings.gameBoardSize = gameBoardSize;
  }

  /**
   * get a path to dictionary.
   *
   * @return dictionary as path.
   */

  public static String getDictionary() {
    return dictionary;
  }
  
  /**
   * set dictionary path.
   */

  public static void setDictionary(String dictionary) {
    GameSettings.dictionary = dictionary;
  }
  /**
   * get amount of Countdown.
   *
   * @return gameCountdown as amount.
   */

  public static int getGameCountdown() {
    return gameCountdown;
  }

  /**
   * set countdown amount.
   */

  public static void setGameCountdown(int gameCountdown) {
    GameSettings.gameCountdown = gameCountdown;
  }

  /**
   * get Bingo amount.
   */

  public static int getBingo() {
    return bingo;
  }
  
  /**
   * set bingo amount.
   */

  public static void setBingo(int bingo) {
    GameSettings.bingo = bingo;
  }
  
  /**
   * set letter hashmap.
   */

  public static void setLetters(HashMap<Character, Letter> letters) {
    GameSettings.letters = letters;
  }
  /**
   * get Lettes as hashmap.
   */

  public static HashMap<Character, Letter> getLetters() {
    return letters;
  }
  
  /**
   * get Letter for specific char.
   */

  public static Letter getLetterForChar(char c) {
    return letters.get(c);
  }

  /**
   * get all special fields.
   */

  public static List<Field> getSpecialFields() {
    return specialFields;
  }
  
  /**
   * set special fields.
   */

  public static void setSpecialFields(List<Field> specialFields) {
    GameSettings.specialFields = specialFields;
  }
  
  /**
   * set star field.
   */

  public static void setStarField(Field starField) {
    GameSettings.starField = starField;
  }
  /**
   * get the start field.
   */

  public static Field getStarField() {
    return starField;
  }

}
