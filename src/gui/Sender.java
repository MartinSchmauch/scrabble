package gui;

/** @autor mschmauch */

import java.util.Date;
import mechanic.Field;
import mechanic.Tile;

public interface Sender {

  /**
   * This interface provice methods to be implemented in the GamePanelController to send gui actions
   * from a local client to the server
   */

  void sendChatMessage(String message, Date timeStamp, String sender);

  void sendTileMove(Tile tile, Field field);

  Boolean sendCommitTurn();

  void sendDisconnectMessage(String playerID);
}
