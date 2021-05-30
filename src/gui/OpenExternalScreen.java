package gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * This Class opens the Tutorial in the user's pdf viewer.
 *
 * @author nilbecke
 */
public class OpenExternalScreen {

  /**
   * Opens an external file given by its path.
   *
   * @param path as the path to the file. 
   */

  public static void open(String path) {
    File tutorial = new File(path);
    if (!Desktop.isDesktopSupported()) {
      CustomAlert.showWarningAlert("ERROR", "File could not open!");
      return;
    }
    try {
      Desktop.getDesktop().open(tutorial);
    } catch (IOException e) {
      e.printStackTrace();
      CustomAlert.showWarningAlert("ERROR", "File could not open!");
    }
  }
}
