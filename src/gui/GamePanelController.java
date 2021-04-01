package gui;

import java.io.IOException;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mechanic.Field;
import mechanic.Tile;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.SendChatMessage;

/** @author mschmauc */

public class GamePanelController extends ClientUI implements Sender {

  public GamePanelController() {
    System.out.println("Controller erzeugt");
  }

  @FXML
  private TextArea tA;
  @FXML
  private TextField tF;

  @FXML
  public void testMessage(ActionEvent event) {
    System.out.println("Test Message from 'Send' Button");
    textFieldToTextArea();
  }

  public void textFieldToTextArea() {
    this.tA.textProperty().setValue(this.tF.textProperty().getValue());
    this.tF.textProperty().setValue("");
  }

  /**
   * 
   * Methods to be used by the ClientProtocol to change the UI of the Client
   * 
   */

  public void updateChat(String message, Date timeStamp, String sender) {

  }

  public void indicatePlayerTurn(String nickName) {

  }

  public void addTile(Tile tile) {

  }

  public void moveTile(Tile tile, Field newField) {

  }

  public void removeTile(Tile tile) {

  }

  public void updateScore(String nickName, int turnScore) {

  }

  /**
   * 
   * Methods to override sender interface methods
   * 
   */

  @Override
  public void sendChatMessage(String message, Date timeStamp, String sender) {
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

