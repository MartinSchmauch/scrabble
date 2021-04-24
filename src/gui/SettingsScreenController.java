package gui;

import game.GameSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Player;

/**
 * 
 * Handles user-inputs in the Gamesettings screen.
 * 
 * @author nilbecke
 * 
 **/

public class SettingsScreenController implements EventHandler<ActionEvent> {

  private Player currentPlayer;
  private static SettingsScreenController instance;
  private static GameSettings settings;

  @FXML
  private Label username, time, overtime, score, size, bingo, ai;
  @FXML
  private ImageView avatar;
  @FXML
  private Button user;

  /**
   * Initialize the Settings Screen with username, avatar and all current settings
   */

  @FXML
  public void initialize() {
    instance = this;
    this.currentPlayer = LobbyScreen.getInstance().getPlayer();
    this.username.setText(this.currentPlayer.getNickname());
    this.avatar
        .setImage(new Image("file:" + FileParameters.datadir + this.currentPlayer.getAvatar()));
    settings = SettingsScreen.getSettings();
    time.setText(settings.getTimePerPlayer() + "");
    overtime.setText(settings.getMaxOvertime() + "");
    score.setText(settings.getMaxScore() + "");
    size.setText(settings.getGameBoardSize() + "");
    bingo.setText(settings.getBingo() + "");
  }

  /**
   * Handles all user inputs.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    System.out.println(s);
    switch (s) {
      case "user":
        new UserSettingsScreen().start(new Stage());
        break;
      default:
        break;
    }
  }

  /**
   * Get a reference on the current Game settings controller.
   * 
   * @return: Current instance of the settings controller if present, null otherwise
   */
  public static SettingsScreenController getInstance() {
    return instance;
  }

  /**
   * Updates the displayed username.
   * 
   * @param newName: updated username
   */
  public void setUserLabel(String newName) {
    this.username.setText(newName);
  }
}
