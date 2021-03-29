/** @author lurny*/
package network.messages;

import mechanic.Tile;

public class TileResponseMessage extends Message{
    private static final long serialVersionUID = 1L;
	private Tile tile;

	public TileResponseMessage(MessageType type, String from, Tile tile) {
		super(type, from);
	}

	public Tile getTile() {
		return tile;
	}

}
