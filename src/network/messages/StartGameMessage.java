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
  private int timerDuration;

  /**
   * Constructor for the StartGameMessage that indicates a start of the game and initiates a 
   * countdown.
   */
  public StartGameMessage(String from, int countdown, int remainingTilesInTileBag,
      String currentPlayer, int timerDuration) {
    super(MessageType.START_GAME, from);
    this.setCountdown(countdown);
    this.remainingTilesInTileBag = remainingTilesInTileBag;
    this.currrentPlayer = currentPlayer;
    this.timerDuration = timerDuration;
  }

  /**
   * gets the variable countdown of the current instance.
   */
  public int getCountdown() {
    return countdown;
  }

  /**
   * sets the variable countdown of the current instance.
   */
  public void setCountdown(int countdown) {
    this.countdown = countdown;
  }

  /**
   * gets the variable remainingTilesInTileBag of the current instance.
   */
  public int getRemainingTilesInTileBag() {
    return remainingTilesInTileBag;
  }

  /**
   * gets the variable currentPlayer of the current instance.
   */
  public String getCurrrentPlayer() {
    return currrentPlayer;
  }

  /**
   * gets the variable timerDuration of the current instance.
   */
  public int getTimerDuration() {
    return timerDuration;
  }

}
