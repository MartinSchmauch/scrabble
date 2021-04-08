package gui;

import java.time.LocalDate;
import mechanic.Field;
import mechanic.Tile;

/**
 * @autor mschmauch
 * 
 *        This interface provice methods to be implemented in the GamePanelController to send gui
 *        actions from a local client to the server
 */

public interface Sender {

  /**
   * This method creates a new SendChatMessage that is supposed to inform the server that a client
   * has send a chat message in his Client UI. Therefore the new message is send to the server,
   * using the sendMessageToServer() method
   * 
   * @param sender
   * @param message
   * @param timeStamp
   */
  void sendChatMessage(String sender, String message, LocalDate timeStamp);

  /**
   * This method creates a new TileRequestMessage that is supposed to inform the server that a
   * client has moved a tile in his Client UI and the tile move needs to be checked for conformitiy.
   * Therefore the new message is send to the server, using the sendMessageToServer() method; the
   * confirmation of the move is handled in ClientProtocol
   * 
   * @param tile
   * @param field
   */
  void sendTileMove(Tile tile, Field field);

  /**
   * This method creates a new CommitTurnMessage that is supposed to inform the server that a client
   * has completed a turn by clicking the 'done' button in his Client UI. Therefore the new message
   * is send to the server, using the sendMessageToServer() method
   * 
   * @param nickName
   */
  void sendCommitTurn(String nickName);

  /**
   * This method creates a new DisconnectMessage that is supposed to inform the server that a client
   * wants to disconnect from the server and stop the game. Therefore the new message is send to the
   * server, using the sendMessageToServer() method.
   * 
   * @param playerID
   */
  void sendDisconnectMessage(String playerID);
}
