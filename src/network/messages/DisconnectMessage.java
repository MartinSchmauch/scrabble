/** @author lurny */

package network.messages;

import network.messages.Message;
import network.messages.MessageType;

public class DisconnectMessage extends Message {
	private static final long serialVersionUID = 1L;

	public DisconnectMessage(String from) {
		super(MessageType.DISCONNECT, from);
	}	
	
}
