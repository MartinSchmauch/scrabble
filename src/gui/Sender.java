package gui;

/**
 * @autor mschmauch
 * 
 *        This interface provice methods to be implemented in the GamePanelController to send gui
 *        actions from a local client to the server
 */

public interface Sender {

  /**
   * This method creates a new TileRequestMessage that is supposed to inform the server that a
   * client has moved a tile in his Client UI and the tile move needs to be checked for conformitiy.
   * Therefore the new message is send to the server, using the sendMessageToServer() method; the
   * confirmation of the move is handled in ClientProtocol
   * 
   * @param nickName
   * @param oldX
   * @param oldY
   * @param newX
   * @param newY
   */
  void sendTileMove(String nickName, int oldX, int oldY, int newX, int newY);

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
