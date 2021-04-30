package gui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import mechanic.Player;

public class GamePanelTest {

  private Player host;
  private Player client;
  LobbyScreenController controller;

  public GamePanelTest() {
    // setting up all players and controller
    this.host = new Player("host");
    this.client = new Player("client");
    this.controller = new LobbyScreenController();
    // host and connection of client
    host.setHost(true);
    host.host();
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    host.getServer().setlpc(this.controller);
    client.setHost(false);
    try {
      client.connect(InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    this.client.getClientProtocol().setLC(this.controller);
    // start game
    this.host.getServer().startGame();
  }


  public static void main(String[] args) {
    new GamePanelTest();
  }
}
