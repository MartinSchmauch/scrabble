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
   * Opens ScrabbleRulebook.pdf Can be accessed statically as the Rules don't change.
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
