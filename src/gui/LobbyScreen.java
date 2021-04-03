package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
import javafx.stage.Stage;

public class LobbyScreen extends Application {

	private Parent root;
	private boolean isHost;
	private static LobbyScreen current;

	@FXML
	private Label ip;
	
	
	public LobbyScreen(boolean host) {
		this.isHost=host;
		current=this;
	}
	
	
	

	/**
	 * Reads the "Lobby.fxml" file (@author nilbecke) to create the Lobby
	 **/
	@Override
	public void start(Stage stage) {
		try {
			this.root = FXMLLoader.load(getClass().getResource("Lobby.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Lobby");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean getHost() {
		return this.isHost;
	}

	public void setHost(boolean host) {
		this.isHost = host;
	}
	public static LobbyScreen getLobby() {
		return current;
	}
}
