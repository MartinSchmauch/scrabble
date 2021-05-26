package network.messages;

/**
 * This class defines the Messsage Types as enums.
 *
 * @author lurny
 */

public enum MessageType {
  CONNECT, DISCONNECT, CONNECTION_REFUSED, INVALID, SHUTDOWN, RESET_TURN, LOBBY_STATUS, START_GAME, 
  TURN_RESPONSE, GAME_STATISTIC, COMMIT_TURN, TILE, SEND_CHAT_TEXT, UPDATE_CHAT, ADD_TILE, 
  MOVE_TILE, REMOVE_TILE;
}
