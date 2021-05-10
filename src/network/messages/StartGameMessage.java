package network.messages;

/**
 * The StartGameMessage indicates a start of the game and initiates a countdown.
 * 
 * @author lurny
 */

public class StartGameMessage extends Message {
  private static final long serialVersionUID = 1L;
  private int countdown;
  private int remainingTilesInTileBag;
  private String currrentPlayer;

  public StartGameMessage(String from, int countdown, int remainingTilesInTileBag,
      String currentPlayer) {
    super(MessageType.START_GAME, from);
    this.setCountdown(countdown);
    this.remainingTilesInTileBag = remainingTilesInTileBag;
    this.currrentPlayer = currentPlayer;
  }

  public int getCountdown() {
    return countdown;
  }

  public void setCountdown(int countdown) {
    this.countdown = countdown;
  }

  public int getRemainingTilesInTileBag() {
    return remainingTilesInTileBag;
  }

  public String getCurrrentPlayer() {
    return currrentPlayer;
  }

}
