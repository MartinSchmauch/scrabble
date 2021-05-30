package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;


/**
 * Opens the user settings menu.
 *
 * @author nilbecke
 */

public class UserSettingsScreen extends Application {

  private Parent root;
  private static Player player;
  private double xoffset;
  private double yoffset;


  @FXML
  private Label nickname;
  @FXML
  private Label vol;
  @FXML
  private Slider volbar;
  @FXML
  private ImageView avatar;
  @FXML
  private ImageView question;
  @FXML
  private TextField namefield;
  
  /**
   * Genererates a new user settings screen. 
   *
   * @param currentPlayer as the current player.
   */

  public UserSettingsScreen(Player currentPlayer) {
    player = currentPlayer;

  }

  /**
   * Reads the "UserSettingsScreen.fxml" file (@author nilbecke) to create the user settings Menu
   **/

  @Override
  public synchronized void start(Stage stage) {

    try {
      this.root = FXMLLoader.load(getClass().getResource("/fxml/UserSettings.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initStyle(StageStyle.UNDECORATED);
      stage.getIcons().add(new Image(getClass().getResourceAsStream("/ScrabbleIcon.png")));

      root.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          xoffset = event.getSceneX();
          yoffset = event.getSceneY();
        }
      });
      root.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          stage.setX(event.getScreenX() - xoffset);
          stage.setY(event.getScreenY() - yoffset);
        }
      });

      stage.setTitle("User Settings");
      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Get the currently used player. 
   *
   * @return player as the current player.
   */

  public static Player getPlayer() {
    return player;
  }

}
