package network.messages;

/** @author lurny */

public class TurnResponseMessage extends Message {

  private static final long serialVersionUID = 1L;
  private boolean isValid;
  private int calculatedTurnScore;

  public TurnResponseMessage(String from, boolean isValid, int calculatedTurnScore) {
    super(MessageType.TURN_RESPONSE, from);
    this.isValid = isValid;
    this.calculatedTurnScore = calculatedTurnScore;
  }

public boolean getIsValid() {
	return isValid;
}

public int getCalculatedTurnScore() {
	return calculatedTurnScore;
}

}
