package gui;

// ** @author mschmauc

/**
 * This class supposed to ensure full file path functionality across all devices by using the
 * file.separator property. File paths for all external resource files used for the GUI are saved in
 * this class as public variable
 */

public class FileParameters {

  private final static String sep = System.getProperty("file.separator");
  private final static String datadir = System.getProperty("user.dir") + sep + "resources";

  public final static String fxmlPath = datadir + sep + "Test_MainGamePanel_Martin.fxml";
}
