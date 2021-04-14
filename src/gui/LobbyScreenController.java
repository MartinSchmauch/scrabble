package gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import game.GameSettings;
import game.GameState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.DisconnectMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.SendChatMessage;
import network.messages.StartGameMessage;
import network.server.Server;

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

		} else {

			sendLobbyMessage(this.player.getNickname(), this.player.getServer().getGameState());
			this.ip.setText("Link:  " + address.getHostAddress());
		}

		// update nicknames and avatars continuously
		Timeline playerUpdate = new Timeline(
				new KeyFrame(Duration.seconds(1),
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						updateJoinedPlayers();
					}
				}));
		playerUpdate.setCycleCount(Timeline.INDEFINITE);
		playerUpdate.play();
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
			break;
		case "send":
		case "sendText":
			// Reset the Textlabel
			this.input.setText("");
			break;
		case "start":
			startGame();
			Stage st = (Stage) ((Button) e.getSource()).getScene().getWindow();
			st.close();
			break;
		case "settings":
			new SettingsScreen(this.gS).start(new Stage());
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
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Send the Lobby status as host to clients
	 * 
	 * @param id: Nickname of Host
	 * @param gS: GameState from host
	 */
	public void sendLobbyMessage(String id, GameState gS) {
		Message m = (Message) new LobbyStatusMessage(id, gS);
		sendMessage(m);
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
	public void updateJoinedPlayers() {
		GameState gS;
		if (player.isHost()) {
			gS = player.getServer().getGameState();
			this.players = gS.getAllPlayers();
		} else {
			gS = player.getClientProtocol().getGameState();
			this.players = gS.getAllPlayers();
		}
		Label[] nicknames = { player1, player2, player3, player4 };
		ImageView[] avatars = { pic1, pic2, pic3, pic4 };
		for (int i = 0; i <= 3; i++) {
			if (i < players.size()) {
				if (players.get(i) != null) {
					nicknames[i].setText(players.get(i).getNickname());
					avatars[i].setImage(new Image("file:" + FileParameters.datadir + players.get(i).getAvatar()));
				}
			}
		}
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
	 * Reads updated game settings and distributes them to all players
	 * 
	 * @param: new Instance of game settings
	 */
	public void updategameSettings(GameSettings settings) {
		// TODO
	}

	public void sendCommitTurn(String nickName) {
	}

	@Override
	public void sendTileMove(String nickName, Tile tile, int newX, int newY) {
		// TODO

	}

}
