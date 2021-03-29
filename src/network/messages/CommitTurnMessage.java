/** @author lurny */
package network.messages;

import mechanic.Turn;
import network.messages.MessageType;

public class CommitTurnMessage extends Message{
	private static final long serialVersionUID = 1L;
	private Turn turn;

	public CommitTurnMessage(MessageType type, String from, Turn turn) {
		super(type, from);
		this.turn = turn;
	}

	public Turn getTurn() {
		return turn;
	}

}
