package mechanic;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/** @author ldreyer */
public class PlayerData {
  private int id;
  private String nickname;
  private Image avatar;
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

  public Image getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    try {
      this.avatar = ImageIO.read(new File(avatar));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }
}
