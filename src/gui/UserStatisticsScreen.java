package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.PlayerData;

/**
 * This Class launches the user statistics screen displaying all user connected game data.
 * 
 * @author nilbecke
 *
 */

public class UserStatisticsScreen extends Application {

  private double xoffset;
  private double yoffset;

  private PlayerData player;
  private static UserStatisticsScreen instance;
  private static boolean login;

  /**
   * This constructor gurantees that player statistics are shown for the correct player.
   * 
   * @param current indicates the player whomst statistics are to be shown.
   * @param from Lobby indicates if the screen is launched from the loginscreen
   */

  public UserStatisticsScreen(PlayerData current, boolean fromLobby) {
    this.player = current;
    instance = this;
    login = fromLobby;
  }


  @Override
  public void start(Stage stage) {
    if (this.player.getPlayerStatistics().getGameCount() == 0) {
      CustomAlert alert = new CustomAlert(AlertType.ERROR);
      alert.setHeaderText("No Games played yet!");
      alert.setContentText("At least one game has to be played to display statistics.");
      alert.show();
      return;
    }

    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserStatisticsScreen.fxml"));
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
  public PlayerData getPlayer() {
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

  public static boolean getLobby() {
    return login;
  }

}
