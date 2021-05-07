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
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
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
  private static boolean selectedTileOnGrid = false;
  private static boolean selectedTileOnRack = false;
  private static boolean exchangeTilesMode = false;
  private static List<Tile> tilesToExchange = new ArrayList<Tile>();
  private static int selectedCoordinates[] = new int[2]; // row, column
  private static int targetCoordinates[] = new int[2]; // row, column
  private ChatController cc;
  private static VisualTile rackTiles[][] = new VisualTile[2][6]; // location of visual tiles on
                                                                  // rack, row;column index
  private static VisualTile boardTiles[][] = new VisualTile[15][15]; // location of visual tiles on
                                                                     // board, row;column index



  @FXML
  private TextArea chat;
  @FXML
  private TextField chatInput;
  @FXML
  private Button sendButton, skipAndChangeButton, doneButton;
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

  private static GamePanelController instance;

  private int min;
  private int sec;
  private Thread thread;
  private double timeLeftBar;
  private boolean turnCountdown;

  public static GamePanelController getInstance() {
    return instance;
  }

  public void initData(Player player) {
    this.player = player;
    cc = new ChatController(player);
    chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");

    Text[] playerLabel = {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    Text[] pointsLabel = {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    Text[] playerNameLabel = {player1, player2, player3, player4};
    ImageView[] avatarImageView = {image1, image2, image3, image4};
    GameState gs;
    if (player.isHost()) {
      gs = player.getServer().getGameState();
      players = gs.getAllPlayers();
    } else {
      gs = player.getClientProtocol().getGameState();
      try {
        players = gs.getAllPlayers();
      } catch (NullPointerException e) {
        return;
      }
    }

    for (int i = 0; i <= 3; i++) {
      if (i < players.size()) {
        playerNameLabel[i].setText(players.get(i).getNickname());
        pointsLabel[i].setText("0");
        playerLabel[i].setText("Points: ");
        avatarImageView[i]
            .setImage(new Image("file:" + FileParameters.datadir + players.get(i).getAvatar()));
      } else {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(null);
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
      }
    }
    remainingLetters.setText("TODO: read max letters from gamesettings");
    timer.setText("TODO: read time per player from gamesettings");
    timeProgress.setProgress(1.0);
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

  public void initializeThread() {
    this.thread = new Thread(this);
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
      }
      // Send it
      else if (this.server != null) {
        System.out.println("lösche das im Gamepanelcontroller Zeile 185");
        this.server.resetTurnForEveryPlayer();
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
    this.min = 10;
    this.sec = 0;
    this.turnCountdown = true;
    this.thread.start();
  }

  /**
   * method to stop the Turn timer.
   *
   * @author lurny
   */

  public void stopTimer() {
    this.turnCountdown = false;
  }

  public int getMin() {
    return min;
  }

  public int getSec() {
    return sec;
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
      case "sendButton":
      case "chatInput":
        sendMessage();
        break;
      case "skipAndChangeButton":
        if (!exchangeTilesMode) {
          exchangeTilesMode = true;
          skipAndChangeButton.setDisable(true);
        }
        break;
      case "doneButton":
        if (exchangeTilesMode) {
          CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
          alert.setTitle("Skip & Exchange selected tiles");
          alert.setHeaderText("Skip & Exchange?");
          alert.setContentText(
              "Do you want to skip the current turn and exchange the selected tiles ");
          alert.initStyle(StageStyle.UNDECORATED);

          alert.changeButtonText("Yes", ButtonType.OK);
          alert.changeButtonText("No", ButtonType.CANCEL);

          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {
            sendTileMessage(this.player.getNickname());
          } else {
            alert.close();
          }

          skipAndChangeButton.setText("Skip & Exchange");
          Rectangle rect[] = {r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
          for (Rectangle r : rect) {
            r.setStroke(Color.BLACK);
          }
          tilesToExchange.removeAll(tilesToExchange); // TODO: correct way to clear list?
          exchangeTilesMode = false;
          skipAndChangeButton.setDisable(false);
        } else {
          completeTurn();
        }
        break;
      default:
        break;
    }
  }

  /**
   * TODO
   * 
   * @param event
   */
  @FXML
  public void rackDragHandling(MouseEvent event) {
    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, true);

    System.out.println("rackPressed at: " + selectedCoordinates[0] + "; " + selectedCoordinates[1]);

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);
    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);
    event.consume();
  }

  /**
   * TODO
   * 
   * @param event
   */
  @FXML
  public void boardDragHandling(MouseEvent event) {
    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, false);
    selectedCoordinates[0]++;
    selectedCoordinates[1]++;

    System.out
        .println("boardPressed at: " + selectedCoordinates[0] + "; " + selectedCoordinates[1]);

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);
    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);

    event.consume();
  }

  /**
   * TODO
   * 
   * @param event
   */
  @FXML
  public void DragOverHandling(DragEvent event) {
    event.acceptTransferModes(TransferMode.ANY);
  }

  /**
   * TODO
   * 
   * @param event
   */
  @FXML
  public void rackDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, true);
    System.out.println("rackReleased at: " + targetCoordinates[0] + "; " + targetCoordinates[1]);

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
   * TODO
   * 
   * @param event
   */
  @FXML
  public void boardDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, false);
    targetCoordinates[0] += 1;
    targetCoordinates[1] += 1;
    System.out.println("boardReleased at: " + targetCoordinates[0] + "; " + targetCoordinates[1]);

    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile
      // System.out.println("deselect grid tile");
    } else if (selectedCoordinates[1] != -1) { // exchange tiles on board
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
      // System.out.println("exchange grid tiles");
    } else if (selectedCoordinates[1] == -1) { // move tile from rack to board
      // System.out.println("rack to grid: coords, x, y: " + selectedCoordinates[0]
      // + targetCoordinates[0] + targetCoordinates[1]);
      player.moveToGameBoard(selectedCoordinates[0], targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
  }

  @FXML
  public void selectToExchange(MouseEvent event) {
    if (exchangeTilesMode) {
      Rectangle rect[] = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
      Node node = (Node) event.getSource();
      int helper[] = getPos(node, true);
      if (player.getRackTile(helper[0]) != null
          && !tilesToExchange.contains(player.getRackTile(helper[0]))) {
        tilesToExchange.add(player.getRackTile(helper[0]));
        rect[helper[0]].setStroke(Color.RED);
      } else if (player.getRackTile(helper[0]) != null
          && tilesToExchange.contains(player.getRackTile(helper[0]))) {
        tilesToExchange.remove(player.getRackTile(helper[0]));
        rect[helper[0]].setStroke(Color.BLACK);
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
  public void sendMessage() {
    this.cc.sendChatMessage(this.player.getNickname(), this.chatInput.getText());
    this.chatInput.setText("");
  }

  /**
   * 
   */
  public void completeTurn() {
    sendCommitTurn(this.player.getNickname());
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
  public void removeJoinedPlayer(String nickname) {
    // TODO
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
  public void indicatePlayerTurn(String newPlayer, String oldPlayer) {
    String[] playerNames =
        {player1.getText(), player2.getText(), player3.getText(), player4.getText()};
    Rectangle[] rect = {currentPlayer1, currentPlayer2, currentPlayer3, currentPlayer4};
    for (int i = 0; i < 4; i++) {
      if (playerNames[i].equals(newPlayer)) {
        rect[i].setVisible(true);
      }
      if (playerNames[i].equals(oldPlayer)) {
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
      rackTiles[row][column] = new VisualTile(Character.toString(letter), tileValue, true);
      rackTiles[row][column].setMouseTransparent(true);
      rack.add(rackTiles[row][column], column, row);
      GridPane.setHalignment(rackTiles[row][column], HPos.CENTER);
      GridPane.setValignment(rackTiles[row][column], VPos.BOTTOM);
      GridPane.setMargin(rackTiles[row][column], new Insets(0, 0, 5.5, 0));

    } else {
      row -= 1;
      column -= 1;
      boardTiles[row][column] = new VisualTile(Character.toString(letter), tileValue, false);
      boardTiles[row][column].setMouseTransparent(true);
      board.add(boardTiles[row][column], column, row);
      GridPane.setHalignment(boardTiles[row][column], HPos.CENTER);
      GridPane.setValignment(boardTiles[row][column], VPos.BOTTOM);
      GridPane.setMargin(boardTiles[row][column], new Insets(0, 10, 8, 0));
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
    // if (player1.getText().equals(nickName)) {
    // // TODO: zug r�ckg�ngig machen
    // } else if (player2.getText().equals(nickName)) {
    // // TODO: zug r�ckg�ngig machen
    // } else if (player3.getText().equals(nickName)) {
    // // TODO: zug r�ckg�ngig machen
    // } else if (player4.getText().equals(nickName)) {
    // // TODO: zug r�ckg�ngig machen
    // }
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        CustomAlert alert = new CustomAlert(AlertType.WARNING);
        alert.setTitle("Invalid Turn");
        alert.setHeaderText("Your turn was not valid");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);

        alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("DialogPaneButtons.css").toExternalForm());
        alert.show();
      }
    });
  }

  /**
   * this method updates the score of an Player and shows the new score in the UI
   * 
   * @param nickName
   * @param turnScore
   * @throws Exception
   */
  public void updateScore(String nickName, int turnScore) {
    String newScore = "";
    if (player1.getText().equals(nickName)) {
      this.playerOnePoints.setText(newScore);
    } else if (player2.getText().equals(nickName)) {
      this.playerTwoPoints.setText(newScore);
    } else if (player3.getText().equals(nickName)) {
      this.playerThreePoints.setText(newScore);
    } else if (player4.getText().equals(nickName)) {
      this.playerFourPoints.setText(newScore);
    } else {
      System.out.println("Player " + nickName + "is not part of the GameBoard");
    }

  }

  /**
   * 
   * Methods to override sender interface methods; documentation in interface
   * 
   * TODO: Sollte man die Methoden nicht doch lieber in ClientUi auslagern?
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
    System.out
        .println("method sendCommitTurn wurde aufgerufen, ausgel�st von " + nickName + "\n");
    Message m = new CommitTurnMessage(nickName);
    if (this.player.isHost()) {
      this.player.getServer().handleCommitTurn((CommitTurnMessage) m);
    } else {
      sendMessage(m);
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
    sendMessage(m);
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
    if (this.player.getServer() != null) {
      this.player.getServer().stopServer();
    } else if (!this.player.isHost()) {
      this.player.getClientProtocol().disconnect();
    }
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

  public static boolean isSelectedTileOnGrid() {
    return selectedTileOnGrid;
  }

  public static void setSelectedTileOnGrid(boolean b) {
    selectedTileOnGrid = b;
  }

  public static boolean isSelectedTileOnRack() {
    return selectedTileOnRack;
  }

  public static void setSelectedTileOnRack(boolean selectedTileOnRack) {
    GamePanelController.selectedTileOnRack = selectedTileOnRack;
  }

  public static int[] getCoordinates() {
    return selectedCoordinates;
  }

  public static void setCoordinates(int coordinates[]) {
    GamePanelController.selectedCoordinates = coordinates;
  }
}
