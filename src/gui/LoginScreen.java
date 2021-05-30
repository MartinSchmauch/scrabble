package gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;
import util.JsonHandler;

/**
 * This Class launches the Login Screen of the Scrabble Application.
 *
 * @author nilbecke
 **/

public class LoginScreen extends Application {

  private Parent root;
  protected static Player currentPlayer;
  protected boolean guest = false;
  private boolean alreadyLaunched;
  private double xoffset;
  private double yoffset;



  @FXML
  private ImageView avatar;
  @FXML
  private Label username;

  /**
   * This method reads the "LoginScreenFXML.fxml" (@author nilbecke) file to create the Login
   * Screen.
   *
   * @param stage stage to be set
   */

  @Override
  public void start(Stage stage) {
    if (new File(FileParameters.datadir).mkdir()) {
      JsonHandler jsonHandler = new JsonHandler();
      InputStream in = getClass().getResourceAsStream("/defaultPlayerProfile.json");
      jsonHandler.writeFileFromStream(in, FileParameters.datadir + "defaultPlayerProfile.json");
      in = getClass().getResourceAsStream("/defaultGameSettings.json");
      jsonHandler.writeFileFromStream(in, FileParameters.datadir + "defaultGameSettings.json");
      in = getClass().getResourceAsStream("/CollinsScrabbleWords.txt");
      jsonHandler.writeFileFromStream(in, FileParameters.datadir + "CollinsScrabbleWords.txt");
      in = getClass().getResourceAsStream("/ScrabbleRules.pdf");
      jsonHandler.writeFileFromStream(in, FileParameters.datadir + "ScrabbleRules.pdf");
    }

    Font.loadFont(getClass().getResourceAsStream("/Scrabble.ttf"), 14);

    if (new File(FileParameters.datadir + "playerProfile.json").exists()) {
      currentPlayer = new JsonHandler()
          .loadPlayerProfile(new File(FileParameters.datadir + "playerProfile.json"));
      setFirstLaunch(true);
    } else {
      currentPlayer = new JsonHandler()
          .loadPlayerProfile(new File(FileParameters.datadir + "defaultPlayerProfile.json"));
      if (!alreadyLaunched) {

        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);

        alert.setTitle("Create Player Profile");
        alert.setHeaderText("Seems like you have no profile created yet");
        alert.setContentText("Do you want to create a pofile?");
        alert.initStyle(StageStyle.UNDECORATED);

        alert.changeButtonText("Create Account", ButtonType.OK);
        alert.changeButtonText("Continue as Guest", ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          File f = new File(FileParameters.datadir + "playerProfile.json");
          try {
            f.createNewFile();
          } catch (IOException e) {
            e.printStackTrace();
          }
          new UserSettingsScreen(currentPlayer).start(new Stage());
          return;
        } else {
          guest = true;
          alert.close();
        }
      }
    }

    try {
      this.root =
          FXMLLoader.load(getClass().getClassLoader().getResource("fxml/LoginScreenFXML.fxml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
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

    stage.setTitle("Scrabble3");
    stage.show();
    setFirstLaunch(true);
  }

  /**
   * Set up if the applictaion was launched before.
   *
   * @param launch indicates launch.
   */

  public void setFirstLaunch(boolean launch) {
    alreadyLaunched = true;
  }

  /**
   * Get the application parent.
   *
   * @return root as the Parent.
   */
  public Parent getParent() {
    return this.root;
  }

  /**
   * Get the loginscreen instance.
   *
   * @return this as the current instance.
   */

  public LoginScreen getLoginScreen() {
    return this;
  }

  public static void main(String[] args) {

    launch(args);
  }

}
