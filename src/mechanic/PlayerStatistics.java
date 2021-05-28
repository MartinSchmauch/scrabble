package mechanic;

import java.io.Serializable;

/**
 * This class represents all user statistics.
 *
 * @author nilbecke
 *
 */

public class PlayerStatistics implements Serializable {
  private static final long serialVersionUID = 1L;

  private int bestTurn;
  private int gameCount;
  private String bestWord;
  private int playTime;
  private int score;
  private int wins;
  private int playedTiles;


  /**
   * This method increments the GameCount by one.
   *
   * @author lurny
   */
  public void incrementGameCount() {
    this.gameCount++;
  }

  public void setGameCount(int gameCount) {
    this.gameCount = gameCount;
  }

  public void setBestTurn(int turn) {
    this.bestTurn = turn;
  }

  public void setBestWord(String word) {
    this.bestWord = word;
  }

  public void setPlayTime(int time) {
    this.playTime = time;
  }

  public void setScore(int score) {
    this.score = score;
  }

  /**
   * This method increments the amount of won games by one.
   *
   * @author lurny
   */
  public void incrementWins() {
    this.wins++;
  }


  public void setPlayedTiles(int tiles) {
    this.playedTiles += tiles;
  }

  /**
   * Getter Methods.
   */
  public int getGameCount() {
    return this.gameCount;
  }

  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getBestTurn() {
    return this.bestTurn;
  }

  public String getBestWord() {
    return this.bestWord;
  }

  public int getPlayTime() {
    return this.playTime;
  }

  /**
   * This method gives back the total game Time in minutes.
   *
   * @author lurny
   */
  public int getPlayTimeInMinutes() {
    return this.playTime / 60;
  }

  public int getScore() {
    return this.score;
  }

  public int getWins() {
    return this.wins;
  }

  public int getPlayedTiles() {
    return this.playedTiles;
  }
}
