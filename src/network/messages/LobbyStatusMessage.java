package network.messages;

import game.GameState;
import java.util.HashMap;
import mechanic.Letter;

/**
 * This message is used to frequently update the Lobbyscreen, which is shown before the start of the
 * game.
 *
 * @author ldreyer
 */


public class LobbyStatusMessage extends Message {

  private static final long serialVersionUID = 1L;
  private int timePerPlayer;
  private int maxScore;
  private int bingo;
  private String aiDifficulty;
  private String dictionary;
  private int tilesOnRack;
  private HashMap<Character, Letter> letters;
  private GameState gameState;


  /**
   * This method creates an instance of the class.
   */
  public LobbyStatusMessage(String from, int timePerPlayer, int maxScore, int bingo,
      String dictionary, String aiDifficulty, int tilesOnRack, HashMap<Character, Letter> letters,
      GameState gameState) {
    super(MessageType.LOBBY_STATUS, from);
    this.timePerPlayer = timePerPlayer;
    this.maxScore = maxScore;
    this.bingo = bingo;
    this.dictionary = dictionary;
    this.aiDifficulty = aiDifficulty;
    this.tilesOnRack = tilesOnRack;
    this.letters = letters;
    this.gameState = gameState;
  }

  public GameState getGameState() {
    return this.gameState;
  }


  public int getTimePerPlayer() {
    return timePerPlayer;
  }


  public int getMaxScore() {
    return maxScore;
  }


  public int getBingo() {
    return bingo;
  }


  public String getDictionary() {
    return dictionary;
  }


  public String getAiDifficulty() {
    return aiDifficulty;
  }


  public int getTilesOnRack() {
    return tilesOnRack;
  }


  public HashMap<Character, Letter> getLetters() {
    return letters;
  }


}
