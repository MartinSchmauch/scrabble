package gui;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// ** @author mschmauc

public class TestGamePanelWithFXML extends Application {

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    VBox box = FXMLLoader.load(new File(FileParameters.fxmlPath).toURI().toURL());
    Scene scene1 = new Scene(box);
    primaryStage.setScene(scene1);
    primaryStage.show();
  }

}
