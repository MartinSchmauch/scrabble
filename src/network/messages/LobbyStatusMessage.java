package network.messages;

/**
 * This message is used to frequently update the Lobbyscreen, which is shown before the start of the
 * game.
 * 
 * @author lurny
 */

import game.GameState;

public class LobbyStatusMessage extends Message {

  private static final long serialVersionUID = 1L;
  private GameState gameState;

  public LobbyStatusMessage(String from, GameState gameState) {
    super(MessageType.LOBBY_STATUS, from);
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return this.gameState;
  }

}
