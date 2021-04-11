package gui;

import java.awt.image.BufferedImage;

/** 
 * @author nilbecke 
 * Opens the user settings menu
 * **/

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import mechanic.Player;
import util.JSONHandler;

public class UserSettingsScreen extends Application {

	private Parent root;
	protected Player player;

	@FXML
	private Label nickname, vol;
	@FXML
	private Slider volbar;
	@FXML
	private ImageView avatar,question;
	@FXML
	private TextField namefield;
	

	/**
	 * initializes the user settings menu by reading json file of the local player
	 * containing all user specified settings
	 **/
	
	@FXML
	public void initialize() {
		this.player = new JSONHandler().loadPlayerProfile("resources/playerProfileTest.json");
		this.nickname.setText(this.player.getNickname());
		this.volbar.setValue((double)this.player.getVolume());
		this.vol.setText((int)this.volbar.getValue()+"");
		this.avatar.setImage(new Image("file:"+FileParameters.datadir+this.player.getAvatar()));	
		this.namefield.setText(this.player.getNickname());
	}
	

	/**
	 * Reads the "UserSettingsScreen.fxml" file (@author nilbecke) to create the
	 * user settings Menu
	 **/

	@Override
	public synchronized void start(Stage stage) {
		try {
			this.root = FXMLLoader.load(getClass().getResource("UserSettings.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			// stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("User Settings");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
