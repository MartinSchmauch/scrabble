package game;

import gui.FileParameters;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mechanic.Field;
import mechanic.GameBoard;
import mechanic.PlayerData;
import util.JsonHandler;

/**
 * This class keeps track whether the game is running or in lobby state. It refers to the
 * GameSettings and holds the player data (including avatars) of all players in the lobby or in the
 * game.
 *
 * @author nilbecke, ldreyer
 */

public class GameState implements Serializable {
  private static final long serialVersionUID = 1L;
  private boolean isRunning;
  private transient GameBoard gb;
  private PlayerData host;
  private String currentPlayer;
  private HashMap<String, PlayerData> allPlayers;
  private HashMap<String, Integer> scores;
  private HashMap<String, GameStatistic> gameStatistics = new HashMap<>();


  /**
   * creates an instance of the class.
   */
  public GameState(PlayerData host, String customGameSettings) {
    this.isRunning = false;
    this.host = host;
    this.allPlayers = new HashMap<String, PlayerData>();
    this.allPlayers.put(this.host.getNickname(), this.host);
    this.scores = new HashMap<String, Integer>();
    JsonHandler jsonHandler = new JsonHandler();

    if (customGameSettings != null) {
      jsonHandler.loadGameSettings(new File(customGameSettings));
    } else {
      jsonHandler.loadGameSettings(new File(FileParameters.datadir + "defaultGameSettings.json"));
    }

  }

  /**
   * Set up gameboard with special fields.
   *
   * @author lurny
   */
  public void setUpGameboard() {

    this.gb = new GameBoard(GameSettings.getGameBoardSize());
    List<Field> specialFields = GameSettings.getSpecialFields();
    for (Field f : specialFields) {
      this.gb.getField(f.getxCoordinate(), f.getyCoordinate())
          .setLetterMultiplier(f.getLetterMultiplier());
      this.gb.getField(f.getxCoordinate(), f.getyCoordinate())
          .setWordMultiplier(f.getWordMultiplier());
    }

    Field starField = GameSettings.getStarField();
    if (starField != null) {
      GameSettings
          .setStarField(gb.getField(starField.getxCoordinate(), starField.getyCoordinate()));
    }
  }

  /**
   * get data from current host.
   */

  public PlayerData getHost() {
    return this.host;
  }

  /**
   * checks if the game is currently running.
   */
  
  public boolean getGameRunning() {
    return this.isRunning;
  }
  
  /**
   * Set the game running or stop it.
   */

  public void setRunning(boolean running) {
    this.isRunning = running;
  }
  
  /**
   * Get the current players nickname.
   */

  public String getCurrentPlayer() {
    return this.currentPlayer;
  }
  
  /**
   * Update the current player.
   */

  public void setCurrentPlayer(String nextPlayer) {
    this.currentPlayer = nextPlayer;
  }
  
  /**
   * get a list of all current players.
   */

  public List<PlayerData> getAllPlayers() {
    return new ArrayList<PlayerData>(this.allPlayers.values());
  }
  
  /**
   * Add a player with his player data to the current game state.
   */

  public void addPlayer(PlayerData p) {
    this.allPlayers.put(this.host.getNickname(), p);
  }
  
  /**
   * get a specific player data by nickname.
   */

  public PlayerData getPlayerData(String nickname) {
    return this.allPlayers.get(nickname);
  }
  
  /**
   * Get the score for a player by his nickname.
   */

  public int getScore(String nickName) {
    return this.scores.get(nickName);
  }

  /**
   * This method is called, when a player joins a game. The Server adds the player to the
   * allPlayersList and to the scoreList.
   */
  public boolean joinGame(PlayerData player) {
    if (isRunning) {
      return false;
    }
    this.allPlayers.put(player.getNickname(), player);
    this.scores.put(player.getNickname(), 0);
    return true;
  }

  /**
   * This method is called, when a player leaves the game.
   */
  public boolean leaveGame(String player) {
    if (player.equals(host.getNickname())) {
      stopGame();
    }
    if (isRunning) {
      gameStatistics.remove(player);
    }
    return (allPlayers.remove(player) != null);
  }

  /**
   * This method is called, when the Game is starting to set the isRunning variable on true.
   */
  public boolean startGame(String player) {
    if (player.equals(host.getNickname())) {
      this.isRunning = true;
      return true;
    }

    return false;
  }
  
  /**
   * Stop the game.
   */

  public void stopGame() {
    this.isRunning = false;
  }
  
  /**
   * Get the currently used gameboard.
   */

  public GameBoard getGameBoard() {
    return gb;
  }
  
  /**
   * Update the currently used gameboard.
   */

  public void setGameBoard(GameBoard gameBoard) {
    this.gb = gameBoard;
  }

  /**
   * This method adds a turn score to a specific player.
   */
  public boolean addScore(String player, int turnScore) {
    int oldScore = this.scores.get(player);
    return this.scores.replace(player, oldScore, oldScore + turnScore);
  }

  /**
   * This Method initializes the scores for all players with zero.
   *
   * @author lurny
   */
  public void initializeScoresWithZero(List<PlayerData> playerList) {
    for (PlayerData player : playerList) {
      this.scores.put(player.getNickname(), 0);
    }
  }
  
  /**
   * Get a hashmap of the current game statistics.
   */

  public HashMap<String, GameStatistic> getGameStatistics() {
    return gameStatistics;
  }
  
  /**
   * get the statistic of a player given his nickname.
   */

  public GameStatistic getGameStatisticsOfPlayer(String player) {
    return gameStatistics.get(player);
  }
  
  /**
   * Add a player to the statistic tracking.
   */

  public void addGameStatistics(String player) {
    this.gameStatistics.put(player, new GameStatistic());
  }

}
