package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mechanic.Field;
import mechanic.GameBoard;
import mechanic.PlayerData;
import util.JSONHandler;

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
  private GameBoard gb;
  private PlayerData host;
  private String currentPlayer;
  private HashMap<String, PlayerData> allPlayers;
  private HashMap<String, Integer> scores;

  public GameState(PlayerData host, String customGameSettings) {
    this.isRunning = false;
    this.host = host;
    this.allPlayers = new HashMap<String, PlayerData>();
    this.allPlayers.put(this.host.getNickname(), this.host);

    JSONHandler jH = new JSONHandler();

    if (customGameSettings != null) {
      jH.loadGameSettings(customGameSettings);
    } else {
      jH.loadGameSettings("resources/defaultGameSettings.json");
    }
  }

  /**
   * setUp Gameboard with special Fields
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


  public PlayerData getPlayerData(String nickname) {
    return this.allPlayers.get(nickname);
  }

  public boolean joinGame(PlayerData player) {
    if (isRunning) {
      return false;
    }

    this.allPlayers.put(player.getNickname(), player);
    this.scores.put(player.getNickname(), 0);
    return true;
  }

  public boolean leaveGame(String player) {
    if (player.equals(host.getNickname())) {
      stopGame();
    }

    return (allPlayers.remove(player) != null); 
  }

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

  public boolean addScore(String player, int turnScore) {
    int oldScore = this.scores.get(player);
    return this.scores.replace(player, oldScore, oldScore + turnScore);
  }

}
