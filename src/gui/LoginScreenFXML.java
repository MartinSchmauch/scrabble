package gui;

/** @author nilbecke **/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** This Class launches the Login Screen of the Scrabble Application **/

public class LoginScreenFXML extends Application {

	private Parent root;
	public Stage stage;

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
		this.stage=stage;
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
