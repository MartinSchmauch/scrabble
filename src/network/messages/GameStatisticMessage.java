package network.messages;

import java.util.HashMap;
/**
 * This Message sends relevant statistcs from the played and finished game to every player.
 * 
 * @author lurny
 */
import game.GameStatistic;

public class GameStatisticMessage extends Message {

  private static final long serialVersionUID = 1L;
  private HashMap<String, GameStatistic> gameStatistics = new HashMap<>();

  public GameStatisticMessage(String from, HashMap<String, GameStatistic> gameStatistics) {
    super(MessageType.GAME_STATISTIC, from);
    this.gameStatistics = gameStatistics;
  }

  public HashMap<String, GameStatistic> getGameStatistics() {
    return gameStatistics;
  }


}
