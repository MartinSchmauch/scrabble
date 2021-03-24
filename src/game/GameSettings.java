package game;

import java.util.HashMap;
import java.util.List;
import mechanic.Field;
import mechanic.Letter;

// ** @author ldreyer
public class GameSettings {
  private int timePerPlayer;
  private int maxOvertime;
  private int maxScore;
  private int gameBoardSize;
  private String dictionary;
  private String aiDifficulty;
  private int gameCountdown;
  private int bingo;

  private HashMap<Character, Letter> letters;
  private List<Field> specialFields;


  public GameSettings(int timePerPlayer, int maxOvertime, int maxScore, int gameBoardSize,
      String dictionary, String aiDifficulty, int gameCountdown, int bingo,
      HashMap<Character, Letter> letters, List<Field> specialFields) {
    this.timePerPlayer = timePerPlayer;
    this.maxOvertime = maxOvertime;
    this.maxScore = maxScore;
    this.gameBoardSize = gameBoardSize;
    this.dictionary = dictionary;
    this.aiDifficulty = aiDifficulty;
    this.gameCountdown = gameCountdown;
    this.bingo = bingo;
    this.letters = letters;
    this.specialFields = specialFields;
  }


  public int getTimePerPlayer() {
    return timePerPlayer;
  }


  public void setTimePerPlayer(int timePerPlayer) {
    this.timePerPlayer = timePerPlayer;
  }


  public int getMaxOvertime() {
    return maxOvertime;
  }


  public void setMaxOvertime(int maxOvertime) {
    this.maxOvertime = maxOvertime;
  }


  public int getMaxScore() {
    return maxScore;
  }


  public void setMaxScore(int maxScore) {
    this.maxScore = maxScore;
  }


  public int getGameBoardSize() {
    return gameBoardSize;
  }


  public void setGameBoardSize(int gameBoardSize) {
    this.gameBoardSize = gameBoardSize;
  }


  public String getDictionary() {
    return dictionary;
  }


  public void setDictionary(String dictionary) {
    this.dictionary = dictionary;
  }


  public String getAiDifficulty() {
    return aiDifficulty;
  }


  public void setAiDifficulty(String aiDifficulty) {
    this.aiDifficulty = aiDifficulty;
  }


  public int getGameCountdown() {
    return gameCountdown;
  }


  public void setGameCountdown(int gameCountdown) {
    this.gameCountdown = gameCountdown;
  }


  public int getBingo() {
    return bingo;
  }


  public void setBingo(int bingo) {
    this.bingo = bingo;
  }


  public HashMap<Character, Letter> getLetters() {
    return letters;
  }


  public void setLetters(HashMap<Character, Letter> letters) {
    this.letters = letters;
  }


  public List<Field> getSpecialFields() {
    return specialFields;
  }


  public void setSpecialFields(List<Field> specialFields) {
    this.specialFields = specialFields;
  }



}
