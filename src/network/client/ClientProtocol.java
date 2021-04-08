/** @author lurny */
package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
// import general.IDGenerator;
import gui.ClientUI;
import gui.GamePanelController;
import gui.LobbyScreenController;
import mechanic.PlayerData;
import network.messages.AddTileMessage;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TileResponseMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;

public class ClientProtocol extends Thread {
	private ClientUI cui;
	private GamePanelController gpc;
	private LobbyScreenController lpc;
	private PlayerData playerData;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;

	public ClientProtocol(String ip, int port, PlayerData playerData, ClientUI cui, GamePanelController gpc,
			LobbyScreenController lpc) {
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
			e.printStackTrace();
			System.out.println("Could not establish connection to " + ip + ":" + port);
		}
	}

	public boolean isOK() {
		return (clientSocket != null) && (clientSocket.isConnected()) && !(clientSocket.isClosed());
	}

	// Verarbeitung der vom Server empfangenen Nachrichten
	public void run() {
		while (running) {
			try {
				Message m = (Message) in.readObject(); // read message from server
				switch (m.getMessageType()) {
				case CONNECTION_REFUSED:
					ConnectionRefusedMessage mrMessage = (ConnectionRefusedMessage) m;
					// tbImplemented
					break;
				case SHUTDOWN:
					ShutdownMessage sMessage = (ShutdownMessage) m;
					// tbImplemented
					break;
				case ADD_TILE:
					AddTileMessage atMessage = (AddTileMessage) m;
					gpc.addTile(atMessage.getTile());
					break;
				case MOVE_TILE:
					MoveTileMessage mtMessage = (MoveTileMessage) m;
					gpc.moveTile(mtMessage.getTile(), mtMessage.getNewField());
					break;
				case REMOVE_TILE:
					MoveTileMessage rtMessage = (MoveTileMessage) m;
					gpc.removeTile(rtMessage.getTile());
					break;
				case TILE_RESPONSE:
					TileResponseMessage trMessage = (TileResponseMessage) m;
					gpc.addTile(trMessage.getTile());
					break;
				case TURN_RESPONSE:
					TurnResponseMessage turnrMessage = (TurnResponseMessage) m;
					if (turnrMessage.getIsValid()) {
						gpc.updateScore(turnrMessage.getFrom(), turnrMessage.getCalculatedTurnScore());
					} else {
						gpc.indicateInvalidTurn(turnrMessage.getFrom());
					}
					break;
				case LOBBY_STATUS:
					LobbyStatusMessage lsMessage = (LobbyStatusMessage) m;
					// tbImplemented
					break;
				case START_GAME:
					StartGameMessage sgMessage = (StartGameMessage) m;
					// tbImplemented
					break;
				case GAME_STATISTIC:
					GameStatisticMessage gsMessage = (GameStatisticMessage) m;
					// tbImplemented
					break;
				case UPDATE_CHAT:
					UpdateChatMessage ucMessage = (UpdateChatMessage) m;
					gpc.updateChat(ucMessage.getText(), ucMessage.getDateTime(), ucMessage.getFrom());
					break;
				default:
					break;
				}
			} catch (ClassNotFoundException | IOException e) {
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block from updateScore() method in
				// GamePanelController
				e.printStackTrace();
			}
		}
	}

	/*
	 * Disconnect client Shutdown streams and sockets
	 */
	public void disconnect() {
		running = false;
		try {
			if (!clientSocket.isClosed()) {
				this.out.writeObject(new DisconnectMessage(this.playerData.getNickname()));
				clientSocket.close(); // close streams and socket
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToServer(Message message) throws IOException {
		this.out.writeObject(message);
		out.flush();
		out.reset();
	}
}
