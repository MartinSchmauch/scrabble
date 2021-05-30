package network.server;

import game.GameController;
import game.GameSettings;
import game.GameState;
import gui.CustomAlert;
import gui.GamePanelController;
import gui.LeaderboardScreen;
import gui.LobbyScreenController;
import gui.LoginScreenController;
import gui.TutorialController;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.stage.Stage;
import mechanic.AiPlayer;
import mechanic.Field;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import mechanic.Turn;
import mechanic.Word;
import network.messages.AddTileMessage;
import network.messages.CommitTurnMessage;
import network.messages.ConnectMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.InvalidMoveMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.SendChatMessage;
import network.messages.StartGameMessage;
import network.messages.TileMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;
import util.Sound;

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
  private boolean semaphoreCommit;
  private boolean semaphoreReset;
  private boolean sem;


  private boolean running;

  private String host;
  private HashMap<String, ServerProtocol> clients = new HashMap<>();
  private HashMap<String, AiPlayer> aiPlayers = new HashMap<>();


  /**
   * Initializes Server with host and customGameSettings. If customGameSettings are null, the
   * default game settings are used.
   *
   * @author ldreyer
   */

  public Server(Player host, String customGameSettings) {
    this.semaphoreCommit = true;
    this.sem = true;
    this.semaphoreReset = true;
    this.host = host.getNickname();
    this.player = host;
    this.gameState = new GameState(host.getPlayerInfo(), customGameSettings);
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
      tileList = this.gameController.drawTiles(GameSettings.getTilesOnRack());
      // UI
      client.sendToClient(new TileMessage(this.getHost(), tileList));
      // sollen die Racks nur lokal gespeichert werden?

    }

    // add Tiles to AI Rack TODO
    for (AiPlayer a : this.aiPlayers.values()) {
      a.addTilesToRack(this.gameController.drawTiles(GameSettings.getTilesOnRack()));
    }

    // add Tiles to host Rack
    Platform.runLater(new Runnable() {
      @Override
      public void run() {

        List<Tile> tileList = new ArrayList<Tile>();
        // check if player is playing a normal game and not a tutorial
        if (gpc == null || gpc.getClass().getCanonicalName().equals("gui.GamePanelController")) {
          tileList = gameController.drawTiles(GameSettings.getTilesOnRack());
        } else { // tutorial
          char[] chars = {'B', 'E', 'D'};
          tileList = gameController.drawTutorial(chars);
        }

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

    this.semaphoreCommit = false;
    if (this.semaphoreReset && sem) {
      this.sem = false;
      this.getGameController().addTilesToTileBag(m.getTiles());
      // If the host wants to perform the exchange
      if (gpc.getClass().getCanonicalName().equals("gui.GamePanelController")) {
        this.gpc.stopTimer();
      }
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
      this.sem = true; // added by pkoenig, um Deadlocks zu verhindern
      this.gameController.getTurn().setStringRepresentation("Tiles changed and turn skipped.");
      resetTurnForEveryPlayer(new ResetTurnMessage(m.getFrom(), null));

    }
  }


  private void handleExchangeTilesForAi(AiPlayer aiplayer, List<Tile> rackTiles) {
    this.getGameController().addTilesToTileBag(rackTiles);
    for (Tile t : rackTiles) {
      this.player.removeRackTile(t.getField().getxCoordinate());
    }
    List<Tile> tileList = gameController.drawTiles(rackTiles.size());
    for (Tile t : tileList) {
      t.setField(player.getFreeRackField());
      t.setOnGameBoard(false);
      t.setOnRack(true);
    }
    this.gameController.getTurn().setStringRepresentation("Tiles changed and turn skipped.");
    resetTurnForEveryPlayer(new ResetTurnMessage(aiplayer.getNickname(), null));
  }

  /**
   * This method is called, when a Turn should be resetet. This is the case if either a player
   * leaves or the timer is up. All tiles are remove from the gameboard and the Tiles are added to
   * the player Rack, if the current player is equal to the server.
   *
   * @author lurny
   */
  public void resetTurnForEveryPlayer(ResetTurnMessage m) {
    this.semaphoreCommit = false;
    if (this.semaphoreReset && sem) {
      this.sem = false;
      String from = this.gameState.getCurrentPlayer();
      List<Tile> tileList = this.gameController.getTurn().getLaydDownTiles();
      this.sendToAll((Message) new ResetTurnMessage(from, tileList));
      if (!this.aiPlayers.containsKey(from)) {
        this.gameState.getGameStatisticsOfPlayer(from)
            .setPlayTime(this.gameState.getGameStatisticsOfPlayer(from).getPlayTime()
                + this.gpc.getTimerDuration() - (this.gpc.getMin() * 60 + this.gpc.getSec()));
      }
      // remove Tiles from domain Gameboard
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          for (Tile t : gameController.getTurn().getLaydDownTiles()) {
            gameState.getGameBoard()
                .getField(t.getField().getxCoordinate(), t.getField().getyCoordinate())
                .setTile(null);
          }
          // if this is the current player: add Tiles to Rack
          if (host.equals(gameState.getCurrentPlayer())) {
            for (Tile t : gameController.getTurn().getLaydDownTiles()) {
              player.addTileToRack(t);
              gpc.addTile(t);
            }
          }
          
          gameController.addTurn(gameController.getTurn());

          // check end game criteria
          if (gameController.checkEndGame(true)) {
            Runnable r = new Runnable() {
              public void run() {
                endGame();
              }
            };
            new Thread(r).start();
            return;
          }

          gameState.setCurrentPlayer(gameController.getNextPlayer());

          sendToAll(new TurnResponseMessage(from, true, gameState.getScore(from),
              gameController.getTurn().toString(), gameState.getCurrentPlayer(),
              gameController.getTileBag().getRemaining(), null));

          gameController.setTurn(new Turn(gameState.getCurrentPlayer(), gameController));

          Runnable r = new Runnable() {
            public void run() {
              handleAi(gameState.getCurrentPlayer());
            }
          };
          new Thread(r).start();

        }
      });
    }
  }

  /**
   * This method is called when a player commits a turn by clicking the "Done" button. It calculates
   * the turn and informs all players about the result with a TurnResponseMessage. If the turn is
   * valid the next player is included in the message as well.
   *
   * @author lurny
   * @author pkoenig
   */
  public void handleCommitTurn(CommitTurnMessage m) {
    this.semaphoreReset = false;
    if (this.semaphoreCommit) {
      String from = m.getFrom();
      Turn turn = this.getGameController().getTurn();
      turn.endTurn();

      if (turn.isValid()) {
        if (turn.getTurnScore() > 0) {
          this.gameController.addScoredTurn(turn);
        }

        for (Tile t : turn.getLaydDownTiles()) {
          t.setPlayed(true);
        }
        this.gameState.addScore(from, turn.getTurnScore());

        if (this.aiPlayers.containsKey(from)) {
          List<Tile> tileList = gameController.drawTiles(turn.getLaydDownTiles().size());
          for (Tile t : tileList) {
            t.setField(this.aiPlayers.get(from).getFreeRackField());
            t.setOnGameBoard(false);
            t.setOnRack(true);
          }
        } else if (m.getFrom().equals(this.getHost())) {
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
        if (this.gameController.checkEndGame(m.getTilesLeftOnRack())) {
          Runnable r = new Runnable() {
            public void run() {
              endGame();
            }
          };
          new Thread(r).start();
          return;
        }

        if (!this.aiPlayers.containsKey(from)) {
          this.gameState.getGameStatisticsOfPlayer(from)
              .setPlayTime(this.gameState.getGameStatisticsOfPlayer(from).getPlayTime()
                  + this.gpc.getTimerDuration() - (this.gpc.getMin() * 60 + this.gpc.getSec()));
        }

        int remainingTiles =
            this.gameController.getTileBag().getRemaining() - turn.getLaydDownTiles().size();
        String nextPlayer = this.getGameController().getNextPlayer();
        this.sendToAll(new TurnResponseMessage(from, turn.isValid(), this.gameState.getScore(from),
            turn.toString(), nextPlayer, remainingTiles, null));
        gameState.setCurrentPlayer(nextPlayer);
        this.getGameController().newTurn();

        Runnable r = new Runnable() {

          public void run() {
            handleAi(nextPlayer);
          }

        };
        new Thread(r).start();


      } else {
        this.semaphoreReset = true;
        this.sendToAll(
            new TurnResponseMessage(from, turn.isValid(), -1, turn.toString(), null, -1, null));
      }
    }
  }


  /**
   * This method checks if it's an AI players turn and calculates the AI turn if needed.
   *
   * @author pkoenig
   */
  private void handleAi(String player) {
    if (this.aiPlayers.containsKey(player)) {
      Turn aiTurn = this.aiPlayers.get(player).runAi(this.gameState.getGameBoard());

      // if no Turn found, exchange all RackTiles
      if (aiTurn == null) {
        handleExchangeTilesForAi(this.aiPlayers.get(player),
            this.aiPlayers.get(player).getRackTiles());
      } else {
        TileMessage tm = new TileMessage(player, aiTurn.getLaydDownTiles());
        this.sendToAll(tm);

        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }


        this.gameController.setTurn(aiTurn);
        CommitTurnMessage ctm =
            new CommitTurnMessage(player, !this.aiPlayers.get(player).getRackTiles().isEmpty());

        this.handleCommitTurn(ctm);
      }
    }
  }


  /**
   * Thread method that continuously checks for new clients trying to connect. When a new clients
   * connects, a new instance of ServerProtocol is created, moderating the client-server connection
   *
   * @author pkoenig
   */

  public void listen(boolean tutorialMode) {
    running = true;
    try {
      serverSocket = new ServerSocket(GameSettings.port);
      System.out.println("Server runs");
      if (!tutorialMode) {
        LoginScreenController.getInstance().startLobby();
      }

      while (running) {
        Socket clientSocket = serverSocket.accept();

        this.serverProtocol = new ServerProtocol(clientSocket, this);
        this.serverProtocol.start();
      }

    } catch (IOException e) {
      if (serverSocket != null && serverSocket.isClosed()) {
        System.out.println("Server stopped.");
      } else {
        CustomAlert.showWarningAlert("Already hosting!",
            "Sorry. You cannot host two games at a time.");
      }
    }
  }

  public GameState getGameState() {
    return this.gameState;
  }

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

  /**
   * Check if a player with a given nickname already joined the lobby.
   *
   * @return true if nickname exists
   * @author ldreyer
   * @author pkoenig
   */

  public boolean checkNickname(String nickname) {
    return this.clients.keySet().contains(nickname) || this.host.equals(nickname)
        || this.aiPlayers.containsKey(nickname);
  }

  /**
   * This method adds a client to the gamestate and registers it's server protocol in the clients
   * hashmap.
   *
   * @author ldreyer
   */

  public void handleJoinLobby(PlayerData player, ServerProtocol serverProtocol) {
    this.gameState.joinGame(player);
    this.clients.put(player.getNickname(), serverProtocol);
  }

  public void handleLeaveLobby(String player) {
    this.gameState.leaveGame(player);
    this.clients.remove(player);
  }


  /** 
   * This method removes a client and ends the game is running and only the host is left. 
   *
   * @author ldreyer
   * @author lurny
   */

  public void handleLeaveGame(String player) {
    Turn turn = gameController.getTurn();

    for (int i = 0; i < gameController.getTurns().size(); i++) {
      if (gameController.getTurns().get(i).getPlayer().equals(player)) {
        gameController.getTurns().remove(i);
      }
    }

    this.gameState.leaveGame(player);
    this.clients.remove(player);
    if (this.gameState.getGameRunning() && this.gameState.getAllPlayers().size() < 2) {
      Runnable r = new Runnable() {
        public void run() {
          Thread.yield();
          gpc.updateChat("You're alone now. The game is over.", null, "");
          gpc.indicatePlayerTurn(host);
          gpc.stopTimer();
          gpc.changeDoneStatus(false);
          gpc.changeSkipAndChangeStatus(false);
          endGame();
        }
      };
      new Thread(r).start();
      return;
    }

    if (this.gameState.getCurrentPlayer().equals(player)) {
      for (Tile t : turn.getLaydDownTiles()) {
        t.getField().setTileOneDirection(null);
      }

      String nextPlayer = this.getGameController().getNextPlayer();
      this.sendToAll(new TurnResponseMessage(player, true, 0, null, nextPlayer,
          this.gameController.getTileBag().getRemaining(), null));
      gameState.setCurrentPlayer(nextPlayer);
      this.getGameController().newTurn();

      Runnable r = new Runnable() {

        public void run() {
          handleAi(nextPlayer);
        }

      };
      new Thread(r).start();
    }
  }

  /** 
   * Handles move from rack to gameBoard (with AddTileMessage). 
   *
   * @author ldreyer
   */

  public void handleAddTileToGameBoard(AddTileMessage m) {
    Field f = m.getTile().getField();
    if (this.gameController == null) {
      this.gameController = TutorialController.getController();
    }
    if (this.gameController.addTileToGameBoard(m.getFrom(), m.getTile(), m.getNewX(),
        m.getNewY())) {
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
      if (m.getFrom().equals(this.host)) {
        gpc.indicateInvalidTurn(m.getFrom(), "Tile could not be added to GameBoard.");
      } else {
        InvalidMoveMessage im =
            new InvalidMoveMessage(m.getFrom(), "Tile could not be added to GameBoard.");
        clients.get(m.getFrom()).sendToClient(im);
      }
    }
  }

  /** 
   * Handles moves to rack from gameBoard and moves on gameboard (with MoveTileMessage). 
   *
   * @author ldreyer
   */

  public void handleMoveTile(MoveTileMessage m) {
    Tile oldTile = this.gameState.getGameBoard()
        .getField(m.getOldX(), m.getOldY()).getTile();
    if (m.getNewY() == -1 && m.getOldY() != -1) { // move to rack
      if (!this.gameController.checkRemoveTileFromGameBoard(m.getFrom(), m.getOldX(),
          m.getOldY())) {
        if (m.getFrom().equals(this.host)) {
          gpc.indicateInvalidTurn(m.getFrom(), "Tile could not be added to GameBoard.");
        } else {
          InvalidMoveMessage im =
              new InvalidMoveMessage(m.getFrom(), "Tile could not be added to GameBoard.");
          clients.get(m.getFrom()).sendToClient(im);
        }
        return;
      }

      if (m.getFrom().equals(this.getHost())) {
        player.moveToRack(oldTile, m.getNewX());
      } else {
        AddTileMessage atm =
            new AddTileMessage(m.getFrom(), oldTile, m.getNewX(), m.getNewY());
        clients.get(m.getFrom()).sendToClient(atm);
      }

    } else if (m.getNewY() != -1 && m.getOldY() != -1) { // move on game board
      if (!this.gameController.moveTileOnGameBoard(m.getFrom(), m.getOldX(),
          m.getOldY(), m.getNewX(), m.getNewY())) {
        if (m.getFrom().equals(this.host)) {
          gpc.indicateInvalidTurn(m.getFrom(), "Tile could not be added to GameBoard.");
        } else {
          InvalidMoveMessage im =
              new InvalidMoveMessage(m.getFrom(), "Tile could not be added to GameBoard.");
          clients.get(m.getFrom()).sendToClient(im);
        }
        return;
      }

      AddTileMessage atm =
          new AddTileMessage(m.getFrom(), oldTile, m.getNewX(), m.getNewY());
      sendToAll(atm);

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      RemoveTileMessage rtm =
          new RemoveTileMessage(m.getFrom(), m.getOldX(), m.getOldY());
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
              if (lsc != null) {
                lsc.updateChat(um.getText(), um.getDateTime(), um.getFrom());
              }
              break;
            case START_GAME:
              lsc.startGameScreen();
              Sound.playStartGameSound();
              gpc.setTimerDuration(GameSettings.getTimePerPlayer());
              gpc.startTimer();
              gpc.indicatePlayerTurn(gameState.getCurrentPlayer());
              StartGameMessage sgm = (StartGameMessage) m;
              gpc.updateRemainingLetters(sgm.getRemainingTilesInTileBag());
              gpc.updateChat("-- " + sgm.getCurrrentPlayer() + ", you begin! --", null, "");
              if (!host.equals(sgm.getCurrrentPlayer())) {
                gpc.changeDoneStatus(false);
                gpc.changeSkipAndChangeStatus(false);
              }
              break;
            case GAME_STATISTIC:
              gpc.stopTimer();
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }

              gpc.close();
              new LeaderboardScreen(gameState.getGameStatistics(), player).start(new Stage());

              break;
            default:
              break;
          }
        } else {
          switch (m.getMessageType()) {
            case DISCONNECT:
              DisconnectMessage dm = (DisconnectMessage) m;
              gpc.removeJoinedPlayer(dm.getFrom());
              gpc.updateChat("-- " + dm.getFrom() + " left the Game! --", null, "");

              if (dm.getTiles() != null) {
                for (Tile t : dm.getTiles()) {
                  gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(),
                      false);
                }
              }
              break;
            case SEND_CHAT_TEXT:
              SendChatMessage scm = (SendChatMessage) m;
              gpc.updateChat(scm.getText(), scm.getDateTime(), scm.getSender());
              break;
            case ADD_TILE:
              AddTileMessage atm = (AddTileMessage) m;
              gpc.addTile(atm.getTile());
              Sound.playMoveTileSound();
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
              if (trm.getTurnInfo() != null) {
                gpc.updateChat(trm.getTurnInfo(), null, "");
              }

              if (trm.getIsValid()) {
                gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
                gpc.updateRemainingLetters(trm.getRemainingTilesInTileBag());
                gpc.stopTimer();
                if (trm.getWinner() == null) {
                  gpc.indicatePlayerTurn(trm.getNextPlayer());
                  gpc.updateChat("-- " + trm.getNextPlayer() + ", it's your turn! --", null, "");
                  gpc.startTimer();
                  gpc.changeDoneStatus(trm.getNextPlayer().equals(host));
                  gpc.changeSkipAndChangeStatus(trm.getNextPlayer().equals(host));
                } else {
                  gpc.updateChat(
                      "-- " + gameState.getGameStatistics().get(player.getNickname()).getWinner()
                          + " won the game --",
                      null, "");
                  gpc.updateChat("Lobby closing in 10 seconds...", null, "");
                  gpc.changeDoneStatus(false);
                  gpc.changeSkipAndChangeStatus(false);
                }

                if (gpc.getAlert2() != null) {
                  gpc.getAlert2().close();
                }
                gpc.resetSkipAndChange();

                semaphoreCommit = true;
                semaphoreReset = true;
                sem = true;
                
                // Audio output
                if (trm.getWinner() != null) {
                  Sound.playEndGameSound();
                } else if (trm.getTurnInfo().equals("Time's up!")) {
                  Sound.playUnsuccessfulTurnSound();
                } else {
                  Sound.playSuccessfulTurnSound();
                }
              } else {
                Sound.playUnsuccessfulTurnSound();
              }
              break;
            case RESET_TURN:
              ResetTurnMessage resetM = (ResetTurnMessage) m;
              for (Tile t : resetM.getTiles()) {
                gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(), false);
              }
              break;
            case UPDATE_CHAT:
              UpdateChatMessage um = (UpdateChatMessage) m;
              gpc.updateChat(um.getText(), um.getDateTime(), um.getFrom());
              break;
            case TILE:
              TileMessage trMessage = (TileMessage) m;
              for (Tile t : trMessage.getTiles()) {
                t.setOnRack(false);
                t.setOnGameBoard(true);
                gpc.addTile(t);
              }
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

    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(0);
      }
    }
  }

  /**
   * This method is called to prepare the server for the game. A GameController is created which
   * initializes the dictionary, the gameboard and the tilebag, taking the GameSettings into
   * account. All Players are notified, that the game starts and receive their initial tiles.
   */

  public void startGame() {
    this.gameController = new GameController(gameState);
    gameState.setCurrentPlayer(this.gameController.getNextPlayer());
    sendToAll(new StartGameMessage(host, 10,
        this.gameController.getTileBag().getRemaining()
            - this.gameState.getAllPlayers().size() * GameSettings.getTilesOnRack(),
        this.gameState.getCurrentPlayer(), GameSettings.getTimePerPlayer()));
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    gameState.setRunning(true);

    distributeInitialTiles();

    this.gameController.newTurn();
    this.gameState.initializeScoresWithZero(this.gameState.getAllPlayers());
    initializeAi();
    Runnable r = new Runnable() {
      public void run() {
        handleAi(gameState.getCurrentPlayer());
      }
    };
    new Thread(r).start();
    for (PlayerData client : gameState.getAllPlayers()) {
      this.gameState.addGameStatistics(client.getNickname());
    }
  }

  private void initializeAi() {
    for (AiPlayer a : this.aiPlayers.values()) {
      a.setGc(this.gameController);
      a.generateTileCombinations();
      a.setAilevel(AiPlayer.AiLevel.valueOf(GameSettings.getAiDifficulty().toUpperCase()));
    }
  }

  /**
   * This method is called, when the game is finished and opens the statistics screen.
   *
   * @author ldreyer
   */
  public void endGame() {
    Turn turn = this.gameController.getTurn();
    calculateGameStatistics();

    sendToAll(new TurnResponseMessage(turn.getPlayer(), turn.isValid(),
        this.gameState.getScore(turn.getPlayer()), turn.toString(), null,
        this.gameController.getTileBag().getRemaining(),
        this.gameState.getGameStatistics().get(this.host).getWinner()));

    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    gameState.setRunning(false);
    sendToAll(new GameStatisticMessage(this.host, this.gameState.getGameStatistics()));
  }

  /**
   * This method is used to calculate relevant gameStatistics.
   *
   * @author lurny
   */
  public void calculateGameStatistics() {
    for (Turn t : this.gameController.getTurns()) {
      String p = t.getPlayer();
      if (this.gameState.getGameStatisticsOfPlayer(p).getBestTurn() < t.getTurnScore()) {
        this.gameState.getGameStatisticsOfPlayer(p).setBestTurn(t.getTurnScore());
        List<String> wordList = new ArrayList<String>();
        for (Word word : t.getWords()) {
          wordList.add(word.toString());
        }
        this.gameState.getGameStatisticsOfPlayer(p).setBestWords(wordList);
      }
      this.gameState.getGameStatisticsOfPlayer(p)
          .setPlayedTiles(this.gameState.getGameStatisticsOfPlayer(p).getPlayedTiles()
              + t.getLaydDownTiles().size());
      this.gameState.getGameStatisticsOfPlayer(p)
          .setTotalTurns(this.gameState.getGameStatisticsOfPlayer(p).getTotalTurns() + 1);
    }
    // for each player

    List<String> playersList = new ArrayList<String>();
    for (PlayerData client : gameState.getAllPlayers()) {
      this.gameState.getGameStatisticsOfPlayer(client.getNickname())
          .setScore(gameState.getScore(client.getNickname()));
      playersList.add(client.getNickname());
    }

    for (int z = 0; z < gameState.getAllPlayers().size(); z++) {
      for (int i = 0; i < gameState.getAllPlayers().size() - 1; i++) {
        if (gameState.getScore(playersList.get(i)) < gameState.getScore(playersList.get(i + 1))) {
          Collections.swap(playersList, i, i + 1);
        }
      }
    }
    for (String p : playersList) {
      this.gameState.getGameStatisticsOfPlayer(p).setAllPlayers(playersList);
    }

  }

  /**
   * This method sends the current lobby status containing the gameState and all relevant game
   * settings.
   *
   * @author ldreyer
   */
  public void sendLobbyStatus() {
    LobbyStatusMessage lsm = new LobbyStatusMessage(this.host, GameSettings.getTimePerPlayer(),
        GameSettings.getMaxScore(), GameSettings.getBingo(), GameSettings.getDictionary(),
        GameSettings.getAiDifficulty(), GameSettings.getTilesOnRack(), GameSettings.getLetters(),
        this.gameState);
    sendToAll(lsm);
  }

  /**
   * gets the Player of the current instance.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * sets the Player of the current instance on parameter player.
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * gets the ServerProtocol of the current instance.
   */
  public ServerProtocol getServerProtocol() {
    return this.serverProtocol;
  }

  /**
   * gets the gamePanelController of the current instance.
   */
  public GamePanelController getGamePanelController() {
    return gpc;
  }

  /**
   * sets the GamePanelController of the current instance on parameter gpc.
   */
  public void setGamePanelController(GamePanelController gpc) {
    this.gpc = gpc;
  }

  /**
   * sets the LobbyScreenController of the current instance on parameter lsc.
   */
  public void setLobbyScreenController(LobbyScreenController lsc) {
    this.lsc = lsc;
  }

  /**
   * gets the lobbyScreenController of the current instance.
   */
  public LobbyScreenController getLobbyScreenController() {
    return this.lsc;
  }

  /**
   * sets the variable running of the current instance.
   */
  public void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * gets the AiPlayers list.
   *
   * @author pkoenig 
   */
  public HashMap<String, AiPlayer> getAiPlayers() {
    return aiPlayers;
  }

  /**
   * sets the variable aiPlayers list of the class server.
   *
   * @author pkoenig
   */
  public void setAiPlayers(HashMap<String, AiPlayer> aiPlayers) {
    this.aiPlayers = aiPlayers;
  }

  /**
   * adds an aiPlayer to the aiPlayers List and joins the game.
   */
  public void addAiPlayer(AiPlayer aiPlayer) {
    this.aiPlayers.put(aiPlayer.getNickname(), aiPlayer);
    this.gameState.joinGame(aiPlayer.getPlayerInfo());
  }

  /**
   * checks if the aiPlayer is in the aiPlayers list.
   */
  public boolean isinAiPlayer(AiPlayer aiPlayer) {
    return this.aiPlayers.containsKey(aiPlayer.getNickname());
  }

  /**
   * removes the player (nickname) from the aiPlayers list.
   */
  public void removeFromAiPlayers(String nickname) {
    this.aiPlayers.remove(nickname);
  }
}
