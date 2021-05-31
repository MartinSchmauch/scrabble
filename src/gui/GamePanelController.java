package gui;

import game.GameState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.ShutdownMessage;
import network.messages.TileMessage;
import network.server.Server;
import util.FileParameters;



/**
 * This class is the Controller Class for the Main Gamel Panel UI for the Client.
 *
 * @author mschmauc
 */

public class GamePanelController implements EventHandler<ActionEvent>, Runnable {

  protected Player player;
  protected List<PlayerData> players;
  protected ClientProtocol cp;
  protected Server server;
  protected static boolean exchangeTilesMode = false;
  protected static boolean fieldLabelsEnabled = true;
  protected List<Tile> tilesToExchange = new ArrayList<Tile>();
  protected static int[] selectedCoordinates = new int[2]; // row, column
  protected static int[] targetCoordinates = new int[2];
  protected ChatController cc;


  protected int min;
  protected int sec;
  protected Thread thread;
  protected int timerDuration;

  protected double timeLeftBar;
  protected boolean turnCountdown;
  protected CustomAlert alert2;

  protected static GamePanelController instance;

  protected Text[] playerLabel;
  protected Text[] pointsLabel;
  protected Text[] playerNameLabel;
  protected ImageView[] avatarImageView;
  protected Rectangle[] rect;
  protected Rectangle[] rackTiles;
  protected Text[] dlsLabel;
  protected Text[] tlsLabel;
  protected Text[] dwsLabel;
  protected Text[] twsLabel;
  protected Button[] buttons;



  // protected VisualTile cursorTile;

  @FXML
  protected StackPane upperPane;
  @FXML
  protected TextArea chat;
  @FXML
  protected TextField chatInput;
  @FXML
  protected Button sendButton;
  @FXML
  protected Button skipAndChangeButton;
  @FXML
  protected Button doneButton;
  @FXML
  protected Button leaveGameButton;
  @FXML
  protected Button settingsButton;
  @FXML
  protected Button rulesButton;
  @FXML
  protected Button activateFieldLabels;
  @FXML
  protected Button darkMode;
  @FXML
  protected ImageView image1;
  @FXML
  protected ImageView image2;
  @FXML
  protected ImageView image3;
  @FXML
  protected ImageView image4;
  @FXML
  protected ImageView background;
  @FXML
  protected Text player1;
  @FXML
  protected Text player2;
  @FXML
  protected Text player3;
  @FXML
  protected Text player4;
  @FXML
  protected Text playerOnePoints;
  @FXML
  protected Text playerTwoPoints;
  @FXML
  protected Text playerThreePoints;
  @FXML
  protected Text playerFourPoints;
  @FXML
  protected Text pointsLabel1;
  @FXML
  protected Text pointsLabel2;
  @FXML
  protected Text pointsLabel3;
  @FXML
  protected Text pointsLabel4;
  @FXML
  protected Text remainingLetters;
  @FXML
  protected Text timer;
  @FXML
  protected Text dws0;
  @FXML
  protected Text dws1;
  @FXML
  protected Text dws2;
  @FXML
  protected Text dws3;
  @FXML
  protected Text dws4;
  @FXML
  protected Text dws5;
  @FXML
  protected Text dws6;
  @FXML
  protected Text dws7;
  @FXML
  protected Text dws8;
  @FXML
  protected Text dws9;
  @FXML
  protected Text dws10;
  @FXML
  protected Text dws11;
  @FXML
  protected Text dws12;
  @FXML
  protected Text dws13;
  @FXML
  protected Text dws14;
  @FXML
  protected Text dws15;
  @FXML
  protected Text dws16;
  @FXML
  protected Text tws0;
  @FXML
  protected Text tws1;
  @FXML
  protected Text tws2;
  @FXML
  protected Text tws3;
  @FXML
  protected Text tws4;
  @FXML
  protected Text tws5;
  @FXML
  protected Text tws6;
  @FXML
  protected Text tws7;
  @FXML
  protected Text dls0;
  @FXML
  protected Text dls1;
  @FXML
  protected Text dls2;
  @FXML
  protected Text dls3;
  @FXML
  protected Text dls4;
  @FXML
  protected Text dls5;
  @FXML
  protected Text dls6;
  @FXML
  protected Text dls7;
  @FXML
  protected Text dls8;
  @FXML
  protected Text dls9;
  @FXML
  protected Text dls10;
  @FXML
  protected Text dls11;
  @FXML
  protected Text dls12;
  @FXML
  protected Text dls13;
  @FXML
  protected Text dls14;
  @FXML
  protected Text dls15;
  @FXML
  protected Text dls16;
  @FXML
  protected Text dls17;
  @FXML
  protected Text dls18;
  @FXML
  protected Text dls19;
  @FXML
  protected Text dls20;
  @FXML
  protected Text dls21;
  @FXML
  protected Text dls22;
  @FXML
  protected Text dls23;
  @FXML
  protected Text tls0;
  @FXML
  protected Text tls1;
  @FXML
  protected Text tls2;
  @FXML
  protected Text tls3;
  @FXML
  protected Text tls4;
  @FXML
  protected Text tls5;
  @FXML
  protected Text tls6;
  @FXML
  protected Text tls7;
  @FXML
  protected Text tls8;
  @FXML
  protected Text tls9;
  @FXML
  protected Text tls10;
  @FXML
  protected Text tls11;
  @FXML
  protected Text remaining;
  @FXML
  protected Text timeLabel;
  @FXML
  protected Rectangle chatBox;
  @FXML
  protected Rectangle rackBox;
  @FXML
  protected Rectangle playerBox;
  @FXML
  protected Rectangle tile1;
  @FXML
  protected Rectangle currentPlayer1;
  @FXML
  protected Rectangle currentPlayer2;
  @FXML
  protected Rectangle currentPlayer3;
  @FXML
  protected Rectangle currentPlayer4;
  @FXML
  protected Rectangle r0;
  @FXML
  protected Rectangle r1;
  @FXML
  protected Rectangle r2;
  @FXML
  protected Rectangle r3;
  @FXML
  protected Rectangle r4;
  @FXML
  protected Rectangle r5;
  @FXML
  protected Rectangle r6;
  @FXML
  protected Rectangle r7;
  @FXML
  protected Rectangle r8;
  @FXML
  protected Rectangle r9;
  @FXML
  protected Rectangle r10;
  @FXML
  protected Rectangle r11;
  @FXML
  protected GridPane board;
  @FXML
  protected GridPane rack;
  @FXML
  protected ProgressBar timeProgress;
  @FXML
  protected Rectangle backgroundGamePanel;
  @FXML
  protected StackPane boardStack;
  @FXML
  protected StackPane playerBoxStackPane;
  @FXML
  protected StackPane chatStackPane;
  @FXML
  protected StackPane stackPlayer1;
  @FXML
  protected StackPane stackPlayer2;
  @FXML
  protected StackPane stackPlayer3;
  @FXML
  protected StackPane stackPlayer4;
  @FXML
  protected StackPane referenceSizeForRack;


  /**
   * This method initializes the GamePanelController and is being called upon creation of the
   * Controller. Here all the labels on the UI are being reset and adapted to the current game
   * state.
   *
   * @param player the player reference of the user who starts this local controller
   */
  public void initData(Player player) {
    this.player = player;
    cc = new ChatController(player);
    chat.setEditable(false);
    this.chat.appendText("Welcome to the chat! Please be gentle :)");
    playerLabel = new Text[] {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    pointsLabel =
        new Text[] {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    playerNameLabel = new Text[] {player1, player2, player3, player4};
    avatarImageView = new ImageView[] {image1, image2, image3, image4};
    dlsLabel = new Text[] {dls0, dls1, dls2, dls3, dls4, dls5, dls6, dls7, dls8, dls9, dls10, dls11,
        dls12, dls13, dls14, dls15, dls16, dls17, dls18, dls19, dls20, dls21, dls22, dls23};
    tlsLabel =
        new Text[] {tls0, tls1, tls2, tls3, tls4, tls5, tls6, tls7, tls8, tls9, tls10, tls11};
    dwsLabel = new Text[] {dws0, dws1, dws2, dws3, dws4, dws5, dws6, dws7, dws8, dws9, dws10, dws11,
        dws12, dws13, dws14, dws15, dws16};
    twsLabel = new Text[] {tws0, tws1, tws2, tws3, tws4, tws5, tws6, tws7};
    rackTiles = new Rectangle[] {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
    buttons = new Button[] {this.doneButton, this.skipAndChangeButton, this.activateFieldLabels,
        this.sendButton, this.darkMode, this.settingsButton, this.leaveGameButton,
        this.rulesButton};


    activateFieldLabels.setText("Disable Labels");
    this.setFieldLabelVisibility(true);

    GameState gs;
    if (player.isHost()) {
      gs = player.getServer().getGameState();
      players = gs.getAllPlayers();
      leaveGameButton.setText("Stop Server");
    } else {
      gs = player.getClientProtocol().getGameState();
      try {
        players = gs.getAllPlayers();
      } catch (NullPointerException e) {
        return;
      }
      leaveGameButton.setText("Leave");
    }

    for (int i = 0; i <= 3; i++) {
      if (i < players.size()) {
        if (players.get(i).isHost()) {
          playerNameLabel[i].setText(players.get(i).getNickname() + " (Host)");
        } else if (players.get(i).getNickname().equals(this.player.getNickname())) {
          playerNameLabel[i].setText(players.get(i).getNickname() + " (Me)");
        } else {
          playerNameLabel[i].setText(players.get(i).getNickname());
        }
        pointsLabel[i].setText("0");
        playerLabel[i].setText("Points: ");
        avatarImageView[i].setImage(
            new Image(getClass().getResource(players.get(i).getAvatar()).toExternalForm()));
        avatarImageView[i].setFitHeight(50);
        avatarImageView[i].setFitWidth(50); 
      } else {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(null);
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
      }
    }
    remainingLetters.setText("");
    timer.setText("");
    timeProgress.setProgress(0.0);
    this.dws16.setText("★");

    // @author pkoenig
    
    // RackFields
    Rectangle rackField;
    ObservableList<Node> rackTiles = rack.getChildren();
    for (Node n : rackTiles) {
      rackField = (Rectangle) n;
      rackField.widthProperty().bind(referenceSizeForRack.widthProperty().add(20));
      rackField.heightProperty().bind(referenceSizeForRack.heightProperty().add(20));
    }


    // Background
    background.fitHeightProperty().bind(upperPane.heightProperty());
    background.fitWidthProperty().bind(upperPane.widthProperty());



    // Gameboard
    backgroundGamePanel.heightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(10));
    backgroundGamePanel.widthProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(10));

    board.prefHeightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.prefWidthProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.maxHeightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.maxWidthProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.minHeightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.minWidthProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.minHeightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));
    board.maxHeightProperty()
        .bind(Bindings.min(boardStack.widthProperty(), boardStack.heightProperty()).subtract(25));


    Pane p;
    Rectangle r;
    Text t;
    ObservableList<Node> guiTiles = board.getChildren();
    DoubleProperty tileFontSize = new SimpleDoubleProperty(10);
    tileFontSize.bind(Bindings.min(board.widthProperty(), board.heightProperty()).divide(85));
    for (Node n : guiTiles) {
      p = (Pane) n;
      try {
        r = (Rectangle) p.getChildren().get(0);
        r.heightProperty().bind(p.heightProperty());
        r.widthProperty().bind(p.widthProperty());
        p.setMinSize(0, 0);

      } catch (Exception e) {
        System.out.println(".");
      }

      try {
        t = (Text) p.getChildren().get(1);
        if (!t.getText().equals("â˜…")) {
          t.styleProperty().bind(Bindings.concat("-fx-font-size: ", tileFontSize.asString(), ";"));
          t.wrappingWidthProperty().bind(board.widthProperty().divide(15).subtract(5));
          p.setMinSize(0, 0);
        } else {
          t.setFont(new Font("Arial", 30));
          t.styleProperty().bind(Bindings.concat("-fx-font-size: ", 
              tileFontSize.add(20).asString(), ";"));
          p.setMinSize(0, 0);
          t.setTextAlignment(TextAlignment.CENTER);
        }

      } catch (Exception e) {
        System.out.println(".");
      }

    }
  }


  /**
   * Thread to countdown the maxmimum length of a turn.
   *
   * @author lurny
   */
  public void run() {
    String secString = "";
    while (turnCountdown) {
      this.timeLeftBar = (this.min * 60.0 + this.sec) / timerDuration;

      if (this.sec == 0 && this.min > 0) {
        this.sec = 59;
        this.min--;
      } else if (this.sec == 0 & this.min == 0) {
        this.turnCountdown = false;
        this.sendResetTurn();
      } else {
        this.sec--;
      }

      if (this.sec > 9) {
        secString = "" + this.sec;
      } else {
        secString = "0" + this.sec;
      }

      this.updateTimer(String.valueOf(min), secString);
      this.updateProgressBar(this.timeLeftBar);

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        this.turnCountdown = false;
      }
    }

  }

  /**
   * method to start the Turn timer.
   *
   * @author lurny
   */
  public void startTimer() {
    if (this.thread != null && !this.thread.isInterrupted()) {
      stopTimer();
    }

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.thread = new Thread(this);
    this.min = this.timerDuration / 60;
    this.sec = this.timerDuration % 60;
    this.turnCountdown = true;
    this.thread.start();
  }

  /**
   * method to stop the Turn timer.
   *
   * @author lurny
   */

  public void stopTimer() {
    this.thread.interrupt();
  }

  public void updateTimer(String min, String sec) {
    timer.setText(min + ":" + sec);
  }

  public void updateRemainingLetters(int number) {
    remainingLetters.setText(String.valueOf(number));
  }

  public void updateProgressBar(Double progress) {
    timeProgress.setProgress(progress);
  }

  /* --Listener methods that are executed upon Player UI Interaction-- */

  /**
   * Handles all button user inputs in the GamePanel.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "settingsButton":
        new SettingsScreen(this.player, false).start(new Stage());;
        break;
      case "leaveGameButton":
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        if (player.isHost()) {
          alert.setTitle("Leave the game and stop the server for all.");
          alert.setHeaderText("Leave game and stop server?");
          alert.setContentText("Do you really want to leave the game and stop the server?");
        } else {
          alert.setTitle("Leave the current game");
          alert.setHeaderText("Leave Game?");
          alert.setContentText("Do you really want to leave the current game?");
        }
        alert.initStyle(StageStyle.UNDECORATED);

        alert.changeButtonText("Yes", ButtonType.OK);
        alert.changeButtonText("No", ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          // if (player.isHost()) {
          // player.getServer().stopServer();
          // // Message m = new ShutdownMessage(this.player.getNickname(), REGULAR_SHUTDOWN);
          // // sendMessage(m);
          // } else {
          // sendGameInfoMessage("'" + this.player.getNickname() + "' left the game");
          // Message m = new DisconnectMessage(this.player.getNickname());
          // sendMessage(m);
          // }
          close(); 
          // Button b = (Button) e.getSource();
          // Stage st = (Stage) (b.getScene().getWindow());
          // st.close();
          new LoginScreen().start(new Stage());
        }
        break;
      case "rulesButton":
        OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
        break;
      case "sendButton":
      case "chatInput":
        sendMessageFromInput();
        break;
      case "skipAndChangeButton":
        if (!exchangeTilesMode) {
          exchangeTilesMode = true;
          changeSkipAndChangeStatus(false);
        }
        break;
      case "doneButton":
        if (exchangeTilesMode) {
          if (!this.tilesToExchange.isEmpty()) {
            alert2 = new CustomAlert(AlertType.CONFIRMATION);
            alert2.setTitle("Skip & Exchange selected tiles");
            alert2.setHeaderText("Skip & Exchange?");
            alert2.setContentText(
                "Do you want to skip the current turn and exchange the selected tiles ");
            alert2.initStyle(StageStyle.UNDECORATED);

            alert2.changeButtonText("Yes", ButtonType.OK);
            alert2.changeButtonText("No", ButtonType.CANCEL);

            Optional<ButtonType> result2 = alert2.showAndWait();
            if (result2.get() == ButtonType.OK) {
              // remove Tiles from GUI
              for (Tile t : this.tilesToExchange) {
                this.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(), true);
                this.player.removeRackTile(t.getField().getxCoordinate());
              }
              sendTileMessage(this.player.getNickname());
            } else {
              alert2.close();
            }

            this.setRackRectanglesBlack();
            tilesToExchange.removeAll(tilesToExchange); 
          }

          exchangeTilesMode = false;
          changeSkipAndChangeStatus(true);
        } else {
          sendCommitTurn(this.player.getNickname());
        }
        break;
      case "activateFieldLabels":
        if (fieldLabelsEnabled) {
          this.setFieldLabelVisibility(false);
          activateFieldLabels.setText("Enable Labels");
          fieldLabelsEnabled = false;
        } else {
          this.setFieldLabelVisibility(true);
          activateFieldLabels.setText("Disable Labels");
          fieldLabelsEnabled = true;
        }
        break;
      case "darkMode":
        switchDarkMode();
        break;
      default:
        break;
    }
  }

  /**
   * This method switches the Theme from dark mode to light mode and vice versa.

   * @author nilbecke
   */

  public void switchDarkMode() {
    ColorAdjust colorAdjust = new ColorAdjust();
    switch (this.darkMode.getText()) {
      case "Dark Mode":
        this.background.setImage(
            new Image(getClass().getResource("/fxml/images/ScrabbleBoardDark.png").toString()));
        colorAdjust.setBrightness(-0.2);
        this.background.setOpacity(1);
        this.background.setEffect(colorAdjust);

        this.remaining.setFill(Color.LIGHTGRAY);
        this.remainingLetters.setFill(Color.LIGHTGRAY);
        this.timer.setFill(Color.LIGHTGRAY);
        this.timeLabel.setFill(Color.LIGHTGRAY);

        rack.setBorder(new Border(new BorderStroke(Color.DARKGREY, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        chatStackPane.setBorder(new Border(new BorderStroke(Color.DARKGREY, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        playerBoxStackPane.setBorder(new Border(new BorderStroke(Color.DARKGREY, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        
        
        this.backgroundGamePanel.setStroke(Color.DARKGRAY);

        this.timeProgress.getStylesheets()
            .add(getClass().getResource("/fxml/DarkMode.css").toString());


        this.chat.getStylesheets().clear();
        this.chat.getStylesheets()
            .add(getClass().getResource("/fxml/DarkMode.css").toExternalForm());
        this.chatInput.getStylesheets().clear();
        this.chatInput.getStylesheets()
            .add(getClass().getResource("/fxml/DarkMode.css").toExternalForm());

        for (int i = 0; i < buttons.length; i++) {
          buttons[i].getStylesheets().clear();
          buttons[i].getStylesheets()
              .add(getClass().getResource("/fxml/DarkMode.css").toExternalForm());
        }

        for (int i = 0; i < rackTiles.length; i++) {
          rackTiles[i].setFill(Color.DARKGRAY);
          rackTiles[i].setStroke(Color.DARKGRAY);
        }
        for (int i = 0; i < playerNameLabel.length; i++) {
          playerNameLabel[i].setFill(Color.LIGHTGRAY);
        }
        this.darkMode.setText("Light Mode");
        this.darkMode.setTextFill(Color.LIGHTGRAY);
        break;

      case "Light Mode":

        this.timeProgress.getStylesheets().clear();

        this.remaining.setFill(Color.BLACK);
        this.remainingLetters.setFill(Color.BLACK);
        this.timer.setFill(Color.BLACK);
        this.timeLabel.setFill(Color.BLACK);

        rack.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        chatStackPane.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        playerBoxStackPane.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        
        this.backgroundGamePanel.setStroke(Color.BLACK);

        this.chat.getStylesheets()
            .add(getClass().getResource("/fxml/LightMode.css").toExternalForm());
        this.chatInput.getStylesheets()
            .add(getClass().getResource("/fxml/LightMode.css").toExternalForm());

        for (int i = 0; i < buttons.length; i++) {
          buttons[i].getStylesheets().clear();
          buttons[i].getStylesheets()
              .add(getClass().getResource("/fxml/Buttons.css").toExternalForm());
        }
        this.darkMode.setTextFill(Color.BLACK);
        for (int i = 0; i < rackTiles.length; i++) {
          rackTiles[i].setFill(Color.WHITE);
          rackTiles[i].setStroke(Color.BLACK);
        }
        for (int i = 0; i < playerNameLabel.length; i++) {
          playerNameLabel[i].setFill(Color.BLACK);
        }
        this.background.setImage(
            new Image(getClass().getResource("/fxml/images/ScrabbleBoard.png").toString()));
        colorAdjust.setBrightness(0);
        this.background.setEffect(colorAdjust);
        this.background.setOpacity(0.12);
        this.darkMode.setText("Dark Mode");
        break;
      default:
        break;
    }
  }



  /**
   * This method sets the Disable property of the skipAndChange Button. When you set toBeActivated
   * on 'true', the Button is being activated.
   *
   * @param toBeActivated the boolean that determines whether the status should be activated
   */
  public void changeSkipAndChangeStatus(boolean toBeActivated) {
    skipAndChangeButton.setDisable(!toBeActivated);
  }

  /**
   * This method sets the Disable property of the done Button. When you set toBeActivated on 'true',
   * the Button is being activated.
   *
   * @param toBeActivated the boolean that determines whether the status should be activated
   */
  public void changeDoneStatus(boolean toBeActivated) {
    doneButton.setDisable(!toBeActivated);
  }

  /**
   * Listener that is called, when a user starts a drag movement from a rack field. The coordinates
   * of the event starting location are being saved for this drag event in the selectedCoordinates
   * array.
   */
  @FXML
  public void rackDragStarted(MouseEvent event) {
    if (!exchangeTilesMode) {
      Node node = (Node) event.getSource();
      selectedCoordinates = getPos(node, true);
      // Image image =
      // new Image("file:" + System.getProperty("user.dir") + "/resources/general/tile.png");
      // rulesButton.getScene().setCursor(new ImageCursor(image));
      rulesButton.getScene().setCursor(Cursor.CLOSED_HAND);
      node.startFullDrag();
      // cursorTile = new VisualTile("H", 3, true);
      // cursorTile.setId("cursorTileFromRack");
      // upperPane.getChildren().add(cursorTile);
      // cursorTile.setOnMouseDragged(new EventHandler<MouseEvent>() {
      // public void handle(MouseEvent event) {
      // cursorTile.relocate(event.getX(), event.getY());
      // // cursorTile.setLayoutX(event.getX());
      // // cursorTile.setLayoutY(event.getY());
      // }
      // });
    }
  }

  @FXML
  public void test0(MouseEvent event) {

    // cursorTile.setTranslateX(event.getX());
    // cursorTile.setTranslateY(event.getY());
    // cursorTile.setLayoutX(event.getX());
    // cursorTile.setLayoutX(event.getY());
  }

  /**
   * Listener method that is called, when a user starts a drag movement from a board field. The
   * coordinates of the event starting location are being saved for this drag event in the
   * selectedCoordinates array.
   * 
   */
  @FXML
  public void boardDragStarted(MouseEvent event) {
    if (!exchangeTilesMode) {
      Node node = (Node) event.getSource();
      selectedCoordinates = getPos(node.getParent(), false);
      selectedCoordinates[0]++;
      selectedCoordinates[1]++;
      rulesButton.getScene().setCursor(Cursor.CLOSED_HAND);
      // Image image =
      // new Image("file:" + System.getProperty("user.dir") + "/resources/general/tile.png");
      // rulesButton.getScene().setCursor(new ImageCursor(image));
      node.startFullDrag();
    }
  }

  /**
   * Method that is executed when the cursor enters a node during a drag and drop event.
   */
  @FXML
  public void test2(MouseDragEvent event) {
    // rulesButton.getScene().setCursor(Cursor.CLOSED_HAND);
    // Node node = (Node) event.getSource();
    // selectedCoordinates = getPos(node, true);
    // System.out.println("node entered: " + selectedCoordinates[0] + "/" + selectedCoordinates[1]);
  }

  /**
   * Method that is executed when the cursor leaves a node during a drag and drop event.
   */
  @FXML
  public void test3(MouseDragEvent event) {
    // rulesButton.getScene().setCursor(Cursor.CLOSED_HAND);
    // Node node = (Node) event.getSource();
    // selectedCoordinates = getPos(node, true);
    // System.out.println("node exited: " + selectedCoordinates[0] + "/" + selectedCoordinates[1]);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a rack field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   */
  @FXML
  public void rackDragReleased(MouseDragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, true);
    // cursorTile = null;
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile

    } else if (selectedCoordinates[1] == -1) { // exchange tiles on rack
      player.reorganizeRackTile(selectedCoordinates[0], targetCoordinates[0]);

    } else if (selectedCoordinates[1] != -1) { // try to move tile from board to rack - sender!
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a board field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   */
  @FXML
  public void boardDragReleased(MouseDragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node.getParent(), false);
    targetCoordinates[0] += 1;
    targetCoordinates[1] += 1;
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile
    } else if (selectedCoordinates[1] != -1) { // exchange tiles on board
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    } else if (selectedCoordinates[1] == -1) { // move tile from rack to board
      player.moveToGameBoard(selectedCoordinates[0], targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
  }

  /**
   * Method that is executed upon the pressing of the mouse.
   */
  @FXML
  public void mousePressed(MouseEvent event) {
    // Node node = (Node) event.getSource();
    // node.setMouseTransparent(true);
  }

  /**
   * Method that is executed upon the release of a mouse click and sets the cursor to default.
   */
  @FXML
  public void mouseReleased(MouseEvent event) {
    // Node node = (Node) event.getSource();
    // node.setMouseTransparent(false);
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
  }

  /**
   * Method that allows player to double click on a tile that the player placed on the game panel at
   * the current turn. This has the effect, that the tile is put back on the rack.
   */
  @FXML
  public void mouseClicked(MouseEvent event) {
    if (event.getButton().equals(MouseButton.PRIMARY)) {
      if (event.getClickCount() == 2) {
        Node node = (Node) event.getSource();
        selectedCoordinates = getPos(node.getParent(), false);
        selectedCoordinates[0] += 1;
        selectedCoordinates[1] += 1;
        targetCoordinates[0] = this.player.getFreeRackField().getxCoordinate();
        targetCoordinates[1] = this.player.getFreeRackField().getyCoordinate();
        sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
            targetCoordinates[0], targetCoordinates[1]);
        resetCoordinates();
      }
    }
  }

  /**
   * Listener method that is called when a field on the rack is clicked. When the exchangeTilesMode
   * was selected before by clicking the Skip&Change button, the tile on the field is selected if
   * there is a tile on the specific field.
   */
  @FXML
  public void selectToExchange(MouseEvent event) {
    if (exchangeTilesMode) {
      Rectangle[] rect = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
      Node node = (Node) event.getSource();
      int x = getPos(node, true)[0];
      if (player.getRackTile(x) != null && !tilesToExchange.contains(player.getRackTile(x))) {
        tilesToExchange.add(player.getRackTile(x));
        rect[x].setStroke(Color.RED);
      } else if (player.getRackTile(x) != null && tilesToExchange.contains(player.getRackTile(x))) {
        tilesToExchange.remove(player.getRackTile(x));
        rect[x].setStroke(Color.BLACK);
      }
    }

  }

  /**
   * This method sets all coordinates for the helper arrays of the tile movements to -2. Thereby,
   * possible errors in the dropHandler methods are evaded.
   */
  public static void resetCoordinates() {
    selectedCoordinates[0] = -2;
    selectedCoordinates[1] = -2;
    targetCoordinates[0] = -2;
    targetCoordinates[1] = -2;
  }

  /**
   * Method to be executed when a player clicks on the "Send" button of the chat area in the
   * GamePanel The ChatController handles this event and gets the text from the textfield of the
   * chat area.
   */
  public void sendMessageFromInput() {
    this.cc.sendChatMessage(this.player.getNickname(), this.chatInput.getText());
    this.chatInput.setText("");
  }

  /**
   * Method that sends a message that is supposed to appear in the chat area and informs the users
   * about a game event e.g. a player left the game. Therefore the sender is left blank.
   *
   * @param message the message as String that is to be send in the chat
   */
  public void sendGameInfoMessage(String message) {
    this.cc.sendChatMessage("", message);
  }


  /* --Methods to be used by the ClientProtocol to change the UI of the Client-- */


  /**
   * Lets a player disconnect.
   *
   * @param playerToBeRemoved nickname of the player disconnecting
   */
  public void removeJoinedPlayer(String playerToBeRemoved) {
    int indexRemoved = 5;
    int currentPlayerIndex = 5;
    StackPane[] stackPane = {stackPlayer1, stackPlayer2, stackPlayer3, stackPlayer4};
    for (int i = 0; i < players.size(); i++) {
      if (stackPane[i].getBorder() != null) {
        currentPlayerIndex = i;
      }
      if (players.get(i).getNickname().equals(playerToBeRemoved)) {
        playerNameLabel[i].setText(null);
        pointsLabel[i].setText(null);
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
        indexRemoved = i;
        stackPane[i].setBorder(null);
      }
      if (i > indexRemoved && !playerNameLabel[i].getText().equals(null)) {
        playerNameLabel[i - 1].setText(playerNameLabel[i].getText());
        pointsLabel[i - 1].setText(pointsLabel[i].getText());
        playerLabel[i - 1].setText(playerLabel[i].getText());
        avatarImageView[i - 1].setImage(avatarImageView[i].getImage());

        playerNameLabel[i].setText(null);
        pointsLabel[i].setText("");
        playerLabel[i].setText(null);
        avatarImageView[i].setImage(null);
        
        if (i == currentPlayerIndex) {
          stackPane[i].setBorder(null);
        }
      }
    }
    players.remove(indexRemoved);
  }

  /**
   * This method sets the visibility of all field labels on the game board on true when the
   * parameter isVisible is true. Vice versa it sets the labels on invisible if isVisible is false.
   * 
   */
  public void setFieldLabelVisibility(boolean isVisible) {
    for (Text t : dwsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : twsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : dlsLabel) {
      t.setVisible(isVisible);
    }
    for (Text t : tlsLabel) {
      t.setVisible(isVisible);
    }
  }

  /**
   * Updates Lobbychat by using the updateChat method in the Chat Controller. The String that is
   * generated by this method from Chat Controller is appended to the chat TextArea and the
   * chatInput TextField is being cleared.
   *
   * @param message the chat Message that is supposed to be send to everyone in the lobby
   * @param dateTime local time and date that is used for the time stamp
   * @param sender nickname of the player who sends this message
   */
  public void updateChat(String message, LocalDateTime dateTime, String sender) {
    this.chat.appendText("\n" + this.cc.updateChat(message, dateTime, sender));
    this.chat.setScrollTop(Double.MAX_VALUE);
  }

  /**
   * This method highlights the player that is playing his turn at the moment by visually
   * emphasizing the players nickname on the game panel.
   *
   * @param nextPlayer nickname of the player who is next
   * 
   */
  public void indicatePlayerTurn(String nextPlayer) {
    if (players.get(0).getNickname().equals(nextPlayer)) {
      stackPlayer1.setBorder(new Border(new BorderStroke(Color.RED, 
          BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
    } else {
      stackPlayer1.setBorder(null);
    }
    if (players.size() > 1) {
      if (players.get(1).getNickname().equals(nextPlayer)) {
        stackPlayer2.setBorder(new Border(new BorderStroke(Color.RED, 
            BorderStrokeStyle.SOLID,  new CornerRadii(10), BorderWidths.DEFAULT)));
      } else {
        stackPlayer2.setBorder(null);
      }
      if (players.size() > 2) {
        if (players.get(2).getNickname().equals(nextPlayer)) {
          stackPlayer3.setBorder(new Border(new BorderStroke(Color.RED, 
              BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
        } else {
          stackPlayer3.setBorder(null);
        }
        if (players.size() > 3) {
          if (players.get(3).getNickname().equals(nextPlayer)) {
            stackPlayer4.setBorder(new Border(new BorderStroke(Color.RED, 
                BorderStrokeStyle.SOLID, new CornerRadii(10), BorderWidths.DEFAULT)));
          } else {
            stackPlayer4.setBorder(null);
          }
        }
      }
    }
  }

  /**
   * This method adds a tile at a location at the game panel either on the rack or on the game
   * board. For instance when a player draws new tiles after he has put some tiles on the game
   * board.
   *
   * @author mschmauch
   * 
   */
  public void addTile(Tile tile) {
    char letter = tile.getLetter().getCharacter();
    int tileValue = tile.getValue();
    int column = tile.getField().getxCoordinate();
    int row = tile.getField().getyCoordinate();



    if (tile.isOnRack()) {
      row = 0;
      if (column > 5) { // case: tile is in the second row of the rack
        row = 1;
        column -= 6;
      }

      final VisualTile visualTile = new VisualTile(Character.toString(letter), tileValue, true);

      visualTile.setMouseTransparent(true);
      rack.add(visualTile, column, row);

      GridPane.setHalignment(visualTile, HPos.CENTER);
      GridPane.setValignment(visualTile, VPos.CENTER);

      // GridPane.setMargin(visualTile, new Insets(0, 0, 5, 0));
    } else {
      row -= 1;
      column -= 1;
      VisualTile visualTile = new VisualTile(Character.toString(letter), tileValue, false);



      visualTile.setMouseTransparent(true);
      board.add(visualTile, column, row);
      GridPane.setHalignment(visualTile, HPos.CENTER);
      GridPane.setValignment(visualTile, VPos.CENTER);
      // GridPane.setMargin(visualTile, new Insets(0, 0, 3, 0));
    }
  }


  /**
   * This method updates a Tile on the UI by putting the tile on a new position on the Rack provided
   * by the parameters parameters and removing it from the last position.
   *
   * @param tile the tile that is supposed to be moved to the rack
   * @param oldX old x-coordinate of the tile
   * @param oldY old y-coordinate that is -1 if the tile was on the rack
   */
  public void moveToRack(Tile tile, int oldX, int oldY) {
    boolean fromRack = false;
    if (oldY == -1) {
      fromRack = true;
    }
    removeTile(oldX, oldY, fromRack);
    addTile(tile);
  }

  /**
   * This method updates a Tile on the UI by putting the tile on a new position on the GamePanel
   * provided by the coordinate parameters and removing it from the last position.
   *
   * @param tile the tile that is supposed to be moved to the game board
   * @param oldX old x-coordinate of the tile
   * @param oldY old y-coordinate that is -1 if the tile was on the rack
   */
  public void moveToGamePanel(Tile tile, int oldX, int oldY) {
    boolean fromRack = false;
    if (oldY == -1) {
      fromRack = true;
    }
    removeTile(oldX, oldY, fromRack);
    addTile(tile);
  }

  /**
   * This method removes a tile on the GamePanel. This might be the case when another player removes
   * a tile during his turn. This method can only remove a tile from the GamePanel and NOT from the
   * rack!
   *
   * @param column x-position in the gridpane of the tile that is supposed to be removed
   * @param row y-position in the gridpane of the tile that is supposed to be removed
   * @param isOnRack is true when the node is on the rack otherwise tile is on game board
   */
  public void removeTile(int column, int row, boolean isOnRack) {
    int x;
    int y;
    ObservableList<Node> list;
    if (isOnRack) {
      list = rack.getChildren();
      for (Node node : list) {
        x = getPos(node, true)[0];
        y = getPos(node, true)[1];
        if (node instanceof Parent && x == column && y == row) {
          rack.getChildren().remove(node);
          break;
        }
      }
    } else {
      column -= 1;
      row -= 1;
      list = board.getChildren();
      for (Node node : list) {
        x = getPos(node, false)[0];
        y = getPos(node, false)[1];
        if (node instanceof VisualTile && x == column && y == row) {
          board.getChildren().remove(node);
          break;
        }
      }
    }
  }


  /**
   * This method is getting returned to the UI after the sendTileMove method has been triggered from
   * the UI. A visual confirmation for a valid turn is shown in the UI.
   *
   * @param nickName name of the player who did an invalid turn
   * @param message message that is shown as content text inside the alert window
   */
  public void indicateInvalidTurn(String nickName, String message) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        alert.setTitle("Invalid Turn");
        alert.setHeaderText("Your turn was not valid");
        alert.setContentText(message);
        alert.initStyle(StageStyle.UNDECORATED);

        alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("/fxml/DialogPaneButtons.css").toExternalForm());
        alert.show();
      }
    });
  }

  /**
   * This method is called, when the host decides to shut down the server. For the clients this
   * method creates a warning alert and after confirmation, the game panel is closed.
   *
   * @param hostName name of the player/host who stops the server
   * @param reason message that is shown to the client as reason for a server shutdown
   */
  public void showShutdownMessage(String hostName, String reason) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        close();
        new LoginScreen().start(new Stage());
        CustomAlert alert = new CustomAlert(AlertType.WARNING);
        alert.setTitle("Server Shutdown");
        alert.setHeaderText("Server stopped and game ended.");
        alert.setContentText("The Server was shut down by '" + hostName + "'. \nReason: " + reason);
        alert.initStyle(StageStyle.UNDECORATED);


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
          alert.close();
        }
      }
    });

  }

  /**
   * This method updates the score of an Player on the UI and shows a new totalScore.
   *
   * @param nickName name of the player, whomst score should be updated
   * @param totalScore the overall score of the player that replaces the actual score
   */
  public void updateScore(String nickName, int totalScore) {
    String newScore = String.valueOf(totalScore);
    // Text playerPoints[] = {playerOnePoints, playerTwoPoints, playerThreePoints,
    // playerFourPoints};
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getNickname().equals(nickName)) {
        pointsLabel[i].setText(newScore);
      }
    }
  }

  /**
   * Method that sets all rectangles that represent a field on the rack to black color.
   */
  public void setRackRectanglesBlack() {
    Rectangle[] rect = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
    for (Rectangle r : rect) {
      r.setStroke(Color.BLACK);
    }
  }

  /**
   * Method resets all variables associated with the skip&exchange mode.
   */
  public void resetSkipAndChange() {
    this.setExchangeTilesMode(false);
    this.setRackRectanglesBlack();
    this.tilesToExchange.removeAll(tilesToExchange);
  }

  /**
   * This method creates a new TileRequestMessage that is supposed to inform the server that a
   * client has moved a tile in his Client UI and the tile move needs to be checked for conformitiy.
   * Therefore the new message is send to the server, using the sendMessageToServer() method; the
   * confirmation of the move is handled in ClientProtocol
   *
   * @param nickName name of the player who wants to move a tile
   * @param oldX x-position of the tile that is supposed to be moved
   * @param oldY y-position of the tile that is supposed to be moved
   * @param newX x-position of the desired target location
   * @param newY y-position of the desired target location
   */
  public void sendTileMove(String nickName, int oldX, int oldY, int newX, int newY) {

    MoveTileMessage m = new MoveTileMessage(nickName, oldX, oldY, newX, newY);
    try {
      if (this.player.isHost()) {
        this.player.getServer().handleMoveTile(m);

      } else {
        sendMessage(m);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method creates a new CommitTurnMessage that is supposed to inform the server that a client
   * has completed a turn by clicking the 'done' button in his Client UI. Therefore the new message
   * is send to the server, using the sendMessageToServer() method
   *
   * @param nickName name of the sender
   */
  public void sendCommitTurn(String nickName) {
    Message m = new CommitTurnMessage(nickName, this.player.getRackTiles().isEmpty());
    if (this.player.isHost()) {
      this.player.getServer().handleCommitTurn((CommitTurnMessage) m);
    } else {
      sendMessage(m);
    }
  }

  /**
   * This Message is used to Reset the current turn for every player.
   *
   * @author lurny
   */
  public void sendResetTurnForEveryPlayer(String nickName) {
    Message m = new ResetTurnMessage(this.player.getNickname(), null);
    if (this.player.isHost()) {
      this.player.getServer().resetTurnForEveryPlayer((ResetTurnMessage) m);
    } else {
      sendMessage(m);
    }
  }

  /**
   * This Method resets the turn for the actual playing player when the timer is up.
   */
  public void sendResetTurn() {
    Message m = new ResetTurnMessage(this.player.getNickname(), null);
    if (this.player.isHost()) {
      if (this.player.getServer().getGameState().getCurrentPlayer()
          .equals(this.player.getNickname())) {
        this.player.getServer().getGameController().getTurn().setStringRepresentation("Time's up!");
        this.player.getServer().resetTurnForEveryPlayer((ResetTurnMessage) m);
      }
    } else {
      if (this.player.getClientProtocol().getGameState().getCurrentPlayer()
          .equals(this.player.getNickname())) {
        sendMessage(m);
      }
    }
  }

  /**
   * This method creates a new DisconnectMessage that is supposed to inform the server that a client
   * wants to disconnect from the server and stop the game. Therefore the new message is send to the
   * server, using the sendMessageToServer() method.
   *
   * @param nickName the name of the sender
   */
  public void sendDisconnectMessage(String nickName) {
    Message m = new DisconnectMessage(nickName, null);
    sendMessage(m);
  }

  /**
   * This method is called, when the player wants to skip his turn and replace his tiles on the rack
   * completely with new tiles. Therefore, a TileMessage is sent to the server, containing the name
   * of the sender and the list of tiles, the player has on his rack.
   *
   * @param nickName the name of the sender
   */
  public void sendTileMessage(String nickName) {
    Message m = new TileMessage(nickName, tilesToExchange);
    if (this.player.isHost()) {
      this.player.getServer().handleExchangeTiles((TileMessage) m);
    } else {
      sendMessage(m);
    }
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
   * This method determines the position of a node in a gridpane and returns the position in a one
   * dimensional int array with x-coordinate on int[0] and y-coordinate on int[1]. The boolean in
   * the parameter determines wether the node is located in the rack gridpane or the gamepanel
   * gridpane - nodeFromRack==true means that the node is located in the rack.
   *
   * @param node the node from which the coordinates are supposed to be determined
   * @param nodeFromRack is true when the node is on the rack
   * @return x and y coordinates of the node as int array - array[0] is the x coord
   */
  protected int[] getPos(Node node, boolean nodeFromRack) {
    int[] result = new int[2];
    Integer columnIndex = GridPane.getColumnIndex(node);
    Integer rowIndex = GridPane.getRowIndex(node);
    if (columnIndex == null) {
      result[0] = 0;
    } else {
      result[0] = columnIndex.intValue();
    }
    if (rowIndex == null) {
      result[1] = 0;
    } else {
      result[1] = rowIndex.intValue();
    }
    if (nodeFromRack) {
      if (result[1] > 0) {
        result[0] = result[0] + 6;
      }
      result[1] = -1;
    }
    return result;
  }

  /**
   * Closes the Game and stops the server if the person who calls the method is the host, otherwise
   * the player is disconnected and the game panel is closed.
   */
  public void close() {
    stopTimer();
    if (this.player.isHost()) {
      this.player.getServer().sendToAll(
          new ShutdownMessage(this.player.getNickname(), "Host closed the server session."));
      this.player.getServer().stopServer();
    } else if (this.player.getClientProtocol().isOk()) {
      Message m = new DisconnectMessage(this.player.getNickname(), null);
      sendMessage(m);
    }
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
  }

  /**
   * Getter-Method to get the reference to the object variable alert2.
   */
  public CustomAlert getAlert2() {
    return alert2;
  }

  /**
   * Getter-Method to get the reference to the instance of the GamePanelController.
   */
  public static GamePanelController getInstance() {
    return instance;
  }

  /**
   * Getter-Method to get the reference to the object variable min.
   */
  public int getMin() {
    return min;
  }

  /**
   * Getter-Method to get the reference to the object variable sec.
   */
  public int getSec() {
    return sec;
  }

  /**
   * Setter-Method to mutate the object variable timerDuration.
   */
  public void setTimerDuration(int timerDuration) {
    this.timerDuration = timerDuration;
  }

  /**
   * Getter-Method to get the reference to the object variable timerDuration.
   */
  public int getTimerDuration() {
    return timerDuration;
  }

  /**
   * Setter-Method to mutate the object variable exchangeTilesMode.
   */
  public void setExchangeTilesMode(boolean exchangeTilesMode) {
    GamePanelController.exchangeTilesMode = exchangeTilesMode;
  }

  /**
   * Setter-Method to mutate the instance of the GamePanelController.
   */
  public static void setInstance(GamePanelController controller) {
    instance = controller;
  }
}
