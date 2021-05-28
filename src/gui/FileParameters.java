package gui;

// ** @author mschmauc */

/**
 * This class supposed to ensure full file path functionality across all devices by using the
 * file.separator property. File paths for all external resource files used for the GUI are saved in
 * this class as public variable
 */

public class FileParameters {

  public static final String sep = System.getProperty("file.separator");
  public static final String datadir = System.getProperty("user.home") + sep + ".scrabble" + sep;

  public static final String generaldir = datadir + "general";

  public static final String fxmlPath = datadir + "fxml" + sep;
}
