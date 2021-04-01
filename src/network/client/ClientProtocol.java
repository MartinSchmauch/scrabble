/** @author lurny */
package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import general.IDGenerator;
import gui.ClientUI;
import gui.GamePanelController;
import gui.LobbyScreenController;
import mechanic.PlayerData;
import network.messages.*;

public class ClientProtocol extends Thread {
	private ClientUI cui;
	private GamePanelController gpc;
	private LobbyScreenController lpc;
	private PlayerData playerData;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;
	
	public ClientProtocol(String ip, int port, PlayerData playerData, ClientUI cui, GamePanelController gpc, LobbyScreenController lpc) {
		this.cui = cui;
		try {
			this.gpc = gpc;
			this.lpc = lpc;
			this.playerData = playerData;
			this.clientSocket = new Socket(ip, port);
			this.out = new ObjectOutputStream(clientSocket.getOutputStream());
			this.in = new ObjectInputStream(clientSocket.getInputStream());
	
			this.out.writeObject(new ConnectMessage(this.playerData));
			out.flush();
			out.reset();
			System.out.println("Local Port (Client): " + this.clientSocket.getLocalPort());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not establish connection to " + ip + ":" + port);
		}
	}	
	
	public boolean isOK(){
		return (clientSocket != null) && (clientSocket.isConnected()) && !(clientSocket.isClosed()); 
	}
	
	// Verarbeitung der vom Server empfangenen Nachrichten
	public void run() {
		while (running) {
			try {
				Message m = (Message) in.readObject(); // read message from server

				switch (m.getMessageType()) {
				case CONNECTION_REFUSED: 
					ConnectionRefusedMessage mrMessage = (ConnectionRefusedMessage)m;					
					break;
				case SHUTDOWN:
					ShutdownMessage sMessage = (ShutdownMessage) m;
//					cui.showAlert("Server is going down immediately");
//					disconnect(); // Verbindung trennen 
//					cui.resetUI(); // gui zuruecksetzen (zurück zu login)
					break;
				case UPDATE_FIELD:
					UpdateFieldMessage ufMessage = (UpdateFieldMessage) m;
					break;
				case TILE_RESPONSE:
					TileResponseMessage trMessage = (TileResponseMessage) m;
					break;
				case TURN_RESPONSE:
					TurnResponseMessage turnrMessage = (TurnResponseMessage) m;
					break;
				case LOBBY_STATUS:
					LobbyStatusMessage lsMessage = (LobbyStatusMessage) m;
					break;
				case START_GAME:
					StartGameMessage sgMessage = (StartGameMessage) m;
					break;
				case GAME_STATISTIC:
					GameStatisticMessage gsMessage = (GameStatisticMessage) m;
					break;
				case UPDATE_CHAT:
					UpdateChatMessage ucMessage = (UpdateChatMessage) m;
					break;
				default:
					break;
				}
			} catch (ClassNotFoundException | IOException e) {
				break;
			}
		}
	}
	
	/*
	 * Client abmelden  sowie
	 * Stroeme und Socket schliessen.
	 */
	public void disconnect(){
		running = false;
		try {
			if (!clientSocket.isClosed()){
				this.out.writeObject(new DisconnectMessage(this.playerData.getNickname()));
				clientSocket.close(); // close streams and socket
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendToServer(Message message) throws IOException {
		this.out.writeObject(message);
		out.flush();
		out.reset();
	}
}
