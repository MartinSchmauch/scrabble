/** @author lurny */
package network.messages;

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
