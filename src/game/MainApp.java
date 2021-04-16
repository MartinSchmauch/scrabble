package game;

import gui.LoginScreen;

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
      LoginScreen.main(args);
    } catch (Exception e) {
      System.out.println("It is doomed");
    }
  }

}
