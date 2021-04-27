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



/**
 * @author nilbecke Opens the user settings menu
 **/

public class UserSettingsScreen extends Application {

  private Parent root;


  @FXML
  private Label nickname, vol;
  @FXML
  private Slider volbar;
  @FXML
  private ImageView avatar, question;
  @FXML
  private TextField namefield;



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
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
