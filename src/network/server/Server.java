package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import game.GameController;
import game.GameSettings;
import game.GameState;
import mechanic.PlayerData;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.ShutdownMessage;


public class Server {

  private ServerSocket serverSocket;
  private GameState gameState;
  private GameController gameController;
  private boolean running;

  private String host;
  private HashMap<String, ServerProtocol> clients = new HashMap<>();

  public Server(PlayerData host, String customGameSettings) {
    this.host = host.getNickname();
    this.gameState = new GameState(host, customGameSettings);
    this.gameController = new GameController(this.gameState);
  }


  public void listen() {
    running = true;
    try {
      serverSocket = new ServerSocket(GameSettings.port);
      System.out.println("Server runs");

      while (running) {
        Socket clientSocket = serverSocket.accept();

        ServerProtocol serverThread = new ServerProtocol(clientSocket, this);
        serverThread.start();
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

  private synchronized void sendTo(List<String> clientNames, Message m) {
    List<String> fails = new ArrayList<String>();
    for (String nickname : clientNames) {
      try {
        ServerProtocol c = clients.get(nickname);
        c.sendToClient((Message) (m));
      } catch (IOException e) {
        fails.add(nickname);
        continue;
      }
    }
    for (String c : fails) {
      System.out.println("Client " + c + " removed (message delivery failed).");
      removeClient(c);
    }
    updateServerUI(m);
  }


  public void sendToAll(Message m) {
    sendTo(new ArrayList<String>(getClientNames()), (Message) (m));
  }


  public void sendToAllBut(String name, Message m) {
    synchronized (this.clients) {
      List<String> senderList = getClientNames();
      senderList.remove(name);
      sendTo(senderList, m);
    }

  }

  public void updateServerUI(Message m) {
    // TODO Server UI updates
  }

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
}
