package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import game.GameState;

/** 
 * @author nilbecke
 * Launch the Lobby GUI
 * **/

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mechanic.Player;
import mechanic.PlayerData;

public class LobbyScreen extends Application {

	private Parent root;
	private static Player player;
	private static LobbyScreen instance;
	List<PlayerData> players;

	@FXML
	private Label ip, player1, player2, player3, player4;
	@FXML
	private ImageView pic1, pic2, pic3, pic4;

	public LobbyScreen(Player current) {
		instance = this;
		player = current;
		if (player.isHost()) {
			player.host();
		}
		try {
			player.connect(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads the "Lobby.fxml" file (@author nilbecke) to create the Lobby
	 **/
	@Override
	public void start(Stage stage) {
		try {
			Font.loadFont(getClass().getResourceAsStream("Scrabble.ttf"), 14);
			this.root = FXMLLoader.load(getClass().getResource("Lobby.fxml"));
			stage.setOnCloseRequest(e -> close());
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Lobby");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Closes the Lobby and stops the server
	 */
	public static void close() {
		if (LobbyScreenController.getLobbyInstance().getServer() != null) {
			LobbyScreenController.getLobbyInstance().getServer().stopServer();
		} else if (!player.isHost()) {
			player.getClientProtocol().disconnect();
		}
		new LoginScreenFXML().start(new Stage());
	}

	/**
	 * Passes on the instance of local current player
	 * 
	 * @return: Instance of current player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Reference to the current lobby
	 * 
	 * @return Instance of Lobby
	 */
	public static LobbyScreen getInstance() {
		return instance;
	}
}
