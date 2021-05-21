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

  /**
   * This is the consturctor of the TurnResponseMessage.
   */
  public TurnResponseMessage(String from, boolean isValid, int calculatedTurnScore,
      String nextPlayer, int remainingTilesInTileBag) {
    super(MessageType.TURN_RESPONSE, from);
    this.isValid = isValid;
    this.calculatedTurnScore = calculatedTurnScore;
    this.nextPlayer = nextPlayer;
    this.remainingTilesInTileBag = remainingTilesInTileBag;
  }

  public boolean getIsValid() {
    return isValid;
  }

  public int getCalculatedTurnScore() {
    return calculatedTurnScore;
  }

  public String getNextPlayer() {
    return nextPlayer;
  }

  public int getRemainingTilesInTileBag() {
    return remainingTilesInTileBag;
  }

}
