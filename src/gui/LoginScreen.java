package gui;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;
import util.JsonHandler;

/**
 * This Class launches the Login Screen of the Scrabble Application
 * 
 * @author nilbecke
 **/

public class LoginScreen extends Application {

  private Parent root;
  protected Player currentPlayer;
  protected boolean guest = false;
  private static boolean alreadyLaunched;


  @FXML
  private ImageView avatar;
  @FXML
  private Label username;


  /**
   * Set up the avatar picture before loginscreen is visible.
   */

  @FXML
  public void initialize() {

    if (new File(FileParameters.datadir + ("/playerProfileTest.json")).exists()) {

      currentPlayer = new JsonHandler().loadPlayerProfile("resources/playerProfileTest.json");
    } else {

      currentPlayer = new JsonHandler().loadPlayerProfile("resources/playerProfile.json");

      if (!alreadyLaunched) {
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);

        alert.setTitle("Create Player Profile");
        alert.setHeaderText("Seems like you have no profile created yet");
        alert.setContentText("Do you want to create a pofile?");
        alert.initStyle(StageStyle.UNDECORATED);

        alert.changeButtonText("Create Account", ButtonType.OK);
        alert.changeButtonText("Continue as Guest", ButtonType.CANCEL);

        alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("DialogPaneButtons.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          File f = new File(FileParameters.datadir + ("/playerProfileTest.json"));
          try {
            f.createNewFile();
          } catch (IOException e) {
            e.printStackTrace();
          }
          new UserSettingsScreen(currentPlayer).start(new Stage());
        } else {
          guest = true;
          alert.close();
        }
      }
    }

    this.username.setText(currentPlayer.getNickname());
    this.avatar.setImage(new Image("file:" + FileParameters.datadir + currentPlayer.getAvatar()));
  }

  /**
   * This method reads the "LoginScreenFXML.fxml" (@author nilbecke) file to create the Login
   * Screen.
   * 
   * @param stage stage to be set
   */

  @Override
  public void start(Stage stage) {

    Font.loadFont(getClass().getResourceAsStream("Scrabble.ttf"), 14);
    try {
      this.root = FXMLLoader.load(getClass().getResource("LoginScreenFXML.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scene scene = new Scene(root);
    stage.setScene(scene);
    // stage.initStyle(StageStyle.UNDECORATED);
    stage.setTitle("Scrabble3");
    setFirstLaunch(true);
    stage.show();

  }

  public static void setFirstLaunch(boolean launch) {
    alreadyLaunched = true;
  }


  public Parent getParent() {
    return this.root;
  }

  public LoginScreen getLoginScreen() {
    return this;
  }

  public static void main(String[] args) {

    launch(args);
  }
}
