package network.messages;

import mechanic.PlayerData;

/**
 * This Message is used, when a client first tries to connect to a Server. The Server uses the
 * playerData to identify the Player.
 *
 * @author lurny
 */

public class ConnectMessage extends Message {
  private static final long serialVersionUID = 1L;

  private PlayerData playerInfo;

  /**
   * This method creates an instance of the class.
   */
  public ConnectMessage(PlayerData playerInfo) {
    super(MessageType.CONNECT, playerInfo.getNickname());
    this.playerInfo = playerInfo;
  }

  public PlayerData getPlayerInfo() {
    return this.playerInfo;
  }

}
