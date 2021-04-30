package network.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import game.GameSettings;
import gui.GamePanelController;
import gui.LobbyScreenController;
import mechanic.Player;
import network.client.ClientProtocol;


/** @author ldreyer */

public class ServerTest {

  private Server server;
  private Player host;
  private Player player;
  private Player player2;
  private ClientProtocol connection;
  private ClientProtocol connection2;

  @Before
  public void setUp() throws Exception {
    this.host = new Player("Host");
    this.player = new Player("ScrabbleGamer");
    this.player2 = new Player("ScrabblePro");
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void test() throws Exception {

    // start Server
    server = new Server(host, null);
    System.out.println(server.getGameState().getAllPlayers());

    Runnable r = new Runnable() {
      public void run() {
        server.listen();
      }
    };
    new Thread(r).start();
    Thread.sleep(50);

    // connect client1: (player)
    this.connection =
        new ClientProtocol("127.0.0.1", GameSettings.port, player, new GamePanelController(),
            new LobbyScreenController());

    if (this.connection.isOK()) {
      this.connection.start();
    } else {
      fail("connection not OK");
    }

    // ConnectMessage cm = new ConnectMessage(this.player.getPlayerInfo());
    // this.connection.sendToServer(cm);
    Thread.sleep(10);
    assertTrue(server.checkNickname("ScrabbleGamer"));
    System.out.println(server.getGameState().getAllPlayers());


    // connect client2: (player 2)
    this.connection2 = new ClientProtocol("127.0.0.1", GameSettings.port, player2,
        new GamePanelController(), new LobbyScreenController());

    if (this.connection2.isOK()) {
      this.connection2.start();
    } else {
      fail("connection not OK");
    }


    Thread.sleep(10);

    System.out.println(server.getGameState().getPlayerData("ScrabblePro"));
    assertTrue(server.getGameState().getAllPlayers().contains(player2.getPlayerInfo()));

    // fill player racks and send StartMessage
    server.startGame();

    server.stopServer();

  }

}
