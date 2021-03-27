package gui;

/** @Author nilbecke **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This Class is a Basic Handler for all the input options present in the Login
 * Screen
 **/

public class LoginScreenActionHandler extends LoginScreenFXML implements EventHandler<ActionEvent> {

	@FXML
	private TextField LinkField;

	/**
	 * Handles the different Buttons in the Login Screen
	 * 
	 * @param Input event
	 * @see Reaction to given input
	 **/
	@Override
	public void handle(ActionEvent e) {
		if (e.getSource().getClass() != Button.class) {
			join();
		} else {
			Button button = (Button) e.getSource();
			System.out.println(button.getText());

			switch (button.getText()) {
			case "Join":
				join();
				break;
			case "Exit":
				System.exit(0);
				break;

			case "Tutorial":
				OpenTutorial.open();
				break;
				
			case "Settings":
				new SettingsScreen().start(new Stage());
				break;
			default:
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Too early");
				alert.setHeaderText(null);
				alert.setContentText("Function for " + button.getText() + " Not Yet Implemented :D");
				alert.show();
			}
		}
	}

	/**
	 * Handles the process when a player is trying to join a game via link. It can
	 * be accessed via "enter" after an input in the LinkField or via a press on the
	 * "Join" Button
	 **/
	public void join() {
		String gameID = LinkField.getText();
		Alert connection;
		if (gameID.length() == 0) {
			connection = new Alert(AlertType.ERROR);
			connection.setTitle("Connection Error");
			connection.setHeaderText(null);
			connection.setContentText("Must enter a Link");
			connection.show();
		} else {
			connection = new Alert(AlertType.CONFIRMATION);
			connection.setTitle("Connecting to game");
			connection.setHeaderText(null);
			connection.setContentText("Connecting to \"" + gameID + "\"");
		}
		connection.show();
	}
}
