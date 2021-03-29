package gui;

/** @author nilbecke **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

import game.*;
import mechanic.*;
import util.JSONHandler;

public class UserSettingsScreenController extends UserSettingsScreen implements EventHandler<ActionEvent> {

	@FXML
	private Label nickname;

	@Override
	public void handle(ActionEvent e) {
		Button b = (Button) e.getSource();
		switch (b.getId()) {
		case "cu":
			String newName = openInputDialog("Change Username", this.nickname.getText(), "Enter new Username here");
			this.nickname.setText(newName);
			this.player.setNickname(newName);
			System.out.println(this.player.getNickname());
			break;
		case "save":
			new JSONHandler().savePlayerProfile("resources/playerProfileTest.json", this.player);
			break;
		}
	}

	/**
	 * Opens a dialog window for user inputs
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
		if (result.isPresent()) {
			return result.get();
		}
		return nickname.getText();
	}
}
