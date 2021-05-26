package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import game.GameSettings;
import game.GameState;
import gui.CustomAlert;
import gui.GamePanelController;
import gui.LeaderboardScreen;
import gui.LobbyScreenController;
import gui.LoginScreenController;
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
  private Message m;
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
      e.printStackTrace();
      System.out.println("Could not establish connection to " + ip + ":" + port);
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
      m = (Message) in.readObject();

      if (m.getMessageType() == MessageType.CONNECT) {
        ConnectMessage cm = (ConnectMessage) m;
        this.player.setNickname(cm.getPlayerInfo().getNickname());
        System.out.println("New username: " + player.getNickname());
        LoginScreenController.getInstance().startLobby();
      } else if (m.getMessageType() == MessageType.CONNECTION_REFUSED) {
        ConnectionRefusedMessage mrMessage = (ConnectionRefusedMessage) m;
        CustomAlert.showWarningAlert(mrMessage.getReason(), "Try another Link or try again later.");
        disconnect();
      }
      while (running) {
        try {
          m = (Message) in.readObject(); // read message from server

          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              switch (m.getMessageType()) {
                case SHUTDOWN:
                  ShutdownMessage shutMessage = (ShutdownMessage) m;
                  disconnect();
                  if (gameState.getGameRunning()) {
                    gpc.showShutdownMessage(shutMessage.getFrom(), shutMessage.getReason());
                  } else {
                    lsc.close();
                    CustomAlert.showWarningAlert("Server closed.", "The host closed the server.");
                  }
                  break;
                case ADD_TILE:
                  AddTileMessage atMessage = (AddTileMessage) m;
                  if (atMessage.getNewYCoordinate() == -1) {
                    player.moveToRack(atMessage.getTile(), atMessage.getNewXCoordinate());
                  } else {
                    atMessage.getTile().setField(
                        new Field(atMessage.getNewXCoordinate(), atMessage.getNewYCoordinate()));
                    atMessage.getTile().setOnRack(false);
                    atMessage.getTile().setOnGameBoard(true);
                    gpc.addTile(atMessage.getTile());
                  }
                  break;
                case REMOVE_TILE:
                  RemoveTileMessage rtMessage = (RemoveTileMessage) m;
                  if (rtMessage.getY() == -1) {
                    player.removeRackTile(rtMessage.getX());
                  }
                  gpc.removeTile(rtMessage.getX(), rtMessage.getY(), (rtMessage.getY() == -1));
                  break;
                case INVALID:
                  InvalidMoveMessage imm = (InvalidMoveMessage) m;
                  gpc.indicateInvalidTurn(imm.getFrom(), imm.getReason());
                  break;
                case RESET_TURN:
                  ResetTurnMessage resettMessage = (ResetTurnMessage) m;
                  List<Tile> tileList = resettMessage.getTiles();
                  System.out.println(resettMessage.getFrom() + "  " + tileList.size());
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
                  TileMessage trMessage = (TileMessage) m;

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
                  System.out.println("TurnResponseMessageReceived");
                  TurnResponseMessage trm = (TurnResponseMessage) m;
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
                  }

                  if (gpc.getAlert2() != null) {
                    gpc.getAlert2().close();
                  }
                  gpc.resetSkipAndChange();
                  break;
                case LOBBY_STATUS:
                  if (lsc != null) {
                    LobbyStatusMessage lsMessage = (LobbyStatusMessage) m;
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
                  StartGameMessage sgMessage = (StartGameMessage) m;
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
                  UpdateChatMessage ucMessage = (UpdateChatMessage) m;
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
                    ConnectMessage conMessage = (ConnectMessage) m;
                    gameState.joinGame(conMessage.getPlayerInfo());
                    lsc.addJoinedPlayer(conMessage.getPlayerInfo());
                  }
                  break;
                case GAME_STATISTIC:
                  GameStatisticMessage gsMessage = (GameStatisticMessage) m;
                  gpc.stopTimer();
                  gpc.close();
                  new LeaderboardScreen(gsMessage.getGameStatistics(), player).start(new Stage());
                  break;
                case DISCONNECT:
                  DisconnectMessage discMessage = (DisconnectMessage) m;
                  gameState.leaveGame(discMessage.getFrom());
                  gpc.updateChat("-- " + discMessage.getFrom() + " left the Game! --", null, "");

                  if (!gameState.getGameRunning()) {
                    lsc.updateJoinedPlayers();
                  } else {
                    
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

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
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

  /**
   * @author pkoenig
   */

  /**
   * @return the ipFromServer
   */
  public String getIpFromServer() {
    return ipFromServer;
  }

  /**
   * @param ipFromServer the ipFromServer to set
   */
  public void setIpFromServer(String ipFromServer) {
    this.ipFromServer = ipFromServer;
  }

  /**
   * @return the portFromServer
   */
  public int getPortFromServer() {
    return portFromServer;
  }

  /**
   * @param portFromServer the portFromServer to set
   */
  public void setPortFromServer(int portFromServer) {
    this.portFromServer = portFromServer;
  }

}
