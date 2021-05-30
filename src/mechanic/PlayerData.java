package mechanic;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * The PlayerData object is used for sending a minimal set of data about the player to the other
 * players. The object is added to the GameState for every player.
 *
 * @author ldreyer
 */

public class PlayerData implements Serializable {


  private static final long serialVersionUID = 1L;
  private PlayerStatistics statistics;
  private boolean isHost;
  private String nickname;
  private String avatar;


  /**
   * creates an instance of the class.
   */
  public PlayerData(String nickname) {
    this.nickname = nickname;
    statistics = new PlayerStatistics();
  }

  /**
   * gets the variable nickname of the current instance.
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * sets the variable nickname of the current instance.
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * gets the variable avatar of the current instance.
   */
  public String getAvatar() {

    return this.avatar;
  }

  /**
   * gets the variable statistics of the current instance.
   */
  public PlayerStatistics getPlayerStatistics() {
    return this.statistics;
  }

  /**
   * This method initializes all user statistics.
   *
   * @author nilbecke
   * 
   * @param gc represents the amount of games played.
   * @param bs represents most points scored by a single turn.
   * @param bw represents the highest scoring word.
   * @param s represents the total score throughout all games.
   * @param pt1 represents the total play time represented in minutes.
   * @param w represents the total amount of wins.
   * @param pt2 represents the sum of all tiles ever played.
   */
  public void setStatistics(int gc, int bs, String bw, int s, int pt1, int w, int pt2) {

    this.statistics.setGameCount(gc);
    this.statistics.setBestTurn(bs);
    this.statistics.setBestWord(bw);
    this.statistics.setScore(s);
    this.statistics.setPlayTime(pt1);
    this.statistics.setWins(w);
    this.statistics.setPlayedTiles(pt2);
  }

  /**
   * sets the variable avatar of the current instance.
   */
  public void setAvatar(String input) {
    this.avatar = input;
  }

  /**
   * sets the variable avatar of the current instance.
   */
  public void setAvatar(Image input) {
    this.avatar = input.getUrl();
  }

  /**
   * This method is used to Convert a BufferedImage to an Image.
   *
   * @author nilbecke
   * @param input of type Buffered Image
   * @return A Java FX Image
   */
  public Image convert(BufferedImage input) {
    WritableImage wr = null;
    if (input != null) {
      wr = new WritableImage(input.getWidth(), input.getHeight());
      PixelWriter pw = wr.getPixelWriter();
      for (int x = 0; x < input.getWidth(); x++) {
        for (int y = 0; y < input.getHeight(); y++) {
          pw.setArgb(x, y, input.getRGB(x, y));
        }
      }
    }

    return (Image) wr;
  }

  /**
   * gets the variable isHost of the current instance.
   */
  public boolean isHost() {
    return this.isHost;
  }

  /**
   * sets the variable host of the current instance.
   */
  public void setHost(boolean host) {
    this.isHost = host;
  }

  /**
   * gives back a string representation of the cuurent instance.
   */
  @Override
  public String toString() {
    return nickname;
  }

  /**
   * equals method to compare two instances.
   */
  @Override
  public boolean equals(Object other) {
    boolean equals;
    PlayerData p;

    if (other == null || other.getClass() != getClass()) {
      return false;
    } else {
      p = (PlayerData) other;
      equals = this.nickname.equals(p.getNickname());

      if (this.avatar == null) {
        equals = equals && (p.getAvatar() == null);
      } else {
        equals = equals && (this.avatar.equals(p.getAvatar()));
      }

      return equals;
    }
  }

}
