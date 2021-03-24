package gui;

import java.util.Optional;

//** @Author nilbecke

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class LoginScreenActionHandler implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent e) {
		Button button = (Button) e.getSource();
		System.out.println(button.getText());

		if (button.getText().equals("Join Game")) {
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

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Too early");
			alert.setHeaderText(null);
			alert.setContentText("Not Yet Implemented :D");
			alert.show();
		}
	}

}
