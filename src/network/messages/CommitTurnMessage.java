/** @author lurny */
package network.messages;

import mechanic.Turn;
import network.messages.MessageType;

public class CommitTurnMessage extends Message{
	private static final long serialVersionUID = 1L;
	private Turn turn;
	private int id;

	public CommitTurnMessage(MessageType type, String from, int id, Turn turn) {
		super(type, from);
		this.turn = turn;
		this.id = id;
	}

	public Turn getTurn() {
		return turn;
	}

	public int getId() {
		return id;
	}

}
