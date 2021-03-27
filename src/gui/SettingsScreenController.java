package gui;

/** @author nilbecke **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/** Handles user-inputs in the Gamesettings screen **/

public class SettingsScreenController extends SettingsScreen implements EventHandler<ActionEvent> {

	/**
	 * Primary handling of user inputs. Redistributes inputs to sub-methods based on
	 * the input object
	 **/
	@Override
	public void handle(ActionEvent e) {
		switch (e.getSource().getClass().getSimpleName()) {
		case "Button":
			button((Button) e.getSource());
			break;
		default:
			break;
		}
	}

	/**
	 * Handles button inputs
	 * 
	 * @param Instance of button Class
	 * 
	 **/
	public void button(Button b) {
		switch (b.getText()) {
		case "Exit":
			Stage s = (Stage) b.getScene().getWindow();
			s.close();
			break;
		default:
			break;
		}
	}

}
