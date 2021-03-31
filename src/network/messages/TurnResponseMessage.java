package network.messages;

/** @author lurny */

import mechanic.Turn;

public class TurnResponseMessage extends Message {

  private static final long serialVersionUID = 1L;
  Turn turn;

  public TurnResponseMessage(String from, Turn turn) {
    super(MessageType.TURN_RESPONSE, from);
    this.turn = turn;
  }

}
