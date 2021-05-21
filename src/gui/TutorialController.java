package gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import game.GameController;
import game.GameSettings;
import game.GameState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Field;
import mechanic.GameBoard;
import mechanic.Letter;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import mechanic.Turn;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.TileMessage;
import network.messages.TurnResponseMessage;
import network.server.Server;

/**
 * @author nilbecke, mschmauc
 * 
 *         This class is a modification of the game panel controller to play the tutorial.
 */

public class TutorialController extends GamePanelController
    implements Sender, EventHandler<ActionEvent>, Runnable {



  private Player player;

  private List<PlayerData> players;
  private ClientProtocol cp;
  private Server server;
  private static boolean exchangeTilesMode = false;
  private List<Tile> tilesToExchange = new ArrayList<Tile>();
  private static int selectedCoordinates[] = new int[2]; // row, column
  private static int targetCoordinates[] = new int[2]; // row, column
  private List<Field> layedDown = new ArrayList<Field>();



  private ChatController cc;
  private static GameController gc;
  private GameState gs;
  private List<Tile> tiles;
  private int indicator = 0;
  private int min;
  private int sec;
  private Thread thread;
  private double timeLeftBar;
  private boolean turnCountdown;



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
  private Text remainingLetters, timer, tut1, tut2, tut3;
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
    this.min = 90;
    this.remainingLetters.setOpacity(0);
    this.player = player;
    cc = new ChatController(player);
    chat.setEditable(false);

    this.chat.appendText(
        "Welcome to the Tutorial :)\n\nYou will be shown Tips to learn the basic mechanics of this game.\n\nStart by dragging the 'B','E' and 'D' on the marked fields.");

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

            closeTutorial(); // TODO: close method not neccesary anymore?
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
      case 0:
        if (validateTurn(indicator)) {
          Turn current =
              new Turn(this.player.getNickname(), this.player.getServer().getGameController());
          sendMessage(
              new TurnResponseMessage(this.player.getNickname(), true, current.calculateTurnScore(),
                  this.player.getServer().getGameController().getNextPlayer(), 99));
          indicator++;
        }
      default:
        break;
    }
  }

  /**
   * This method validates the turns made by players while playing the tutorial.
   * 
   * @param index indicates which phase of the tutorial was played last.
   * @return
   */
  public boolean validateTurn(int index) {
    GameBoard gb = this.player.getServer().getGameController().getGameState().getGameBoard();
    for (int i = 0; i < gb.getFields().length; i++) {
      for (int k = 0; k < gb.getFields()[i].length; k++) {
        if (gb.getFields()[i][k].getTile() != null) {
          if (!this.layedDown.contains(gb.getFields()[i][k])) {
            this.layedDown.add(gb.getFields()[i][k]);
          }
        }
      }
    }
    switch (index) {
      case 0:
        try {
          if (gb.getField(8, 8).getTile().getLetter().getCharacter() == 'B'
              && gb.getField(9, 8).getTile().getLetter().getCharacter() == 'E'
              && gb.getField(10, 8).getTile().getLetter().getCharacter() == 'D') {
            return true;
          } else {
            showInvalidTurn();
            for (int i = 0; i < layedDown.size(); i++) {
              this.player.moveToRack(this.layedDown.get(i).getTile(), i);

            }
          }

        } catch (NullPointerException e) {
          showInvalidTurn();
          for (int i = 0; i < layedDown.size(); i++) {
            this.player.moveToRack(this.layedDown.get(i).getTile(), i);
            sendMessage(new RemoveTileMessage(this.player.getNickname(),
                this.layedDown.get(i).getxCoordinate(), this.layedDown.get(i).getyCoordinate()));

          }
        }

        break;
      default:
        break;
    }
    return true;

  }

  /**
   * This method informs the user of a mistake in the tutorial.
   */
  public void showInvalidTurn() {
    CustomAlert alert = new CustomAlert(AlertType.ERROR);
    alert.setHeaderText("Retry move!");
    alert.setContentText("Look in the chat for instructions and try again.");
    alert.show();

  }


  /**
   * This mthod closes the tutorial and cuts all server connections.
   */
  public void closeTutorial() {
    this.player.getServer().stopServer();
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
    new LoginScreen().start(new Stage());
  }

  /**
   * This method sets the Disable property of the skipAndChange Button. When you set toBeActivated
   * on 'true', the Button is being activated.
   * 
   * @param toBeActivated
   */
  public void changeSkipAndChangeStatus(boolean toBeActivated) {
    skipAndChangeButton.setDisable(!toBeActivated);
  }

  /**
   * This method sets the Disable property of the done Button. When you set toBeActivated on 'true',
   * the Button is being activated.
   * 
   * @param toBeActivated
   */
  public void changeDoneStatus(boolean toBeActivated) {
    doneButton.setDisable(!toBeActivated);
  }

  /**
   * Listener that is called, when a user starts a drag movement from a rack field. The coordinates
   * of the event starting location are being saved for this drag event in the selectedCoordinates
   * array.
   * 
   * 
   * @param event
   */
  @FXML
  public void rackDragHandling(MouseEvent event) {
    // Image image = new Image(System.getProperty("user.dir") + "/ressources/general/tile.png");
    // rulesButton.getScene().setCursor(new ImageCursor(image));

    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, true);

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);
    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);
    event.consume();
  }

  /**
   * Listener method that is called, when a user starts a drag movement from a board field. The
   * coordinates of the event starting location are being saved for this drag event in the
   * selectedCoordinates array.
   * 
   * @param event
   */
  @FXML
  public void boardDragHandling(MouseEvent event) {
    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, false);
    selectedCoordinates[0]++;
    selectedCoordinates[1]++;

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);

    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);

    event.consume();
  }

  /**
   * Listener method that is called, when a user drags a tile over a tile. The transfer mode to be
   * accepted upon a drop action can be of any type, since there is only one type in the game.
   * 
   * @param event
   */
  @FXML
  public void DragOverHandling(DragEvent event) {
    event.acceptTransferModes(TransferMode.ANY);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a rack field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   * 
   * @param event
   */
  @FXML
  public void rackDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, true);
    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile

    } else if (selectedCoordinates[1] == -1) { // exchange tiles on rack
      player.reorganizeRackTile(selectedCoordinates[0], targetCoordinates[0]);

    } else if (selectedCoordinates[1] != -1) { // try to move tile from board to rack - sender!
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a board field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   * 
   * @param event
   */
  @FXML
  public void boardDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, false);
    targetCoordinates[0] += 1;
    targetCoordinates[1] += 1;

    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile
    } else if (selectedCoordinates[1] != -1) { // exchange tiles on board
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    } else if (selectedCoordinates[1] == -1) { // move tile from rack to board
      player.moveToGameBoard(selectedCoordinates[0], targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
  }

  /**
   * Listener method that is called when a field on the rack is clicked. When the exchangeTilesMode
   * was selected before by clicking the Skip&Change button, the tile on the field is selected if
   * there is a tile on the specific field.
   * 
   * @param event
   */
  @FXML
  public void selectToExchange(MouseEvent event) {
    if (exchangeTilesMode) {
      Rectangle rect[] = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
      Node node = (Node) event.getSource();
      int x = getPos(node, true)[0];
      if (player.getRackTile(x) != null && !tilesToExchange.contains(player.getRackTile(x))) {
        tilesToExchange.add(player.getRackTile(x));
        rect[x].setStroke(Color.RED);
      } else if (player.getRackTile(x) != null && tilesToExchange.contains(player.getRackTile(x))) {
        tilesToExchange.remove(player.getRackTile(x));
        rect[x].setStroke(Color.BLACK);
      }
    }
  }

  /**
   * This method sets all coordinates for the helper arrays of the tile movements to -2. Thereby,
   * possible errors in the dropHandler methods are evaded.
   */
  public static void resetCoordinates() {
    selectedCoordinates[0] = -2;
    selectedCoordinates[1] = -2;
    targetCoordinates[0] = -2;
    targetCoordinates[1] = -2;
  }

  /**
   * Method to be executed when a player clicks on the "Send" button of the chat area in the
   * GamePanel The ChatController handles this event and gets the text from the textfield of the
   * chat area.
   * 
   */
  public void sendMessageFromInput() {
    this.cc.sendChatMessage(this.player.getNickname(), this.chatInput.getText());
    this.chatInput.setText("");
  }

  /**
   * Method that sends a message that is supposed to appear in the chat area and informs the users
   * about a game event e.g. a player left the game. Therefore the sender is left blank.
   * 
   * @param nickname
   * @param message
   */
  public void sendGameInfoMessage(String message) {
    this.cc.sendChatMessage("", message);
  }

  /**
   * 
   * Methods to be used by the ClientProtocol to change the UI of the Client
   * 
   */

  /**
   * Lets a player disconnect
   * 
   * @param nickname of the player disconnecting
   */
  public void removeJoinedPlayer(String playerToBeRemoved) {
    Text[] playerLabel = {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    Text[] pointsLabel = {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    Text[] playerNameLabel = {player1, player2, player3, player4};
    ImageView[] avatarImageView = {image1, image2, image3, image4};
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getNickname().equals(playerToBeRemoved)) {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(null);
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
      }
    }
  }

  /**
   * Updates Lobbychat by using the updateChat method in the Chat Controller. The String that is
   * generated by this method from Chat Controller is appended to the chat TextArea and the
   * chatInput TextField is being cleared.
   * 
   * @param sender
   * @param message
   * @param dateTime
   */
  public void updateChat(String message, LocalDateTime dateTime, String sender) {
    if (!sender.equals("")) {
      this.chat.appendText("\n" + this.cc.updateChat(message, dateTime, sender));
    } else {
      this.chat.appendText("\n" + this.cc.updateChat(message, dateTime, sender) + "");
    }
    this.chat.setScrollTop(Double.MAX_VALUE);
  }

  /**
   * This method highlights the player that is playing his turn at the moment by visually
   * emphasizing the players nickname on the game panel.
   * 
   * @param nickName
   */
  public void indicatePlayerTurn(String newPlayer) {
    return;
  }

  /**
   * This method adds a tile at a location at the game panel either on the rack or on the game
   * board. For instance when a player draws new tiles after he has put some tiles on the game
   * board.
   * 
   * @param tile
   */
  public void addTile(Tile tile) {
    char letter = tile.getLetter().getCharacter();
    int tileValue = tile.getValue();
    int column = tile.getField().getxCoordinate();
    int row = tile.getField().getyCoordinate();

    if (tile.isOnRack()) {
      row = 0;
      if (column > 5) { // case: tile is in the second row of the rack
        row = 1;
        column -= 6;
      }
      VisualTile rackTile = new VisualTile(Character.toString(letter), tileValue, true);
      rackTile.setMouseTransparent(true);
      rack.add(rackTile, column, row);
      GridPane.setHalignment(rackTile, HPos.CENTER);
      GridPane.setValignment(rackTile, VPos.BOTTOM);
      GridPane.setMargin(rackTile, new Insets(0, 0, 5, 0));
    } else {
      row -= 1;
      column -= 1;
      VisualTile boardTile = new VisualTile(Character.toString(letter), tileValue, false);
      boardTile.setMouseTransparent(true);
      board.add(boardTile, column, row);
      GridPane.setHalignment(boardTile, HPos.CENTER);
      GridPane.setValignment(boardTile, VPos.BOTTOM);
      GridPane.setMargin(boardTile, new Insets(0, 0, 3, 0));
    }
  }


  /**
   * This method updates a Tile on the UI by putting the tile on a new position on the Rack provided
   * by the parameters parameters and removing it from the last position.
   * 
   * @param tile
   * @param oldXCoordinate
   * @param oldYCoordinate
   */
  public void moveToRack(Tile tile, int oldXCoordinate, int oldYCoordinate) {
    boolean fromRack = false;
    if (oldYCoordinate == -1) {
      fromRack = true;
    }
    removeTile(oldXCoordinate, oldYCoordinate, fromRack);
    addTile(tile);
  }

  /**
   * This method updates a Tile on the UI by putting the tile on a new position on the GamePanel
   * provided by the coordinate parameters and removing it from the last position.
   * 
   * @param tile
   * @param oldXCoordinate
   * @param oldYCoordinate
   */
  public void moveToGamePanel(Tile tile, int oldXCoordinate, int oldYCoordinate) {
    boolean fromRack = false;
    if (oldYCoordinate == -1) {
      fromRack = true;
    }
    removeTile(oldXCoordinate, oldYCoordinate, fromRack);
    addTile(tile);
  }

  /**
   * This method removes a tile on the GamePanel. This might be the case when another player removes
   * a tile during his turn. This method can only remove a tile from the GamePanel and NOT from the
   * rack!
   * 
   * @param column
   * @param row
   * @param isOnRack
   */
  public void removeTile(int column, int row, boolean isOnRack) {
    int x, y;
    ObservableList<Node> list;
    if (isOnRack) {
      list = rack.getChildren();
      for (Node node : list) {
        x = getPos(node, true)[0];
        y = getPos(node, true)[1];
        if (node instanceof Parent && x == column && y == row) {
          rack.getChildren().remove(node);
          break;
        }
      }
    } else {
      column -= 1;
      row -= 1;
      list = board.getChildren();
      for (Node node : list) {
        x = getPos(node, false)[0];
        y = getPos(node, false)[1];
        if (node instanceof Parent && x == column && y == row) {
          board.getChildren().remove(node);
          break;
        }
      }
    }
  }


  /**
   * This method is getting returned to the UI after the sendTileMove method has been triggered from
   * the UI. A visual confirmation for a valid turn is shown in the UI.
   * 
   * @param nickName
   */
  public void indicateInvalidTurn(String nickName, String message) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        alert.setTitle("Invalid Turn");
        alert.setHeaderText("Your turn was not valid");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);

        alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("/fxml/DialogPaneButtons.css").toExternalForm());
        alert.show();
      }
    });
  }

  /**
   * This method is called, when the host decides to shut down the server. For the clients this
   * method creates a warning alert and after confirmation, the game panel is closed.
   * 
   * @param hostName
   * @param reason
   */
  public void showShutdownMessage(String hostName, String reason) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        CustomAlert alert = new CustomAlert(AlertType.WARNING);
        alert.setTitle("Server Shutdown");
        alert.setHeaderText("Server stopped and game ended.");
        alert.setContentText("The Server was shut down by '" + hostName + "'. \nReason: " + reason);
        alert.initStyle(StageStyle.UNDECORATED);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          Stage st = (Stage) rulesButton.getScene().getWindow(); // TODO: das muss schoener gehen
                                                                 // als einen random knopf zu nehmen
          st.close();
          alert.close();
        }
      }
    });
    new LoginScreen().start(new Stage());
  }

  /**
   * This method updates the score of an Player on the UI and shows a new totalScore.
   * 
   * @param nickName
   * @param turnScore
   */
  public void updateScore(String nickName, int totalScore) {
    String newScore = String.valueOf(totalScore);
    playerOnePoints.setText(newScore);

  }

  /**
   * 
   * Methods to override sender interface methods; documentation in interface
   * 
   */

  @Override
  public void sendTileMove(String nickName, int oldX, int oldY, int newX, int newY) {
    MoveTileMessage m = new MoveTileMessage(nickName, oldX, oldY, newX, newY);
    if (this.player.isHost()) {
      this.player.getServer().handleMoveTile(m);
    } else {
      sendMessage(m);
    }
  }

  @Override
  public void sendCommitTurn(String nickName) {
    Message m = new CommitTurnMessage(nickName, this.player.getRackTiles().isEmpty());
    if (this.player.isHost()) {
      this.player.getServer().handleCommitTurn((CommitTurnMessage) m);
    } else {
      sendMessage(m);
    }
  }

  /**
   * This Message is used to Reset the current turn for every player.
   * 
   * @author lurny
   */
  public void sendResetTurnForEveryPlayer(String nickName) {
    System.out.println("Test1");
    Message m = new ResetTurnMessage(this.player.getNickname(), null);
    if (this.player.isHost()) {
      this.player.getServer().resetTurnForEveryPlayer((ResetTurnMessage) m);
    } else {
      sendMessage(m);
    }
  }

  public void sendResetTurn() {
    Message m = new ResetTurnMessage(this.player.getNickname(), null);
    if (this.player.isHost()) {
      this.player.getServer().resetTurnForEveryPlayer((ResetTurnMessage) m);
    }
  }

  @Override
  public void sendDisconnectMessage(String nickName) {
    Message m = new DisconnectMessage(nickName);
    sendMessage(m);
  }

  @Override
  public void sendTileMessage(String nickName) {
    Message m = new TileMessage(nickName, tilesToExchange);
    if (this.player.isHost()) {
      this.player.getServer().handleExchangeTiles((TileMessage) m);
    } else {
      sendMessage(m);
    }
  }

  /**
   * Sends a given message to all players.
   * 
   * @param m The Message to be sent
   */
  public boolean sendMessage(Message m) {
    if (this.player.isHost()) {
      this.player.getServer().sendToAll(m);
    } else {
      this.player.getClientProtocol().sendToServer(m);
    }
    return true;
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
  public void close() {
    if (this.player.getServer() != null) { // TODO: this.player.isHost() nutzen?
      this.player.getServer().stopServer();
      // Message m = new ShutdownMessage(this.player.getNickname(), REGULAR_SHUTDOWN);
      // sendMessage(m);
    } else if (!this.player.isHost()) {
      Rectangle[] rect = {currentPlayer1, currentPlayer2, currentPlayer3, currentPlayer4};
      for (int i = 0; i < players.size(); i++) {
        if (players.get(i).getNickname().equals(this.player.getNickname())) {
          if (rect[i].isVisible()) {
            sendResetTurnForEveryPlayer(this.player.getNickname()); // TODO:
          }
        }
      }
      sendGameInfoMessage("'" + this.player.getNickname() + "' left the game");
      // this.player.getClientProtocol().disconnect(); // in DisconnectMessage included?
      Message m = new DisconnectMessage(this.player.getNickname());
      sendMessage(m);
    }
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
    new LoginScreen().start(new Stage());
  }

  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  public ClientProtocol getCp() {
    return cp;
  }

  public void setCp(ClientProtocol cp) {
    this.cp = cp;
  }

  public static int[] getCoordinates() {
    return selectedCoordinates;
  }


  /**
   * Thread to countdown the maxmimum length of a turn.
   *
   * @author lurny
   */
  public void run() {
    String secString = "";
    while (turnCountdown) {
      if (this.sec > 9) {
        secString = "" + this.sec;
      } else {
        secString = "0" + this.sec;
      }

      this.timeLeftBar = (this.min * 60.0 + this.sec) / 600.0;

      if (this.sec == 0 && this.min > 0) {
        this.sec = 59;
        this.min--;
      } else if (this.sec == 0 & this.min == 0) {
        this.turnCountdown = false;
        this.sendResetTurn();
      } else {
        this.sec--;
      }

      this.updateTimer(String.valueOf(min), secString);
      this.updateProgressBar(this.timeLeftBar);

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        this.turnCountdown = false;
      }
    }

  }

  /**
   * method to start the Turn timer.
   *
   * @author lurny
   */
  public void startTimer() {
    if (this.thread != null && !this.thread.isInterrupted()) {
      stopTimer();
    }

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.thread = new Thread(this);
    this.min = 90;
    this.sec = 20;
    this.turnCountdown = true;
    this.thread.start();
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


  public static GameController getController() {
    return gc;
  }
}
