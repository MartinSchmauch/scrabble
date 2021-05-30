package network.messages;

/**
 * This message is send after every finished turn. It is used to update some Key values and to
 * indicate the nextPlayer turn.
 *
 * @author lurny
 *
 */

public class TurnResponseMessage extends Message {

  private static final long serialVersionUID = 1L;
  private boolean isValid;
  private int calculatedTurnScore;
  private String nextPlayer;
  private int remainingTilesInTileBag;
  private String turnInfo;
  private String winner;

  /**
   * This is the constructor of the TurnResponseMessage.
   */
  public TurnResponseMessage(String from, boolean isValid, int calculatedTurnScore, String turnInfo,
      String nextPlayer, int remainingTilesInTileBag, String winner) {
    super(MessageType.TURN_RESPONSE, from);
    this.isValid = isValid;
    this.calculatedTurnScore = calculatedTurnScore;
    this.nextPlayer = nextPlayer;
    this.remainingTilesInTileBag = remainingTilesInTileBag;
    this.turnInfo = turnInfo;
    this.winner = winner;
  }

  /**
   * gets the variable isValid of the current instance.
   */
  public boolean getIsValid() {
    return isValid;
  }

  /**
   * gets the variable calculatedTurnScore of the current instance.
   */
  public int getCalculatedTurnScore() {
    return calculatedTurnScore;
  }

  /**
   * gets the variable turnInfo of the current instance.
   */
  public String getTurnInfo() {
    return turnInfo;
  }

  /**
   * gets the variable nextPlayer of the current instance.
   */
  public String getNextPlayer() {
    return nextPlayer;
  }

  /**
   * gets the variable remainingTilesInTileBag of the current instance.
   */
  public int getRemainingTilesInTileBag() {
    return remainingTilesInTileBag;
  }

  /**
   * gets the variable winner of the current instance.
   */
  public String getWinner() {
    return winner;
  }

}
