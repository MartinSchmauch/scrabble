package gui;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import game.GameSettings;
import game.GameState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import mechanic.Player;
import mechanic.PlayerData;
import network.messages.DisconnectMessage;
import network.messages.LobbyStatusMessage;
import network.messages.Message;
import network.messages.StartGameMessage;
import network.server.Server;

/**
 * Handles all User inputs in the Lobby Screen as well as the connection of players.
 *
 * @author nilbecke
 *
 */
public class LobbyScreenController implements EventHandler<ActionEvent> {

  private Player player;
  private InetAddress address;
  private static LobbyScreenController instance;
  private GameSettings gs;
  private List<PlayerData> players;
  private ChatController cc;

  @FXML
  private Label ip;
  @FXML
  private Label player1;
  @FXML
  private Label player2;
  @FXML
  private Label player3;
  @FXML
  private Label player4;
  @FXML
  private Label countdown;
  @FXML
  private TextField input;
  @FXML
  private TextArea chat;
  @FXML
  private Button start;
  @FXML
  private Button settings;
  @FXML
  private ImageView pic1;
  @FXML
  private ImageView pic2;
  @FXML
  private ImageView pic3;
  @FXML
  private ImageView pic4;

  /**
   * Set up labels etc before launching the lobby screen.
   */
  @FXML
  public synchronized void initialize() {

    // TODO remove later (just demo purposes)
    this.countdown.setText(5 + "");
    this.player = LobbyScreen.getInstance().getPlayer();
    this.chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");
    this.cc = new ChatController(this.player);
    address = null;
    instance = this;

    try {
      address = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    if (!this.player.isHost()) {
      this.start.setOpacity(0.4);
      this.start.setDisable(true);
      this.ip.setOpacity(0);
      this.settings.setOpacity(0.4);
      this.player.getClientProtocol().setLC(instance);

    } else {

      sendLobbyMessage(this.player.getNickname(), this.player.getServer().getGameState());
      this.ip.setText("Link:  " + address.getHostAddress());
    }

    // update nicknames and avatars continuously
    Timeline playerUpdate =
        new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            updateJoinedPlayers();
          }
        }));
    playerUpdate.setCycleCount(Timeline.INDEFINITE);
    playerUpdate.play();
  }

  /**
   * Handles all user inputs in the LobbyScreen.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "leavelobby":
        sendDisconnectMessage(this.player.getNickname());
        LobbyScreen.close();
        closeWindow();
        break;
      case "send":
      case "input":
        this.cc.sendChatMessage(this.player.getNickname(), this.input.getText());

        // Reset the Textlabel
        this.input.setText("");
        break;
      case "start":
        startGame();
        break;
      case "settings":
        new SettingsScreen(this.gs).start(new Stage());
        break;
      default:
        break;
    }
  }

  /**
   * Starts the countdown before the game launches.
   */
  public void startGame() {
    this.start.setDisable(true);
    this.settings.setDisable(true);
    sendMessage((Message) new StartGameMessage(this.player.getNickname(), 10));
  }


  /**
   * Starts the game screen for all clients. Is called when a host starts a game from the lobby.
   */
  public synchronized void startGameScreen(Player currentPlayer) {


    // Displays countdown
    Timeline cdLabel =
        new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
          int counter = 4;

          @Override
          public void handle(ActionEvent e) {
            updateCountdown(counter);
            counter--;
          }
        }));
    cdLabel.setCycleCount(5);
    cdLabel.play();

    cdLabel.setOnFinished(e -> Platform.runLater(new Runnable() {
      @Override
      public void run() {
        try {
          new GamePanel(player).start(new Stage());
          closeWindow();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }));



  }

  public void updateCountdown(int c) {
    this.countdown.setText(c + "");
  }

  /**
   * Closes the current lobby window. Is called when game starts or user decides to leave lobby.
   */
  public void closeWindow() {
    Stage s = (Stage) this.chat.getScene().getWindow();
    s.close();
  }

  /**
   * Sends a given message to all players.
   * 
   * @param m The Message to be sent
   */
  public boolean sendMessage(Message m) {
    if (this.player.isHost()) {
      this.player.getServer().sendToAll(m);

    } else {
      this.player.getClientProtocol().sendToServer(m);
    }
    return true;
  }

  /**
   * Send the Lobby status as host to clients.
   * 
   * @param id Nickname of Host
   * @param gs GameState from host
   */
  public void sendLobbyMessage(String id, GameState gs) {
    Message m = (Message) new LobbyStatusMessage(id, gs);
    sendMessage(m);
  }


  /**
   * Updates Lobbychat by using the updateChat method in the Chat Controller.
   * 
   * @param message
   * @param dateTime
   * @param sender
   */
  public void updateChat(String message, LocalDateTime dateTime, String sender) {
    this.chat.appendText("\n" + this.cc.updateChat(message, dateTime, sender));
    this.chat.setScrollTop(Double.MAX_VALUE);
  }


  /**
   * Lets a player disconnect from the current game. If the leaving player is the host, the lobby
   * closes.
   * 
   * @param playerId Nickname of leaving player
   */

  public void sendDisconnectMessage(String playerId) {
    Message m = (Message) new DisconnectMessage(playerId);
    sendMessage(m);
  }

  /**
   * Getter Method for the current Instance of the controller.
   *
   * @return Current Instance of the contoller
   */
  public static LobbyScreenController getLobbyInstance() {
    return instance;
  }

  /**
   * Lets a player disconnect or connect.
   * 
   */
  public void updateJoinedPlayers() {
    GameState gs;
    if (this.player.isHost()) {
      gs = this.player.getServer().getGameState();
      this.players = gs.getAllPlayers();
    } else {
      gs = player.getClientProtocol().getGameState();
      try {
        this.players = gs.getAllPlayers();
      } catch (NullPointerException e) {
        return;
      }
    }

    Label[] nicknames = {player1, player2, player3, player4};
    ImageView[] avatars = {pic1, pic2, pic3, pic4};
    for (int i = 0; i <= 3; i++) {
      if (i < players.size()) {
        // Player connects
        nicknames[i].setText(players.get(i).getNickname());
        avatars[i]
            .setImage(new Image("file:" + FileParameters.datadir + players.get(i).getAvatar()));
      } else {
        // Player disconnects
        nicknames[i].setText("");
        avatars[i].setImage(null);
      }
    }
  }

  /**
   * Reads updated game settings and distributes them to all players.
   * 
   * @param: settings new Instance of game settings
   */
  public void updateGameSettings(GameSettings settings) {
    // TODO
  }

  /**
   * Get the current Server.
   * 
   * @return Current instance of the server if present else null
   */
  public Server getServer() {
    return this.player.getServer();
  }

  /**
   * Get a reference onto the game settings currently used.
   * 
   * @return Currently used game settings
   */
  public GameSettings getSettings() {
    return this.gs;
  }

  /**
   * Lets a player connects, is calles by the server.
   * 
   * @param player Playerdata of the player to be (dis-)connecting
   */
  public void addJoinedPlayer(PlayerData player) {

  }

  /**
   * Lets a player disconnect, is called by the server.
   * 
   * @param nickname of the player disconnecting
   */

  public void removeJoinedPlayer(String nickname) {
    // TODO
  }


  /**
   * Closes current window.
   */
  public void closeWindow(Button b) {
    Stage st = (Stage) b.getScene().getWindow();
    st.close();
  }

  /**
   * Reads updated game settings and distributes them to all players.
   * 
   * @param settings new Instance of game settings
   */
  public void updategameSettings(GameSettings settings) {
    // TODO
  }

}
