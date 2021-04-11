package network.messages;

/** @author lurny */

public class TurnResponseMessage extends Message {

  private static final long serialVersionUID = 1L;
  private boolean isValid;
  private int calculatedTurnScore;
  private String nextPlayer;

  public TurnResponseMessage(String from, boolean isValid, int calculatedTurnScore,
      String nextPlayer) {
    super(MessageType.TURN_RESPONSE, from);
    this.isValid = isValid;
    this.calculatedTurnScore = calculatedTurnScore;
    this.nextPlayer = nextPlayer;
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

}
