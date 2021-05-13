package gui;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

/**
 * This class is an extention of the JavaFX Alert Class to provide styling options like css support
 * for alerts used within the application.
 * 
 * @author nilbecke
 *
 */

public class CustomAlert extends Alert {

  private double xOffset;
  private double yOffset;

  /**
   * Creates a custom alert with a css style applied.
   * 
   * @param arg0 defines the alert type
   */

  public CustomAlert(AlertType arg0) {
    super(arg0);
    this.getDialogPane().getStylesheets()
        .add(getClass().getResource("/DialogPaneButtons.css").toExternalForm());
    this.initStyle(StageStyle.UNDECORATED);
    DialogPane dp = this.getDialogPane();
    dp.getScene().getRoot().setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
      }
    });
    dp.getScene().getRoot().setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        dp.getScene().getWindow().setX(event.getScreenX() - xOffset);
        dp.getScene().getWindow().setY(event.getScreenY() - yOffset);
      }
    });

  }

  public void changeButtonText(String content, ButtonType type) {
    ((Button) this.getDialogPane().lookupButton(type)).setText(content);
  }

}
