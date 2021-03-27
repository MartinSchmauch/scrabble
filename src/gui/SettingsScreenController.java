package gui;

/** @author nilbecke **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.Stage;

/** Handles user-inputs in the Gamesettings screen **/

public class SettingsScreenController extends SettingsScreen implements EventHandler<ActionEvent> {

	@FXML
	private MenuButton mb;

	/**
	 * Primary handling of user inputs. Redistributes inputs to sub-methods based on
	 * the input object
	 **/
	@Override
	public void handle(ActionEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName());
		switch (e.getSource().getClass().getSimpleName()) {
		case "Button":
			button((Button) e.getSource());
			break;
		case "MenuItem":
			menuItem((MenuItem) e.getSource());
			break;
			
		default:
			break;
		}
	}

	/**
	 * Handles button inputs
	 * 
	 * @param Instance of button Class
	 **/
	public void button(Button b) {
		switch (b.getText()) {
		case "Exit":
			Stage s = (Stage) b.getScene().getWindow();
			s.close();
			break;
		case "Tutorial":
			OpenTutorial.open();
			break;
		default:
			break;
		}
	}

	/**
	 * Handles MenuItem inputs
	 * 
	 * @param Instance of MenuButton Class
	 **/
	public void menuItem(MenuItem item) {
		System.out.println(item.getParentPopup().getId());
	}

}
