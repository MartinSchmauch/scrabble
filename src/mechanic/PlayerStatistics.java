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

  private int gameCount;
  private int bestTurn;
  private String bestWord;
  private int playTime;
  private int score;
  private int wins;
  private int playedTiles;

  /**
   * Setter Methods.
   */

  public void setGameCount(int count) {
    this.gameCount = count;
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

  public void setWins(int wins) {
    this.wins = wins;
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

  public int getBestTurn() {
    return this.bestTurn;
  }

  public String getBestWord() {
    return this.bestWord;
  }

  public int getPlayTime() {
    return this.playTime;
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
