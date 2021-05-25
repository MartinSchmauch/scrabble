package game;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gui.FileParameters;
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
  // private GameSettings gameSettings;



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


  public PlayerData getHost() {
    return this.host;
  }

  public boolean getGameRunning() {

    return this.isRunning;
  }

  public void setRunning(boolean running) {
    this.isRunning = running;
  }

  public String getCurrentPlayer() {
    return this.currentPlayer;
  }

  public void setCurrentPlayer(String nextPlayer) {
    this.currentPlayer = nextPlayer;
  }

  public List<PlayerData> getAllPlayers() {
    return new ArrayList<PlayerData>(this.allPlayers.values());
  }

  public void addPlayer(PlayerData p) {
    this.allPlayers.put(this.host.getNickname(), p);
  }

  public PlayerData getPlayerData(String nickname) {
    return this.allPlayers.get(nickname);
  }

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
    gameStatistics.remove(player);
    scores.remove(player);
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

  public void stopGame() {
    this.isRunning = false;
  }

  public GameBoard getGameBoard() {
    return gb;
  }

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
   * @param playerList
   */
  public void initializeScoresWithZero(List<PlayerData> playerList) {
    for (PlayerData player : playerList) {
      this.scores.put(player.getNickname(), 0);
    }
  }

  public HashMap<String, GameStatistic> getGameStatistics() {
    return gameStatistics;
  }

  public GameStatistic getGameStatisticsOfPlayer(String player) {
    return gameStatistics.get(player);
  }

  public void addGameStatistics(String player) {
    this.gameStatistics.put(player, new GameStatistic());
  }

}
