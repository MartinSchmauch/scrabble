package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 * This class is an extention of the JavaFX Alert Class to provide styling options like css support
 * for alerts used within the application.
 * 
 * @author nilbecke
 *
 */

public class CustomAlert extends Alert {

  public CustomAlert(AlertType arg0) {
    super(arg0);
    this.getDialogPane().getStylesheets()
        .add(getClass().getResource("DialogPaneButtons.css").toExternalForm());
  }

  public void changeButtonText(String content, ButtonType type) {
    ((Button) this.getDialogPane().lookupButton(type)).setText(content);
  }

}
