package gui;

import java.io.File;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Player;
import util.FileParameters;
import util.JsonHandler;

/**
 * This class handles all user based action in the User Settings Screen like changing nocknam,
 * avatar and volume.
 *
 * @author nilbecke
 **/

public class UserSettingsScreenController implements EventHandler<ActionEvent> {

  private Player player;
  private static UserSettingsScreenController instance;

  @FXML
  private Label nickname;
  @FXML
  private Label vol;
  @FXML
  private TextField namefield;
  @FXML
  private Button cu;
  @FXML
  private Button next;
  @FXML
  private Button back;
  @FXML
  private Button delete;
  @FXML
  private Slider volbar;
  @FXML
  private ImageView avatar;

  // Holds which of the 10 avatars is currently selected.
  private int currentAvatar;

  /**
   * This method is called to initialize the screen controller.
   */
  public void initialize() {
    instance = this;
    this.player = UserSettingsScreen.getPlayer();
    setUp();
  }

  /**
   * Main handling method of button based user inputs.
   *
   * @param e user input event.
   **/

  @Override
  public void handle(ActionEvent e) {

    Button b = (Button) e.getSource();
    Stage s = (Stage) b.getScene().getWindow();
    switch (b.getId()) {
      case "cu":
        if (cu.getText().equals("Change Username")) {
          labelToTextfield();
        } else {
          textfieldToLabel();
        }
        break;
      // Save changes and exit screen
      case "save":
      case "exit":

        textfieldToLabel();
        new JsonHandler().savePlayerProfile(new File(FileParameters.datadir + "playerProfile.json"),
            this.player);

        if (SettingsScreenController.getInstance() != null) {
          SettingsScreenController.getInstance().setUserLabel(this.player.getNickname());
        }
        s.close();
        /*
         * @author pkoenig
         */
        Stage newLoginStage = new Stage();
        newLoginStage.setX(s.getScene().getWindow().getX());
        newLoginStage.setY(s.getScene().getWindow().getY());
        /*
         * @author nilbecke
         */
        new LoginScreen().start(newLoginStage);
        break;
      case "tut":
        OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
        break;
      case "next":
        updateAvatar(true);
        break;
      case "back":
        updateAvatar(false);
        break;
      case "delete":
        deletePlayerProfile();
        break;
      default:
        break;
    }
  }

  /**
   * Sets up all labels etc. Gets called when window is opened or user profile is reset to the
   * default profile
   */

  public void setUp() {
    if (this.player.getNickname().equals("Guest")) {
      this.player.setNickname("ScrabbleGamer");
      this.player.setAvatar("/avatars/avatar0.png");
    }
    this.nickname.setText(this.player.getNickname());
    this.volbar.setValue((double) this.player.getVolume());
    this.vol.setText((int) this.volbar.getValue() + "");
    this.avatar
        .setImage(new Image(getClass().getResource(this.player.getAvatar()).toExternalForm()));
    this.namefield.setText(this.player.getNickname());
  }


  /**
   * Gets called if a player wants to delete his current profile. His profile will be replaced by
   * the default profile.
   * 
   */
  public void deletePlayerProfile() {
    CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
    alert.setTitle("Are you sure?");
    alert.setHeaderText("Are you sure you want to delete your Profile?");
    alert.setContentText("The Application will close after deletion");

    alert.changeButtonText("Delete", ButtonType.OK);
    alert.changeButtonText("Cancel", ButtonType.CANCEL);



    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      JsonHandler jh = new JsonHandler();
      this.player =
          jh.loadPlayerProfile(new File(FileParameters.datadir + "defaultPlayerProfile.json"));
      new File(FileParameters.datadir + "playerProfile.json").delete();
      System.exit(0);
    } else {
      alert.close();
    }

  }

  /**
   * Saves all changes to the LoginScreen.
   */
  public void updateLoginScreen() {

    if (LoginScreenController.getInstance() != null) {
      LoginScreenController.getInstance().setUsername(this.player.getNickname());
      LoginScreenController.getInstance()
          .setAvatar(getClass().getResource(this.player.getAvatar()).toExternalForm());
    }
  }

  /** Adds volume adaptions to volume label under volume bar. **/

  public void slider() {
    this.vol.setText((int) this.volbar.getValue() + "");
    this.player.setVolume((int) this.volbar.getValue());
  }

  /**
   * Allows the User to change his Username. This method deals with graphical pleasure.
   **/
  public void labelToTextfield() {
    this.namefield.setText(this.nickname.getText());
    this.namefield.setOpacity(1);
    this.namefield.requestFocus();
    cu.setText("Save");
  }

  /**
   * Sets the Usename to a previous user input given in the labelToTextfield method.
   **/
  public void textfieldToLabel() {
    this.namefield.setOpacity(0);
    if (this.namefield.getText().length() > 0) {
      if (this.namefield.getText().substring(0, 2).contentEquals("AI")) {
        CustomAlert alert = new CustomAlert(AlertType.ERROR);
        alert.setTitle("Invalid name");
        alert.setHeaderText("Username not valid");
        alert.setContentText("Usernames starting with \"AI\" are reserved for AI");
        alert.showAndWait();
      } else {
        this.nickname.setText(this.namefield.getText());
        this.player.setNickname(this.nickname.getText());
      }
    } else {
      CustomAlert alert = new CustomAlert(AlertType.INFORMATION);
      alert.setTitle("Invalid name");
      alert.setHeaderText("Username not valid");
      alert.setContentText("Username reset, because you entered nothing as username.");
      alert.showAndWait();
    }
    cu.setText("Change Username");
  }

  /**
   * Updates the avatar picture of the current player.
   *
   * @param increment true if picture is to be incremented, false if it should be decremented
   */
  public void updateAvatar(boolean increment) {
    this.currentAvatar = Character
        .getNumericValue(this.player.getAvatar().charAt(this.player.getAvatar().length() - 5));
    if (increment) {
      if (this.currentAvatar < 9) {
        this.currentAvatar++;
      } else {
        this.currentAvatar = 0;
      }
    } else {
      if (this.currentAvatar > 0) {
        this.currentAvatar--;
      } else {
        this.currentAvatar = 9;
      }
    }
    this.player.setAvatar("/avatars/avatar" + this.currentAvatar + ".png");
    this.avatar
        .setImage(new Image(getClass().getResource(this.player.getAvatar()).toExternalForm()));
  }
  
  /**
   * This method disables the buttons for non-hosts.
   */

  public void disableDeletion() {
    this.delete.setDisable(true);
  }
  
  /**
   * This method gives the current user settings controller instance.
   *
   * @return instance as the current instance.
   */

  public static UserSettingsScreenController getInstance() {
    return instance;
  }

  
}
