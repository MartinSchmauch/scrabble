package gui;

import java.io.File;
import game.GameSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mechanic.Letter;
import mechanic.Player;
import util.JSONHandler;

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
  private static GameSettings settings;

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

  /**
   * Initialize the Settings Screen with username, avatar and all current settings
   */

  @FXML
  public void initialize() {
    instance = this;
    this.currentPlayer = LobbyScreen.getInstance().getPlayer();
    this.username.setText(this.currentPlayer.getNickname());
    this.avatar
        .setImage(new Image("file:" + FileParameters.datadir + this.currentPlayer.getAvatar()));
    settings = SettingsScreen.getSettings();
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
        new UserSettingsScreen().start(new Stage());
        break;
      case "tppUp":
        updateLabel(this.time, Integer.parseInt(time.getText()) + 1);
        break;
      case "tppDown":
        updateLabel(this.time, Integer.parseInt(time.getText()) - 1);
        break;
      case "moDown":
        updateLabel(this.overtime, Integer.parseInt(overtime.getText()) - 1);
        break;
      case "moUp":
        updateLabel(this.overtime, Integer.parseInt(overtime.getText()) + 1);
        break;
      case "msUp":
        updateLabel(this.score, Integer.parseInt(score.getText()) + 1);
        break;
      case "msDown":
        updateLabel(this.score, Integer.parseInt(score.getText()) - 1);
        break;
      case "sUp":
        updateLabel(this.size, Integer.parseInt(size.getText()) + 1);
        break;
      case "sDown":
        updateLabel(this.size, Integer.parseInt(size.getText()) - 1);
        break;
      case "bUp":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) + 1);
        break;
      case "bDown":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) - 1);
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
        OpenExternalScreen.open(settings.getDictionary());
        break;
      case "restore":
        settings = new JSONHandler().loadGameSettings("resources/defaultGameSettings.json");
        setUpLabels();
      default:
        break;
    }
  }

  /**
   * 
   */
  public void setUpLabels() {
    this.time.setText(settings.getTimePerPlayer() + "");
    this.overtime.setText(settings.getMaxOvertime() + "");
    this.score.setText(settings.getMaxScore() + "");
    this.size.setText(settings.getGameBoardSize() + "");
    this.bingo.setText(settings.getBingo() + "");
    this.ai.setText(settings.getAiDifficulty().substring(0, 1).toUpperCase()
        + settings.getAiDifficulty().substring(1));
    this.dic0.setText(settings.getDictionary());
  }

  /**
   * Lets a user choose a new dictionary to be used
   */
  public void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Dictionary");
    File f = fileChooser.showOpenDialog(new Stage());
    if (f != null && f.getPath().equals("*.csv")) {
      settings.setDictionary(f.getPath());
      this.valid.setOpacity(0);
    } else {
      this.valid.setOpacity(1);
    }
  }

  /**
   * Changes the AI Difficulty between easy and hard.
   */
  public void changeAi() {
    if (this.ai.getText().equals("Easy")) {
      this.ai.setText("Hard");
      settings.setAiDifficulty("hard");
    } else {
      this.ai.setText("Easy");
      settings.setAiDifficulty("easy");
    }
  }

  /**
   * Updates the value and count of letters after performing a change. The changes will be saved in
   * the letters Hashmap.
   */
  public void updateValueOrCount() {
    settings.getLetters().put(this.letter.getText().charAt(0),
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
    this.letterValue.setText(settings.getLetters().get(current).getLetterValue() + "");
    this.letterAmount.setText(settings.getLetters().get(current).getCount() + "");
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
