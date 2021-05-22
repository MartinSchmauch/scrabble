package gui;

import java.io.IOException;
import game.GameSettings;
/** @author nilbecke **/
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This Class launches the Settings Screen.
 *
 * @author nilbecke
 */

public class SettingsScreen extends Application {

  private static GameSettings settings;
  private double xoffset;
  private double yoffset;



  /**
   * Set up the Settings Screen with an instance of the GameSettings class.
   *
   * @param s Currently used settings
   */
  public SettingsScreen(GameSettings s) {
    settings = s;

  }

  /**
   * Reads the "SettingsScreen.fxml" file (@author nilbecke) to create the Settings Menu.
   **/

  @Override
  public void start(Stage stage) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/SettingsScreenFXML.fxml"));
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.initStyle(StageStyle.UNDECORATED);

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

      stage.setTitle("Settings");
      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Get the currently used game settings.
   *
   * @return currently used game settings
   */
  public static GameSettings getSettings() {
    return settings;
  }

  /**
   * Update the settings used.
   */
  public void setSettings(GameSettings s) {
    settings = s;
  }

  /**
   * Access the current Gamesettingsscreen.
   *
   * @return Current instance of Gamesettings Screen
   */
  public SettingsScreen getSettingScreen() {
    return this;
  }

  public static void main(String[] args) {
    launch(args);
  }

}
