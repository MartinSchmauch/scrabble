package game;

import gui.LoginScreenFXML;
import javafx.stage.Stage;

/**
 * This class launches the application.
 * 
 * @author nilbecke
 * 
 * 
 * 
 */

public class MainApp {
  /**
   * Launches the game by starting the LoginScreen.
   * 
   * @param args: Arguments of class
   */
  public static void main(String[] args) {
    try {
      new LoginScreenFXML().start(new Stage());
    } catch (Exception e) {
      System.out.println("It is doomed");
    }
  }

}
