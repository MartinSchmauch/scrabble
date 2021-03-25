package gui;

//** @author nilbecke **//

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** This Class launches the Login Screen of the Scrabble Application **/

public class LoginScreenFXML extends Application {

	/**
	 * This method reads the "LoginScreenFXML.fxml" (@author nilbecke) file to create
	 * the Login Screen
	 */

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("LoginScreenFXML.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Scrabble3");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);

	}

}
