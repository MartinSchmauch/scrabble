package network.messages;

/**
 * This message is used to indicate, that the Game ends. Furhermore it includes the gameState, which
 * contains all relevant statistics.
 * 
 * @author lurny
 */

import game.GameState;

public class EndGameMessage extends Message {

  private static final long serialVersionUID = 1L;
  private GameState gameState;

  public EndGameMessage(String from, GameState gameState) {
    super(MessageType.LOBBY_STATUS, from);
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return this.gameState;
  }

}
