package gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.ShutdownMessage;
import network.messages.TileMessage;
import network.server.Server;

/**
 * @author mschmauc
 * 
 *         This class is the Controller Class for the Main Gamel Panel UI for the Client
 */

public class GamePanelController implements Sender, EventHandler<ActionEvent>, Runnable {

  private Player player;
  private List<PlayerData> players;
  private ClientProtocol cp;
  private Server server;
  private static boolean exchangeTilesMode = false;
  private List<Tile> tilesToExchange = new ArrayList<Tile>();
  private static int selectedCoordinates[] = new int[2]; // row, column
  private static int targetCoordinates[] = new int[2]; // row, column
  private ChatController cc;

  private int min;
  private int sec;
  private Thread thread;
  private int timerDuration;

  private double timeLeftBar;
  private boolean turnCountdown;
  private CustomAlert alert2;

  // private VisualTile cursorTile;

  @FXML
  private Pane upperPane;
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
  private Text dws0;
  @FXML
  private Text dws1;
  @FXML
  private Text dws2;
  @FXML
  private Text dws3;
  @FXML
  private Text dws4;
  @FXML
  private Text dws5;
  @FXML
  private Text dws6;
  @FXML
  private Text dws7;
  @FXML
  private Text dws8;
  @FXML
  private Text dws9;
  @FXML
  private Text dws10;
  @FXML
  private Text dws11;
  @FXML
  private Text dws12;
  @FXML
  private Text dws13;
  @FXML
  private Text dws14;
  @FXML
  private Text dws15;
  @FXML
  private Text dws16;
  @FXML
  private Text tws0;
  @FXML
  private Text tws1;
  @FXML
  private Text tws2;
  @FXML
  private Text tws3;
  @FXML
  private Text tws4;
  @FXML
  private Text tws5;
  @FXML
  private Text tws6;
  @FXML
  private Text tws7;
  @FXML
  private Text dls0;
  @FXML
  private Text dls1;
  @FXML
  private Text dls2;
  @FXML
  private Text dls3;
  @FXML
  private Text dls4;
  @FXML
  private Text dls5;
  @FXML
  private Text dls6;
  @FXML
  private Text dls7;
  @FXML
  private Text dls8;
  @FXML
  private Text dls9;
  @FXML
  private Text dls10;
  @FXML
  private Text dls11;
  @FXML
  private Text dls12;
  @FXML
  private Text dls13;
  @FXML
  private Text dls14;
  @FXML
  private Text dls15;
  @FXML
  private Text dls16;
  @FXML
  private Text dls17;
  @FXML
  private Text dls18;
  @FXML
  private Text dls19;
  @FXML
  private Text dls20;
  @FXML
  private Text dls21;
  @FXML
  private Text dls22;
  @FXML
  private Text dls23;
  @FXML
  private Text tls0;
  @FXML
  private Text tls1;
  @FXML
  private Text tls2;
  @FXML
  private Text tls3;
  @FXML
  private Text tls4;
  @FXML
  private Text tls5;
  @FXML
  private Text tls6;
  @FXML
  private Text tls7;
  @FXML
  private Text tls8;
  @FXML
  private Text tls9;
  @FXML
  private Text tls10;
  @FXML
  private Text tls11;
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

  private static GamePanelController instance;

  public static GamePanelController getInstance() {
    return instance;
  }

  private Text[] playerLabel;
  private Text[] pointsLabel;
  private Text[] playerNameLabel;
  private ImageView[] avatarImageView;
  private Rectangle[] rect;
  private Text[] dlsLabel;
  private Text[] tlsLabel;
  private Text[] dwsLabel;
  private Text[] twsLabel;

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
    playerLabel = new Text[] {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    pointsLabel =
        new Text[] {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    playerNameLabel = new Text[] {player1, player2, player3, player4};
    avatarImageView = new ImageView[] {image1, image2, image3, image4};
    rect = new Rectangle[] {currentPlayer1, currentPlayer2, currentPlayer3, currentPlayer4};
    dlsLabel = new Text[] {dls0, dls1, dls2, dls3, dls4, dls5, dls6, dls7, dls8, dls9, dls10, dls11,
        dls12, dls13, dls14, dls15, dls16, dls17, dls18, dls19, dls20, dls21, dls22, dls23};
    tlsLabel =
        new Text[] {tls0, tls1, tls2, tls3, tls4, tls5, tls6, tls7, tls8, tls9, tls10, tls11};
    dwsLabel = new Text[] {dws0, dws1, dws2, dws3, dws4, dws5, dws6, dws7, dws8, dws9, dws10, dws11,
        dws12, dws13, dws14, dws15, dws16};
    twsLabel = new Text[] {tws0, tws1, tws2, tws3, tws4, tws5, tws6, tws7};
    GameState gs;
    if (player.isHost()) {
      gs = player.getServer().getGameState();
      players = gs.getAllPlayers();
      leaveGameButton.setText("Stop Server");
    } else {
      gs = player.getClientProtocol().getGameState();
      try {
        players = gs.getAllPlayers();
      } catch (NullPointerException e) {
        return;
      }
      leaveGameButton.setText("Leave Game");
    }

    for (int i = 0; i <= 3; i++) {
      if (i < players.size()) {
        if (players.get(i).isHost()) {
          playerNameLabel[i].setText(players.get(i).getNickname() + " (Host)");
        } else if (players.get(i).getNickname().equals(this.player.getNickname())) {
          playerNameLabel[i].setText(players.get(i).getNickname() + " (Me)");
        } else {
          playerNameLabel[i].setText(players.get(i).getNickname());
        }
        pointsLabel[i].setText("0");
        playerLabel[i].setText("Points: ");
        avatarImageView[i].setImage(
            new Image(getClass().getResource(players.get(i).getAvatar()).toExternalForm()));
      } else {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(null);
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
      }
    }
    remainingLetters.setText("");
    timer.setText("");
    timeProgress.setProgress(0.0);
    // backgroundGamePanel.heightProperty().bind(((StackPane)
    // backgroundGamePanel.getParent()).heightProperty());
    // backgroundGamePanel.widthProperty().bind(((StackPane)
    // backgroundGamePanel.getParent()).widthProperty());
    // backgroundGamePanel.heightProperty().bind(board.heightProperty());
    // backgroundGamePanel.widthProperty().bind(board.widthProperty());

    /**
     * @author pkoenig
     */
    // ChangeListener cl = new ChangeListener() {
    // public void changed(ObservableValue observable, Object oldValue, Object newValue) {
    //// Double changeWidth = (Double)newValue - (Double)oldValue;
    // Double newWidth = (Double)newValue;
    // System.out.println("### WIDTH HAT SICH GEÄNDERT AUF " + newWidth + " ###");
    // backgroundGamePanel.setWidth(backgroundGamePanel.getWidth() + changeWidth);
    // board.setPrefWidth(board.getPrefWidth() + changeWidth);
    // backgroundGamePanel.setHeight(backgroundGamePanel.getWidth() + changeWidth);
    // board.setPrefHeight(board.getPrefWidth() + changeWidth);

    // backgroundGamePanel.setWidth(newWidth / 1.9);
    // board.setPrefWidth(newWidth / 2.0);
    // board.setMaxWidth(newWidth / 2.0);
    // board.setMinWidth(newWidth / 2.0);
    // backgroundGamePanel.setHeight(newWidth / 1.9);
    //// board.setPrefHeight(newWidth / 2.0);
    //// board.setMaxHeight(newWidth / 2.0);
    //// board.setMinHeight(newWidth / 2.0);
    // }
    // };
    // scene.widthProperty().addListener(cl);
    //
    // scene.heightProperty().addListener(cl);
    //// backgroundGamePanel.setWidth(820);
    // board.setPrefWidth(800);
    // backgroundGamePanel.setHeight(820);
    // board.setPrefHeight(800);


    /**
     * @author nilbecke
     */

  }

  /**
   * Thread to countdown the maxmimum length of a turn.
   *
   * @author lurny
   */
  public void run() {
    String secString = "";
    while (turnCountdown) {
      this.timeLeftBar = (this.min * 60.0 + this.sec) / timerDuration;

      if (this.sec == 0 && this.min > 0) {
        this.sec = 59;
        this.min--;
      } else if (this.sec == 0 & this.min == 0) {
        this.turnCountdown = false;
        this.sendResetTurn();
      } else {
        this.sec--;
      }

      if (this.sec > 9) {
        secString = "" + this.sec;
      } else {
        secString = "0" + this.sec;
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
    this.min = this.timerDuration / 60;
    this.sec = this.timerDuration % 60;
    this.turnCountdown = true;
    this.thread.start();
  }

  /**
   * method to stop the Turn timer.
   *
   * @author lurny
   */

  public void stopTimer() {
    this.thread.interrupt();
  }

  public int getMin() {
    return min;
  }

  public int getSec() {
    return sec;
  }

  public void setTimerDuration(int timerDuration) {
    this.timerDuration = timerDuration;
  }

  public void updateTimer(String min, String sec) {
    timer.setText(min + ":" + sec);
  }

  public void updateRemainingLetters(int number) {
    remainingLetters.setText(String.valueOf(number));
  }

  public void updateProgressBar(Double progress) {
    timeProgress.setProgress(progress);
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
        new SettingsScreen(this.player, false).start(new Stage());;
        break;
      case "leaveGameButton":
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        if (player.isHost()) {
          alert.setTitle("Leave the game and stop the server for all.");
          alert.setHeaderText("Leave game and stop server?");
          alert.setContentText("Do you really want to leave the game and stop the server?");
        } else {
          alert.setTitle("Leave the current game");
          alert.setHeaderText("Leave Game?");
          alert.setContentText("Do you really want to leave the current game?");
        }
        alert.initStyle(StageStyle.UNDECORATED);

        alert.changeButtonText("Yes", ButtonType.OK);
        alert.changeButtonText("No", ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          // if (player.isHost()) {
          // player.getServer().stopServer();
          // // Message m = new ShutdownMessage(this.player.getNickname(), REGULAR_SHUTDOWN);
          // // sendMessage(m);
          // } else {
          // sendGameInfoMessage("'" + this.player.getNickname() + "' left the game");
          // Message m = new DisconnectMessage(this.player.getNickname());
          // sendMessage(m);
          // }
          close(); // TODO: close method not neccesary anymore?
          // Button b = (Button) e.getSource();
          // Stage st = (Stage) (b.getScene().getWindow());
          // st.close();
          new LoginScreen().start(new Stage());
        }
        break;
      case "rulesButton":
        OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
        break;
      case "sendButton":
      case "chatInput":
        sendMessageFromInput();
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
            alert2 = new CustomAlert(AlertType.CONFIRMATION);
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

            this.setRackRectanglesBlack();
            tilesToExchange.removeAll(tilesToExchange); // TODO: correct way to clear list?
          }

          exchangeTilesMode = false;
          changeSkipAndChangeStatus(true);
        } else {
          sendCommitTurn(this.player.getNickname());
        }
        break;
      default:
        break;
    }
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
  public void rackDragStarted(MouseEvent event) {
    if (!exchangeTilesMode) {
      Node node = (Node) event.getSource();
      selectedCoordinates = getPos(node, true);
      // Image image =
      // new Image("file:" + System.getProperty("user.dir") + "/resources/general/tile.png");
      // rulesButton.getScene().setCursor(new ImageCursor(image));
      rulesButton.getScene().setCursor(Cursor.MOVE);
      node.startFullDrag();
      // cursorTile = new VisualTile("H", 3, true);
      // cursorTile.setId("cursorTileFromRack");
      // upperPane.getChildren().add(cursorTile);
      // cursorTile.setOnMouseDragged(new EventHandler<MouseEvent>() {
      // public void handle(MouseEvent event) {
      // cursorTile.relocate(event.getX(), event.getY());
      // // cursorTile.setLayoutX(event.getX());
      // // cursorTile.setLayoutY(event.getY());
      // }
      // });
    }
  }

  @FXML
  public void test0(MouseEvent event) {

    // cursorTile.setTranslateX(event.getX());
    // cursorTile.setTranslateY(event.getY());
    // cursorTile.setLayoutX(event.getX());
    // cursorTile.setLayoutX(event.getY());
  }

  /**
   * Listener method that is called, when a user starts a drag movement from a board field. The
   * coordinates of the event starting location are being saved for this drag event in the
   * selectedCoordinates array.
   * 
   * @param event
   */
  @FXML
  public void boardDragStarted(MouseEvent event) {
    if (!exchangeTilesMode) {
      Node node = (Node) event.getSource();
      selectedCoordinates = getPos(node, false);
      selectedCoordinates[0]++;
      selectedCoordinates[1]++;
      rulesButton.getScene().setCursor(Cursor.MOVE);
      // Image image =
      // new Image("file:" + System.getProperty("user.dir") + "/resources/general/tile.png");
      // rulesButton.getScene().setCursor(new ImageCursor(image));
      node.startFullDrag();
    }
  }

  /**
   * 
   * @param event
   */
  @FXML
  public void test2(MouseDragEvent event) {
    // Node node = (Node) event.getSource();
    // selectedCoordinates = getPos(node, true);
    // System.out.println("node entered: " + selectedCoordinates[0] + "/" + selectedCoordinates[1]);
  }

  /**
   * 
   * @param event
   */
  @FXML
  public void test3(MouseDragEvent event) {
    // Node node = (Node) event.getSource();
    // selectedCoordinates = getPos(node, true);
    // System.out.println("node exited: " + selectedCoordinates[0] + "/" + selectedCoordinates[1]);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a rack field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   * 
   * @param event
   */
  @FXML
  public void rackDragReleased(MouseDragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, true);
    // cursorTile = null;
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile

    } else if (selectedCoordinates[1] == -1) { // exchange tiles on rack
      player.reorganizeRackTile(selectedCoordinates[0], targetCoordinates[0]);

    } else if (selectedCoordinates[1] != -1) { // try to move tile from board to rack - sender!
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a board field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   * 
   * @param event
   */
  @FXML
  public void boardDragReleased(MouseDragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, false);
    targetCoordinates[0] += 1;
    targetCoordinates[1] += 1;
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
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

  @FXML
  public void mousePressed(MouseEvent event) {
    // Node node = (Node) event.getSource();
    // node.setMouseTransparent(true);
  }

  @FXML
  public void mouseReleased(MouseEvent event) {
    // Node node = (Node) event.getSource();
    // node.setMouseTransparent(false);
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
  }

  /**
   * Method that allows player to double click on a tile that the player placed on the game panel at
   * the current turn. This has the effect, that the tile is put back on the rack.
   * 
   * @param event
   */
  @FXML
  public void mouseClicked(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY)) {
      if (event.getClickCount() == 2) {
        Node node = (Node) event.getSource();
        selectedCoordinates = getPos(node, false);
        selectedCoordinates[0] += 1;
        selectedCoordinates[1] += 1;
        targetCoordinates[0] = this.player.getFreeRackField().getxCoordinate();
        targetCoordinates[1] = this.player.getFreeRackField().getyCoordinate();
        sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
            targetCoordinates[0], targetCoordinates[1]);
        resetCoordinates();
      }
    }
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
    // Text[] playerLabel = {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    // Text[] pointsLabel = {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    // Text[] playerNameLabel = {player1, player2, player3, player4};
    // ImageView[] avatarImageView = {image1, image2, image3, image4};
    int indexRemoved = 5;
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getNickname().equals(playerToBeRemoved)) {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(""); // better to use "" instead of null?
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
        indexRemoved = i;
        rect[i].setVisible(false);
        players.remove(indexRemoved);
      }
      if (i > indexRemoved && !playerNameLabel[i].getText().equals(null)) {
        // move other players up
        playerNameLabel[i - 1].setText(playerNameLabel[i].getText());
        pointsLabel[i - 1].setText(pointsLabel[i].getText());
        playerLabel[i - 1].setText(playerLabel[i].getText());
        avatarImageView[i - 1].setImage(avatarImageView[i].getImage());

        playerNameLabel[i].setText(null);
        pointsLabel[i].setText("");
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
      }
    }
  }

  /**
   * This method sets the visibility of all field labels on the game board on true when the
   * parameter isVisible is true. Vice versa it sets the labels on invisible if isVisible is false.
   * 
   * @param isVisible
   */
  public void setFieldLabelVisibility(boolean isVisible) {
    for (Text t : dwsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : twsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : dlsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : tlsLabel) {
      t.setVisible(isVisible);
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
    this.chat.appendText("\n" + this.cc.updateChat(message, dateTime, sender));
    this.chat.setScrollTop(Double.MAX_VALUE);
  }

  /**
   * This method highlights the player that is playing his turn at the moment by visually
   * emphasizing the players nickname on the game panel.
   * 
   * @param nickName
   */
  public void indicatePlayerTurn(String nextPlayer) {
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getNickname().equals(nextPlayer)) {
        rect[i].setVisible(true);
      } else {
        rect[i].setVisible(false);
      }
    }
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
        close();
        new LoginScreen().start(new Stage());
        CustomAlert alert = new CustomAlert(AlertType.WARNING);
        alert.setTitle("Server Shutdown");
        alert.setHeaderText("Server stopped and game ended.");
        alert.setContentText("The Server was shut down by '" + hostName + "'. \nReason: " + reason);
        alert.initStyle(StageStyle.UNDECORATED);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          alert.close();
        }
      }
    });

  }

  /**
   * This method updates the score of an Player on the UI and shows a new totalScore.
   * 
   * @param nickName
   * @param turnScore
   */
  public void updateScore(String nickName, int totalScore) {
    String newScore = String.valueOf(totalScore);
    // Text playerPoints[] = {playerOnePoints, playerTwoPoints, playerThreePoints,
    // playerFourPoints};
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getNickname().equals(nickName)) {
        pointsLabel[i].setText(newScore);
      }
    }
  }

  /**
   * 
   */
  public void setRackRectanglesBlack() {
    Rectangle rect[] = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
    for (Rectangle r : rect) {
      r.setStroke(Color.BLACK);
    }
  }

  public void resetSkipAndChange() {
    this.setExchangeTilesMode(false);
    this.setRackRectanglesBlack();
    this.tilesToExchange.removeAll(tilesToExchange);
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
      if (this.player.getServer().getGameState().getCurrentPlayer()
          .equals(this.player.getNickname())) {
        this.player.getServer().getGameController().getTurn().setStringRepresentation("Time's up!");
        this.player.getServer().resetTurnForEveryPlayer((ResetTurnMessage) m);
      }
    } else {
      if (this.player.getClientProtocol().getGameState().getCurrentPlayer()
          .equals(this.player.getNickname())) {
        sendMessage(m);
      }
    }
  }

  @Override
  public void sendDisconnectMessage(String nickName) {
    Message m = new DisconnectMessage(nickName, null);
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
    stopTimer();
    if (this.player.isHost()) {
      this.player.getServer().sendToAll(
          new ShutdownMessage(this.player.getNickname(), "Host closed the server session."));
      this.player.getServer().stopServer();
    } else if (this.player.getClientProtocol().isOk()) {
      Message m = new DisconnectMessage(this.player.getNickname(), null);
      sendMessage(m);
    }
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
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

  public static void setCoordinates(int coordinates[]) {
    GamePanelController.selectedCoordinates = coordinates;
  }

  public CustomAlert getAlert2() {
    return alert2;
  }

  public int getTimerDuration() {
    return timerDuration;
  }

  public boolean isExchangeTilesMode() {
    return exchangeTilesMode;
  }

  public void setExchangeTilesMode(boolean exchangeTilesMode) {
    GamePanelController.exchangeTilesMode = exchangeTilesMode;
  }
}
