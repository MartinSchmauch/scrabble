package network.client;

import game.GameSettings;
import game.GameState;
import gui.CustomAlert;
import gui.GamePanelController;
import gui.LeaderboardScreen;
import gui.LobbyScreenController;
import gui.LoginScreenController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javafx.application.Platform;
import javafx.stage.Stage;
import mechanic.Field;
import mechanic.Player;
import mechanic.Tile;
import network.messages.AddTileMessage;
import network.messages.ConnectMessage;
import network.messages.ConnectionRefusedMessage;
import network.messages.DisconnectMessage;
import network.messages.GameStatisticMessage;
import network.messages.InvalidMoveMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TileMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;
import util.Sound;

/**
 * This is the client Protocol, which is used for the client server communication. Every connected
 * client has an instance of this class.
 *
 * @author lurny
 **/

public class ClientProtocol extends Thread {
  private GameState gameState;
  private GamePanelController gpc;
  private LobbyScreenController lsc;
  private Player player;
  private Message message;
  private String ipFromServer;
  private int portFromServer;
  private Socket clientSocket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private boolean running = true;

  /**
   * This method creates an instance of the class.
   */
  public ClientProtocol(String ip, int port, Player player) {
    try {
      this.player = player;
      this.clientSocket = new Socket(ip, port);
      this.out = new ObjectOutputStream(clientSocket.getOutputStream());
      this.in = new ObjectInputStream(clientSocket.getInputStream());
      this.out.writeObject(new ConnectMessage(this.player.getPlayerInfo()));
      out.flush();
      out.reset();
      System.out.println("Local Port (Client): " + this.clientSocket.getLocalPort());
    } catch (IOException e) {
      CustomAlert.showWarningAlert("Invalid link.", "Try another Link or try again later");
    }
  }

  public boolean isOk() {
    return (clientSocket != null) && (clientSocket.isConnected()) && !(clientSocket.isClosed());
  }

  /**
   * Handling of the Server messages.
   */
  public void run() {
    try {
      message = (Message) in.readObject();

      if (message.getMessageType() == MessageType.CONNECT) {
        ConnectMessage cm = (ConnectMessage) message;
        this.player.setNickname(cm.getPlayerInfo().getNickname());
        System.out.println("New username: " + player.getNickname());
        LoginScreenController.getInstance().startLobby();
      } else if (message.getMessageType() == MessageType.CONNECTION_REFUSED) {
        ConnectionRefusedMessage mrMessage = (ConnectionRefusedMessage) message;
        CustomAlert.showWarningAlert(mrMessage.getReason(), "Try another Link or try again later.");
        disconnect();
      }
      while (running) {
        try {
          message = (Message) in.readObject(); // read message from server

          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              switch (message.getMessageType()) {
                case SHUTDOWN:
                  ShutdownMessage shutMessage = (ShutdownMessage) message;
                  disconnect();
                  if (gameState.getGameRunning()) {
                    gpc.showShutdownMessage(shutMessage.getFrom(), shutMessage.getReason());
                  } else {
                    lsc.close();
                    CustomAlert.showWarningAlert("Server closed.", "The host closed the server.");
                  }
                  break;
                case ADD_TILE:
                  AddTileMessage atMessage = (AddTileMessage) message;
                  if (atMessage.getNewY() == -1) {
                    player.moveToRack(atMessage.getTile(), atMessage.getNewX());
                  } else {
                    atMessage.getTile().setField(
                        new Field(atMessage.getNewX(), atMessage.getNewY()));
                    atMessage.getTile().setOnRack(false);
                    atMessage.getTile().setOnGameBoard(true);
                    gpc.addTile(atMessage.getTile());
                    Sound.playMoveTileSound();
                  }
                  break;
                case REMOVE_TILE:
                  RemoveTileMessage rtMessage = (RemoveTileMessage) message;
                  if (rtMessage.getY() == -1) {
                    player.removeRackTile(rtMessage.getX());
                  }
                  gpc.removeTile(rtMessage.getX(), rtMessage.getY(), (rtMessage.getY() == -1));
                  break;
                case INVALID:
                  InvalidMoveMessage imm = (InvalidMoveMessage) message;
                  gpc.indicateInvalidTurn(imm.getFrom(), imm.getReason());
                  break;
                case RESET_TURN:
                  ResetTurnMessage resettMessage = (ResetTurnMessage) message;
                  List<Tile> tileList = resettMessage.getTiles();
                  // remove Tiles from UI Gameboard and domain Gameboard
                  for (Tile t : tileList) {
                    gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(),
                        false);
                  }
                  // if this is the current player: add Tiles to Rack
                  if (player.getNickname().equals(resettMessage.getFrom())) {
                    for (Tile t : tileList) {
                      player.addTileToRack(t);
                      gpc.addTile(t);
                    }
                  }
                  break;
                case TILE:
                  TileMessage trMessage = (TileMessage) message;

                  for (Tile t : trMessage.getTiles()) {
                    if (t.getField() != null && t.getField().getyCoordinate() != -1) {
                      // case "on rack"
                      t.setOnRack(false);
                      t.setOnGameBoard(true);
                      gpc.addTile(t);
                    } else {
                      // case "on board"
                      player.addTileToRack(t);
                      gpc.addTile(t);
                    }

                  }
                  break;
                case TURN_RESPONSE:
                  TurnResponseMessage trm = (TurnResponseMessage) message;
                  if (trm.getTurnInfo() != null) {
                    gpc.updateChat(trm.getTurnInfo(), null, "");
                  }

                  if (trm.getIsValid()) {
                    gpc.updateRemainingLetters(trm.getRemainingTilesInTileBag());
                    gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
                    gpc.stopTimer();
                    if (trm.getWinner() == null) {
                      gpc.indicatePlayerTurn(trm.getNextPlayer());
                      gameState.setCurrentPlayer(trm.getNextPlayer());
                      gpc.startTimer();
                      gpc.updateChat("-- " + trm.getNextPlayer() + ", it's your turn! --", null,
                          "");
                      gpc.changeDoneStatus(trm.getNextPlayer().equals(player.getNickname()));
                      gpc.changeSkipAndChangeStatus(
                          trm.getNextPlayer().equals(player.getNickname()));
                    } else {
                      gpc.updateChat("-- " + trm.getWinner() + " won the game --", null, "");
                      gpc.updateChat("Lobby closing in 10 seconds...", null, "");
                      gpc.changeDoneStatus(false);
                      gpc.changeSkipAndChangeStatus(false);
                    }
                    
                    // Audio output
                    if (trm.getWinner() != null) {
                      Sound.playEndGameSound();
                    } else if (trm.getTurnInfo() != null && trm.getTurnInfo().equals("Time's up!")) {
                      Sound.playUnsuccessfulTurnSound();
                    } else {
                      Sound.playSuccessfulTurnSound();
                    }
                  } else {
                    Sound.playUnsuccessfulTurnSound();
                  }

                  if (gpc.getAlert2() != null) {
                    gpc.getAlert2().close();
                  }
                  gpc.resetSkipAndChange();
                  break;
                case LOBBY_STATUS:
                  if (lsc != null) {
                    LobbyStatusMessage lsMessage = (LobbyStatusMessage) message;
                    gameState = lsMessage.getGameState();
                    lsc.updateJoinedPlayers();
                    GameSettings.setTimePerPlayer(lsMessage.getTimePerPlayer());
                    GameSettings.setMaxScore(lsMessage.getMaxScore());
                    GameSettings.setBingo(lsMessage.getBingo());
                    GameSettings.setDictionary(lsMessage.getDictionary());
                    GameSettings.setAiDifficulty(lsMessage.getAiDifficulty());
                    GameSettings.setTilesOnRack(lsMessage.getTilesOnRack());
                    GameSettings.setLetters(lsMessage.getLetters());
                  }
                  break;
                case START_GAME:
                  StartGameMessage sgMessage = (StartGameMessage) message;
                  Sound.playStartGameSound();
                  gameState.setCurrentPlayer(sgMessage.getFrom());
                  gameState.setRunning(true);
                  lsc.startGameScreen();
                  gpc.setTimerDuration(sgMessage.getTimerDuration());
                  gpc.startTimer();
                  gpc.updateRemainingLetters(sgMessage.getRemainingTilesInTileBag());
                  gpc.indicatePlayerTurn(sgMessage.getCurrrentPlayer());
                  gpc.updateChat("-- " + sgMessage.getCurrrentPlayer() + ", you begin! --", null,
                      "");
                  if (!player.getNickname().equals(sgMessage.getCurrrentPlayer())) {
                    gpc.changeDoneStatus(false);
                    gpc.changeSkipAndChangeStatus(false);
                  }
                  break;
                case UPDATE_CHAT:
                  UpdateChatMessage ucMessage = (UpdateChatMessage) message;
                  if (gameState == null) { // only true for AIplayer
                    break;
                  }
                  if (!gameState.getGameRunning()) {
                    lsc.updateChat(ucMessage.getText(), ucMessage.getDateTime(),
                        ucMessage.getFrom());
                  } else {
                    gpc.updateChat(ucMessage.getText(), ucMessage.getDateTime(),
                        ucMessage.getFrom());
                  }
                  break;
                case CONNECT:
                  if (lsc != null) {
                    ConnectMessage conMessage = (ConnectMessage) message;
                    gameState.joinGame(conMessage.getPlayerInfo());
                    lsc.addJoinedPlayer(conMessage.getPlayerInfo());
                  }
                  break;
                case GAME_STATISTIC:
                  GameStatisticMessage gsMessage = (GameStatisticMessage) message;
                  gpc.stopTimer();
                  gpc.close();
                  new LeaderboardScreen(gsMessage.getGameStatistics(), player).start(new Stage());
                  break;
                case DISCONNECT:
                  DisconnectMessage discMessage = (DisconnectMessage) message;
                  gameState.leaveGame(discMessage.getFrom());

                  if (!gameState.getGameRunning()) {
                    if (discMessage.getFrom().equals(player.getNickname())) {
                      disconnect();
                      lsc.close();
                      CustomAlert.showWarningAlert("You were kicked!", "The host kicked you from "
                          + "the lobby.");
                    } else {
                      lsc.updateJoinedPlayers();
                    }
                  } else {
                    gpc.updateChat("-- " + discMessage.getFrom() + " left the Game! --", null, "");
                    if (discMessage.getTiles() != null) {
                      for (Tile t : discMessage.getTiles()) {
                        gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(),
                            false);
                      }
                    }

                    gpc.removeJoinedPlayer(discMessage.getFrom());
                  }
                  break;
                default:
                  break;
              }
            }
          });
        } catch (ClassNotFoundException | IOException e) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setLc(LobbyScreenController lc) {
    this.lsc = lc;
  }

  /**
   * Disconnect client Shutdown streams and sockets.
   */
  public void disconnect() {
    running = false;
    try {
      if (!clientSocket.isClosed()) {
        clientSocket.close(); // close streams and socket
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method sends a given message to the server that will be handled in ServerProtocol.
   */
  public void sendToServer(Message message) {
    try {
      this.out.writeObject(message);
      this.out.flush();
      this.out.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * gets the variable gameState of the current instance.
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * sets the variable gameState of the current instance.
   */
  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * gets the variable player of the current instance.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * sets the variable player of the current instance.
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * gets the variable gpc of the current instance.
   */
  public GamePanelController getGamePanelController() {
    return gpc;
  }

  /**
   * sets the variable gpc of the current instance.
   */
  public void setGamePanelController(GamePanelController gpc) {
    this.gpc = gpc;
  }

  /**
   * sets the variable lsc of the current instance.
   */
  public void setLobbyScreenController(LobbyScreenController lsc) {
    this.lsc = lsc;
  }

  /**
   * gets the variable ipFromServer of the current instance.
   */
  public String getIpFromServer() {
    return ipFromServer;
  }

  /**
   * sets the variable ipFromServer of the current instance.
   */
  public void setIpFromServer(String ipFromServer) {
    this.ipFromServer = ipFromServer;
  }

  /**
   * gets the variable portFromServer of the current instance.
   */
  public int getPortFromServer() {
    return portFromServer;
  }

  /**
   * sets the variable portFromServer of the current instance.
   */
  public void setPortFromServer(int portFromServer) {
    this.portFromServer = portFromServer;
  }

}
