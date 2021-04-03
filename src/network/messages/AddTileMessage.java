package network.messages;

import mechanic.Tile;

/**@author lurny*/

public class AddTileMessage extends Message{

	  private static final long serialVersionUID = 1L;

	  private Tile tile;

	  public AddTileMessage(String from, Tile tile) {
	    super(MessageType.ADD_TILE, from);
	    this.tile = tile;
	  }

	  public Tile getTile() {
	    return this.tile;
	  }

}
