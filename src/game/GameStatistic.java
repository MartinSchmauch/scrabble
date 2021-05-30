package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds data for one player for one Game. It is later used to fill the
 * LeaderboardScreen.
 *
 * @author lurny
 */

public class GameStatistic implements Serializable {

  private static final long serialVersionUID = 1L;
  private int bestTurn;
  private List<String> bestWords;
  private int averageTimeperTurn;
  private int totalPlayTime;
  private int totalTurns;
  private int score;
  private int playedTiles;
  private List<String> allPlayers;


  /**
  * creates an instance of the class.
  */
  public GameStatistic() {
    this.bestWords = new ArrayList<String>();
    this.allPlayers = new ArrayList<String>();

  }

  /**
   * gets the winner of the current instance.
   */
  public String getWinner() {
    return this.allPlayers.get(0);
  }

  /**
   * gets the variable bestTurn of the current instance.
   */
  public int getBestTurn() {
    return bestTurn;
  }

  /**
   * sets the variable bestTurn of the current instance.
   */
  public void setBestTurn(int bestTurn) {
    this.bestTurn = bestTurn;
  }

  /**
   * gets the variable bestWords of the current instance.
   */
  public List<String> getBestWords() {
    return bestWords;
  }

  /**
   * sets the variable bestWords of the current instance.
   */
  public void setBestWords(List<String> bestWords) {
    this.bestWords = bestWords;
  }

  /**
   * provides the best words as a continous String.
   */
  public String getBestWordAsString() {
    String s = "";
    boolean firstWord = true;
    for (String word : bestWords) {
      if (firstWord) {
        s = word;
        firstWord = false;
      } else {
        s = s + "; " + word;
      }
    }
    return s;
  }

  /**
   * gets the variable averageTimeperTurn of the current instance.
   */
  public int getAverageTimeperTurn() {
    return averageTimeperTurn;
  }

  /**
   * sets the variable averageTimeperTurn of the current instance.
   */
  public void setAverageTimeperTurn(int averageTimeperTurn) {
    this.averageTimeperTurn = averageTimeperTurn;
  }

  /**
   * gets the variable totalPlayTime of the current instance.
   */
  public int getPlayTime() {
    return totalPlayTime;
  }

  /**
   * sets the variable totalPlayTime of the current instance.
   */
  public void setPlayTime(int playTime) {
    this.totalPlayTime = playTime;
  }

  /**
   * gets the variable score of the current instance.
   */
  public int getScore() {
    return score;
  }

  /**
   * sets the variable score of the current instance.
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * gets the variable playedTiles of the current instance.
   */
  public int getPlayedTiles() {
    return playedTiles;
  }

  /**
   * gets the variable playedTiles of the current instance.
   */
  public void setPlayedTiles(int playedTiles) {
    this.playedTiles = playedTiles;
  }

  /**
   * gets the variable allPlayers of the current instance.
   */
  public List<String> getAllPlayers() {
    return allPlayers;
  }

  /**
   * sets the variable allPlayers of the current instance.
   */
  public void setAllPlayers(List<String> allPlayers) {
    this.allPlayers = allPlayers;
  }

  /**
   * gets the variable totalTurns of the current instance.
   */
  public int getTotalTurns() {
    return totalTurns;
  }

  /**
   * sets the variable totalTurns of the current instance.
   */
  public void setTotalTurns(int totalTurns) {
    this.totalTurns = totalTurns;
  }
}
