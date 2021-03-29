package network.messages;

import mechanic.Turn;

public class TurnResponseMessage extends Message {

  private static final long serialVersionUID = 1L;
  Turn turn;

  public TurnResponseMessage(MessageType type, String from, Turn turn) {
    super(type, from);
    this.turn = turn;
  }

}
