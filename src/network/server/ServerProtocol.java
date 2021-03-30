package network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import game.GameState;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MessageType;


/** @author ldreyer */
public class ServerProtocol extends Thread {
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private Server server;
  private String clientName;
  private boolean running = true;

  ServerProtocol(Socket client, Server server) {
    this.socket = client;
    this.server = server;
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendInitialGameState() throws IOException {
    GameState gameState = server.getGameState();
    LobbyStatusMessage m =
        new LobbyStatusMessage(MessageType.LOBBY_STATUS, server.getHost(), gameState);
    sendToClient(m);
  }

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

  public void run() {
    Message m;
    try {
      m = (Message) in.readObject();
      if (m.getMessageType() == MessageType.CONNECT) {
        String from = m.getFrom();
        ConnectMessage cm = (ConnectMessage) m;
        this.clientName = cm.getPlayerInfo().getNickname();
        if (server.checkNickname(from)) {
          Message rmsg = new ConnectionRefusedMessage(MessageType.CONNECTION_REFUSED,
              server.getHost(), "Your nickname was already taken");
          out.writeObject(rmsg);
          out.flush();
          out.reset();
          disconnect();
        } else if (!from.equals(this.clientName)) {
          Message rmsg = new ConnectionRefusedMessage(MessageType.CONNECTION_REFUSED,
              server.getHost(), "Your sender name did not match your nickname. Error.");
          out.writeObject(rmsg);
          out.flush();
          out.reset();
          disconnect();
        } else {
          server.addClient(cm.getPlayerInfo(), this);
          server.sendToAllBut(from, cm);
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
