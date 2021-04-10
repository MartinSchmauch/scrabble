package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    // System.out.println(customGameSettings);
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
