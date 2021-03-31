package gui;

/** 
 * @author nilbecke
 * Launch the Lobby GUI
 * **/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class LobbyScreen extends Application{
	
	private Parent root;

	/**
	 * Reads the "Lobby.fxml" file (@author nilbecke) to create the
	 * Lobby
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

}
