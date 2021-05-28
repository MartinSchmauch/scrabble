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
  private int position;
  private int playedTiles;
  private List<String> allPlayers;



  public GameStatistic() {
    this.bestWords = new ArrayList<String>();
    this.allPlayers = new ArrayList<String>();

  }

  public String getWinner() {
    return this.allPlayers.get(0);
  }

  public int getBestTurn() {
    return bestTurn;
  }

  public void setBestTurn(int bestTurn) {
    this.bestTurn = bestTurn;
  }

  public List<String> getBestWords() {
    return bestWords;
  }

  public void setBestWords(List<String> bestWords) {
    this.bestWords = bestWords;
  }

  /**
   * This method returns the best words as a continous String.
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

  public int getAverageTimeperTurn() {
    return averageTimeperTurn;
  }

  public void setAverageTimeperTurn(int averageTimeperTurn) {
    this.averageTimeperTurn = averageTimeperTurn;
  }

  public int getPlayTime() {
    return totalPlayTime;
  }

  public void setPlayTime(int playTime) {
    this.totalPlayTime = playTime;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getPlayedTiles() {
    return playedTiles;
  }

  public void setPlayedTiles(int playedTiles) {
    this.playedTiles = playedTiles;
  }

  public List<String> getAllPlayers() {
    return allPlayers;
  }

  public void setAllPlayers(List<String> allPlayers) {
    this.allPlayers = allPlayers;
  }

  public int getTotalTurns() {
    return totalTurns;
  }

  public void setTotalTurns(int totalTurns) {
    this.totalTurns = totalTurns;
  }
}
