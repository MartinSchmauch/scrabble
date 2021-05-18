package gui;

import java.io.File;
import game.GameSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mechanic.Letter;
import mechanic.Player;
import util.JsonHandler;

/**
 * 
 * Handles user-inputs in the Gamesettings screen.
 * 
 * @author nilbecke
 * 
 **/

public class SettingsScreenController implements EventHandler<ActionEvent> {

  private Player currentPlayer;
  private static SettingsScreenController instance;


  @FXML
  private Label username;
  @FXML
  private Label time;
  @FXML
  private Label overtime;
  @FXML
  private Label score;
  @FXML
  private Label size;
  @FXML
  private Label bingo;
  @FXML
  private Label ai;
  @FXML
  private Label dic0;
  @FXML
  private Label letter;
  @FXML
  private Label letterValue;
  @FXML
  private Label letterAmount;
  @FXML
  private Label valid;
  @FXML
  private ImageView avatar;
  @FXML
  private Button user;
  @FXML
  private Button tpp;
  @FXML
  private Button tppDown;
  @FXML
  private Button tppUp;
  @FXML
  private Button mo;
  @FXML
  private Button moDown;
  @FXML
  private Button moUp;
  @FXML
  private Button ms;
  @FXML
  private Button msDown;
  @FXML
  private Button msUp;
  @FXML
  private Button s;
  @FXML
  private Button sDown;
  @FXML
  private Button sUp;
  @FXML
  private Button b;
  @FXML
  private Button bDown;
  @FXML
  private Button bUp;
  @FXML
  private Button dif;
  @FXML
  private Button dic1;
  @FXML
  private Button dic2;
  @FXML
  private Button letterDown;
  @FXML
  private Button letterUp;
  @FXML
  private Button valueDown;
  @FXML
  private Button valueUp;
  @FXML
  private Button amountDown;
  @FXML
  private Button amountUp;
  @FXML
  private TextField tpptf;
  @FXML
  private TextField motf;
  @FXML
  private TextField mstf;
  @FXML
  private TextField stf;
  @FXML
  private TextField btf;


  /**
   * Initialize the Settings Screen with username, avatar and all current settings
   */

  @FXML
  public void initialize() {
    instance = this;
    this.currentPlayer = LobbyScreenController.getLobbyInstance().getPlayer();
    this.username.setText(this.currentPlayer.getNickname());
    this.avatar.setImage(
        new Image(getClass().getResource(this.currentPlayer.getAvatar()).toExternalForm()));


    setUpLabels();

  }

  /**
   * Handles all user inputs.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "user":
        showUserProfile();
        break;
      case "tppUp":
        updateLabel(this.time, Integer.parseInt(time.getText()) + 1);
        break;
      case "tppDown":
        updateLabel(this.time, Integer.parseInt(time.getText()) - 1);
        break;
      case "tpp":
        labelTextfield(this.time, this.tpptf, (Button) e.getSource());
        break;
      case "moDown":
        updateLabel(this.overtime, Integer.parseInt(overtime.getText()) - 1);
        break;
      case "moUp":
        updateLabel(this.overtime, Integer.parseInt(overtime.getText()) + 1);
        break;
      case "mo":
        labelTextfield(this.overtime, this.motf, (Button) e.getSource());
        break;
      case "msUp":
        updateLabel(this.score, Integer.parseInt(score.getText()) + 1);
        break;
      case "msDown":
        updateLabel(this.score, Integer.parseInt(score.getText()) - 1);
        break;
      case "ms":
        labelTextfield(this.score, this.mstf, (Button) e.getSource());
      case "sUp":
        updateLabel(this.size, Integer.parseInt(size.getText()) + 1);
        break;
      case "sDown":
        updateLabel(this.size, Integer.parseInt(size.getText()) - 1);
        break;
      case "s":
        labelTextfield(this.size, this.stf, (Button) e.getSource());
        break;
      case "bUp":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) + 1);
        break;
      case "bDown":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) - 1);
        break;
      case "b":
        labelTextfield(this.bingo, this.btf, (Button) e.getSource());
        break;
      case "letterUp":
        updateLetterLabel(this.letter.getText().charAt(0), true);
        break;
      case "letterDown":
        updateLetterLabel(this.letter.getText().charAt(0), false);
        break;
      case "valueUp":
        updateLabel(this.letterValue, Integer.parseInt(letterValue.getText()) + 1);
        updateValueOrCount();
        break;
      case "valueDown":
        updateLabel(this.letterValue, Integer.parseInt(letterValue.getText()) - 1);
        updateValueOrCount();
        break;
      case "amountUp":
        updateLabel(this.letterAmount, Integer.parseInt(letterAmount.getText()) + 1);
        updateValueOrCount();
        break;
      case "amountDown":
        updateLabel(this.letterAmount, Integer.parseInt(letterAmount.getText()) - 1);
        updateValueOrCount();
        break;
      case "dif":
        changeAi();
        break;
      case "dic1":
        chooseFile();
        break;
      case "dic2":
        System.out.println(FileParameters.datadir + GameSettings.getDictionary());
        OpenExternalScreen.open(FileParameters.datadir + GameSettings.getDictionary());
        break;
      case "restore":
        new JsonHandler()
            .loadGameSettings(new File(FileParameters.datadir + "defaultGameSettings.json"));
        setUpLabels();
        break;
      case "exit":
      case "save":
        saveSettings();
        Button b = (Button) e.getSource();
        Stage st = (Stage) (b.getScene().getWindow());
        st.close();
        break;

      default:
        break;
    }
  }

  /**
   * Saves the settings upon closing the window.
   */
  public void saveSettings() {
    GameSettings.setTimePerPlayer(Integer.parseInt(time.getText()));
    GameSettings.setMaxOvertime(Integer.parseInt(overtime.getText()));
    GameSettings.setMaxScore(Integer.parseInt(score.getText()));
    GameSettings.setGameBoardSize(Integer.parseInt(size.getText()));
    GameSettings.setBingo(Integer.parseInt(bingo.getText()));
    new JsonHandler()
        .saveGameSettings(new File(FileParameters.datadir + "customGameSettings.json"));

  }

  /**
   * This method is used for changing settings by using a textfield to commit changes.
   * 
   * @param lbl defines the label to be updated.
   * @param tf defines the Textfield with the expected changes.
   * @param trigger defines the button pushed to invoke the action.
   */
  public void labelTextfield(Label lbl, TextField tf, Button trigger) {
    // Label to Textfield
    if (trigger.getText().equals("Change")) {
      trigger.setText("OK");
      lbl.setOpacity(0);
      tf.setOpacity(1);
      tf.setText(lbl.getText());
    } else { // Textfield to Label
      trigger.setText("Change");
      tf.setOpacity(0);
      try {
        Integer.parseInt(tf.getText());
      } catch (NumberFormatException e) {
        tf.setText(lbl.getText());
      }
      if (!tf.getText().equals("")) {
        lbl.setText(tf.getText());
      }
      lbl.setOpacity(1);
    }
  }

  /**
   * Shows the user his profile without the possibility to make changes. Gets called if the user
   * clicks on his username in the game settings screen
   */
  public void showUserProfile() {
    CustomAlert alert = new CustomAlert(AlertType.INFORMATION);
    alert.show();
  }

  /**
   * Set up all labels etc.
   */
  public void setUpLabels() {
    this.time.setText(GameSettings.getTimePerPlayer() + "");
    this.overtime.setText(GameSettings.getMaxOvertime() + "");
    this.score.setText(GameSettings.getMaxScore() + "");
    this.size.setText(GameSettings.getGameBoardSize() + "");
    this.bingo.setText(GameSettings.getBingo() + "");
    this.ai.setText(GameSettings.getAiDifficulty().substring(0, 1).toUpperCase()
        + GameSettings.getAiDifficulty().substring(1));
    this.dic0.setText(GameSettings.getDictionary());
  }

  /**
   * Lets a user choose a new dictionary to be used
   */
  public void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Dictionary");
    File f = fileChooser.showOpenDialog(new Stage());
    if (f != null && f.getPath().equals("*.txt")) {
      GameSettings.setDictionary(f.getPath());
      this.valid.setOpacity(0);
    } else {
      this.valid.setOpacity(1);
    }
  }

  /**
   * Changes the AI Difficulty between easy, medium, hard and unbeatable.
   */
  public void changeAi() {
    switch (this.ai.getText()) {
      case "Easy":
        this.ai.setText("Medium");
        GameSettings.setAiDifficulty("MEDIUM");
        break;
      case "Medium":
        this.ai.setText("Hard");
        GameSettings.setAiDifficulty("HIGH");
        break;
      case "Hard":
        this.ai.setText("Unbeatable");
        GameSettings.setAiDifficulty("Unbeatable");
        break;
      case "Unbeatable":
        this.ai.setText("Easy");
        GameSettings.setAiDifficulty("LOW");
        break;
      default:
        break;
    }
  }

  /**
   * Updates the value and count of letters after performing a change. The changes will be saved in
   * the letters Hashmap.
   */
  public void updateValueOrCount() {
    GameSettings.getLetters().put(this.letter.getText().charAt(0),
        new Letter(this.letter.getText().charAt(0), Integer.parseInt(letterValue.getText()),
            Integer.parseInt(letterAmount.getText())));
  }

  /**
   * Lets a user cycle through all letters to be able to change amount and value for this letter.
   * 
   * @param current the currently selected character out of [A-Z].
   * @param direction indicates if the letter is to be incremented or decremented.
   */

  public void updateLetterLabel(Character current, boolean direction) {
    if (direction) {
      if (current < 'Z') {
        current++;
      } else {
        current = 'A';
      }
    } else {
      if (current > 'A') {
        current--;
      } else {
        current = 'Z';
      }
    }
    this.letter.setText(current + "");
    this.letterValue.setText(GameSettings.getLetters().get(current).getLetterValue() + "");
    this.letterAmount.setText(GameSettings.getLetters().get(current).getCount() + "");
  }

  /**
   * Update a label.
   * 
   * @param update new Time per player.
   * @param toBeUpdated Label to be changed
   */
  public void updateLabel(Label toBeUpdated, int update) {
    if (update >= 0) {
      toBeUpdated.setText(update + "");
    }
  }

  /**
   * Get a reference on the current Game settings controller.
   * 
   * @return: Current instance of the settings controller if present, null otherwise
   */
  public static SettingsScreenController getInstance() {
    return instance;
  }

  /**
   * Updates the displayed username.
   * 
   * @param newName: updated username
   */
  public void setUserLabel(String newName) {
    this.username.setText(newName);
  }
}
