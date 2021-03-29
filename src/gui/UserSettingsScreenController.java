package gui;

/** 
 * @author nilbecke 
 * Action-Handler of the User Setting Screen
 * **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

import game.*;
import mechanic.*;
import util.JSONHandler;

public class UserSettingsScreenController extends UserSettingsScreen implements EventHandler<ActionEvent> {

	@FXML
	private Label nickname, vol;
	@FXML
	private TextField namefield;
	@FXML
	private Button cu;
	@FXML
	private Slider volbar;

	/** Main handling method of button based user inputs **/

	@Override
	public void handle(ActionEvent e) {
		Button b = (Button) e.getSource();
		switch (b.getId()) {
		case "cu":
			if (cu.getText().equals("Change Username")) {
				labelToTextfield();
			} else {
				textfieldToLabel();
			}
			break;
		case "volume":
			this.player.setVolume((int)this.volbar.getValue());
			break;
		case "save":
			new JSONHandler().savePlayerProfile("resources/playerProfileTest.json", this.player);
			break;
		}
	}

	/** Adds volume adaptions to volume label under volume bar **/

	public void slider() {
		this.vol.setText((int) this.volbar.getValue() + "");
	}

	/**
	 * Allows the User to change his Username. This method deals with graphical
	 * pleasure
	 **/
	public void labelToTextfield() {
		this.namefield.setText(this.nickname.getText());
		this.namefield.setOpacity(1);
		this.nickname.setOpacity(0);
		cu.setText("Save");
	}

	/**
	 * Sets the Usename to a previous user input given in the labelToTextfield ethod
	 **/

	public void textfieldToLabel() {
		this.namefield.setOpacity(0);
		this.nickname.setText(this.namefield.getText());
		this.nickname.setOpacity(1);
		cu.setText("Change Username");
	}

	/**
	 * Opens a dialog window for user inputs (unused)
	 * 
	 * @param title     of the dialog window
	 * @param oldValue: Old value now to be changed
	 * @param content   of the subject to be changed
	 * @return changed content
	 */

	public String openInputDialog(String title, String oldValue, String content) {
		TextInputDialog dialog = new TextInputDialog(oldValue);
		dialog.setTitle(title);
		dialog.setHeaderText(null);
		dialog.setContentText(content);
		Optional<String> result = dialog.showAndWait();

		String newName = openInputDialog("Change Username", this.nickname.getText(), "Enter new Username here");
		this.nickname.setText(newName);
		this.player.setNickname(newName);

		System.out.println(this.player.getNickname());
		if (result.isPresent()) {
			return result.get();
		}
		return nickname.getText();
	}
}
