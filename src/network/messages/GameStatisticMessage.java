package network.messages;

/** @author lurny */

import game.GameStatistic;

public class GameStatisticMessage extends Message {

  private static final long serialVersionUID = 1L;
  private GameStatistic gameStatistic;

  public GameStatisticMessage(String from, GameStatistic gameStatistic) {
    super(MessageType.GAME_STATISTIC, from);
    this.gameStatistic = gameStatistic;
  }

  public GameStatistic getGameStatistic() {
    return this.gameStatistic;
  }

}