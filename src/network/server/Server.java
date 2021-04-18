package network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import game.GameController;
import game.GameSettings;
import game.GameState;
import gui.GamePanelController;
import gui.LobbyScreenController;
import mechanic.Player;
import mechanic.PlayerData;
import network.messages.AddTileMessage;
import network.messages.ConnectMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.SendChatMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;

/**
 * Manages network Scrabble game, by keeping track of GameState, addressing the GameController and
 * sending the defined messages to connected clients. It is also responsible for updating the UI of
 * the game's host.
 *
 * @author ldreyer
 */

public class Server {

  private ServerSocket serverSocket;
  private GameState gameState;
  private GameController gameController;
  private Player player;
  private ServerProtocol serverProtocol;

  private GamePanelController gpc;
  private LobbyScreenController lsc;

  private boolean running;

  private String host;
  private HashMap<String, ServerProtocol> clients = new HashMap<>();

  /**
   * Initializes Server with host and customGameSettings. If customGameSettings are null, the
   * default game settings are used.
   */

  public Server(PlayerData host, String customGameSettings) {
    this.host = host.getNickname();
    this.gameState = new GameState(host, customGameSettings);
    this.gameController = new GameController(this.gameState);
  }

  /**
   * Thread method that continuously checks for new clients trying to connect. When a new clients
   * connects, a new instance of ServerProtocol is created, moderating the client-server connection
   */

  public void listen() {
    running = true;
    try {
      serverSocket = new ServerSocket(GameSettings.port);
      System.out.println("Server runs");

      while (running) {
        Socket clientSocket = serverSocket.accept();

        this.serverProtocol = new ServerProtocol(clientSocket, this);
        this.serverProtocol.start();
      }

    } catch (IOException e) {
      if (serverSocket != null && serverSocket.isClosed()) {
        System.out.println("Server stopped.");
      } else {
        e.printStackTrace();
      }
    }
  }

  public GameState getGameState() {
    return this.gameState;
  }

  // TODO split in relevant getter/setter methods
  public GameController getGameController() {
    return this.gameController;
  }

  public String getHost() {
    return this.host;
  }

  /** This method gets the ip address the server is hosted on. */
  public InetAddress getInetAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean checkNickname(String nickname) {
    return this.clients.keySet().contains(nickname);
  }

  public void addClient(PlayerData player, ServerProtocol serverProtocol) {
    this.gameState.joinGame(player);
    this.clients.put(player.getNickname(), serverProtocol);
  }

  public void removeClient(String player) {
    this.gameState.leaveGame(player);
    this.clients.remove(player);
  }

  public synchronized List<String> getClientNames() {
    Set<String> clientNames = this.clients.keySet();
    return new ArrayList<String>(clientNames);
  }

  /** This method sends a message to a list of clients. */
  private synchronized void sendTo(List<String> clientNames, Message m) {
    List<String> fails = new ArrayList<String>();
    for (String nickname : clientNames) {
      try {
        ServerProtocol c = clients.get(nickname);
        c.sendToClient((Message) (m));
      } catch (IOException e) {
        e.printStackTrace();
        fails.add(nickname);
        continue;
      }
    }
    for (String c : fails) {
      System.out.println("Client " + c + " removed (message delivery failed).");
      removeClient(c);
    }
    try {
      updateServerUi(m);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /** This method sends a message to all connected clients. */


  public void sendToAll(Message m) {
    sendTo(new ArrayList<String>(getClientNames()), (Message) (m));

  }

  /**
   * <<<<<<< HEAD This method sends a message to all connected clients, except the one client given
   * as parameter. ======= sends a message to all connected clients, except the one client who was
   * given as parameter. >>>>>>> refs/remotes/origin/develop_loginscreen
   */

  public void sendToAllBut(String name, Message m) {
    synchronized (this.clients) {
      List<String> senderList = getClientNames();
      senderList.remove(name);
      sendTo(senderList, m);
    }

  }

  /**
   * This method is called whenever a relevant message is send to clients and is updating the
   * server's user interface accordingly.
   */

  public void updateServerUi(Message m) throws Exception {
    if (!this.gameState.getGameRunning()) {
      if (this.lsc == null) {
        lsc = LobbyScreenController.getLobbyInstance();
      }

      switch (m.getMessageType()) {
        case CONNECT:
          ConnectMessage cm = (ConnectMessage) m;
          lsc.addJoinedPlayer(cm.getPlayerInfo());
          break;
        case DISCONNECT:
          lsc.removeJoinedPlayer(m.getFrom());
          break;
        case UPDATE_CHAT:
          UpdateChatMessage um = (UpdateChatMessage) m;
          System.out.println(lsc + " server");
          lsc.updateChat(um.getText(), um.getDateTime(), um.getFrom());
          break;
        case START_GAME:
          StartGameMessage sgm = (StartGameMessage) m;
          lsc.startGame();
          break;
        case GAME_STATISTIC:
          GameStatisticMessage gsm = (GameStatisticMessage) m;
          break;
        // TODO
        default:
          break;
      }
    } else {

      if (this.gpc == null) {
        gpc = GamePanelController.getInstance();
      }

      switch (m.getMessageType()) {
        case DISCONNECT:
          DisconnectMessage dm = (DisconnectMessage) m;
          gpc.removeJoinedPlayer(dm.getFrom());
          break;
        case SEND_CHAT_TEXT:
          SendChatMessage scm = (SendChatMessage) m;
          gpc.updateChat(scm.getText(), scm.getDateTime(), scm.getSender());
          break;
        case ADD_TILE:
          AddTileMessage atm = (AddTileMessage) m;
          gpc.addTile(atm.getTile());
          break;
        case REMOVE_TILE:
          RemoveTileMessage rtm = (RemoveTileMessage) m;
          gpc.removeTile(rtm.getTile());
          break;
        case MOVE_TILE:
          MoveTileMessage mtm = (MoveTileMessage) m;
          gpc.moveToRack(mtm.getTile(), mtm.getNewXCoordinate(), mtm.getNewYCoordinate());
          break;
        case TURN_RESPONSE:
          TurnResponseMessage trm = (TurnResponseMessage) m;
          if (!trm.getIsValid()) {
            gpc.indicateInvalidTurn(trm.getFrom());
          } else {
            gameState.addScore(trm.getFrom(), trm.getCalculatedTurnScore());
            gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
            this.gameState.setCurrentPlayer(trm.getNextPlayer());
            gpc.indicatePlayerTurn(trm.getNextPlayer());
          }
          break;
        default:
          break;
      }
    }
  }

  /**
   * This method is called when the server is supposed to stop. All clients are informed that the
   * server is no longer being hosted.
   */
  public void stopServer() {
    running = false;
    sendToAll(new ShutdownMessage(this.host, "Server closed session."));

    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public ServerProtocol getServerProtocol() {
    return this.serverProtocol;
  }
}
