package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mechanic.Player;
import mechanic.PlayerData;

/**
 * This class launches the Lobby UI as a host or as a client.
 * 
 * @author nilbecke
 **/

public class LobbyScreen extends Application {

  private Parent root;
  private static Player player;
  private static LobbyScreen instance;
  List<PlayerData> players;

  /**
   * Launches the Lobby Screen and hosts a game/ connects to a game.
   * 
   * @param current Player connetcting/hosting
   * @param connection "Game Link"
   */

  public LobbyScreen(Player current, String connection) {
    instance = this;
    player = current;
    if (player.isHost()) {
      player.host();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      if (connection.equals("")) {
        try {
          player.connect(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
      } else {
        try {
          player.connect(connection);

        } catch (Exception e) {
          System.out.println("Kein Host unter dieser Adresse");
        }
      }
    }

  }


  /**
   * Reads the "Lobby.fxml" file (@author nilbecke) to create the Lobby.
   **/
  @Override
  public void start(Stage stage) {
    try {
      Font.loadFont(getClass().getResourceAsStream("Scrabble.ttf"), 14);
      this.root = FXMLLoader.load(getClass().getResource("Lobby.fxml"));
      stage.setOnCloseRequest(e -> close());
      Scene scene = new Scene(root);
      stage.setScene(scene);
      // stage.initStyle(StageStyle.UNDECORATED);
      stage.setTitle("Lobby");
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Closes the Lobby and stops the server.
   */
  public static void close() {
    if (LobbyScreenController.getLobbyInstance().getServer() != null) {
      LobbyScreenController.getLobbyInstance().getServer().stopServer();
    } else if (!player.isHost()) {
      player.getClientProtocol().disconnect();
    }
    new LoginScreen().start(new Stage());
  }

  /**
   * Passes on the instance of local current player.
   * 
   * @return Instance of current player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Reference to the current lobby.
   * 
   * @return Instance of Lobby
   */
  public static LobbyScreen getInstance() {
    return instance;
  }
}
