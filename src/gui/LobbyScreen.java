package gui;

/** 
 * @author nilbecke
 * Launch the Lobby GUI
 * **/

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mechanic.Player;

public class LobbyScreen extends Application {

	private Parent root;
	private Player player;
	private static LobbyScreen instance;

	@FXML
	private Label ip;

	public LobbyScreen(Player current) {
		instance = this;
		this.player = current;
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
		}
		new LoginScreenFXML().start(new Stage());
	}

	/**
	 * Passes on the instance of local current player
	 * 
	 * @return: Instance of current player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Reference to the current lobby
	 * @return Instance of Lobby
	 */
	public static LobbyScreen getInstance() {
		return instance;
	}

}
