/** @author lurny */
package network.messages;

import network.messages.Message;
import network.messages.MessageType;

public class ConnectMessage extends Message{
	private static final long serialVersionUID = 1L;

	public ConnectMessage(String name) {
		super(MessageType.CONNECT, name);
	}
	
}
