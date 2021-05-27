package network.server;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import game.GameSettings;
import mechanic.Player;
import network.client.ClientProtocol;


/**
 * This class tests the connection setUp of client and server. It is asserted that the client's
 * nickname was added to the player list.
 *
 * @author ldreyer
 */

public class ServerTest {

  private Server server;
  private Player host;
  private Player player;
  private Player player2;
  private ClientProtocol connection;
  private ClientProtocol connection2;

  /** Player objects are initialized. */

  @Before
  public void setUp() throws Exception {
    this.host = new Player("Host");
    this.player = new Player("ScrabbleGamer");
    this.player2 = new Player("ScrabblePro");
  }

  /** Client Server connection is tested. */

  @Test
  public void testClientServer() throws Exception {

    // start Server
    server = new Server(host, null);
    System.out.println(server.getGameState().getAllPlayers());

    Runnable r = new Runnable() {
      public void run() {
        server.listen(true);
      }
    };
    new Thread(r).start();
    Thread.sleep(50);

    // connect client1: (player)
    this.connection = new ClientProtocol("127.0.0.1", GameSettings.port, player);

    if (this.connection.isOk()) {
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
    this.connection2 = new ClientProtocol("127.0.0.1", GameSettings.port, player2);

    if (this.connection2.isOk()) {
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
