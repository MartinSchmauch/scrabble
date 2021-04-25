package gui;

import java.io.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mechanic.Field;
import mechanic.Letter;
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

public class GamePanelController extends ClientUI implements Sender {

  // private Player player;
  private ClientProtocol cp;
  private Server server;
  private static boolean selectedTileOnGrid = false;
  private static boolean selectedTileOnRack = false;
  private static int coordinates[] = new int[2];
  private ChatController cc;

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
  private GridPane grid, rack; // grid is the Main Game Board
  @FXML
  private ProgressBar timeProgress;

  private static GamePanelController instance;

  public static GamePanelController getInstance() {
    if (instance == null) {
      instance = new GamePanelController();
    }
    return instance;
  }

  public GamePanelController() { // being called before @FXML annotated fields were populated
    System.out.println("Controller erzeugt \n");
  }

  public void initialize() { // being called after @FXML annotated fields were populated
    ClientUI.player = ClientUI.getInstance().getPlayer(); // TODO: ClientUI zu GamePanel umbenennen?
    cc = new ChatController(ClientUI.player);
    chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");
    SimpleIntegerProperty letterProperty = new SimpleIntegerProperty(17); // TODO: 17 durch referenz
                                                                          // ersetzen
    remainingLetters.textProperty().bind(letterProperty.asString());
    SimpleStringProperty timerProperty = new SimpleStringProperty("Timer Referenz!");
    timer.textProperty().bind(timerProperty);
    SimpleDoubleProperty progressProperty = new SimpleDoubleProperty(0.5); // TODO: restliche zeit
                                                                           // als anteil von 1 hier
                                                                           // einfügen
    timeProgress.progressProperty().bind(progressProperty);
    // TEST:
    Tile t2 = new Tile(new Letter('A', 3, 5), new Field(3, 5, 1, 0));
    t2.setOnRack(true);
    addTile(t2);
    Tile t1 = new Tile(new Letter('C', 5, 5), new Field(5, 5, 1, 0));
    t1.setOnGameBoard(true);
    addTile(t1);
  }

  /**
   * 
   * Listener methods that are executed upon Player UI Interaction
   * 
   */

  @FXML
  public void sendMessage(ActionEvent event) {
    this.cc.sendChatMessage(ClientUI.player.getNickname(), this.textField.getText());
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
    if (selectedTileOnRack == false && selectedTileOnGrid == false) {
      if (cp.getPlayer().getRackTile(x) != null) {
        setSelectedTileOnRack(true);
        coordinates[0] = x;
        coordinates[1] = -1;
      }
    }
    if (selectedTileOnRack) {
      if (cp.getPlayer().getRackTile(x) != null) {
        if (cp.getPlayer().getRackTile(x).getField().getxCoordinate() == x) {
          setSelectedTileOnRack(false); // selected tile gets deselected
        } else {// switch positions of tiles on rack
          // TODO: lokal rack tile positionen in der player klasse ändern
          setSelectedTileOnRack(false);
        }
      } else { // move tile to empty field
        // TODO: lokal rack tile positionen in der player klasse ändern
        setSelectedTileOnRack(false);
      }
    }
    if (selectedTileOnGrid) {
      if (cp.getPlayer().getRackTile(x) != null) { // exchange tiles on rack and board
        // case: check, wether you can exchange tiles
        // when true: setSelectedTileOnGrid(false);
        // when false: error message
      } else { // move tile from board to rack
        // case: check, wether you can move tile to rack
        // when true: setSelectedTileOnGrid(false);
        // when false: error message
      }
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
  public void gridClicked(MouseEvent event) {
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
    if (selectedTileOnRack == false && selectedTileOnGrid == false) {
      if (cp.getPlayer().getRackTile(x) != null) {
        setSelectedTileOnGrid(true);
        coordinates[0] = x;
        coordinates[1] = y;
      } else {
        // TODO: maybe an error sound to occur?
      }
    }
    if (selectedTileOnRack) {
      if (cp.getGameState().getGameBoard().getField(x, y).getTile() != null) {
        // case: exchange tiles on rack and grid
        // check wether its is a valid move
        // sendTileMove(cp.getGameState().getCurrentPlayer(), null, x, y); // stein 1
        // sendTileMove(cp.getGameState().getCurrentPlayer(), null, x, y); // stein 2
        // if yes: setSelectedTileOnRack(false);
      } else {
        // MAINCASE: move tile from rack(coordinates[0]) to empty grid field(x,y)
        // check wether it is a valid move
        // sendTileMove(player.getNickname(), player.getRackTile(coordinates[0]), x, y);
        // if yes: setSelectedTileOnRack(false);
      }
    } else if (selectedTileOnGrid) {
      if (cp.getGameState().getGameBoard().getField(x, y).getTile() != null) {
        // case: exchange tiles on grid
        // check, wether you can exchange tiles
        // sendTileMove(player.getNickname(), player.getRackTile(x), coordinates[0],
        // coordinates[1]); // stein
        // 1
        // sendTileMove(cp.getGameState().getCurrentPlayer(), null, x, y); // stein 2
        // when true: setSelectedTileOnGrid(false);
      } else {
        // case: move tile on grid to empty field
        // check wether you can exchange tiles
        // sendTileMove(cp.getGameState().getCurrentPlayer(), null, x, y);
        // when true: setSelectedTileOnGrid(false);
      }
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
      VisualTile newTile = new VisualTile(Character.toString(letter), tileValue, true);
      newTile.setMouseTransparent(true);
      rack.add(newTile, column, row);
      GridPane.setHalignment(newTile, HPos.CENTER);
      GridPane.setValignment(newTile, VPos.BOTTOM);
      GridPane.setMargin(newTile, new Insets(0, 0, 5.5, 0));

    } else {
      VisualTile newTile = new VisualTile(Character.toString(letter), tileValue, false);
      newTile.setMouseTransparent(true);
      grid.add(newTile, column, row);
      GridPane.setHalignment(newTile, HPos.CENTER);
      GridPane.setValignment(newTile, VPos.BOTTOM);
      GridPane.setMargin(newTile, new Insets(0, 10, 8, 0));
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
      for (Node node : grid.getChildren()) {
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
          grid.getChildren().remove(node);
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
    if (player1.getText().equals(nickName)) {
      // TODO: zug rückgängig machen
    } else if (player2.getText().equals(nickName)) {
      // TODO: zug rückgängig machen
    } else if (player3.getText().equals(nickName)) {
      // TODO: zug rückgängig machen
    } else if (player4.getText().equals(nickName)) {
      // TODO: zug rückgängig machen
    }
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.show();
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
    sendMessageToServer(m);
  }

  @Override
  public void sendCommitTurn(String nickName) {
    System.out
        .println("method sendCommitTurn wurde aufgerufen, ausgelï¿½st von " + nickName + "\n");
    Message m = new CommitTurnMessage(nickName);
    sendMessageToServer(m);
  }

  @Override
  public void sendDisconnectMessage(String nickName) {
    Message m = new DisconnectMessage(nickName);
    sendMessageToServer(m);
  }

  /**
   * Method to send a Message to the server instance, using a Client Protocoll instance
   * 
   * @param m
   */

  public void sendMessageToServer(Message m) {
    try {
      if (getConnection() != null) {
        getConnection().sendToServer(m);
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  // public Player getPlayer() {
  // return ClientUI.getPlayer();
  // }


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

