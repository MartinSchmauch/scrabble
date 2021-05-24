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
    this.winner = winner;
  }

  public boolean getIsValid() {
    return isValid;
  }

  public int getCalculatedTurnScore() {
    return calculatedTurnScore;
  }

  public String getTurnInfo() {
    return turnInfo;
  }

  public String getNextPlayer() {
    return nextPlayer;
  }

  public int getRemainingTilesInTileBag() {
    return remainingTilesInTileBag;
  }

  public String getWinner() {
    return winner;
  }

}
