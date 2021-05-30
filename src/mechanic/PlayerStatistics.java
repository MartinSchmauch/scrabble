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

  /**
   * sets the variable gameCount of the current instance.
   */
  public void setGameCount(int gameCount) {
    this.gameCount = gameCount;
  }

  /**
   * sets the variable bestTurn of the current instance.
   */
  public void setBestTurn(int turn) {
    this.bestTurn = turn;
  }

  /**
   * sets the variable bestWord of the current instance.
   */
  public void setBestWord(String word) {
    this.bestWord = word;
  }

  /**
   * sets the variable playTime of the current instance.
   */
  public void setPlayTime(int time) {
    this.playTime = time;
  }

  /**
   * sets the variable score of the current instance.
   */
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


  /**
   * sets the variable playedTiles of the current instance.
   */
  public void setPlayedTiles(int tiles) {
    this.playedTiles += tiles;
  }

  /**
   * Getter Methods.
   */
  public int getGameCount() {
    return this.gameCount;
  }

  /**
   * sets the variable wins of the current instance.
   */
  public void setWins(int wins) {
    this.wins = wins;
  }

  /**
   * gets the variable bestTurn of the current instance.
   */
  public int getBestTurn() {
    return this.bestTurn;
  }

  /**
   * gets the variable bestWord of the current instance.
   */
  public String getBestWord() {
    return this.bestWord;
  }

  /**
   * gets the variable playtime of the current instance.
   */
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

  /**
   * gets the variable score of the current instance.
   */
  public int getScore() {
    return this.score;
  }

  /**
   * gets the variable wins of the current instance.
   */
  public int getWins() {
    return this.wins;
  }

  /**
   * gets the variable playedTiles of the current instance.
   */
  public int getPlayedTiles() {
    return this.playedTiles;
  }
}
