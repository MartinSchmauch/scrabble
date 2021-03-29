package network.messages;

import mechanic.Tile;

public class UpdateGameboardMessage extends Message {

  private static final long serialVersionUID = 1L;
  Tile tile;


  public UpdateGameboardMessage(MessageType type, String from, Tile tile) {
    super(type, from);
    this.tile = tile;
  }

}
