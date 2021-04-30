package network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import mechanic.Turn;
import network.messages.AddTileMessage;
import network.messages.CommitTurnMessage;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.MoveTileMessage;
import network.messages.SendChatMessage;
import network.messages.TileMessage;
import network.messages.TurnResponseMessage;
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


  /** Sends the initialGameState to the client who uses this protocol. */

  public void sendInitialGameState() throws IOException {
    LobbyStatusMessage m = new LobbyStatusMessage(server.getHost(), server.getGameState());

    // Changed to send To All
    this.server.sendToAll(m);

  }


  /** Sends message to the client who uses this protocol. */


  public void sendToClient(Message m) {
    try {
      this.out.writeObject(m);
      out.flush();
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
            sendInitialGameState();
            disconnect();
            break;
          case ADD_TILE:
            AddTileMessage atm = (AddTileMessage) m;
            server.handleAddTileToGameBoard(atm);
            break;
          case MOVE_TILE:
            MoveTileMessage mtm = (MoveTileMessage) m;
            server.handleMoveTile(mtm);
            break;
          case COMMIT_TURN:
            CommitTurnMessage ctm = (CommitTurnMessage) m;
            if (!ctm.getFrom().equals(server.getGameState().getCurrentPlayer())) {
              break;
            }

            Turn turn = server.getGameController().getTurn();
            turn.endTurn();

            String nextPlayer;

            if (turn.isValid()) {
              nextPlayer = server.getGameController().getNextPlayer();
              TileMessage trm =
                  new TileMessage(server.getHost(), server.getGameController().drawTiles());
              sendToClient(trm);
            } else {
              nextPlayer = null;
            }

            server.sendToAll(new TurnResponseMessage(ctm.getFrom(), turn.isValid(),
                turn.getTurnScore(), nextPlayer));

            sendInitialGameState();
            running = false;
            disconnect();
            break;

          case TILE:
            TileMessage tm = (TileMessage) m;
            this.server.getGameController().addTilesToTileBag(tm.getTiles());
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
