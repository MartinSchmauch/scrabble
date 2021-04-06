package gui;

import network.client.ClientProtocol;
import network.messages.*;
import network.server.Server;
import network.server.ServerProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;

import game.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mechanic.Field;
import mechanic.Player;
import mechanic.Tile;

/**
 * 
 * @author nilbecke Handles all User inputs in the Lobby Screen as well as the
 *         connection of players
 *
 */
public class LobbyScreenController implements EventHandler<ActionEvent>, Sender {

	private Player player;
	private ClientProtocol client = null;
	private ServerProtocol host = null;
	private Server server=null;
	private InetAddress address;

	@FXML
	private Label countdown;
	@FXML
	private Label ip;
	@FXML
	private TextField input;
	@FXML
	private TextArea chat;
	@FXML
	private Button start;
	@FXML
	private Button settings;

	/**
	 * Set up labels etc before launching the lobby screen
	 */
	@FXML
	public void initialize() {
		this.player = LobbyScreen.getPlayer();
		address = null;
		try {
			address = InetAddress.getLocalHost();
			this.player.setLocation(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// Initialize Host/Client
		if (this.player.getIsHost()) {
			//this.server = new Server(this.player.getPlayerInfo(),"C:/Users/Nils Becker/SE2021/scrabble3/resources/SettingsTest.json");
			try {
				this.host = new ServerProtocol(new Socket(this.address,5678),this.server);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.ip.setText("Link:  " + address.getHostAddress());
		} else {
			this.client = new ClientProtocol(address + "", 5678, this.player.getPlayerInfo(), null, null, this);
			this.start.setOpacity(0.4);
			this.start.setDisable(true);
			this.ip.setOpacity(0);
			this.settings.setOpacity(0.4);
			this.settings.setDisable(true);
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
			new SettingsScreen().start(new Stage());
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
	 * updates the chat for all connected players
	 * 
	 * @param message is the message to be added in the chat
	 * @param sender  is the player sending the message
	 * @param time    represents the time when the given message was sent
	 */
	@Override
	public void sendChatMessage(String sender, String message, LocalDate timeStamp) {
		if (message.length() == 0) {
			return;
		}
		Message m = new SendChatMessage(sender, message, timeStamp);
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
		Message m = new DisconnectMessage(playerID);
		sendMessage(m);
	}
	
	public boolean sendMessage(Message m) {
		try {
			if(this.player.getIsHost()) {
				this.host.sendToClient(m);
			} else {
				this.client.sendToServer(m);
			}
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
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
