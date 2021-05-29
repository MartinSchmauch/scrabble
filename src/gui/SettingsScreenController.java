package gui;

import game.GameSettings;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mechanic.Letter;
import mechanic.Player;
import network.messages.UpdateChatMessage;
import util.JsonHandler;

/**
 * Handles user-inputs in the Gamesettings screen.
 *
 * @author nilbecke
 * 
 **/

public class SettingsScreenController implements EventHandler<ActionEvent> {

  private Player currentPlayer;
  private boolean changed;
  private List<Character> letters;
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
  private Label tor;
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
  private Button ms;
  @FXML
  private Button msDown;
  @FXML
  private Button msUp;
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
  private Button torUp;
  @FXML
  private Button torDown;
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
  private Button restore;
  @FXML
  private Button save;
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
  @FXML
  private Tooltip tooltip;


  /**
   * Initialize the Settings Screen with username, avatar and all current settings.
   */
  @FXML
  public void initialize() {
    tooltip.setText(GameSettings.getDictionary());
    instance = this;
    this.changed = false;
    this.currentPlayer = LobbyScreenController.getLobbyInstance().getPlayer();
    this.letters = new ArrayList<Character>();
    for (Letter l : GameSettings.getLetters().values()) {
      this.letters.add(l.getCharacter());
    }
    Collections.sort(this.letters);
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

    if (!s.equals("exit") && !s.equals("save") && !s.equals("letterUp")
        && !s.equals("letterDown")) {
      changed = true;
    }

    switch (s) {
      case "user":
        // showUserProfile();
        break;
      case "tppUp":
        updateLabel(this.time, Integer.parseInt(time.getText()) + 10);
        break;
      case "tppDown":
        updateLabel(this.time, Integer.parseInt(time.getText()) - 10);
        break;
      case "tpp":
      case "tpptf":
        labelTextfield(this.time, this.tpptf, tpp);
        break;
      case "msUp":
        if (this.score.getText().equals("\u221e")) {
          this.score.setText("0");
        } else {
          updateLabel(this.score, Integer.parseInt(score.getText()) + 10);
        }
        break;
      case "msDown":
        if (this.score.getText().equals("\u221e")) {
          break;
        } else if (Integer.parseInt(this.score.getText()) - 10 < 0) {
          this.score.setText("\u221e");
        } else {
          updateLabel(this.score, Integer.parseInt(score.getText()) - 10);
        }
        break;
      case "ms":
      case"mstf":
        labelTextfield(this.score, this.mstf, ms);
        break;
      case "bUp":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) + 1);
        break;
      case "bDown":
        updateLabel(this.bingo, Integer.parseInt(bingo.getText()) - 1);
        break;
      case "b":
      case "btf":
        labelTextfield(this.bingo, this.btf, this.b);
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
        OpenExternalScreen.open(GameSettings.getDictionary());
        break;
      case "torUp":
        updateTilesOnRack(1);
        break;
      case "torDown":
        updateTilesOnRack(-1);
        break;
      case "restore":
        new JsonHandler()
            .loadGameSettings(new File(FileParameters.datadir + "defaultGameSettings.json"));
        this.currentPlayer.setCustomGameSettings(null);
        setUpLabels();
        break;
      case "exit":
      case "save":
        if (changed) {
          saveSettings();
        }
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
    if (this.score.getText().equals("\u221e")) {
      GameSettings.setMaxScore(-1);
    } else {
      GameSettings.setMaxScore(Integer.parseInt(score.getText()));
    }
    GameSettings.setBingo(Integer.parseInt(bingo.getText()));
    GameSettings.setTilesOnRack(Integer.parseInt(this.tor.getText()));
    new JsonHandler()
        .saveGameSettings(new File(FileParameters.datadir + "customGameSettings.json"));
    this.currentPlayer.setCustomGameSettings(FileParameters.datadir + "customGameSettings.json");
    new JsonHandler().savePlayerProfile(new File(FileParameters.datadir + "playerProfile.json"),
        this.currentPlayer);

    this.currentPlayer.getServer().sendLobbyStatus();
    this.currentPlayer.getServer()
        .sendToAll(new UpdateChatMessage("", "Game's settings were updated!", null));
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
      tf.requestFocus();
    } else { // Textfield to Label
      trigger.setText("Change");
      tf.setOpacity(0);
      int newValue = -1;
      try {
        newValue = Integer.parseInt(tf.getText());
      } catch (NumberFormatException e) {
        tf.setText(lbl.getText());
      }
      if (!tf.getText().equals("") && newValue >= 0) {
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
    this.tor.setText(GameSettings.getTilesOnRack() + "");
    if (GameSettings.getMaxScore() < 0) {
      this.score.setText("\u221e");
    } else {
      this.score.setText(GameSettings.getMaxScore() + "");
    }
    this.bingo.setText(GameSettings.getBingo() + "");
    String input = GameSettings.getAiDifficulty();
    switch (input) {
      case "EASY":
        this.ai.setText("Easy");
        break;
      case "MEDIUM":
        this.ai.setText("Medium");
        break;
      case "HARD":
        this.ai.setText("Hard");
        break;
      default:
        this.ai.setText("Unbeatable");
        break;
    }

    this.dic0.setText(GameSettings.getDictionary());
    this.letter.setText(GameSettings.getLetters().get('A').getCharacter() + "");
    this.letterValue.setText(GameSettings.getLetters().get('A').getLetterValue() + "");
    this.letterAmount.setText(GameSettings.getLetters().get('A').getCount() + "");

    if (SettingsScreen.getDisable()) {
      tpp.setDisable(true);;
      tppDown.setDisable(true);
      tppUp.setDisable(true);
      ms.setDisable(true);
      msDown.setDisable(true);
      msUp.setDisable(true);
      b.setDisable(true);
      bDown.setDisable(true);
      bUp.setDisable(true);
      dif.setDisable(true);
      dic1.setDisable(true);
      dic2.setDisable(true);
      torUp.setDisable(true);
      torDown.setDisable(true);
      valueDown.setDisable(true);
      valueUp.setDisable(true);
      amountDown.setDisable(true);
      amountUp.setDisable(true);
      save.setText("Done");
      restore.setDisable(true);
      restore.setOpacity(0);
    }
  }

  /**
   * Lets a user choose a new dictionary to be used.
   */
  public void chooseFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Dictionary");
    try {
      File f = fileChooser.showOpenDialog(new Stage());
      String extension = f.getPath().substring(f.getPath().length() - 3);
      if (f != null && extension.equals("txt")) {
        GameSettings.setDictionary(f.getPath());
        this.valid.setOpacity(0);
      } else {
        this.valid.setOpacity(1);
      }
    } catch (NullPointerException e) { // No file selected.
      return;
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
        GameSettings.setAiDifficulty("HARD");
        break;
      case "Hard":
        this.ai.setText("Unbeatable");
        GameSettings.setAiDifficulty("UNBEATABLE");
        break;
      case "Unbeatable":
        this.ai.setText("Easy");
        GameSettings.setAiDifficulty("EASY");
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
    GameSettings.getLetters().replace(this.letter.getText().charAt(0),
        new Letter(this.letter.getText().charAt(0), Integer.parseInt(letterValue.getText()),
            Integer.parseInt(letterAmount.getText())));

    if (this.letter.getText().charAt(0) == '*') {
      for (Letter l : GameSettings.getLetters().values()) {
        l.setJokerValue(Integer.parseInt(this.letterValue.getText()));
      }
    }
  }

  /**
   * Lets a user cycle through all letters to be able to change amount and value for this letter.
   *
   * @param current the currently selected character out of [A-Z].
   * @param direction indicates if the letter is to be incremented or decremented.
   */

  public void updateLetterLabel(char current, boolean direction) {
    int currentIndex = this.letters.indexOf(current);
    
    if (direction) {
      currentIndex++;

      if (currentIndex > this.letters.size() - 1) {
        current = this.letters.get(0);
      } else {
        current = this.letters.get(currentIndex);
      }
    } else {
      currentIndex--;

      if (currentIndex < 0) {
        current = this.letters.get(this.letters.size() - 1);
      } else {
        current = this.letters.get(currentIndex);
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
    } else {
      toBeUpdated.setText(0 + "");
    }
  }

  /**
   * updates the Tiles on the Rack at position input.
   */

  public void updateTilesOnRack(int input) {
    int newValue = Integer.parseInt(this.tor.getText()) + input;
    if (newValue <= 0) {
      return;
    } else if (newValue > 10) {
      newValue = 10;
    }
    updateLabel(this.tor, newValue);
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
   * @param newName updated username
   */
  public void setUserLabel(String newName) {
    this.username.setText(newName);
  }
}
