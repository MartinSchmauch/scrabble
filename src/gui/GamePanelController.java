package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** @author mschmauc */

public class GamePanelController extends ClientUI {

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


}

