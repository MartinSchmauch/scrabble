package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * @author nilbecke
 * Action Handler for the game lobby
 * **/

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LobbyScreenController  implements EventHandler<ActionEvent>{
	


	@FXML
	private Label countdown;
	@FXML 
	private Label ip;
	
	
	/**
	 * Set up labels etc before launching the lobby screen
	 */
	@FXML
	public void initialize() {
		System.out.println();
		if (LobbyScreen.getLobby().getHost()) {
			InetAddress inetAddress = null;
			try {
				inetAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			this.ip.setText("Link:  " + inetAddress.getHostAddress());
		}else {
			this.ip.setOpacity(0);
		}
	}

	
	@Override
	public void handle(ActionEvent e) {
		Button b = (Button)e.getSource();
		this.countdown.setOpacity(1);
		for(int i=10; i>=0; i--) {
			this.countdown.setText(i+"");
			System.out.println(this.countdown.getText());
			try {
				Thread.sleep(100);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

}
