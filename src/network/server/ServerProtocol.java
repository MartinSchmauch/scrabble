package network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.MoveTileMessage;
import network.messages.TileResponseMessage;

/**
 * The ServerProtocol processes all messages from the client and forwards them to the Server class.
 * This class is run as a thread and is created for every connected client. The first message has to
 * be a CONNECT_MESSAGE. If the nickname exists on the server already, the client is rejected.
 * 
 * @author ldreyer
 */

public class ServerProtocol extends Thread {
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private Server server;
  private String clientName;
  private boolean running = true;

  public ServerProtocol(Socket client, Server server) {
    this.socket = client;
    this.server = server;
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Sends the initialGameState to the client who uses this protocol */

  public void sendInitialGameState() throws IOException {
    LobbyStatusMessage m = new LobbyStatusMessage(server.getHost(), server.getGameState());
    sendToClient(m);
  }

  /** Sends message to the client who uses this protocol */

  public void sendToClient(Message m) throws IOException {
    this.out.writeObject(m);
    out.flush();
    out.reset();
  }

  void disconnect() {
    running = false;
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Client messages are handled in this run method. First, the server awaits a CONNECT_MESSAGE with
   * client information.
   */
  public void run() {
    Message m;
    try {
      m = (Message) in.readObject();
      if (m.getMessageType() == MessageType.CONNECT) {
        String from = m.getFrom();
        ConnectMessage cm = (ConnectMessage) m;
        this.clientName = cm.getPlayerInfo().getNickname();

        if (server.checkNickname(from)) {
          Message rmsg =
              new ConnectionRefusedMessage(server.getHost(), "Your nickname was already taken");
          out.writeObject(rmsg);
          out.flush();
          out.reset();
          disconnect();
        } else if (!from.equals(this.clientName)) {
          Message rmsg = new ConnectionRefusedMessage(server.getHost(),
              "Your sender name did not match your nickname. Error.");
          out.writeObject(rmsg);
          out.flush();
          out.reset();
          disconnect();
        } else {
          server.addClient(cm.getPlayerInfo(), this);
          server.sendToAll(cm);
          sendInitialGameState();
        }
      } else {
        disconnect();
      }

      while (running) {
        m = (Message) in.readObject();

        switch (m.getMessageType()) {
          case DISCONNECT:
            server.removeClient(m.getFrom());
            running = false;
            disconnect();
            break;
          case MOVE_TILE:
            MoveTileMessage mtm = (MoveTileMessage) m;
            m.getFrom();
            mtm.getTile();
            server.sendToAll(m);
          case TILE_REQUEST:
            TileResponseMessage trm = (TileResponseMessage) m;
          default:
            break;
        }
      }
    } catch (IOException e) {
      running = false;
      if (socket.isClosed()) {
        System.out.println("Client: " + this.clientName + "disconnected");
      } else {
        try {
          socket.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    } catch (ClassNotFoundException e2) {
      System.out.println(e2.getMessage());
      e2.printStackTrace();
    }
  }

}


