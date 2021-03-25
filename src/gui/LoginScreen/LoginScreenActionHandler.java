package gui.LoginScreen;

/** @Author nilbecke **/

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

/**
 * This Class is a Basic Handler for all the input options present in the Login
 * Screen
 **/

public class LoginScreenActionHandler implements EventHandler<ActionEvent> {

	/**
	 * Handles the different Buttons in the Login Screen
	 * 
	 * @param Input event
	 * @see Reaction to given input
	 **/
	@Override
	public void handle(ActionEvent e) {
		Button button = (Button) e.getSource();
		System.out.println(button.getText());

		switch (button.getText()) {
		case "Join":
			TextInputDialog join = new TextInputDialog("");
			join.setTitle("Join Game");
			join.setHeaderText(null);
			join.setContentText("Enter Link");

			Optional<String> result = join.showAndWait();

			if (result.isPresent()) {
				Alert connection = new Alert(AlertType.CONFIRMATION);
				connection.setTitle("Connecting Game");
				connection.setHeaderText(null);
				connection.setContentText("Connection to " + result);
				connection.show();
			}
			break;
		case "Exit":
			System.exit(0);
			break;

		case "Tutorial":
			OpenTutorial.open();
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
