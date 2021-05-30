package gui;

import game.GameStatistic;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mechanic.Player;
import mechanic.PlayerData;
import util.JsonHandler;

/**
 * This class initializes all leaderboard related labels.
 *
 * @author nilbecke
 *
 */

public class LeaderboardController implements EventHandler<ActionEvent> {

  private HashMap<String, GameStatistic> gs = new HashMap<>();
  private List<String> players;
  private Player player;

  @FXML
  private Label leaderboard;
  @FXML
  private Label first;
  @FXML
  private Label second;
  @FXML
  private Label third;
  @FXML
  private Label fourth;
  @FXML
  private Label time;
  @FXML
  private Label turn;
  @FXML
  private Label word;
  @FXML
  private Label bestWordKey;
  @FXML
  private Label bestTurnKey;

  @FXML
  public void initialize() {
    this.gs = LeaderboardScreen.getGameStatistic();
    this.player = LeaderboardScreen.getPlayer();

    Font f = Font.loadFont(getClass().getResourceAsStream("/fxml/Scrabble.ttf"), 40);
    leaderboard.setFont(f);
    this.players = this.gs.get(player.getNickname()).getAllPlayers();
    Label[] standings = {first, second, third, fourth};
    for (int i = 0; i < this.players.size(); i++) {
      // Enter player nicknames
      if (players.get(i) != null) {
        String ru = "Runner Up:           ";
        if (i != 3) {
          standings[i].setText(players.get(i) + " - " + gs.get(players.get(i)).getScore() + " pt");
        } else {
          standings[i]
              .setText(ru + players.get(i) + " - " + gs.get(players.get(i)).getScore() + " pt");
        }
      } else {
        if (i == 3) {
          standings[i].setOpacity(0);
        }
        standings[i].setDisable(true);
      }
      // find max Turn and best Word
      int maxTurn = -1;
      String maxTurnPlayer = "";
      for (String p : players) {
        if (gs.get(p).getBestTurn() > maxTurn) {
          maxTurn = gs.get(p).getBestTurn();
          maxTurnPlayer = p;
        }
      }
      turn.setText("" + maxTurn);
      bestTurnKey.setText("Best Turn (" + maxTurnPlayer + "):");
      word.setText(gs.get(maxTurnPlayer).getBestWordAsString());
      int totalGameTime = 0;
      for (String p : players) {
        totalGameTime += this.gs.get(p).getPlayTime();
      }
      int min = totalGameTime / 60;
      int sec = totalGameTime % 60;
      String secondString = "";
      if (sec < 10) {
        secondString = "0" + String.valueOf(sec);
      } else {
        secondString = String.valueOf(sec);
      }
      time.setText("" + min + ":" + secondString);
    }
    updateStatistics();
  }

  @Override
  public void handle(ActionEvent event) {
    // Only "leave game" button.
    Stage st = (Stage) this.bestTurnKey.getScene().getWindow();
    st.close();
    new LoginScreen().start(new Stage());
  }

  /**
   * Determines the winner based on the score archieved. Players will be sorted by descending order
   * into the "players" List.
   */
  // public void determineWinner() {
  // boolean swap = true;
  // while (swap) {
  // swap = false;
  // for (int i = 0; i < players.size() - 1; i++) {
  // if (gs.getScore(players.get(i).getNickname()) < gs
  // .getScore(players.get(i + 1).getNickname())) {
  // Collections.swap(this.players, i, i + 1);
  // swap = true;
  // }
  // }
  // }
  // }

  /**
   * This methods updates all user statistics acooring to the game state.
   *
   * @author lurny
   */

  public void updateStatistics() {
    PlayerData pd = player.getPlayerInfo();
    pd.getPlayerStatistics().incrementGameCount();
    pd.getPlayerStatistics()
        .setScore(pd.getPlayerStatistics().getScore() + gs.get(player.getNickname()).getScore());
    pd.getPlayerStatistics().setPlayedTiles(
        pd.getPlayerStatistics().getPlayedTiles() + gs.get(player.getNickname()).getPlayedTiles());
    pd.getPlayerStatistics().setPlayTime(pd.getPlayerStatistics().getPlayTime()
        + gs.get(player.getNickname()).getPlayTime());
    if (pd.getPlayerStatistics().getBestTurn() < gs.get(player.getNickname()).getBestTurn()) {
      pd.getPlayerStatistics().setBestTurn(gs.get(player.getNickname()).getBestTurn());
      pd.getPlayerStatistics().setBestWord(gs.get(player.getNickname()).getBestWordAsString());
    }
    if (players.get(0).equals(player.getNickname())) {
      pd.getPlayerStatistics().incrementWins();
    }
    new JsonHandler().savePlayerProfile(new File(FileParameters.datadir + "playerProfile.json"),
        this.player);
  }

}
