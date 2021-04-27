package gui;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Player;



/**
 * @author nilbecke Opens the user settings menu
 **/

public class UserSettingsScreen extends Application {

  private Parent root;
  private static Player player;


  @FXML
  private Label nickname;
  @FXML
  private Label vol;
  @FXML
  private Slider volbar;
  @FXML
  private ImageView avatar, question;
  @FXML
  private TextField namefield;

  public UserSettingsScreen(Player currentPlayer) {
    player = currentPlayer;

  }

  /**
   * Reads the "UserSettingsScreen.fxml" file (@author nilbecke) to create the user settings Menu
   **/

  @Override
  public synchronized void start(Stage stage) {
    try {
      this.root = FXMLLoader.load(getClass().getResource("UserSettings.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      // stage.initStyle(StageStyle.UNDECORATED);
      stage.setTitle("User Settings");
      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Player getPlayer() {
    return player;
  }

}
