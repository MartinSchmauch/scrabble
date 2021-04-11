package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;



import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Player;

/** @author nilbecke **/
/** Handles user-inputs in the Gamesettings screen **/

public class SettingsScreenController extends SettingsScreen implements EventHandler<ActionEvent> {
	
	private Player currentPlayer;

	@FXML
	private Label username;
	@FXML
	private ImageView avatar;
	
	/**
	 * Initialize the Settings Screen with username and avatar
	 */
	
	@FXML
	public void initialize() {
		this.currentPlayer= LobbyScreen.getPlayer();
		this.username.setText(currentPlayer.getNickname());
		this.avatar.setImage(new Image("file:"+FileParameters.datadir+this.currentPlayer.getAvatar()));
	}
	/**
	 * Handles all user inputs
	 */
	@Override
	public void handle(ActionEvent e) {
		String s = ((Node) e.getSource()).getId();
		switch (s) 	{
		case "user":
			new UserSettingsScreen().start(new Stage());
			break;
		}
	}
}
