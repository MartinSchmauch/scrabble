package gui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import mechanic.Field;
import mechanic.Letter;
import mechanic.Player;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.SendChatMessage;
import network.server.Server;

/**
 * @author mschmauc
 * 
 *         This class is the Controller Class for the Main Gamel Panel UI for the Client
 */

public class GamePanelController extends ClientUI implements Sender {

  public GamePanelController() {
    System.out.println("Controller erzeugt \n");
  }

  private Player player;
  private ClientProtocol cp;
  private Server server;
  @FXML
  private TextArea textArea;
  @FXML
  private TextField textField;
  @FXML
  private Button sendButton;
  @FXML
  private Button skipAndChange;
  @FXML
  private Text playerOnePoints;
  @FXML
  private Text playerTwoPoints;
  @FXML
  private Text playerThreePoints;
  @FXML
  private Text playerFourPoints;
  @FXML
  private Text player1;
  @FXML
  private Text player2;
  @FXML
  private Text player3;
  @FXML
  private Text player4;
  @FXML
  private Rectangle tile1;
  @FXML
  private GridPane grid; // Main Game Panel
  @FXML
  private GridPane rack;



  private static GamePanelController instance;

  public static GamePanelController getInstance() {
    if (instance == null) {
      instance = new GamePanelController();
    }
    return instance;
  }


  /**
   * 
   * Listener methods that are executed upon Player UI Interaction
   * 
   */

  @FXML
  public void testMessage(ActionEvent event) {
    System.out.println("Test Message from 'Send' Button \n");
    textFieldToTextArea();
    try {
      updateScore("Player 2", 1);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    addTile(new Tile(new Letter('C', 3, 5), new Field(3, 5, 7, 0)));
  }

  @FXML
  public void rackClicked(MouseEvent event) {
    System.out.println("rackClicked wird aufgerufen");
    Node node = (Node) event.getSource();
    int x;
    int y;
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
    System.out.println("column: " + x + " row: " + y);
  }

  @FXML
  public void rackPaneDragged(DragEvent event) {
    System.out.println("rackPanelDragged Methode wurde aufgerufen");
  }

  @FXML
  public void rackPaneDropped(DragEvent event) {
    System.out.println("rackPanelDropped Methode wurde aufgerufen");
  }

  @FXML
  public void gamePanelDragged(DragEvent event) {
    System.out.println("gamePanelDragged Methode wurde aufgerufen");
  }

  @FXML
  public void gamePanelDropped(DragEvent event) {
    System.out.println("gamePanelDropped Methode wurde aufgerufen");
  }

  @FXML
  public void completeTurn(ActionEvent event) {
    String userName = "Martin";
    sendCommitTurn(userName);
  }


  /** puts a message from the textField to the textArea */
  public void textFieldToTextArea() {
    toTextArea(this.textField.textProperty().getValue());
    this.textField.textProperty().setValue("");
  }

  /** puts a String from param in a new row in the TextArea */
  public void toTextArea(String message) {
    String chatHistory = this.textArea.textProperty().getValue();
    this.textArea.textProperty().setValue(chatHistory + "\n" + message);
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
   * This method updates the TextArea in the Client UI and puts the newest Chat message from the
   * method parameter in the TextArea including timestamp and sender. Hereby a ChatMessage is built
   * as string containing timeStamp, sender and message and puts them to the textArea as new row
   * 
   * @param sender
   * @param message
   * @param dateTime
   */
  public void updateChat(String message, LocalDateTime dateTime, String sender) {
    String newChatMessage = "";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    newChatMessage = newChatMessage + sender + ", ";
    newChatMessage = newChatMessage + dateTime.format(dtf) + ": ";
    newChatMessage = newChatMessage + message;
    toTextArea(newChatMessage);
  }

  /**
   * This method highlights the player that is playing his turn at the moment by visually
   * emphasizing the players nickname on the game panel.
   * 
   * @param nickName
   */
  public void indicatePlayerTurn(String nickName) {
    if (player1.getText().equals(nickName)) {
      // Effekt f�r player 1
    } else if (player2.getText().equals(nickName)) {
      // Effekt f�r player 2
    } else if (player3.getText().equals(nickName)) {
      // Effekt f�r player 3
    } else if (player4.getText().equals(nickName)) {
      // Effekt f�r player 4
    }
  }

  /**
   * This method adds a tile at a location at the game panel for instance when a player gets new
   * tiles after he has put some tiles on the game board.
   * 
   * @param tile
   */
  public void addTile(Tile tile) {
    char letter = tile.getLetter().getChar();
    int tileValue = tile.getValue();
    int column = tile.getField().getxCoordinate();
    int row = 0;
    if (column > 5) { // case: tile is in the second row of the rack
      row = 1;
      column = column - 5;
    }
    StackPane sP = new StackPane();
    Rectangle r1 = new Rectangle();
    r1.setHeight(26);
    r1.setWidth(26);
    r1.setArcHeight(10);
    r1.setArcWidth(10);
    r1.setOnDragDetected((MouseEvent event) -> {
      // We want the textArea to be dragged. Could also be copied.
      Dragboard db = textArea.startDragAndDrop(TransferMode.MOVE);
      // Put a string on a dragboard as an identifier
      ClipboardContent content = new ClipboardContent();
      content.putString(textArea.getId());
      db.setContent(content);
      // Consume the event
      event.consume();
    });
    // MouseControlUtil.makeDraggable(r1);
    Paint rectangle = Color.web("BLUE");
    r1.setFill(rectangle);
    Text t1 = new Text(25, 27, Character.toString(letter));
    Text t2 = new Text(0, 0, String.valueOf(tileValue));
    t2.setFont(new Font(7));
    sP.getChildren().add(r1);
    sP.getChildren().add(t1);
    sP.getChildren().add(t2);
    rack.add(sP, column, row);
  }


  /**
   * This method updates a Tile on the UI by putting the tile on a new position provided by the
   * newField parameter and removing it from the last position.
   * 
   * @param tile
   * @param newField
   */
  public void moveTile(Tile tile, int newXCoordinate, int newYCoordinate) {

  }

  /**
   * This method removes a tile on the UI. This might be the case when another player removes a tile
   * during his turn.
   * 
   * @param tile
   */
  public void removeTile(Tile tile) {

  }

  /**
   * This method is getting returned to the UI after the sendTileMove method has been triggered from
   * the UI. A visual confirmation for a valid turn is shown in the UI.
   * 
   * @param nickName
   */
  public void indicateInvalidTurn(String nickName) {
    if (player1.getText().equals(nickName)) {
      // TODO: zug r�ckg�ngig machen
    } else if (player2.getText().equals(nickName)) {
      // TODO: zug r�ckg�ngig machen
    } else if (player3.getText().equals(nickName)) {
      // TODO: zug r�ckg�ngig machen
    } else if (player4.getText().equals(nickName)) {
      // TODO: zug r�ckg�ngig machen
    }
  }

  /**
   * this method updates the score of an Player and shows the new score in the UI
   * 
   * @param nickName
   * @param turnScore
   * @throws Exception
   */
  public void updateScore(String nickName, int turnScore) throws Exception {
    String newScore;
    if (turnScore != 1) {
      newScore = turnScore + " Points";
    } else {
      newScore = turnScore + " Point";
    }

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
  public void sendChatMessage(String sender, String message) {
    Message m = new SendChatMessage(sender, message, LocalDateTime.now());
    sendMessageToServer(m);

  }

  @Override
  public void sendTileMove(String nickName, Tile tile, int newX, int newY) {
    int oldX = tile.getField().getxCoordinate();
    int oldY = tile.getField().getyCoordinate();
    Message m = new MoveTileMessage(nickName, tile, oldX, oldY, newX, newY);
    sendMessageToServer(m);
  }

  @Override
  public void sendCommitTurn(String nickName) {
    System.out
        .println("method sendCommitTurn wurde aufgerufen, ausgel�st von " + nickName + "\n");
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

  public Player getPlayer() {
    return player;
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

}

