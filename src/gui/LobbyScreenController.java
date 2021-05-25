package gui;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import game.GameSettings;
import game.GameState;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.AIplayer;
import mechanic.Player;
import mechanic.PlayerData;
import network.messages.ConnectMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;

/**
 * Handles all User inputs in the Lobby Screen as well as the connection of players.
 *
 * @author nilbecke
 *
 */
public class LobbyScreenController implements EventHandler<ActionEvent> {

  private Player player;
  private String address;
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
  private Label bestWordKey;
  @FXML
  private Label bestTurnKey;
  @FXML
  private TextField input;
  @FXML
  private TextArea chat;
  @FXML
  private Button start;
  @FXML
  private Button settings;
  @FXML
  private Button add1;
  @FXML
  private Button add2;
  @FXML
  private Button add3;
  @FXML
  private Button remove1;
  @FXML
  private Button remove2;
  @FXML
  private Button remove3;
  @FXML
  private Button tutorial;
  @FXML
  private Button profile0;
  @FXML
  private Button profile1;
  @FXML
  private Button profile2;
  @FXML
  private Button profile3;
  @FXML
  private Button copy;
  @FXML
  private ImageView pic1;
  @FXML
  private ImageView pic2;
  @FXML
  private ImageView pic3;
  @FXML
  private ImageView pic4;
  @FXML
  private GridPane gp;



  /**
   * Handles all user inputs in the LobbyScreen.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "tutorial":
        OpenExternalScreen.open(FileParameters.datadir + "/ScrabbleRules.pdf");
        break;
      case "leavelobby":
        close();
        break;
      case "send":
      case "input":
        this.cc.sendChatMessage(this.player.getNickname(), this.input.getText());
        // Reset the Textlabel
        this.input.setText("");
        break;
      case "start":
        this.start.setDisable(true);
        this.settings.setDisable(true);
        Task<Void> task = new Task<Void>() {
          @Override
          public Void call() {
            player.getServer().startGame();
            return null;
          }
        };
        new Thread(task).start();
        break;
      case "settings":
        new SettingsScreen(this.player, true).start(new Stage());
        break;
      case "add1":
      case "add2":
      case "add3":
        addAiPlayer(Character.getNumericValue(s.charAt(3)));
        break;
      case "remove1":
      case "remove2":
      case "remove3":
        removePlayer(Character.getNumericValue(s.charAt(6)));
        break;
      case "profile1":
      case "profile2":
      case "profile0":
      case "profile3":
        showStatistics(Character.getNumericValue(s.charAt(7)));
        break;
      case "copy":
        ClipboardContent content = new ClipboardContent();
        content.putString(this.ip.getText().substring(7));
        Clipboard.getSystemClipboard().setContent(content);
        break;
      default:
        break;
    }
  }



  public void initData(Player current, String connection) {

    instance = this;
    this.player = current;
    this.countdown.setText(5 + "");
    this.chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");
    this.cc = new ChatController(this.player);

    if (player.isHost()) {
      this.address = current.getServer().getInetAddress().getHostAddress();
      ConnectMessage cm = new ConnectMessage(this.player.getPlayerInfo());
      sendMessage(cm);
      this.ip.setText("Link:  " + address);

    } else {
      this.player.getClientProtocol().setLc(this);
      this.address = connection;
      this.ip.setText("");
      this.start.setDisable(true);
      this.copy.setDisable(true);
      this.copy.setOpacity(0);
    }
  }

  /**
   * This methods gets called if a user clicks on the avatar or username of another player to show
   * the statistics of the user.
   * 
   * @param index defines in which position the user is placed.
   */
  public void showStatistics(int index) {
    if (index > players.size() - 1) {
      return;
    }
    if (players.get(index) != null) {
      Pattern p = Pattern.compile("AI\\s.");
      Matcher m = p.matcher(players.get(index).getNickname());
      if (!m.matches()) {
        new UserStatisticsScreen(players.get(index), false).start(new Stage());
      }
    }
  }

  /**
   * This method adds an AI Player to the Lobby. Gets called when the host clicks on the "+" button.
   * Can only be called by the host. in the Lobby.
   * 
   * @param index defines in which slot the ai player needs to be put.
   */
  public void addAiPlayer(int index) {
    int i = 1;
    String aiName = "AI " + i;
    while (this.player.getServer().checkNickname(aiName)) {
      i++;
      aiName = "AI " + i;
    }
    AIplayer p = new AIplayer(aiName, this.getPlayer().getServer().getGameController(),
        AIplayer.AiLevel.valueOf(GameSettings.getAiDifficulty().toUpperCase()));
    p.setHost(false);
    p.setAvatar("/avatars/avatar" + (int) (Math.random() * 10) + ".png");

    this.getPlayer().getServer().addAiPlayer(p);
    this.getPlayer().getServer().sendToAll(new ConnectMessage(p.getPlayerInfo()));

    // try {
    // Thread.sleep(100);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    //
    // }
    // LobbyStatusMessage m = new LobbyStatusMessage(this.getPlayer().getServer().getHost(),
    // this.getPlayer().getServer().getGameState());

    // this.getPlayer().getServer().sendToAll(m);

    // try {
    // p.connect(InetAddress.getLocalHost().getHostAddress());
    // this.getPlayer().getServer().addAiPlayer(p);
    // } catch (UnknownHostException e) {
    // e.printStackTrace();
    // }


  }

  /**
   * Updates the "+" and "-" buttons according to the current lobby state. Only gets called by host.
   */

  public void updateButtons() {
    int playersJoined = this.player.getServer().getGameState().getAllPlayers().size();
    switch (playersJoined) {
      // Only Host
      case 1:
        this.remove1.setOpacity(0);
        this.remove1.setDisable(true);
        this.add1.setOpacity(1);
        this.add1.setDisable(false);
        this.add2.setOpacity(0);
        this.add2.setDisable(true);
        break;
      case 2:
        this.add1.setDisable(true);
        this.add1.setOpacity(0);
        this.add2.setDisable(false);
        this.add2.setOpacity(1);
        this.add3.setDisable(true);
        this.add3.setOpacity(0);
        this.remove1.setDisable(false);
        this.remove1.setOpacity(1);
        this.remove2.setDisable(true);
        this.remove2.setOpacity(0);
        this.remove3.setDisable(true);
        this.remove3.setOpacity(0);
        break;
      case 3:
        this.add2.setDisable(true);
        this.add2.setOpacity(0);
        this.add3.setDisable(false);
        this.add3.setOpacity(1);
        this.remove2.setDisable(false);
        this.remove2.setOpacity(1);
        this.remove3.setDisable(true);
        this.remove3.setOpacity(0);
        break;
      case 4:
        this.add3.setDisable(true);
        this.add3.setOpacity(0);
        this.remove3.setDisable(false);
        this.remove3.setOpacity(1);
        break;
      default:
        break;
    }
  }

  /**
   * "Kicks" a player. Can only be called by host. Requires confirmation before kick.
   * 
   * @param index represents the position of the player to be removed.
   */
  public void removePlayer(int index) {
    String nickname = this.players.get(index).getNickname();

    CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
    alert.setTitle("Are you sure?");
    alert.setHeaderText(null);
    alert.setContentText("Are you sure you want to remove " + nickname + " from the Lobby?");

    alert.changeButtonText("Remove", ButtonType.OK);
    alert.changeButtonText("Cancel", ButtonType.CANCEL);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      Pattern p = Pattern.compile("AI\\s.");
      Matcher m = p.matcher(nickname);
      if (m.matches()) {
        this.player.getServer().handleLeaveLobby(nickname);
        this.player.getServer().sendLobbyStatus();

        this.player.getServer().removeFromAiPlayers(nickname);
      } else {
        DisconnectMessage dm = new DisconnectMessage(nickname, null);
        sendMessage(dm);
      }
      updateJoinedPlayers();
    } else {
      alert.close();
    }

  }

  public void refuseConnection() {
    closeWindow();
    CustomAlert alert = new CustomAlert(AlertType.ERROR);
    alert.setTitle("Lobby full");
    alert.setHeaderText("Lobby Full!");
    alert.setContentText("Try another Link or try again later");
    alert.show();
  }


  /**
   * Starts the game screen for all clients. Is called when a host starts a game from the lobby.
   */

  public void startGameScreen() {
    try {
      Stage stage = new Stage(StageStyle.DECORATED);

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainGameScreen.fxml"));
      stage.setScene(new Scene(loader.load()));

      GamePanelController controller = loader.getController();

      if (player.isHost()) {
        player.getServer().setGamePanelController(controller);
      } else {
        player.getClientProtocol().setGamePanelController(controller);
      }

      player.setGamePanelController(controller);

      controller.initData(player);
      stage.setOnCloseRequest(e -> controller.close());
      stage.setTitle("Scrabble3");
      // stage.setResizable(false);
      stage.show();

      closeWindow();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
   * Getter Method for the current Instance of the controller.
   *
   * @return Current Instance of the contoller
   */
  public static LobbyScreenController getLobbyInstance() {
    return instance;
  }

  /**
   * Lets a player disconnect or connect. Also updates the "remove" button depending on the amount
   * of players joined.
   * 
   */
  public void updateJoinedPlayers() {

    GameState gs;
    if (player.isHost()) {
      updateButtons();
      gs = player.getServer().getGameState();
      players = gs.getAllPlayers();
    } else {
      gs = player.getClientProtocol().getGameState();
      try {
        players = gs.getAllPlayers();
      } catch (NullPointerException e) {
        return;
      }
    }
    updateLabels(this.players);
    // TODO NOT WORKING!!
    // Kicks a player instantly if the lobby is full.
    if (players.size() == 5) {
      if (players.get(1).getNickname().equals(this.player.getNickname())) {
        close();
        CustomAlert alert = new CustomAlert(AlertType.ERROR);
        alert.setTitle("Lobby Full");
        alert.setHeaderText("Lobby already full!");
        alert.setContentText("Try another link or try again later");
        alert.show();
        return;
      }
    }
  }

  public void updateLabels(List<PlayerData> list) {

    Label[] nicknames = {player1, player2, player3, player4};
    ImageView[] avatars = {pic1, pic2, pic3, pic4};

    /**
     * Will ensure, that every Player sees him on top and with a "YOU"
     * 
     * @author pkoenig
     */
    // TODO tbd
    /**
     * @author nilbecke
     */

    for (int i = 0; i <= 3; i++) {
      if (i < players.size()) {
        // Player connects
        if (players.get(i).isHost()) {
          if (i != 0) {
            PlayerData swap = players.get(0);
            players.set(0, players.get(i));
            players.set(i, swap);
            updateLabels(players);
          } else {
            nicknames[i].setText(players.get(i).getNickname() + " (Host)");
          }
        } else if (players.get(i).getNickname().equals(this.player.getNickname())) {
          nicknames[i].setText(players.get(i).getNickname() + " (Me)");
        } else {
          nicknames[i].setText(players.get(i).getNickname());
        }
        avatars[i].setImage(
            new Image(getClass().getResource(players.get(i).getAvatar()).toExternalForm()));
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
    updateJoinedPlayers();
  }

  /**
   * Lets a player disconnect, is called by the server.
   * 
   * @param nickname of the player disconnecting
   */

  public void removeJoinedPlayer(String nickname) {
    updateJoinedPlayers();
  }

  /**
   * Closes the Lobby and stops the server.
   */
  public void close() {
    if (this.player.getServer() != null) {
      for (PlayerData p : players) {
        if (!p.isHost()) {
          sendMessage(new DisconnectMessage(p.getNickname(), null));
        }
      }
      sendMessage(new DisconnectMessage(this.player.getNickname(), null));
      this.player.getServer().stopServer();
    } else if (!this.player.isHost()) {
      sendMessage(new DisconnectMessage(this.player.getNickname(), null));
      this.player.getClientProtocol().disconnect();
    }
    closeWindow();
    /**
     * @author pkoenig
     */
    Stage newLoginStage = new Stage();
    newLoginStage.setX(this.chat.getScene().getWindow().getX());
    newLoginStage.setY(this.chat.getScene().getWindow().getY());
    /**
     * @author nilbecke
     */
    new LoginScreen().start(newLoginStage);
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

  public Player getPlayer() {
    return player;
  }

}
