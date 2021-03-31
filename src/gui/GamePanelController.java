package gui;

import java.io.IOException;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mechanic.Field;
import mechanic.Tile;
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

  @Override
  public void sendChatMessage(String message, Date timeStamp, String sender) {
    Message m = new SendChatMessage(sender, message, timeStamp);
    sendMessageToServer(m);
  }

  @Override
  public void sendTileMove(Tile tile, Field field) {
    // TODO Auto-generated method stub

  }

  @Override
  public Boolean sendCommitTurn() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void sendDisconnectMessage(String playerID) {
    // TODO Auto-generated method stub

  }

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

