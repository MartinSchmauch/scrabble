package network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import network.messages.AddTileMessage;
import network.messages.CommitTurnMessage;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.SendChatMessage;
import network.messages.TileMessage;
import network.messages.UpdateChatMessage;

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

  /**
   * Server Protocol is initialized with a socket as client and a server object for game state and
   * server ui handling.
   */

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


  /** Sends message to the client who uses this protocol. */


  public void sendToClient(Message m) {

    try {
      this.out.writeObject(m);
      out.reset();
    } catch (IOException e) {
      System.out.println("Client " + this.clientName + " removed (message delivery failed).");
      server.removeClient(this.clientName);
      e.printStackTrace();
    }
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
   *
   * @author ldreyer
   * @author pkoenig (nickname appendix)
   */
  public void run() {
    Message m;
    try {
      m = (Message) in.readObject();
      if (this.server.getGameState().getGameRunning()) {
        Message rmsg = new ConnectionRefusedMessage(server.getHost(), "Game already started.");
        out.writeObject(rmsg);
        out.flush();
        out.reset();
        disconnect();
      } else if (m.getMessageType() == MessageType.CONNECT) {
        if (server.getGameState().getAllPlayers().size() >= 4) {
          sendToClient(new ConnectionRefusedMessage(m.getFrom(), "Lobby full"));
          return;
        }
        String from = m.getFrom();
        ConnectMessage cm = (ConnectMessage) m;
        this.clientName = cm.getPlayerInfo().getNickname();

        if (server.checkNickname(this.clientName)) {
          int i = 1;
          while (server.checkNickname(this.clientName)) {
            this.clientName = cm.getPlayerInfo().getNickname() + "-" + i;
            i++;
          }
          cm.getPlayerInfo().setNickname(this.clientName);
          server.addClient(cm.getPlayerInfo(), this);
          server.sendToAll(cm);
          server.sendLobbyStatus();
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
          server.sendLobbyStatus();
        }
      } else {
        disconnect();
      }

      while (running) {
        m = (Message) in.readObject();
        switch (m.getMessageType()) {
          case DISCONNECT:
            DisconnectMessage dm = (DisconnectMessage) m;
            dm.setTiles(this.server.getGameController().getTurn().getLaydDownTiles());
            server.removeClient(dm.getFrom());
            server.sendToAll(m);
            disconnect();
            break;
          case ADD_TILE:
            AddTileMessage atm = (AddTileMessage) m;
            server.handleAddTileToGameBoard(atm);
            break;
          case REMOVE_TILE:
            RemoveTileMessage rtm = (RemoveTileMessage) m;
            server.getGameController().removeTileFromGameBoard(rtm.getFrom(), rtm.getX(),
                rtm.getY());
            server.sendToAll(rtm);
            break;
          case MOVE_TILE:
            MoveTileMessage mtm = (MoveTileMessage) m;
            server.handleMoveTile(mtm);
            break;
          case COMMIT_TURN:
            CommitTurnMessage ctm = (CommitTurnMessage) m;
            this.server.handleCommitTurn(ctm);
            break;
          case TILE:
            TileMessage tm = (TileMessage) m;
            this.server.handleExchangeTiles(tm);
            break;
          case RESET_TURN:
            ResetTurnMessage rtMessage = (ResetTurnMessage) m;
            this.server.getGameController().getTurn().setStringRepresentation("Time's up!");
            this.server.resetTurnForEveryPlayer(rtMessage);
            break;
          case SEND_CHAT_TEXT:
            SendChatMessage scm = (SendChatMessage) m;
            UpdateChatMessage ucm =
                new UpdateChatMessage(scm.getSender(), scm.getText(), scm.getDateTime());
            server.sendToAll(ucm);
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
