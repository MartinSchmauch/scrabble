package game;

import java.util.HashMap;
import java.util.List;
import mechanic.PlayerData;
import util.JSONHandler;

/**
 * This class surveils the game's state.
 * 
 * @author nilbecke, ldreyer
 */

public class GameState {

  boolean isRunning;
  GameSettings gameSettings;
  PlayerData host;
  String currentPlayer;
  HashMap<String, PlayerData> allPlayers;

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

  public boolean getGameRunning() {
    return this.isRunning;
  }

  public void setRunning(boolean running) {
    this.isRunning = running;
  }

  public String getCurrentPlayer() {
    return this.currentPlayer;
  }

  public List<PlayerData> getAllPlayers() {
    return (List<PlayerData>) this.allPlayers.values();
  }

  public boolean joinGame(PlayerData player) {
    if (isRunning) {
      return false;
    }

    this.allPlayers.put(player.getNickname(), player);
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

}
