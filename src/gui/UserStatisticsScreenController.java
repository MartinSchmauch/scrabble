package gui;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mechanic.Player;

/**
 * This Class handles all User Inputs concerning the user statistics screen launchable from the
 * LoginScreen. On this screen no user information can be changed.
 * 
 * @author nilbecke
 *
 */

public class UserStatisticsScreenController implements EventHandler<ActionEvent> {

  private UserStatisticsScreen uss;
  private Player player;
  private String[] stats =
      {"Total Score", "Wins", "Best Word", "Best Turn", "Played Tiles", "Good invested time"};
  private byte indicator = 0;

  @FXML
  private Label nickname;
  @FXML
  private ImageView avatar;
  @FXML
  private Label value1;
  @FXML
  private Label value2;
  @FXML
  private Label key;
  @FXML
  private Button next;
  @FXML
  private Button prev;
  @FXML
  private Button rules;


  /**
   * This methods sets up all label and images with user specified data.
   */
  public void initialize() {
    this.uss = UserStatisticsScreen.getInstance();
    this.player = uss.getPlayer();
    setUpLabels();
    this.avatar.setImage(new Image("file:" + FileParameters.datadir + this.player.getAvatar()));
  }


  @Override
  public void handle(ActionEvent e) {
    Button b = (Button) e.getSource();
    switch (b.getId()) {
      case "exit":
        Stage s = (Stage) b.getScene().getWindow();
        s.close();
        new LoginScreen().start(new Stage());
        break;
      case "rules":
        OpenExternalScreen
            .open(System.getProperty("user.dir") + "/src/gui/images/ScrabbleRules.pdf");
        break;
      case "prev":
        updateStatistics(false);
        break;
      case "next":
        updateStatistics(true);
        break;
      default:
        break;

    }
  }

  /**
   * This method updates the displayed statistics and is called when the user hits the "next" or
   * "previous" button.
   * 
   * @param increment indicates if the next or previous statistic shoud be shown
   */
  public void updateStatistics(boolean increment) {
    if (increment) {
      if (indicator < 5) {
        indicator++;
      } else {
        indicator = 0;
      }
    } else {
      if (indicator > 0) {
        indicator--;
      } else {
        indicator = 5;
      }
    }
    this.key.setText(this.stats[indicator]);
    this.value1.setText("Value1 for " + this.key.getText());
    this.value2.setText("Value2 for " + this.key.getText());
  }

  /**
   * This method sets up all Labels with username and the initial presented statistics.
   */
  private void setUpLabels() {

    this.nickname.setText(player.getNickname());
    this.key.setText(this.stats[indicator]);
    this.value1.setText("Value1 for " + this.key.getText());
    this.value2.setText("Value2 for " + this.key.getText());
  }
}
