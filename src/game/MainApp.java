package game;

import gui.LoginScreenFXML;

/**
 * @author nilbecke
 * 
 * 
 *         This class launches the application
 */

public class MainApp {

  public static void main(String[] args) {
    try {
      LoginScreenFXML.main(args);
    } catch (Exception e) {
      System.out.println("It is doomed");
    }
  }

}
