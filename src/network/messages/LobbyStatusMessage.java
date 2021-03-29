package network.messages;

import game.GameState;

public class LobbyStatusMessage extends Message {

  private static final long serialVersionUID = 1L;
  private GameState gameState;

  public LobbyStatusMessage(MessageType type, String from, GameState gameState) {
    super(type, from);
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return this.gameState;
  }

}
