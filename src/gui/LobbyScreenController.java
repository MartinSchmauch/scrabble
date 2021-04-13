package gui;

import network.client.ClientProtocol;
import network.messages.*;
import network.server.Server;
import network.server.ServerProtocol;
import util.JSONHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import game.GameSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Field;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.SendChatMessage;
import network.messages.StartGameMessage;
import network.server.Server;
import network.server.ServerProtocol;

/**
 * 
 * @author nilbecke Handles all User inputs in the Lobby Screen as well as the
 *         connection of players
 *
 */
public class LobbyScreenController implements EventHandler<ActionEvent>, Sender {

	private Player player;
	private InetAddress address;
	private static LobbyScreenController instance;
	private GameSettings gS;
	List<PlayerData> players;

	@FXML
	private Label ip, player1, player2, player3, player4, countdown;
	@FXML
	private TextField input;
	@FXML
	private TextArea chat;
	@FXML
	private Button start;
	@FXML
	private Button settings;
	@FXML
	private ImageView pic1, pic2, pic3, pic4;

	/**
	 * Set up labels etc before launching the lobby screen
	 */
	@FXML
	public synchronized void initialize() {
		this.player = LobbyScreen.getInstance().getPlayer();
		address = null;
		instance = this;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (!this.player.isHost()) {
			this.start.setOpacity(0.4);
			this.start.setDisable(true);
			this.ip.setOpacity(0);
			this.settings.setOpacity(0.4);
			this.settings.setDisable(true);
			try {
				this.player.connect(InetAddress.getLocalHost());

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			this.player.host();
			System.out.println(this.player.getLocation());
			this.ip.setText("Link:  " + address.getHostAddress());
		}
		// Initialize Nicknames and avatars
		if (this.player.isHost()) {
			this.players = this.player.getServer().getGameState().getAllPlayers();
		} else {
			//TODO get Server as client
		}
	}

	/**
	 * Handles all user inputs in the LobbyScreen
	 */
	@Override
	public void handle(ActionEvent e) {
		String s = ((Node) e.getSource()).getId();
		switch (s) {
		case "leavelobby":
			sendDisconnectMessage(this.player.getNickname());
			closeWindow((Button) e.getSource());
			LobbyScreen.close();
			break;
		case "send":
		case "sendText":
			// Reset the Textlabel
			this.input.setText("");
			break;
		case "start":
			startGame();
			closeWindow((Button) e.getSource());
			break;
		case "settings":
			new SettingsScreen(this.gS).start(new Stage());
			break;
		}
	}

	/**
	 * Starts the countdown before the game launches
	 */
	public void startGame() {
		new StartGameMessage(this.player.getNickname(), 10);
		try {
			new ClientUI().start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a given message to all players
	 * 
	 * @param m: The Message to be sent
	 * @return true if message was sent, false otherwise
	 */
	public boolean sendMessage(Message m) {
		try {
			if (this.player.isHost()) {
				this.player.getServer().sendToAll(m);

			} else {
				this.player.getClientProtocol().sendToServer(m);
				System.out.println("!");
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * sends the chat to server
	 * 
	 * @param message is the message to be added in the chat
	 * @param sender  is the player sending the message
	 * @param time    represents the time when the given message was sent
	 */
	@Override
	public void sendChatMessage(String sender, String message) {
		if (message.length() == 0) {
			return;
		}
		Message m = new SendChatMessage(sender, message, LocalDateTime.now());
		sendMessage(m);
	}

	/**
	 * Lets a player disconnect from the current game. If the leaving player is the
	 * host, the lobby closes
	 * 
	 * @param playerID: Nickname of leaving player
	 */
	@Override
	public void sendDisconnectMessage(String playerID) {
		Message m = (Message) new DisconnectMessage(playerID);
		sendMessage(m);
	}
	

	/**
	 * Getter Method for the current Instance of the controller
	 *
	 * @return Current Instance of the contoller
	 */
	public static LobbyScreenController getLobbyInstance() {
		return instance;
	}

	/**
	 * Lets a player disconnect or connect
	 * 
	 * @param player: Playerdata of the player to be (dis-)connecting
	 */
	public void updateJoinedPlayers(PlayerData player) {

	}

	/**
	 * Reads updated game settings and distributes them to all players
	 * 
	 * @param: new Instance of game settings
	 */
	public void updateGameSettings(GameSettings settings) {
		// TODO
	}

	/**
	 * Get the current Server
	 * 
	 * @return: Current instance of the server if present else null
	 */
	public Server getServer() {
		return this.player.getServer();
	}

	/**
	 * Get a reference onto the game settings currently used
	 * 
	 * @return: currently used game settings
	 */
	public GameSettings getSettings() {
		return this.gS;
	}

	/**
	 * Lets a player connect
	 * 
	 * @param player: Playerdata of the player to be (dis-)connecting
	 */
	public void addJoinedPlayer(PlayerData player) {
		// TODO
	}

	/**
	 * Lets a player disconnect
	 * 
	 * @param nickname of the player disconnecting
	 */

	public void removeJoinedPlayer(String nickname) {
		// TODO
	}

	/**
	 * Adds text message from server to chat history.
	 * 
	 * @param message:   message
	 * @param sender:    nickname of sender
	 * @param timestamp: date and time from when the message has been sent
	 */
	public void updateChat(String message, String sender, LocalDateTime timestamp) {
		// TODO
	}

	/**
	 * 
	 */
	public void closeWindow(Button b) {
		Stage st = (Stage) b.getScene().getWindow();
		st.close();
	}

	/**
	 * Obsolete Methods from the Sender Interface
	 */
	@Override
	public void sendTileMove(Tile tile, Field field) {
	}

	public void sendCommitTurn(String nickName) {
	}

}
