/** @author lurny */
package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import general.IDGenerator;
import gui.ClientUI;
import network.messages.*;

public class ClientProtocol extends Thread {
	private ClientUI cui;
	private String username;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean running = true;
	
	public ClientProtocol(String ip, int port, String username, ClientUI cui) {
		this.cui = cui;
		try {
			this.username = username;
			this.clientSocket = new Socket(ip, port);
			this.out = new ObjectOutputStream(clientSocket.getOutputStream());
			this.in = new ObjectInputStream(clientSocket.getInputStream());
	
			this.out.writeObject(new ConnectMessage(this.username));
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
				/* ***  Aufgabenteil (a), (b), (c), (d) *** */
				case UPDATE_TEXT:
					UpdateMessage update = (UpdateMessage) m;
					cui.updateWBTextObjectText(update.getID(), update.getNewText());
					break;
				case UPDATE_ALLOW:
					UpdateRequestMessage updateAllow = (UpdateRequestMessage) m;
					cui.triggerEdit(updateAllow.getID());
					break;
				case REMOVE_OBJECT:
					RemoveMessage remove = (RemoveMessage) m;
					cui.removeWBTextObject(remove.getID());
					cui.removeWBShapeObject(remove.getID());
					break;
				case SEND_TEXT:
					TextMessage text = (TextMessage) m;
					cui.addWBTextObject(text.getID(), text.getText(), text.getX(), text.getY());
					break;
				case SEND_SHAPE:
					ShapeMessage shape = (ShapeMessage) m;
					cui.addWBShapeObject(shape.getID(), shape.getShape());
					break;
				case SEND_ID:
					IDConfirmMessage confirm = (IDConfirmMessage) m;
					cui.updateWBObjectID(confirm.getID(), confirm.getTempID());
					break;
				case CONNECTION_REFUSED: 
					ConnectionRefusedMessage mRef = (ConnectionRefusedMessage)m;
					cui.showAlert("Connection refused: " + mRef.getReason());
					disconnect(); // und gui informieren (zurück zu login)
					cui.resetUI();
					break;
				case SERVERSHUTDOWN:
					cui.showAlert("Server is going down immediately");
					disconnect(); // Verbindung trennen 
					cui.resetUI(); // gui zuruecksetzen (zurück zu login)
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
				this.out.writeObject(new DisconnectMessage(this.username));
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
