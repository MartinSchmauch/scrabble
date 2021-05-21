package gui;

// ** @author mschmauc */

/**
 * This class supposed to ensure full file path functionality across all devices by using the
 * file.separator property. File paths for all external resource files used for the GUI are saved in
 * this class as public variable
 */

public class FileParameters {

  public final static String sep = System.getProperty("file.separator");
  public final static String datadir = System.getProperty("user.home") + sep + ".scrabble" + sep;

  public final static String generaldir = datadir + "general";

  public final static String fxmlPath = datadir + "fxml" + sep;
}
