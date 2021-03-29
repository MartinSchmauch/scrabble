/** @author lurny */
package network.messages;

import java.util.List;

import mechanic.Tile;
import network.messages.MessageType;

public class UpdateRequestGameBoardMessage extends Message{
	private static final long serialVersionUID = 1L;
	private int id;
	private List<Tile> tiles;

	public UpdateRequestGameBoardMessage(MessageType type, String from, int id, List<Tile> tiles) {
		super(type, from);
		this.id = id;
		this.tiles = tiles;
	}

	public int getID(){
		return this.id;
	}
	
	public List<Tile> getTiles(){
		return tiles;
		
	}
}
