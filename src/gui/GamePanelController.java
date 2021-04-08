package gui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mechanic.Field;
import mechanic.Tile;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.SendChatMessage;

/**
 * @author mschmauc
 * 
 *         This class is the Controller Class for the Main Gamel Panel UI for the Client
 */

public class GamePanelController extends ClientUI implements Sender {

  public GamePanelController() {
    System.out.println("Controller erzeugt \n");
  }

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
  private GridPane grid;

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
   * This method updates the TextArea in the Client UI and puts the newest Chat message from the
   * method parameter in the TextArea including timestamp and sender. Hereby a ChatMessage is built
   * as string containing timeStamp, sender and message and puts them to the textArea as new row
   * 
   * @param sender
   * @param message
   * @param timeStamp
   */
  public void updateChat(String message, LocalDate timeStamp, String sender) {
    String newChatMessage = "";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    newChatMessage = newChatMessage + sender + ", ";
    newChatMessage = newChatMessage + timeStamp.format(dtf) + ": ";
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
    int row = tile.getField().getyCoordinate();
    grid.add(tile1, column, row);
  }

  /**
   * This method updates a Tile on the UI by putting the tile on a new position provided by the
   * newField parameter and removing it from the last position.
   * 
   * @param tile
   * @param newField
   */
  public void moveTile(Tile tile, Field newField) {

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
  public void sendChatMessage(String sender, String message, LocalDate timeStamp) {
    Message m = new SendChatMessage(sender, message, timeStamp);
    sendMessageToServer(m);
  }

  @Override
  public void sendTileMove(Tile tile, Field field) {
    // Message m = new TileRequestMessage(tile, field);
    // sendMessageToServer(m);
  }

  @Override
  public void sendCommitTurn(String nickName) {
    System.out.println("method sendCommitTurn wurde aufgerufen, ausgel�st von " + nickName + "\n");
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

}

