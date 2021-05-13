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
import network.messages.CommitTurnMessage;
import network.messages.ConnectMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.InvalidMoveMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
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
      tileList = this.gameController.drawTiles(7);
      // UI
      client.sendToClient(new TileMessage(this.getHost(), tileList));
      // sollen die Racks nur lokal gespeichert werden?

    }

    // add Tiles to host Rack
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        List<Tile> tileList = gameController.drawTiles(7);

        for (Tile t : tileList) {
          t.setField(player.getFreeRackField());
          t.setOnGameBoard(false);
          t.setOnRack(true);
          gpc.addTile(t);
        }
      }
    });
  }

  /**
   * This Method is used to Exchange Tiles via the Skip and change Button. It adds the Tiles you
   * want to exchange to the tilebag and draws the same amount of new tiles.
   * 
   * @author lurny
   */
  public void handleExchangeTiles(TileMessage m) {
    // String nextPlayer = this.gameController.getNextPlayer();

    // this.sendToAll(new TurnResponseMessage(m.getFrom(), this.gameController.getTurn().isValid(),
    // this.gameState.getScore(m.getFrom()), nextPlayer,
    // this.gameController.getTileBag().getRemaining()));
    // this.gameState.setCurrentPlayer(nextPlayer);


    this.getGameController().addTilesToTileBag(m.getTiles());
    // If the host wants to perform the exchange
    if (m.getFrom().equals(this.getHost())) {
      // delete Old tiles From Domain
      for (Tile t : m.getTiles()) {
        this.player.removeRackTile(t.getField().getxCoordinate());
      }
      // add new tiles to Domain and UI
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          List<Tile> tileList = gameController.drawTiles(m.getTiles().size());
          for (Tile t : tileList) {
            t.setField(player.getFreeRackField());
            t.setOnGameBoard(false);
            t.setOnRack(true);
            gpc.addTile(t);
          }
        }
      });
    } else {
      List<Tile> tileList;
      String receiver = m.getFrom();
      tileList = this.gameController.drawTiles(m.getTiles().size());
      ServerProtocol client = this.clients.get(receiver);
      client.sendToClient(new TileMessage(this.getHost(), tileList));
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    this.resetTurnForEveryPlayer(new ResetTurnMessage(m.getFrom(), null));
  }

  /**
   * This method is called, when a Turn should be resetet. All tiles are remove from the gameboard
   * and the Tiles are added to the player Rack, if the current player is equal to the server.
   * 
   * @author lurny
   */
  public void resetTurnForEveryPlayer(ResetTurnMessage m) {
    System.out.println("Test");
    List<Tile> tileList = this.gameController.getTurn().getLaydDownTiles();
    this.sendToAll((Message) new ResetTurnMessage(m.getFrom(), tileList));
    // remove Tiles from domain Gameboard
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        for (Tile t : gameController.getTurn().getLaydDownTiles()) {
          gameState.getGameBoard()
              .getField(t.getField().getxCoordinate(), t.getField().getyCoordinate()).setTile(null);
        }
        // if this is the current player: add Tiles to Rack
        if (host.equals(gameState.getCurrentPlayer())) {
          for (Tile t : gameController.getTurn().getLaydDownTiles()) {
            player.addTileToRack(t);
            gpc.addTile(t);
          }
        }
        gameState.setCurrentPlayer(gameController.getNextPlayer());
        gameController.setTurn(new Turn(gameState.getCurrentPlayer(), gameController));
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        sendToAll(new TurnResponseMessage(m.getFrom(), true, gameState.getScore(m.getFrom()),
            gameState.getCurrentPlayer(), gameController.getTileBag().getRemaining()));

      }
    });
  }

  /**
   * This method is called to verify the turn.
   */
  public void handleCommitTurn(CommitTurnMessage m) {
    String from = m.getFrom();
    Turn turn = this.getGameController().getTurn();
    turn.endTurn();
    sendToAll(new UpdateChatMessage("", turn.toString(), null));

    if (turn.isValid()) {
      int remainingTiles =
          this.gameController.getTileBag().getRemaining() - turn.getLaydDownTiles().size();
      for (Tile t : turn.getLaydDownTiles()) {
        t.setPlayed(true);
      }
      this.gameState.addScore(from, turn.getTurnScore());
      String nextPlayer = this.getGameController().getNextPlayer();

      if (m.getFrom().equals(this.getHost())) {
        // add new tiles to Domain and UI
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            List<Tile> tileList = gameController.drawTiles(turn.getLaydDownTiles().size());
            for (Tile t : tileList) {
              t.setField(player.getFreeRackField());
              t.setOnGameBoard(false);
              t.setOnRack(true);
              gpc.addTile(t);
            }
          }
        });
      } else {
        List<Tile> tileList = gameController.drawTiles(turn.getLaydDownTiles().size());
        TileMessage tm = new TileMessage(from, tileList);
        this.clients.get(from).sendToClient((Message) tm);
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      if (turn.isContainedStarTiles()) {
        for (Tile t : turn.getStarTiles()) {
          this.sendToAll(new RemoveTileMessage(from, t.getField().getxCoordinate(),
              t.getField().getyCoordinate()));

          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          this.sendToAll(new AddTileMessage(from, t, t.getField().getxCoordinate(),
              t.getField().getyCoordinate()));
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }

      // check end game criteria
      List<Turn> turns = this.getGameController().getTurns();
      boolean fiveScorelessRounds = false;

      if (turns.size() > 5) {
        fiveScorelessRounds = true;
        for (int i = 0; i < 5; i++) {
          if (turns.get(i).getTurnScore() > 0) {
            fiveScorelessRounds = false;
            break;
          }
        }
      }

      if (!m.getTilesLeftOnRack() && this.gameController.getTileBag().isEmpty()
          || fiveScorelessRounds) {
        endGame();
        return;
      }

      this.sendToAll(new TurnResponseMessage(from, turn.isValid(), this.gameState.getScore(from),
          nextPlayer, remainingTiles));
      gameState.setCurrentPlayer(nextPlayer);
      this.getGameController().newTurn();

    }
    // else { // turn is invalid
    // sendToAll(new TurnResponseMessage(from, turn.isValid(), this.gameState.getScore(from), null,
    // this.gameController.getTileBag().getRemaining()));
    // }

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
    return this.clients.keySet().contains(nickname) || this.host.equals(nickname);
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

      RemoveTileMessage rtm =
          new RemoveTileMessage(m.getFrom(), f.getxCoordinate(), f.getyCoordinate());
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
    // TODO temporary
    if (this.gameState.getGameBoard().getField(m.getOldXCoordinate(), m.getOldYCoordinate())
        .getTile() == null) {
      InvalidMoveMessage im =
          new InvalidMoveMessage(m.getFrom(), "Tile could not be removed from GameBoard.");
      sendToAll(im);
      return;
    }

    Tile oldTile = this.gameState.getGameBoard()
        .getField(m.getOldXCoordinate(), m.getOldYCoordinate()).getTile();
    if (m.getNewYCoordinate() == -1 && m.getOldYCoordinate() != -1) { // move to rack
      if (!this.gameController.checkRemoveTileFromGameBoard(m.getFrom(), m.getOldXCoordinate(),
          m.getOldYCoordinate())) {
        InvalidMoveMessage im =
            new InvalidMoveMessage(m.getFrom(), "Tile could not be removed from GameBoard.");
        sendToAll(im);
        return;
      }

      if (m.getFrom().equals(this.getHost())) {
        player.moveToRack(oldTile, m.getNewXCoordinate());
      } else {
        AddTileMessage atm =
            new AddTileMessage(m.getFrom(), oldTile, m.getNewXCoordinate(), m.getNewYCoordinate());
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

      RemoveTileMessage rtm =
          new RemoveTileMessage(m.getFrom(), m.getOldXCoordinate(), m.getOldYCoordinate());
      sendToAll(rtm);
    }

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
              gpc.startTimer();
              gpc.indicatePlayerTurn(gameState.getCurrentPlayer());
              gpc.updateRemainingLetters(sgm.getRemainingTilesInTileBag());
              gpc.updateChat("-- " + sgm.getCurrrentPlayer() + ", you begin! --", null, "");
              if (!host.equals(sgm.getCurrrentPlayer())) {
                gpc.changeDoneStatus(false);
                gpc.changeSkipAndChangeStatus(false);
              }
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
            case SHUTDOWN:
              // TODO:
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
              if (rtm.getY() == -1) {
                player.removeRackTile(rtm.getX());
              }
              gpc.removeTile(rtm.getX(), rtm.getY(), (rtm.getY() == -1));
              break;
            case TURN_RESPONSE:
              TurnResponseMessage trm = (TurnResponseMessage) m;
              if (!trm.getIsValid()) {
                gpc.indicateInvalidTurn(trm.getFrom(), "turn invalid");
              } else {
                gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
                gpc.indicatePlayerTurn(trm.getNextPlayer());
                System.out.println("remainingTiles " + trm.getRemainingTilesInTileBag());
                gpc.updateRemainingLetters(trm.getRemainingTilesInTileBag());
                gpc.startTimer();
                gpc.indicatePlayerTurn(trm.getNextPlayer());
                gpc.changeDoneStatus(trm.getNextPlayer().equals(host));
                gpc.changeSkipAndChangeStatus(trm.getNextPlayer().equals(host));
                sendToAll(new UpdateChatMessage("",
                    "-- " + trm.getNextPlayer() + ", it's your turn! --", null));
              }
              break;
            case RESET_TURN:
              ResetTurnMessage resetM = (ResetTurnMessage) m;
              for (Tile t : resetM.getTiles()) {
                gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(), false);
              }
              break;
            case INVALID:
              InvalidMoveMessage imm = (InvalidMoveMessage) m;
              gpc.indicateInvalidTurn(imm.getFrom(), imm.getReason());
              break;
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
    gameState.setCurrentPlayer(this.gameController.getNextPlayer());
    sendToAll(new StartGameMessage(host, 10,
        this.gameController.getTileBag().getRemaining() - this.gameState.getAllPlayers().size() * 7,
        this.gameState.getCurrentPlayer()));
    System.out.println("groeße: " + this.gameState.getAllPlayers().size());
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    gameState.setRunning(true);
    distributeInitialTiles();
    this.gameController.newTurn();

    this.gameState.initializeScoresWithZero(this.gameState.getAllPlayers());
  }

  public void endGame() {
    Turn turn = this.gameController.getTurn();
    sendToAll(new TurnResponseMessage(turn.getPlayer(), turn.isValid(),
        this.gameState.getScore(turn.getPlayer()), null,
        this.gameController.getTileBag().getRemaining()));

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    sendToAll(new GameStatisticMessage(this.host, null));
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
