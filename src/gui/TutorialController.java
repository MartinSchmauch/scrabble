package gui;

import game.GameController;
import game.GameSettings;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mechanic.GameBoard;
import mechanic.Letter;
import mechanic.Player;
import mechanic.PlayerData;
import mechanic.Tile;
import mechanic.TileBag;
import network.client.ClientProtocol;
import network.messages.CommitTurnMessage;
import network.messages.DisconnectMessage;
import network.messages.Message;
import network.messages.MoveTileMessage;
import network.messages.ResetTurnMessage;
import network.messages.TileMessage;
import network.server.Server;

/**
 * This class is a modification of the game panel controller to play the tutorial.
 *
 * @author nilbecke, mschmauc
 * 
 */

public class TutorialController extends GamePanelController
    implements EventHandler<ActionEvent>, Runnable {



  private Player player;

  private List<PlayerData> players;
  private ClientProtocol cp;
  private Server server;
  private static boolean exchangeTilesMode = false;
  private List<Tile> tilesToExchange = new ArrayList<Tile>();
  private static int[] selectedCoordinates = new int[2]; // row, column
  private static int[] targetCoordinates = new int[2]; // row, column

  private ChatController cc;
  private static GameController gc;
  private List<Tile> tiles;
  private int indicator = 0;
  private int min;
  private int sec;
  private Thread thread;
  private double timeLeftBar;
  private boolean turnCountdown;

  protected Text[] playerLabel;
  protected Text[] pointsLabel;
  protected Text[] playerNameLabel;
  protected ImageView[] avatarImageView;

  @FXML
  private TextArea chat;
  @FXML
  private TextField chatInput;
  @FXML
  private Button sendButton;
  @FXML
  private Button skipAndChangeButton;
  @FXML
  private Button doneButton;
  @FXML
  private Button leaveGameButton;
  @FXML
  private Button settingsButton;
  @FXML
  private Button rulesButton;
  @FXML
  private ImageView image1;
  @FXML
  private ImageView image2;
  @FXML
  private ImageView image3;
  @FXML
  private ImageView image4;
  @FXML
  private Text player1;
  @FXML
  private Text player2;
  @FXML
  private Text player3;
  @FXML
  private Text player4;
  @FXML
  private Text playerOnePoints;
  @FXML
  private Text playerTwoPoints;
  @FXML
  private Text playerThreePoints;
  @FXML
  private Text playerFourPoints;
  @FXML
  private Text pointsLabel1;
  @FXML
  private Text pointsLabel2;
  @FXML
  private Text pointsLabel3;
  @FXML
  private Text pointsLabel4;
  @FXML
  private Text remainingLetters;
  @FXML
  private Text timer;
  @FXML
  private Text tut1;
  @FXML
  private Text tut2;
  @FXML
  private Text tut3;
  @FXML
  private Text tut4;
  @FXML
  private Text tut5;
  @FXML
  private Text tut6;
  @FXML
  private Text tut7;
  @FXML
  private Text tut8;
  @FXML
  private Text tut9;
  @FXML
  private Text tut0;
  @FXML
  private Rectangle tile1;
  @FXML
  private Rectangle currentPlayer1;
  @FXML
  private Rectangle currentPlayer2;
  @FXML
  private Rectangle currentPlayer3;
  @FXML
  private Rectangle currentPlayer4;
  @FXML
  private Rectangle r0;
  @FXML
  private Rectangle r1;
  @FXML
  private Rectangle r2;
  @FXML
  private Rectangle r3;
  @FXML
  private Rectangle r4;
  @FXML
  private Rectangle r5;
  @FXML
  private Rectangle r6;
  @FXML
  private Rectangle r7;
  @FXML
  private Rectangle r8;
  @FXML
  private Rectangle r9;
  @FXML
  private Rectangle r10;
  @FXML
  private Rectangle r11;
  @FXML
  private GridPane board;
  @FXML
  private GridPane rack;
  @FXML
  private ProgressBar timeProgress;
  @FXML
  private Rectangle backgroundGamePanel;


  private static TutorialController instance;


  public static TutorialController getInstance() {
    return instance;
  }

  /**
   * This method initializes the GamePanelController and is being called upon creation of the
   * Controller. Here all the labels on the UI are being reset and adapted to the current game
   * state.
   */
  public void initData(Player player) {
    this.settingsButton.setText("Show Tip");
    this.skipAndChangeButton.setDisable(true);
    this.remainingLetters.setOpacity(0);
    this.player = player;
    cc = new ChatController(player);
    chat.setEditable(false);
    updateChat("\n\nStart by dragging the 'B','E' and 'D' on the marked fields and hit \"Done\".");
    playerLabel = new Text[] {pointsLabel1, pointsLabel2, pointsLabel3, pointsLabel4};
    pointsLabel =
        new Text[] {playerOnePoints, playerTwoPoints, playerThreePoints, playerFourPoints};
    playerNameLabel = new Text[] {player1, player2, player3, player4};
    avatarImageView = new ImageView[] {image1, image2, image3, image4};


    List<Letter> letters = new ArrayList<Letter>(GameSettings.getLetters().values());
    Iterator<Letter> it = letters.iterator();

    this.tiles = new ArrayList<Tile>();
    while (it.hasNext()) {
      Letter l = it.next();
      this.tiles.add(new Tile(l));
    }

    leaveGameButton.setText("End Tutorial");
    playerNameLabel[0].setText(this.player.getNickname() + " (Host)");
    pointsLabel[0].setText("0");
    playerLabel[0].setText("Points: ");
    avatarImageView[0]
        .setImage(new Image(getClass().getResource(this.player.getAvatar()).toExternalForm()));

    remainingLetters.setText("");
    timer.setText("");
    timeProgress.setProgress(0.0);

  }

  /**
   * Handles all button user inputs in the GamePanel.
   */
  @Override
  public void handle(ActionEvent e) {
    String s = ((Node) e.getSource()).getId();
    switch (s) {
      case "settingsButton":
        showTip(this.indicator);
        break;
      case "leaveGameButton":
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
        if (player.isHost()) {

          alert.setHeaderText("End Tutorial?");
          alert.setContentText("Do you really want to end the tutorial?");

          alert.initStyle(StageStyle.UNDECORATED);

          alert.changeButtonText("Yes", ButtonType.OK);
          alert.changeButtonText("No", ButtonType.CANCEL);

          Optional<ButtonType> result = alert.showAndWait();
          if (result.get() == ButtonType.OK) {

            closeTutorial();
          }
        }
        break;
      case "rulesButton":
        OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
        break;
      case "sendButton":
      case "chatInput":
        this.chat.appendText("\n" + this.player.getNickname() + ": " + this.chatInput.getText());
        this.chatInput.setText("");
        break;
      case "skipAndChangeButton":
        if (!exchangeTilesMode) {
          exchangeTilesMode = true;
          changeSkipAndChangeStatus(false);
          this.doneButton.setDisable(false);
        }
        break;
      case "doneButton":
        if (exchangeTilesMode) {
          if (!this.tilesToExchange.isEmpty()) {
            CustomAlert alert2 = new CustomAlert(AlertType.CONFIRMATION);
            alert2.setTitle("Skip & Exchange selected tiles");
            alert2.setHeaderText("Skip & Exchange?");
            alert2.setContentText(
                "Do you want to skip the current turn \nand exchange the selected tiles? ");
            alert2.initStyle(StageStyle.UNDECORATED);

            alert2.changeButtonText("Yes", ButtonType.OK);
            alert2.changeButtonText("No", ButtonType.CANCEL);

            Optional<ButtonType> result2 = alert2.showAndWait();
            if (result2.get() == ButtonType.OK) {
              // remove Tiles from GUI
              for (Tile t : this.tilesToExchange) {

                // TODO bei dem gesetzten True koennte ein Fehler entstehen
                this.removeTile(t.getField().getxCoordinate(), t.getField().getyCoordinate(), true);
                this.player.removeRackTile(t.getField().getxCoordinate());
                Tile newTile = new TileBag().drawTile();
                this.player.addTileToRack(newTile);
                this.addTile(newTile);
              }
              setRackRectanglesBlack();
              this.chat.appendText("\n\n THANK YOU FOR PLAYING");
              endTutorial();
            } else {
              alert2.close();
            }

            Rectangle[] rect = {r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11};
            for (Rectangle r : rect) {
              r.setStroke(Color.BLACK);
            }
            tilesToExchange.removeAll(tilesToExchange);
          }
          exchangeTilesMode = false;
          changeSkipAndChangeStatus(true);
        } else {

          nextTurn(indicator);
        }
        break;
      
      default:
        break;
    }
  }

  /**
   * This method tells the user what to do.
   *
   * @param index indicates in which state of the tutorial is.
   */
  public void showTip(int index) {
    CustomAlert alert = new CustomAlert(AlertType.INFORMATION);
    switch (index) {
      case 0:
        alert.setHeaderText("Lay your first word");
        alert.setContentText(
            "Drag the correct letters on their designated place.\nFirst word has to go through the middle!");
        break;
      case 1:
        alert.setHeaderText("React");
        alert.setContentText(
            "Lay the next indicated word.\nA new word always has to connect with a word already placed.\n\nIf the layed down tiles generate more than one word, \nyou will recieve points for all words generated.\n\n'*' Tiles can be used as a Joker,\nso they can represtent every Letter.");
        break;
      case 2:
        alert.setHeaderText("Exhange your tiles");
        alert.setContentText(
            "Hit \"Skip & Change\"!\nSelect the tiles you want to change and hit \"Done\".\nIf you change tiles, you can't lay words in this round.");
        break;
      default:
        break;
    }
    alert.show();
  }

  /**
   * This method gets called when the tutorial has successfully ended. It gives the user the
   * opportunity to just leave the screen or additionally view the rules.
   */
  public void endTutorial() {
    CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION);
    alert.setHeaderText("Thank you for playing the Tutorial!");
    alert.setContentText(
        "Now try your skills in a real game!\nFor more theoretical info visit the rules.");
    alert.changeButtonText("Leave", ButtonType.OK);
    alert.changeButtonText("Show Rules", ButtonType.CANCEL);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get().equals(ButtonType.OK)) {
      closeTutorial();
    } else {
      closeTutorial();
      OpenExternalScreen.open(FileParameters.datadir + "ScrabbleRules.pdf");
    }
  }

  /**
   * This method indicates the next strp in the tutorial.
   *
   * @author nilbecke
   * @param indicator indicates which phase of the tutorial is currently played.
   */
  public void nextTurn(int indicator) {
    switch (indicator) {
      case 0:
        if (validateTurn(indicator)) {
          this.playerOnePoints.setText("12");
          updateChat(
              "\n\nWell done, you layed your first word!\n\nLook, your opponent has layed down a word as well.\n\nNow lay the words \"DO\" and \"NOMINATE\" by dragging the letters onto the designated fields. Use the '*' as a 'T'. ");
          tutorialTurn(indicator);
          showTip(1);
          this.indicator++;
        }
        break;
      case 1:
        if (validateTurn(indicator)) {
          updateChat(
              "\n\nAs these letters form two words (DO and NOMINATE), you will recieve the points for both these words.\n\nThe last task is to exchange Tiles. To do so hit the \"Skip and Exchange\" Button and select the Tiles you want to change. After that hit \"Done\". ");
          tutorialTurn(indicator);
          showTip(2);
          this.indicator++;
        }
        break;
      default:
        break;
    }
  }

  /**
   * This methods appends text to chat area, depending on tutorial status.
   * 
   * @param input indicates the input.
   */

  public void updateChat(String input) {
    this.chat.setText(
        "Welcome to the Tutorial :)\n\nYou will be shown Tips to learn the basic mechanics of this game. If you need help, click on \"Show Tip\"."
            + input);
  }


  /**
   * This methods deals with all the turns the cpu makes during the tutorial.
   *
   * @param indicator indicates in which turn of the tutorial the user currently is.
   */

  public void tutorialTurn(int indicator) {
    GameBoard gb = this.player.getServer().getGameController().getGameState().getGameBoard();
    TileBag tb = new TileBag();
    Tile[] toAdd;
    Tile[] newTiles;
    switch (indicator) {
      case 0:
        toAdd = new Tile[5];
        toAdd[0] = tb.drawTile('S');
        toAdd[0].setField(gb.getField(8, 7));
        toAdd[1] = tb.drawTile('N');
        toAdd[1].setField(gb.getField(8, 9));
        toAdd[2] = tb.drawTile('D');
        toAdd[2].setField(gb.getField(8, 10));
        toAdd[3] = tb.drawTile('E');
        toAdd[3].setField(gb.getField(8, 11));
        toAdd[4] = tb.drawTile('R');
        toAdd[4].setField(gb.getField(8, 12));
        for (int i = 0; i < toAdd.length; i++) {
          toAdd[i].setOnRack(false);
          addTile(toAdd[i]);
          toAdd[i].setPlayed(true);
        }
        newTiles = new Tile[7];
        newTiles[0] = tb.drawTile('M');
        newTiles[1] = tb.drawTile('O');
        newTiles[3] = tb.drawTile('E');
        newTiles[2] = tb.drawTile('I');
        newTiles[4] = tb.drawTile('*');
        newTiles[5] = tb.drawTile('A');
        newTiles[6] = tb.drawTile('N');
        for (int i = 0; i < newTiles.length; i++) {
          newTiles[i].setField(gb.getField(i + 1, 1));
          newTiles[i].setOnRack(true);
          addTile(newTiles[i]);
          this.player.setRackTile(i + 1, newTiles[i]);
        }
        tut4.setOpacity(0.5);
        tut5.setOpacity(0.5);
        tut6.setOpacity(0.5);
        tut7.setOpacity(0.5);
        tut8.setOpacity(0.5);
        tut9.setOpacity(0.5);
        tut0.setOpacity(0.5);
        break;
      case 1:
        toAdd = new Tile[3];
        toAdd[0] = tb.drawTile('G');
        toAdd[0].setField(gb.getField(12, 7));
        toAdd[1] = tb.drawTile('O');
        toAdd[1].setField(gb.getField(12, 8));
        toAdd[2] = tb.drawTile('E');
        toAdd[2].setField(gb.getField(12, 10));
        for (int i = 0; i < toAdd.length; i++) {
          toAdd[i].setOnRack(false);
          addTile(toAdd[i]);
          toAdd[i].setPlayed(true);
        }
        newTiles = new Tile[7];
        for (int i = 0; i < newTiles.length; i++) {

          newTiles[i] = tb.drawTile((char) (i + 70));
          newTiles[i].setField(gb.getField(i + 1, 1));
          newTiles[i].setOnRack(true);
          addTile(newTiles[i]);
          this.player.setRackTile(i + 1, newTiles[i]);
        }
        this.doneButton.setDisable(true);
        break;
      default:
        break;
    }


  }

  /**
   * This method validates the turns made by players while playing the tutorial.
   *
   * @param index indicates which phase of the tutorial was played last
   */
  public boolean validateTurn(int index) {
    GameBoard gb = this.player.getServer().getGameController().getGameState().getGameBoard();
    switch (index) {
      case 0:
        try {
          if (gb.getField(7, 8).getTile().getLetter().getCharacter() == 'B'
              && gb.getField(8, 8).getTile().getLetter().getCharacter() == 'E'
              && gb.getField(9, 8).getTile().getLetter().getCharacter() == 'D') {
            gb.getField(7, 8).getTile().setPlayed(true);
            gb.getField(8, 8).getTile().setPlayed(true);
            gb.getField(9, 8).getTile().setPlayed(true);
            return true;
          } else {
            showInvalidTurn();
          }
        } catch (NullPointerException e) {
          showInvalidTurn();
        }
        break;
      case 1:
        char[] nominate = new char[7];
        try {
          for (int i = 9; i < nominate.length + 9; i++) {
            nominate[i - 9] = gb.getField(i, 9).getTile().getLetter().getCharacter();
          }
        } catch (NullPointerException e) {
          showInvalidTurn();
        }

        if (new String(nominate).equals("OMINA*E")) {
          removeTile(14, 9, false);
          Tile t = new TileBag().drawTile('T');
          t.setField(gb.getField(14, 9));
          t.setOnRack(false);
          addTile(t);
          for (int i = 9; i < 16; i++) {
            gb.getField(i, 9).getTile().setPlayed(true);
            this.skipAndChangeButton.setDisable(false);
          }
          return true;
        } else {
          showInvalidTurn();
        }
        break;
      default:
        break;
    }
    return false;

  }

  /**
   * This method informs the user of a mistake in the tutorial.
   */
  public void showInvalidTurn() {
    CustomAlert alert = new CustomAlert(AlertType.ERROR);
    alert.setHeaderText("Retry move!");
    alert.setContentText("Look in the chat for instructions or view tip and try again.");
    alert.show();

  }


  /**
   * This mthod closes the tutorial and cuts all server connections.
   */
  public void closeTutorial() {
    this.player.getServer().stopServer();
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
    new LoginScreen().start(new Stage());
  }


  /**
   * Listener that is called, when a user starts a drag movement from a rack field. The coordinates
   * of the event starting location are being saved for this drag event in the selectedCoordinates
   * array.
   */
  @FXML
  public void rackDragHandling(MouseEvent event) {
    // Image image = new Image(System.getProperty("user.dir") + "/ressources/general/tile.png");
    // rulesButton.getScene().setCursor(new ImageCursor(image));

    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, true);

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);
    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);
    event.consume();
  }

  /**
   * Listener method that is called, when a user starts a drag movement from a board field. The
   * coordinates of the event starting location are being saved for this drag event in the
   * selectedCoordinates array.
   */
  @FXML
  public void boardDragHandling(MouseEvent event) {
    Node node = (Node) event.getSource();
    selectedCoordinates = getPos(node, false);
    selectedCoordinates[0]++;
    selectedCoordinates[1]++;

    Dragboard db = node.startDragAndDrop(TransferMode.ANY);

    ClipboardContent cb = new ClipboardContent();
    cb.putString("[" + selectedCoordinates[0] + "," + selectedCoordinates[1] + "]");
    db.setContent(cb);

    event.consume();
  }

  /**
   * Listener method that is called, when a user drags a tile over a tile. The transfer mode to be
   * accepted upon a drop action can be of any type, since there is only one type in the game.
   */
  @FXML
  public void DragOverHandling(DragEvent event) {
    event.acceptTransferModes(TransferMode.ANY);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a rack field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   */
  @FXML
  public void rackDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, true);
    if (targetCoordinates[0] == selectedCoordinates[0]
        && targetCoordinates[1] == selectedCoordinates[1]) { // deselect tile

    } else if (selectedCoordinates[1] == -1) { // exchange tiles on rack
      player.reorganizeRackTile(selectedCoordinates[0], targetCoordinates[0]);

    } else if (selectedCoordinates[1] != -1) { // try to move tile from board to rack - sender!
      sendTileMove(player.getNickname(), selectedCoordinates[0], selectedCoordinates[1],
          targetCoordinates[0], targetCoordinates[1]);
    }
    resetCoordinates();
    rulesButton.getScene().setCursor(Cursor.DEFAULT);
  }

  /**
   * Listener method that is called, when a user completes a drag&drop event by dropping the item on
   * a board field. For the different tile movement scenarios, the events are passed on to the
   * backend.
   */
  @FXML
  public void boardDropHandling(DragEvent event) {
    Node node = (Node) event.getSource();
    targetCoordinates = getPos(node, false);
    targetCoordinates[0] += 1;
    targetCoordinates[1] += 1;

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
   * Closes the Game and stops the server.
   */
  public void close() {
    if (this.player.getServer() != null) { // TODO: this.player.isHost() nutzen?
      this.player.getServer().stopServer();
      // Message m = new ShutdownMessage(this.player.getNickname(), REGULAR_SHUTDOWN);
      // sendMessage(m);
    } else if (!this.player.isHost()) {
      Rectangle[] rect = {currentPlayer1, currentPlayer2, currentPlayer3, currentPlayer4};
      for (int i = 0; i < players.size(); i++) {
        if (players.get(i).getNickname().equals(this.player.getNickname())) {
          if (rect[i].isVisible()) {
            sendResetTurnForEveryPlayer(this.player.getNickname()); // TODO:
          }
        }
      }
      sendGameInfoMessage("'" + this.player.getNickname() + "' left the game");
      // this.player.getClientProtocol().disconnect(); // in DisconnectMessage included?
      Message m = new DisconnectMessage(this.player.getNickname(), null);
      sendMessage(m);
    }
    Stage st = (Stage) (rulesButton.getScene().getWindow());
    st.close();
    new LoginScreen().start(new Stage());
  }

  public static GameController getController() {
    return gc;
  }
}


