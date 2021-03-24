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

  private HashMap<Letter, Integer> letters;
  private List<Field> specialFields;


  public GameSettings(int timePerPlayer, int maxOvertime, int maxScore, int gameBoardSize,
      String dictionary, String aiDifficulty, int gameCountdown, int bingo,
      HashMap<Letter, Integer> letters, List<Field> specialFields) {
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



}
