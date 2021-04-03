package gui;



import network.messages.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mechanic.Player;
/**
 * 
 * @author nilbecke
 * Handles all User inputs in the Lobby Screen as well as the connection of players 
 *
 */
public class LobbyScreenController implements EventHandler<ActionEvent> {
	
	private Player player;

	@FXML
	private Label countdown;
	@FXML
	private Label ip;
	 @FXML
	 private TextField input;
	 @FXML
	 private TextArea chat;
	 @FXML
	 private Button start;
	

	/**
	 * Set up labels etc before launching the lobby screen
	 */
	@FXML
	public void initialize() {
		this.player = LobbyScreen.getPlayer();
		if (this.player.getIsHost()) {
			InetAddress inetAddress = null;
			try {
				inetAddress = InetAddress.getLocalHost();
				//Player is Host
				this.player.setLocation(inetAddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			this.ip.setText("Link:  " + inetAddress.getHostAddress());
		} else {
			this.start.setOpacity(0.4);
			this.start.setDisable(true);
			this.ip.setOpacity(0);
		}
	}

	/**
	 * Handles all user inputs in the LobbyScreen
	 */
	@Override
	public void handle(ActionEvent e) {
		String s = ((Node) e.getSource()).getId();
		switch (s) {
		case "leavelobby":
			leaveGame();
			break;
		case "send":
		case "sendText":
			updateChat(this.input.getText(), this.player.getNickname());
			this.input.setText("");
			break;
		case "start":
			startGame();
			Stage st = (Stage) ((Button)e.getSource()).getScene().getWindow();
			st.close();
		}
	}
	
	/**
	 * Starts the countdown before the game launches
	 */
	public void startGame() {
		new StartGameMessage(this.player.getNickname(),10);
		try {
			new ClientUI().start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lets a player disconnect from the current game. If the leaving player is the
	 * host, the lobby closes
	 * 
	 */
	public void leaveGame() {
		
		
	}
	/** 
	 * updates the chat for all connected players
	 * @param message is the message to be added in the chat
	 * @param sender is the player sending the message
	 * @param time represents the time when the given message was sent 
	 */
	public void updateChat(String message, String sender) {
		if(message.length()==0) {
			return;
		}
		Message m = new SendChatMessage(message, sender, LocalDate.now());
	}

}
