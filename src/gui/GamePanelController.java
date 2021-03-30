package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/** @author mschmauc */

public class GamePanelController {

  public GamePanelController() {
    System.out.println("Controller erzeugt");
  }

  @FXML
  public void testMessage(ActionEvent event) {
    System.out.println("Test Message from 'Send' Button");
  }


}

