package gui;

/** @author nilbecke **/

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;
import util.JSONHandler;

/** This Class launches the Login Screen of the Scrabble Application **/

public class LoginScreenFXML extends Application {

	private Parent root;
	private Player currentPlayer;
	
	
	@FXML 
	private ImageView avatar;
	@FXML
	private Label username;
	
	
	/**
	 * Set up the avatar picture before loginscreen is visible
	 */
	
	@FXML
	public void initialize() {
		this.currentPlayer = new JSONHandler().loadPlayerProfile("resources/playerProfileTest.json");
		this.username.setText(this.currentPlayer.getNickname());
		this.avatar.setImage(new Image("file:"+FileParameters.datadir+this.currentPlayer.getAvatar()));
	}

	/**
	 * This method reads the "LoginScreenFXML.fxml" (@author nilbecke) file to
	 * create the Login Screen
	 */

	@Override
	public void start(Stage stage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("Scrabble.ttf"), 14);
		this.root = FXMLLoader.load(getClass().getResource("LoginScreenFXML.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		//stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle("Scrabble3");
		stage.show();
	}
	
	
	public Parent getParent() {
		return this.root;
	}

	public LoginScreenFXML getLoginScreen() {
		return this;
	}

	public static void main(String[] args) {
		launch(args);

	}

}
