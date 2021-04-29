package gui;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import mechanic.Field;
import mechanic.Letter;
import mechanic.Player;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.server.Server;

/**
 * @author mschmauc
 * 
 *         This class is the Controller Class for the Main Gamel Panel UI for the Client
 */

public class GamePanelController implements Sender {

  private Player player;
  private ClientProtocol cp;
  private Server server;
  private static boolean selectedTileOnGrid = false;
  private static boolean selectedTileOnRack = false;
  private static int coordinates[] = new int[2];
  private ChatController cc;
  private static VisualTile rackTiles[][] = new VisualTile[2][6]; // location of visual tiles on
                                                                  // rack
  private static VisualTile boardTiles[][] = new VisualTile[15][15]; // location of visual tiles on
                                                                     // board

  @FXML
  private TextArea chat;
  @FXML
  private TextField textField;
  @FXML
  private Button sendButton, skipAndChange, done;
  @FXML
  private Text playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints;
  @FXML
  private Text remainingLetters, timer;
  @FXML
  private Text player1, player2, player3, player4;
  @FXML
  private Rectangle tile1;
  @FXML
  private GridPane board, rack;
  @FXML
  private ProgressBar timeProgress;

  private static GamePanelController instance;

  public static GamePanelController getInstance() {
    if (instance == null) {
      instance = new GamePanelController();
    }
    return instance;
  }

  /**
   * Constructor of the class that is being called before @FXML annotated fields were populated
   */
  public GamePanelController() {
    System.out.println("Controller erzeugt \n");
  }

  /**
   * initialize method of the GamePanelController, that is being called after @FXML annotated fields
   * were populated
   */
  public void initialize() {
    this.player = GamePanel.getInstance().getPlayer();
    cc = new ChatController(player);
    chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");
    SimpleIntegerProperty letterProperty = new SimpleIntegerProperty(17); // TODO: 17 durch referenz
                                                                          // ersetzen aus gamestate
                                                                          // Klasse
    remainingLetters.textProperty().bind(letterProperty.asString());
    SimpleStringProperty timerProperty = new SimpleStringProperty("Timer Referenz!");
    timer.textProperty().bind(timerProperty);
    SimpleDoubleProperty progressProperty = new SimpleDoubleProperty(0.5); // TODO: restliche zeit
                                                                           // als anteil von 1 hier
                                                                           // einfügen aus gamestate
                                                                           // Klasse
    timeProgress.progressProperty().bind(progressProperty);
    // TEST:
    Tile t2 = new Tile(new Letter('A', 3, 5), new Field(3, 5, 1, 0));
    t2.setOnRack(true);
    addTile(t2);
    Tile t1 = new Tile(new Letter('C', 5, 5), new Field(5, 5, 1, 0));
    t1.setOnGameBoard(true);
    addTile(t1);
    // TEST ENDE
  }

  /**
   * 
   * Listener methods that are executed upon Player UI Interaction
   * 
   */

  /**
   * Method to be executed when a player clicks on the "Send" button of the chat area in the
   * GamePanel The ChatController handles this event and gets the text from the textfield of the
   * chat area.
   * 
   * @param event
   */
  @FXML
  public void sendMessage(ActionEvent event) {
    this.cc.sendChatMessage(GamePanel.player.getNickname(), this.textField.getText());
  }

  @FXML
  public void rackPressed(MouseEvent event) {

  }

  @FXML
  public void rackDragged(MouseEvent event) {

  }

  @FXML
  public void rackReleased(MouseEvent event) {

  }

  @FXML
  public void boardPressed(MouseEvent event) {

  }

  @FXML
  public void boardDragged(MouseEvent event) {

  }

  @FXML
  public void boardReleased(MouseEvent event) {

  }



  /**
   * Listener method that is executed, when a rectangle in the GridPane 'rack' is clicked. The
   * method checks which tile was selected before and causes the right action for the desired tile
   * movement.
   * 
   * @param event
   */
  @FXML
  public void rackClicked(MouseEvent event) {
    Node node = (Node) event.getSource();
    int[] pos = getPos(node, true);
    int x = pos[0];
    int y = pos[1];
    // // GUI test start:
    // setSelectedTileOnRack(true);
    // coordinates[0] = x;
    // // GUI test ende.
    if (x == coordinates[0] && y == coordinates[1]) { // deselect tile
      setSelectedTileOnRack(false);
      System.out.println("deselect rack tile");
    } else if (selectedTileOnRack == false && selectedTileOnGrid == false) { // select tile
      setSelectedTileOnRack(true);
      coordinates[0] = x;
      coordinates[1] = -1;
      System.out.println("rack tile selected");
    } else if (selectedTileOnRack) { // exchange tiles on rack
      player.reorganizeRackTile(x, coordinates[0]);
      setSelectedTileOnRack(false);
      System.out.println("reorganizeRackTiles");
    } else if (selectedTileOnGrid) { // try to move tile from board to rack - sender!
      sendTileMove(player.getNickname(), coordinates[0], coordinates[1], x, y);
      setSelectedTileOnGrid(false);
      System.out.println("move tile from grid to rack: " + coordinates[0] + ", " + coordinates[1]);
    }
  }

  /**
   * Listener method that is executed, when a rectangle in the GridPane 'grid' is clicked. The
   * method checks which tile was selected before and causes the right action for the desired tile
   * movement.
   *
   * @param event
   */
  @FXML
  public void boardClicked(MouseEvent event) {
    Node node = (Node) event.getSource();
    int[] pos = getPos(node, false);
    int x = pos[0];
    int y = pos[1];
    // // GUI test start:
    // if (selectedTileOnRack) {
    // Tile t2 = new Tile(new Letter('A', 3, 5), new Field(3, 5, 1, 0));
    // t2.setOnRack(true);
    // moveToGamePanel(t2, x, y);
    // }
    // // GUI test ende.
    if (x == coordinates[0] && y == coordinates[1]) { // deselect tile
      setSelectedTileOnGrid(false);
      System.out.println("deselect grid tile");
    } else if (selectedTileOnRack == false && selectedTileOnGrid == false) { // select tile
      setSelectedTileOnGrid(true);
      coordinates[0] = x;
      coordinates[1] = -1;
      System.out.println("select grid tile");
    } else if (selectedTileOnGrid) { // exchange tiles on board
      sendTileMove(player.getNickname(), coordinates[0], coordinates[1], x, y);
      setSelectedTileOnGrid(false);
      System.out.println("exchange grid tiles");
    } else if (selectedTileOnRack) { // move tile from rack to board
      System.out.println("rack to grid: coords, x, y: " + coordinates[0] + x + y);
      player.moveToGameBoard(coordinates[0], x, y);
      setSelectedTileOnRack(false);
    }
  }

  /**
   * 
   * @param event
   */
  @FXML
  public void completeTurn(ActionEvent event) {
    String userName = "Martin";
    sendCommitTurn(userName);
  }

  /**
   * 
   * @param event
   */
  @FXML
  public void skipAndChange(ActionEvent event) {

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
   * Updates Lobbychat by using the updateChat method in the Chat Controller.
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
  public void indicatePlayerTurn(String nickName) {
    if (player1.getText().equals(nickName)) {
      // Effekt fï¿½r player 1
    } else if (player2.getText().equals(nickName)) {
      // Effekt fï¿½r player 2
    } else if (player3.getText().equals(nickName)) {
      // Effekt fï¿½r player 3
    } else if (player4.getText().equals(nickName)) {
      // Effekt fï¿½r player 4
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
        column -= 5;
      }
      rackTiles[row][column] = new VisualTile(Character.toString(letter), tileValue, true);
      rackTiles[row][column].setMouseTransparent(true);
      rack.add(rackTiles[row][column], column, row);
      GridPane.setHalignment(rackTiles[row][column], HPos.CENTER);
      GridPane.setValignment(rackTiles[row][column], VPos.BOTTOM);
      GridPane.setMargin(rackTiles[row][column], new Insets(0, 0, 5.5, 0));

    } else {
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
    if (isOnRack) {
      for (Node node : rack.getChildren()) {
        Integer columnIndex = GridPane.getColumnIndex(node);
        Integer rowIndex = GridPane.getRowIndex(node);
        if (columnIndex == null) {
          x = 0;
        } else {
          x = columnIndex.intValue();
        }
        if (rowIndex == null) {
          y = 0;
        } else {
          y = rowIndex.intValue();
        }
        if (node instanceof Parent && x == column && y == row) {
          rack.getChildren().remove(node);
          break;
        }
      }
    } else {
      for (Node node : board.getChildren()) {
        Integer columnIndex = GridPane.getColumnIndex(node);
        Integer rowIndex = GridPane.getRowIndex(node);
        if (columnIndex == null) {
          x = 0;
        } else {
          x = columnIndex.intValue();
        }
        if (rowIndex == null) {
          y = 0;
        } else {
          y = rowIndex.intValue();
        }
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
    // // TODO: zug rückgängig machen
    // } else if (player2.getText().equals(nickName)) {
    // // TODO: zug rückgängig machen
    // } else if (player3.getText().equals(nickName)) {
    // // TODO: zug rückgängig machen
    // } else if (player4.getText().equals(nickName)) {
    // // TODO: zug rückgängig machen
    // }
    CustomAlert alert = new CustomAlert(AlertType.WARNING);
    alert.setTitle("Invalid Turn");
    alert.setHeaderText("Your turn was not valid");
    alert.setContentText("Please try a new turn, according to the scrabble rules");
    alert.initStyle(StageStyle.UNDECORATED);

    alert.getDialogPane().getStylesheets()
        .add(getClass().getResource("DialogPaneButtons.css").toExternalForm());
  }

  /**
   * this method updates the score of an Player and shows the new score in the UI
   * 
   * @param nickName
   * @param turnScore
   * @throws Exception
   */
  public void updateScore(String nickName, int turnScore) throws Exception {
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
      throw new Exception("Player " + nickName + "is not part of the GameBoard");
    }

  }

  /**
   * 
   * Methods to override sender interface methods; documentation in interface
   * 
   * TODO: Sollte man die Methoden nicht doch lieber in ClientUi auslagern?
   */

  @Override
  public void sendChatMessage(String sender, String message) {}

  @Override
  public void sendTileMove(String nickName, int oldX, int oldY, int newX, int newY) {
    Message m = new MoveTileMessage(nickName, oldX, oldY, newX, newY);
    sendMessage(m);
  }

  @Override
  public void sendCommitTurn(String nickName) {
    System.out
        .println("method sendCommitTurn wurde aufgerufen, ausgelï¿½st von " + nickName + "\n");
    Message m = new CommitTurnMessage(nickName);
    sendMessage(m);
  }

  @Override
  public void sendDisconnectMessage(String nickName) {
    Message m = new DisconnectMessage(nickName);
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
    return coordinates;
  }

  public static void setCoordinates(int coordinates[]) {
    GamePanelController.coordinates = coordinates;
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
    }
    return result;
  }

}

