package gui;

import java.io.IOException;

/** @author nilbecke **/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** This Class launches the Settings Screen **/

public class SettingsScreen extends Application {

	/**
	 * Reads the "SettingsScreen.fxml" file (@author nilbecke) to create the
	 * Settings Menu
	 **/

	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("SettingsScreenFXML.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			//stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Settings");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**Access the current Gamesettingsscreen
	 * 
	 * @return Current instance of Gamesettings Screen
	 */
	public SettingsScreen getSettingScreen() {
		return this;
	}
	public static void main(String[]args) {
		launch(args);
	}

}
