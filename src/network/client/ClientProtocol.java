/** @author lurny */
package network.client;

import game.GameState;
import gui.GamePanelController;
import gui.LobbyScreenController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import mechanic.Player;
import mechanic.Tile;
import network.messages.AddTileMessage;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TileResponseMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;

public class ClientProtocol extends Thread {
  private GameState gameState;
  private GamePanelController gpc;
  private LobbyScreenController lpc;
  private Player player;

  private Socket clientSocket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private boolean running = true;

  public ClientProtocol(String ip, int port, Player player, GamePanelController gpc,
      LobbyScreenController lpc) {
    try {
      this.gpc = gpc;
      this.lpc = lpc;
      this.player = player;
      this.clientSocket = new Socket(ip, port);
      this.out = new ObjectOutputStream(clientSocket.getOutputStream());
      this.in = new ObjectInputStream(clientSocket.getInputStream());
      this.out.writeObject(new ConnectMessage(this.player.getPlayerInfo()));
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
    Message m;
    try {
      m = (Message) in.readObject();

      if (m.getMessageType() == MessageType.CONNECT) {
        ConnectMessage cm = (ConnectMessage) m;
        this.player.setNickname(cm.getPlayerInfo().getNickname());
        System.out.println("New username: " + player.getNickname());
      } else {
        disconnect();
      }

      while (running) {
        try {
          m = (Message) in.readObject(); // read message from server

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
              atMessage.getTile().setField(gameState.getGameBoard()
                  .getField(atMessage.getNewXCoordinate(), atMessage.getNewYCoordinate()));
              gpc.addTile(atMessage.getTile());
              break;
            case MOVE_TILE:
              MoveTileMessage mtMessage = (MoveTileMessage) m;
              // gpc.moveToRack(mtMessage.getTile(), mtMessage.getNewXCoordinate(),
              // mtMessage.getNewYCoordinate()); // TODO:param oldX, oldY !!
              break;
            case REMOVE_TILE:
              RemoveTileMessage rtMessage = (RemoveTileMessage) m;
              // rtMessage.getTile().setField(gameState.getGameBoard()
              // .getField(rtMessage.getX(), rtMessage.getY()));
              // gpc.removeTile(rtMessage.getTile()); // TODO:param: int x, int y, boolean isOnRack
              break;
            case TILE_RESPONSE:
              TileResponseMessage trMessage = (TileResponseMessage) m;
              for (Tile t : trMessage.getTiles()) {
                gpc.addTile(t);
              }
              break;
            case TURN_RESPONSE:
              TurnResponseMessage turnrMessage = (TurnResponseMessage) m;
              if (turnrMessage.getIsValid()) {
                gpc.updateScore(turnrMessage.getFrom(), turnrMessage.getCalculatedTurnScore());
                turnrMessage.getNextPlayer();
              } else {
                gpc.indicateInvalidTurn(turnrMessage.getFrom(), "Invalid Turn");
              }
              break;
            case LOBBY_STATUS:
              LobbyStatusMessage lsMessage = (LobbyStatusMessage) m;
              this.gameState = lsMessage.getGameState();
              break;
            case START_GAME:
              StartGameMessage sgMessage = (StartGameMessage) m;
              this.lpc.startGameScreen(this.player);
              // tbImplemented
              break;
            case GAME_STATISTIC:
              GameStatisticMessage gsMessage = (GameStatisticMessage) m;
              // tbImplemented
              break;
            case UPDATE_CHAT:
              UpdateChatMessage ucMessage = (UpdateChatMessage) m;
              gpc.updateChat(ucMessage.getText(), ucMessage.getDateTime(), ucMessage.getFrom());
              lpc.updateChat(ucMessage.getText(), ucMessage.getDateTime(), ucMessage.getFrom());
              break;
            default:
              break;
          }
        } catch (ClassNotFoundException | IOException e) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setLC(LobbyScreenController lc) {
    this.lpc = lc;
  }

  /*
   * Disconnect client Shutdown streams and sockets
   */
  public void disconnect() {
    running = false;
    try {
      if (!clientSocket.isClosed()) {
        this.out.writeObject(new DisconnectMessage(this.player.getNickname()));
        clientSocket.close(); // close streams and socket
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendToServer(Message message) {
    try {
      this.out.writeObject(message);
      this.out.flush();
      this.out.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * @author mschmauc
   */

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
}
