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

  @FXML
  private Label nickname;
  @FXML
  private ImageView avatar;


  /**
   * This methods sets up all label and images with user specified data.
   */
  public void initialize() {
    this.uss = UserStatisticsScreen.getInstance();
    this.player = uss.getPlayer();
    this.nickname.setText(player.getNickname());
    this.avatar.setImage(new Image("file:" + FileParameters.datadir + this.player.getAvatar()));
  }

  @Override
  public void handle(ActionEvent e) {
    Button b = (Button) e.getSource();
    switch (b.getId()) {
      case "exit":
        Stage s = (Stage) b.getScene().getWindow();
        s.close();
        break;
      default:
        break;

    }
  }
}
