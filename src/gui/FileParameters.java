package gui;

//** @author mschmauc

public class FileParameters {

	private final static String sep = System.getProperty("file.separator");
	private final static String datadir = System.getProperty("user.dir") +  sep + "resources";
	
	public final static String fxmlPath = datadir + sep + "testGUI.fxml";
}
