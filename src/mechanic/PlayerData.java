package mechanic;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * The PlayerData object is used for sending a minimal set of data about the player to the other
 * players. The object is added to the GameState for every player.
 * 
 * @author ldreyer
 */

public class PlayerData {
  private int id;
  private String nickname;
  private String avatar;

  // private GameStatistic Statistic;

  public PlayerData(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatar() {

    return this.avatar;
  }



  /** @author nilbecke */
  public void setAvatar(String input) {

    this.avatar = input;
  }

  /**
   * obsolete
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

  public void setAvatar(Image input) {
    this.avatar = input.getUrl();
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }
}
