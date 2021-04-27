package gui;

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import util.JsonHandler;

/**
 * This class handles all user based action in the User Settings Screen like changing nocknam,
 * avatar and volume.
 * 
 * @author nilbecke
 **/

public class UserSettingsScreenController implements EventHandler<ActionEvent> {

  private Player player;

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


  public void initialize() {
    this.player = new JsonHandler().loadPlayerProfile("resources/playerProfileTest.json");
    setUp();
  }

  /**
   * Main handling method of button based user inputs.
   * 
   * @param e user input event
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
        this.player.setNickname(this.namefield.getText());
        new JsonHandler().savePlayerProfile("resources/playerProfileTest.json", this.player);
        updateLoginScreen();
        if (SettingsScreenController.getInstance() != null) {
          SettingsScreenController.getInstance().setUserLabel(this.player.getNickname());
        }
        s.close();
        break;
      case "tut":
        OpenExternalScreen
            .open(System.getProperty("user.dir") + "/src/gui/images/ScrabbleRules.pdf");
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
    this.nickname.setText(this.player.getNickname());
    this.volbar.setValue((double) this.player.getVolume());
    this.vol.setText((int) this.volbar.getValue() + "");
    this.avatar.setImage(new Image("file:" + FileParameters.datadir + this.player.getAvatar()));
    this.namefield.setText(this.player.getNickname());
  }


  /**
   * Gets called if a player wants to delete his current profile. His profile will be replaced by
   * the default profile.
   * 
   */
  public void deletePlayerProfile() {

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Confirm Delete");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to delete your Profile?");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      JsonHandler jh = new JsonHandler();
      this.player = jh.loadPlayerProfile("resources/playerProfile.json");
      jh.savePlayerProfile("resources/playerProfileTest.json", this.player);
      updateLoginScreen();
      setUp();

    } else {
      alert.close();
    }

  }

  /**
   * Saves all changes to the LoginScreen
   */
  public void updateLoginScreen() {
    LoginScreenController.getInstance().setUsername(this.player.getNickname());
    LoginScreenController.getInstance()
        .setAvatar("file:" + FileParameters.datadir + this.player.getAvatar());
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
    cu.setText("Save");
  }

  /**
   * Sets the Usename to a previous user input given in the labelToTextfield method.
   **/

  public void textfieldToLabel() {
    this.namefield.setOpacity(0);
    if (this.namefield.getText().length() > 0) {
      this.nickname.setText(this.namefield.getText());
      this.player.setNickname(this.nickname.getText());
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
    this.player.setAvatar("/avatar" + this.currentAvatar + ".png");
    this.avatar.setImage(new Image("file:" + FileParameters.datadir + this.player.getAvatar()));
  }

  /**
   * Opens a dialog window for user inputs (unused).
   * 
   * @param title of the dialog window
   * @param oldValue Old value now to be changed
   * @param content of the subject to be changed
   * @return changed content
   */

  public String openInputDialog(String title, String oldValue, String content) {
    TextInputDialog dialog = new TextInputDialog(oldValue);
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.setContentText(content);
    Optional<String> result = dialog.showAndWait();

    System.out.println(this.player.getNickname());
    if (result.isPresent()) {
      return result.get();
    }
    return nickname.getText();
  }
}
