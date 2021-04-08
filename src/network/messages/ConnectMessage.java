package network.messages;

/**
 * This Message is used, when a client first tries to connect to a Server. The Server uses the
 * playerData to identify the Player.
 * 
 * @author lurny
 */

import mechanic.PlayerData;

public class ConnectMessage extends Message {
  private static final long serialVersionUID = 1L;

  private PlayerData playerInfo;

  public ConnectMessage(PlayerData playerInfo) {
    super(MessageType.CONNECT, playerInfo.getNickname());
    this.playerInfo = playerInfo;
  }

  public PlayerData getPlayerInfo() {
    return this.playerInfo;
  }

}
