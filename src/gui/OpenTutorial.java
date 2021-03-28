package gui;

/** @Author nilbecke **/

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/** This Class opens the Tutorial in the user´s pdf viewer **/
public class OpenTutorial {

	/**
	 * Opens ScrabvleRulebook.pdf
	 * Can be accessed statically as the Rules don´t change
	 */
	
	public static void open() {
		File tutorial = new File(System.getProperty("user.dir") + "/src/gui/images/ScrabbleRules.pdf");
		if (!Desktop.isDesktopSupported()) {
			System.out.println("not supported");
			return;
		}
		try {
			Desktop.getDesktop().open(tutorial);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("File Could Not Open");
		}
	}
}
