/** @author lurny*/
package network.messages;

public class TileRequestMessage extends Message{
	private static final long serialVersionUID = 1L;
	
	public TileRequestMessage(MessageType type, String from) {
		super(type, from);
	}

}
