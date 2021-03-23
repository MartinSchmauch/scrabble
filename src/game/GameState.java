package game;

import mechanic.*;

//** @author nilbecke

public class GameState {

	boolean isRunning;
	Player currentPlayer;
	Player[] allPlayers;

	public GameState(Player[] players) {
		this.isRunning = true;
		this.currentPlayer = players[0];
		this.allPlayers = players;
	}

	public boolean getGameRunning() {
		return this.isRunning;
	}

	public void setRunning(boolean running) {
		this.isRunning = running;
	}

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public Player[] getAllPlayers() {
		return allPlayers;

	}

}
