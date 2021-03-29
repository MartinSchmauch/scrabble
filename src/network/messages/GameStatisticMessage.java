package network.messages;

import game.GameStatistic;

public class GameStatisticMessage extends Message {

  private static final long serialVersionUID = 1L;
  private GameStatistic gameStatistic;

  public GameStatisticMessage(MessageType type, String from, GameStatistic gameStatistic) {
    super(type, from);
    this.gameStatistic = gameStatistic;
  }

  public GameStatistic getGameStatistic() {
    return this.gameStatistic;
  }

}
