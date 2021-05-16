package gui;

import java.io.File;
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
 * This Class launches the user statistics screen displaying all user connected game data.
 * 
 * @author nilbecke
 *
 */

public class UserStatisticsScreen extends Application {

  private double xoffset;
  private double yoffset;

  private Player player;
  private static UserStatisticsScreen instance;

  /**
   * This constructor gurantees that player statistics are shown for the correct player.
   * 
   * @param current indicates the player whomst statistics are to be shown.
   */

  public UserStatisticsScreen(Player current) {
    this.player = current;
    instance = this;
  }


  @Override
  public void start(Stage stage) {

    try {
      Parent root = FXMLLoader
          .load(new File(FileParameters.fxmlPath + "UserStatisticsScreen.fxml").toURI().toURL());
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

      stage.setTitle("User Statistics");
      stage.showAndWait();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * This method gives a reference to the player whomst statistics are to be displayed.
   * 
   * @return the current player
   */
  public Player getPlayer() {
    return this.player;
  }

  /**
   * This method gives a reference to the current UserSettingsScreen
   * 
   * @return the current Screen
   */

  public static UserStatisticsScreen getInstance() {
    return instance;
  }

}
