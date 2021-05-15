/** @author lurny */
package network.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import game.GameState;
import gui.FileParameters;
import gui.GamePanelController;
import gui.LobbyScreenController;
import javafx.application.Platform;
import mechanic.Field;
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
import network.messages.RemoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.ShutdownMessage;
import network.messages.StartGameMessage;
import network.messages.TileMessage;
import network.messages.TurnResponseMessage;
import network.messages.UpdateChatMessage;
import util.JsonHandler;

public class ClientProtocol extends Thread {
  private GameState gameState;
  private GamePanelController gpc;
  private LobbyScreenController lsc;
  private Player player;
  private Message m;
  /**
   * @author pkoenig
   */
  private String ipFromServer;
  private int portFromServer;

  /**
   * @author lurny
   */
  private Socket clientSocket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private boolean running = true;

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

  public boolean isOK() {
    return (clientSocket != null) && (clientSocket.isConnected()) && !(clientSocket.isClosed());
  }

  // Verarbeitung der vom Server empfangenen Nachrichten
  public void run() {
    try {
      m = (Message) in.readObject();

      if (m.getMessageType() == MessageType.CONNECT) {
        ConnectMessage cm = (ConnectMessage) m;
        this.player.setNickname(cm.getPlayerInfo().getNickname());
        System.out.println("New username: " + player.getNickname());
      } else if (m.getMessageType() == MessageType.CONNECTION_REFUSED) {
        this.clientSocket.close();
        /**
         * @author pkoenig
         */
        // } else if (m.getMessageType() == MessageType.CONNECTION_REFUSED){
        // ConnectionRefusedMessage crm = (ConnectionRefusedMessage) m;
        // this.player.setNickname(crm.getAlternativeUserName());
        // //sendToServer(new ConnectMessage(this.player.getPlayerInfo()));
        // System.out.println("cp, line 87");
        // disconnect();
        // player.connect(ipFromServer);
        //// m = (Message) in.readObject();
        //// if (m.getMessageType() == MessageType.CONNECT) {
        //// ConnectMessage cm = (ConnectMessage) m;
        //// this.player.setNickname(cm.getPlayerInfo().getNickname());
        //// System.out.println("New username: " + player.getNickname());
        //// }
        //// else {
        //// disconnect();
        //// }
      } else {
        disconnect();
      }
      /**
       * @author lurny
       */

      while (running) {
        try {
          m = (Message) in.readObject(); // read message from server

          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              switch (m.getMessageType()) {
                case CONNECTION_REFUSED:
                  ConnectionRefusedMessage mrMessage = (ConnectionRefusedMessage) m;
                  System.out.println("cp, l 115");
                  // tbImplemented
                  break;
                case SHUTDOWN:
                  ShutdownMessage sMessage = (ShutdownMessage) m;
                  disconnect();
                  if (gpc != null) {
                    gpc.showShutdownMessage(sMessage.getFrom(), sMessage.getReason());
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

                case RESET_TURN:
                  ResetTurnMessage resetTMessage = (ResetTurnMessage) m;
                  List<Tile> tileList = resetTMessage.getTiles();
                  // remove Tiles from UI Gameboard and domain Gameboard
                  for (Tile t : tileList) {
                    gpc.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(),
                        false);
                  }
                  // if this is the current player: add Tiles to Rack
                  if (player.getNickname().equals(gameState.getCurrentPlayer())) {
                    for (Tile t : tileList) {
                      player.addTileToRack(t);
                      gpc.addTile(t);
                    }
                  }
                  break;
                case TILE:
                  TileMessage trMessage = (TileMessage) m;

                  for (Tile t : trMessage.getTiles()) {
                    player.addTileToRack(t);
                    gpc.addTile(t);
                  }
                  break;
                case TURN_RESPONSE:
                  TurnResponseMessage trm = (TurnResponseMessage) m;
                  // TODO please check if it is correct
                  gpc.updateRemainingLetters(trm.getRemainingTilesInTileBag());
                  if (trm.getIsValid()) {
                    gpc.updateScore(trm.getFrom(), trm.getCalculatedTurnScore());
                    gpc.indicatePlayerTurn(trm.getNextPlayer());
                    gameState.setCurrentPlayer(trm.getNextPlayer()); // do we need to do this step -
                                                                     // // fat client!
                    gpc.startTimer();
                    if (trm.getFrom().equals(player.getNickname())) {

                    }
                    gpc.changeDoneStatus(trm.getNextPlayer().equals(player.getNickname()));
                    gpc.changeSkipAndChangeStatus(trm.getNextPlayer().equals(player.getNickname()));
                  } else {
                    gpc.indicateInvalidTurn(trm.getFrom(), "Invalid Turn");
                  }
                  break;
                case LOBBY_STATUS:
                  if (lsc != null) {
                    LobbyStatusMessage lsMessage = (LobbyStatusMessage) m;
                    gameState = lsMessage.getGameState();
                    lsc.updateJoinedPlayers();
                  }
                  break;
                case START_GAME:
                  StartGameMessage sgMessage = (StartGameMessage) m;
                  gameState.setCurrentPlayer(sgMessage.getFrom());
                  gameState.setRunning(true);
                  // TODO replace with Server Game Settings (or important parts) eg. joker value
                  JsonHandler jsonHandler = new JsonHandler();
                  jsonHandler.loadGameSettings(
                      new File(FileParameters.datadir + "defaultGameSettings.json"));
                  lsc.startGameScreen();
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
                case GAME_STATISTIC:
                  GameStatisticMessage gsMessage = (GameStatisticMessage) m;
                  // tbImplemented
                  break;
                case UPDATE_CHAT:
                  UpdateChatMessage ucMessage = (UpdateChatMessage) m;
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
                    ConnectMessage cMessage = (ConnectMessage) m;
                    gameState.joinGame(cMessage.getPlayerInfo());
                    lsc.addJoinedPlayer(cMessage.getPlayerInfo());
                  }
                  break;
                case DISCONNECT:
                  if (gameState != null) {
                    DisconnectMessage dMessage = (DisconnectMessage) m;
                    gameState.leaveGame(dMessage.getFrom());

                    if (!gameState.getGameRunning()) {
                      if (lsc != null) {
                        if (dMessage.getFrom().equals(player.getNickname())) {
                          lsc.removeJoinedPlayer(dMessage.getFrom());
                          lsc.close();
                        } else {
                          lsc.updateJoinedPlayers();
                        }
                      }
                    } else {
                      if (gpc != null) {
                        gpc.removeJoinedPlayer(dMessage.getFrom());
                      }
                    }
                  }
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

  public void setLC(LobbyScreenController lc) {
    this.lsc = lc;
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
