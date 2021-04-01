package network.messages;

import mechanic.Tile;

/**@author lurny*/

public class RemoveTileMessage extends Message{

	  private static final long serialVersionUID = 1L;

	  private Tile tile;

	  public RemoveTileMessage(String from, Tile tile) {
	    super(MessageType.REMOVE_TILE, from);
	    this.tile = tile;
	  }

	  public Tile getTile() {
	    return this.tile;
	  }

}