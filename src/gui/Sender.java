package gui;

import java.time.LocalDate;

/** @autor mschmauch */

import mechanic.Field;
import mechanic.Tile;

public interface Sender {

  /**
   * This interface provice methods to be implemented in the GamePanelController to send gui actions
   * from a local client to the server
   */

  void sendChatMessage(String sender, String message, LocalDate timeStamp);

  void sendTileMove(Tile tile, Field field);

  void sendCommitTurn(String nickName);

  void sendDisconnectMessage(String playerID);
}
