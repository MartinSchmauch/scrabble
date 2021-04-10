package game;

import gui.LoginScreenFXML;
import javafx.stage.Stage;

/** @author nilbecke
 * 
 * 
 *This class launches the application
 */

public class MainApp {

	public static void main(String[] args) {
		try {
			new LoginScreenFXML().start(new Stage());
		} catch (Exception e) {
				System.out.println("It is doomed");
		}
	}

}
