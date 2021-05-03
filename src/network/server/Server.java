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
import javafx.application.Platform;
import mechanic.Field;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import mechanic.Turn;
import network.messages.AddTileMessage;
import network.messages.ConnectMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.InvalidMoveMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.SendChatMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TileMessage;
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

  public Server(Player host, String customGameSettings) {
    this.host = host.getNickname();
    this.player = host;
    this.gameState = new GameState(host.getPlayerInfo(), customGameSettings);
    this.gameController = new GameController(this.gameState);
    this.lsc = LobbyScreenController.getLobbyInstance();
  }

  /**
   * This method fills the racks of every player with initial tiles.
   * 
   * @author lurny
   */
  public void distributeInitialTiles() {
    List<Tile> tileList;

    // add Tiles to players
    for (ServerProtocol client : this.clients.values()) {
      tileList = this.gameController.drawInitialTiles();
      // UI
      client.sendToClient(new TileMessage(this.getHost(), tileList));
      // sollen die Racks nur lokal gespeichert werden?

    }

    // add Tiles to host Rack
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        List<Tile> tileList = gameController.drawInitialTiles();

        // UI
        for (Tile t : tileList) {
          t.setField(player.getFreeRackField());
          t.setOnGameBoard(false);
          t.setOnRack(true);
          gpc.addTile(t);
        }

        // domain
        player.addTilesToRack(tileList);
      }
    });

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

  /** Handles move from rack to gameBoard (with AddTileMessage). */

  public void handleAddTileToGameBoard(AddTileMessage m) {
    Field f = m.getTile().getField();

    if (this.gameController.addTileToGameBoard(m.getFrom(), m.getTile(), m.getNewXCoordinate(),
        m.getNewYCoordinate())) {
      sendToAll(m);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      RemoveTileMessage rtm = new RemoveTileMessage(host, f.getxCoordinate(), f.getyCoordinate());
      if (m.getFrom().equals(this.getHost())) {
        updateServerUi((Message) rtm);
      } else {
        clients.get(m.getFrom()).sendToClient(rtm);
      }
    } else {
      InvalidMoveMessage im =
          new InvalidMoveMessage(m.getFrom(), "Tile could not be added to GameBoard.");
      sendToAll(im);
    }
  }

  /** Handles moves to rack from gameBoard and moves on gameboard (with MoveTileMessage). */

  public void handleMoveTile(MoveTileMessage m) {
    Tile oldTile = this.gameState.getGameBoard()
        .getField(m.getOldXCoordinate(), m.getOldYCoordinate()).getTile();

    if (m.getNewYCoordinate() == -1 && m.getOldYCoordinate() != -1) { // move to rack
      if (!this.gameController.removeTileFromGameBoard(m.getFrom(), m.getOldXCoordinate(),
          m.getOldYCoordinate())) {
        InvalidMoveMessage im =
            new InvalidMoveMessage(m.getFrom(), "Tile could not be removed from GameBoard.");
        sendToAll(im);
        return;
      }

      AddTileMessage atm =
          new AddTileMessage(m.getFrom(), oldTile, m.getNewXCoordinate(), m.getNewYCoordinate());

      if (m.getFrom().equals(this.getHost())) {
        updateServerUi((Message) atm);
      } else {
        clients.get(m.getFrom()).sendToClient(atm);
      }

    } else if (m.getNewYCoordinate() != -1 && m.getOldYCoordinate() != -1) { // move on game board
      if (!this.gameController.moveTileOnGameBoard(m.getFrom(), m.getOldXCoordinate(),
          m.getOldYCoordinate(), m.getNewXCoordinate(), m.getNewYCoordinate())) {
        InvalidMoveMessage im =
            new InvalidMoveMessage(m.getFrom(), "Tile could not be moved on GameBoard.");
        sendToAll(im);
        return;
      }

      AddTileMessage atm =
          new AddTileMessage(m.getFrom(), oldTile, m.getNewXCoordinate(), m.getNewYCoordinate());
      sendToAll(atm);

    }

    RemoveTileMessage rtm =
        new RemoveTileMessage(m.getFrom(), m.getOldXCoordinate(), m.getOldYCoordinate());

    sendToAll(rtm);

  }


  public synchronized List<String> getClientNames() {
    Set<String> clientNames = this.clients.keySet();
    return new ArrayList<String>(clientNames);
  }

  /** This method sends a message to a list of clients. */
  private synchronized void sendTo(List<String> clientNames, Message m) {
    for (String nickname : clientNames) {
      ServerProtocol c = clients.get(nickname);
      c.sendToClient((Message) (m));
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
   * This method sends a message to all connected clients, except the one client given as parameter.
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

  public void updateServerUi(Message m) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        if (!gameState.getGameRunning()) {
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
              lsc.updateChat(um.getText(), um.getDateTime(), um.getFrom());
              break;
            case START_GAME:
              StartGameMessage sgm = (StartGameMessage) m;
              lsc.startGameScreen();
              gpc.initializeThread();
              gpc.startTimer();
              break;
            case GAME_STATISTIC:
              GameStatisticMessage gsm = (GameStatisticMessage) m;
              break;
            // TODO
            default:
              break;
          }
        } else {
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
              System.out.println("Hi Add");
              AddTileMessage atm = (AddTileMessage) m;
              if (atm.getNewYCoordinate() == -1) {
                atm.getTile().setField(player.getRackField(atm.getNewXCoordinate()));
                atm.getTile().setOnRack(true);
                atm.getTile().setOnGameBoard(false);
              } else {
                atm.getTile().setField(gameState.getGameBoard().getField(atm.getNewXCoordinate(),
                    atm.getNewYCoordinate()));
                atm.getTile().setOnRack(false);
                atm.getTile().setOnGameBoard(true);
              }
              gpc.addTile(atm.getTile());
              break;
            case REMOVE_TILE:
              System.out.println("Hi Remove");
              RemoveTileMessage rtm = (RemoveTileMessage) m;
              gpc.removeTile(rtm.getX(), rtm.getY(), (rtm.getY() == -1));
              break;
            case TURN_RESPONSE:
              TurnResponseMessage trm = (TurnResponseMessage) m;
              if (!trm.getIsValid()) {
                gpc.indicateInvalidTurn(trm.getFrom(), "turn invalid");
              } else {
                gameState.addScore(trm.getFrom(), trm.getCalculatedTurnScore());
                gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
                gameState.setCurrentPlayer(trm.getNextPlayer());
                gpc.indicatePlayerTurn(trm.getNextPlayer());
                gpc.startTimer();
              }
              break;
            case INVALID:
              InvalidMoveMessage imm = (InvalidMoveMessage) m;
              gpc.indicateInvalidTurn(imm.getFrom(), imm.getReason());
            case UPDATE_CHAT:
              UpdateChatMessage um = (UpdateChatMessage) m;
              gpc.updateChat(um.getText(), um.getDateTime(), um.getFrom());
              break;
            default:
              break;
          }
        }
      }
    });
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

  public void startGame() {
    sendToAll(new StartGameMessage(host, 10));

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    gameState.setRunning(true);
    gameState.setCurrentPlayer(host);
    distributeInitialTiles();
    this.gameController.setTurn(new Turn(this.host, this.gameController));
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

  public GamePanelController getGamePanelController() {
    return gpc;
  }

  public void setGamePanelController(GamePanelController gpc) {
    this.gpc = gpc;
  }

  public void setLobbyScreenController(LobbyScreenController lsc) {
    this.lsc = lsc;
  }


  public void setRunning(boolean running) {
    this.running = running;
  }
}
