package game;

import java.util.HashMap;
import java.util.List;
import mechanic.Field;
import mechanic.Letter;

// ** @author ldreyer
public class GameSettings {
  private static int timePerPlayer;
  private static int maxOvertime;
  private static int maxScore;
  private static int gameBoardSize;
  private static String dictionary;
  private static String aiDifficulty;
  private static int gameCountdown;
  private static int bingo;

  private static HashMap<Character, Letter> letters;
  private static List<Field> specialFields;


  public static int getTimePerPlayer() {
    return timePerPlayer;
  }


  public static void setTimePerPlayer(int timePerPlayer) {
    GameSettings.timePerPlayer = timePerPlayer;
  }


  public static int getMaxOvertime() {
    return maxOvertime;
  }


  public static void setMaxOvertime(int maxOvertime) {
    GameSettings.maxOvertime = maxOvertime;
  }


  public static int getMaxScore() {
    return maxScore;
  }


  public static void setMaxScore(int maxScore) {
    GameSettings.maxScore = maxScore;
  }


  public static int getGameBoardSize() {
    return gameBoardSize;
  }


  public static void setGameBoardSize(int gameBoardSize) {
    GameSettings.gameBoardSize = gameBoardSize;
  }


  public static String getDictionary() {
    return dictionary;
  }


  public static void setDictionary(String dictionary) {
    GameSettings.dictionary = dictionary;
  }


  public static String getAiDifficulty() {
    return aiDifficulty;
  }


  public static void setAiDifficulty(String aiDifficulty) {
    GameSettings.aiDifficulty = aiDifficulty;
  }


  public static int getGameCountdown() {
    return gameCountdown;
  }


  public static void setGameCountdown(int gameCountdown) {
    GameSettings.gameCountdown = gameCountdown;
  }


  public static int getBingo() {
    return bingo;
  }


  public static void setBingo(int bingo) {
    GameSettings.bingo = bingo;
  }


  public static HashMap<Character, Letter> getLetters() {
    return letters;
  }


  public static void setLetters(HashMap<Character, Letter> letters) {
    GameSettings.letters = letters;
  }


  public static List<Field> getSpecialFields() {
    return specialFields;
  }


  public static void setSpecialFields(List<Field> specialFields) {
    GameSettings.specialFields = specialFields;
  }



}
