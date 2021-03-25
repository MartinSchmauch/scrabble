package gui.LoginScreen;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OpenTutorial {

	public void open() {
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
