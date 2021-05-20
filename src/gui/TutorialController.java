package gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import game.GameController;
import game.GameSettings;
import game.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import mechanic.Letter;
import mechanic.Player;
import mechanic.Tile;

/**
 * @author nilbecke, mschmauc
 * 
 *         This class is a modification of the game panel controller to play the tutorial.
 */

public class TutorialController extends GamePanelController
    implements Sender, EventHandler<ActionEvent>, Runnable {

  private Player player;

  private static boolean exchangeTilesMode = false;
  private List<Tile> tilesToExchange = new ArrayList<Tile>();

  private ChatController cc;
  private static GameController gc;
  private GameState gs;
  private List<Tile> tiles;
  private int indicator = 0;


  @FXML
  private TextArea chat;
  @FXML
  private TextField chatInput;
  @FXML
  private Button sendButton, skipAndChangeButton, doneButton, leaveGameButton, settingsButton,
      rulesButton;
  @FXML
  private ImageView image1, image2, image3, image4;
  @FXML
  private Text player1, player2, player3, player4;
  @FXML
  private Text playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints;
  @FXML
  private Text pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4;
  @FXML
  private Text remainingLetters, timer;
  @FXML
  private Rectangle tile1;
  @FXML
  private Rectangle currentPlayer1, currentPlayer2, currentPlayer3, currentPlayer4;
  @FXML
  private Rectangle r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11;
  @FXML
  private GridPane board, rack;
  @FXML
  private ProgressBar timeProgress;
  @FXML
  private Rectangle backgroundGamePanel;

  private static TutorialController instance;

  public static TutorialController getInstance() {
    return instance;
  }

  /**
   * This method initializes the GamePanelController and is being called upon creation of the
   * Controller. Here all the labels on the UI are being reset and adapted to the current game
   * state.
   * 
   * @param player
   */
  public void initData(Player player) {
    this.player = player;
    cc = new ChatController(player);
    chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");

    Text[] playerLabel = {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    Text[] pointsLabel = {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    Text[] playerNameLabel = {player1, player2, player3, player4};
    ImageView[] avatarImageView = {image1, image2, image3, image4};


    List<Letter> letters = new ArrayList<Letter>(GameSettings.getLetters().values());
    Iterator<Letter> it = letters.iterator();

    this.tiles = new ArrayList<Tile>();
    while (it.hasNext()) {
      Letter l = it.next();
      this.tiles.add(new Tile(l));
    }

    leaveGameButton.setText("End Tutorial");
    playerNameLabel[0].setText(this.player.getNickname() + " (Host)");
    pointsLabel[0].setText("0");
    playerLabel[0].setText("Points: ");
    avatarImageView[0]
        .setImage(new Image(getClass().getResource(this.player.getAvatar()).toExternalForm()));

    remainingLetters.setText("");
    timer.setText("");
    timeProgress.setProgress(0.0);
  }

  /**
   * 
   * Listener methods that are executed upon Player UI Interaction
   * 
   */

  /**
   * Handles all button user inputs in the GamePanel
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "settingsButton":
        // TODO: any settings here to be adjusted?
        break;
      case "leaveGameButton":
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        if (player.isHost()) {

          alert.setHeaderText("End Tutorial?");
          alert.setContentText("Do you really want to end the tutorial?");

          alert.initStyle(StageStyle.UNDECORATED);

          alert.changeButtonText("Yes", ButtonType.OK);
          alert.changeButtonText("No", ButtonType.CANCEL);

          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {

            close(); // TODO: close method not neccesary anymore?
          }
        }
        break;
      case "rulesButton":
        OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
        break;
      case "sendButton":
      case "chatInput":
        this.chat.appendText("\n" + this.player.getNickname() + ": " + this.chatInput.getText());
        this.chatInput.setText("");
        break;
      case "skipAndChangeButton":
        if (!exchangeTilesMode) {
          exchangeTilesMode = true;
          changeSkipAndChangeStatus(false);
        }
        break;
      case "doneButton":
        if (exchangeTilesMode) {
          if (!this.tilesToExchange.isEmpty()) {
            CustomAlert alert2 = new CustomAlert(AlertType.CONFIRMATION);
            alert2.setTitle("Skip & Exchange selected tiles");
            alert2.setHeaderText("Skip & Exchange?");
            alert2.setContentText(
                "Do you want to skip the current turn and exchange the selected tiles ");
            alert2.initStyle(StageStyle.UNDECORATED);

            alert2.changeButtonText("Yes", ButtonType.OK);
            alert2.changeButtonText("No", ButtonType.CANCEL);

            Optional<ButtonType> result2 = alert2.showAndWait();
            if (result2.get() == ButtonType.OK) {
              // remove Tiles from GUI
              for (Tile t : this.tilesToExchange) {
                // TODO bei dem gesetzten True koennte ein Fehler entstehen
                this.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(), true);
                this.player.removeRackTile(t.getField().getxCoordinate());
              }
              sendTileMessage(this.player.getNickname());
            } else {
              alert2.close();
            }

            Rectangle rect[] = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
            for (Rectangle r : rect) {
              r.setStroke(Color.BLACK);
            }
            tilesToExchange.removeAll(tilesToExchange); // TODO: correct way to clear list?
          }
          exchangeTilesMode = false;
          changeSkipAndChangeStatus(true);
        } else {
          indicator++;
          nextTurn(indicator);
        }
        break;
      default:
        break;
    }
  }

  /**
   * This method indicates the next strp in the tutorial.
   * 
   * @author nilbecke
   * @param indictor indicates which phase of the tutorial is currently played.
   */
  public void nextTurn(int indicator) {
    switch (indicator) {
      default:
        break;
    }
  }



  /**
   * This method determines the position of a node in a gridpane and returns the position in a one
   * dimensional int array with x-coordinate on int[0] and y-coordinate on int[1]. The boolean in
   * the parameter determines wether the node is located in the rack gridpane or the gamepanel
   * gridpane - nodeFromRack==true means that the node is located in the rack.
   * 
   * @param node
   * @param nodeFromRack
   * @return
   */
  private int[] getPos(Node node, boolean nodeFromRack) {
    int[] result = new int[2];
    Integer columnIndex = GridPane.getColumnIndex(node);
    Integer rowIndex = GridPane.getRowIndex(node);
    if (columnIndex == null) {
      result[0] = 0;
    } else {
      result[0] = columnIndex.intValue();
    }
    if (rowIndex == null) {
      result[1] = 0;
    } else {
      result[1] = rowIndex.intValue();
    }
    if (nodeFromRack) {
      if (result[1] > 0) {
        result[0] = result[0] + 6;
      }
      result[1] = -1;
    }
    return result;
  }

  /**
   * Closes the Game and stops the server.
   */

  public static GameController getController() {
    return gc;
  }
}
