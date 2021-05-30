package gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;

/**
 * This Class launches the Settings Screen.
 *
 * @author nilbecke
 */

public class SettingsScreen extends Application {

  private static boolean disable;
  private double xoffset;
  private double yoffset;



  /**
   * Set up the Settings Screen with an instance of the GameSettings class.
   */
  public SettingsScreen(Player p, boolean lobby) {
    if (p.isHost() == false || lobby == false) {
      disable = true;
    } else {
      disable = false;
    }
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
   * indicates whether the buttons are diabled or not.
   *
   * @return disable is true if buttons are disabled, false otherwise.
   */

  public static boolean getDisable() {
    return disable;
  }

  /**
   * Access the current Gamesettingsscreen.
   *
   * @return Current instance of Gamesettings Screen
   */
  public SettingsScreen getSettingScreen() {
    return this;
  }

}
