package game;

import java.util.ArrayList;
import java.util.List;
import mechanic.Player;
import util.JSONHandler;

/**
 * This class surveils the game's state.
 * 
 * @author nilbecke, ldreyer
 */

public class GameState {

  boolean isRunning;
  GameSettings gameSettings;
  GameController gameController;
  Player host;
  Player currentPlayer;
  List<Player> allPlayers;

  public GameState(Player host) {
    this.isRunning = false;
    // this.gameSettings = new GameSettings();
    this.host = host;
    this.allPlayers = new ArrayList<Player>();
    this.allPlayers.add(host);

    JSONHandler jH = new JSONHandler();
    if (host.getCustomGameSettings() != null) {
      jH.loadGameSettings(host.getCustomGameSettings());
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

  public Player getCurrentPlayer() {
    return this.currentPlayer;
  }

  public List<Player> getAllPlayers() {
    return allPlayers;
  }

  public boolean joinGame(Player player) {
    if (isRunning) {
      return false;
    }

    this.allPlayers.add(player);
    return true;
  }

  public boolean leaveGame(Player player) {
    if (player.equals(host)) {
      stopGame();
    }

    return allPlayers.remove(player);
  }

  public boolean startGame(Player player) {
    if (player.equals(host)) {
      this.gameController = new GameController(this);
      this.isRunning = true;
      return true;
    }

    return false;
  }

  public void stopGame() {
    this.isRunning = false;
  }

}
