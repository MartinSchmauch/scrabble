package mechanic;

// ** @author ldreyer

public class Player {
  private String id;
  private String password;
  private String nickname;
  private String avatar;
  private int volume;

  public Player(String id, String password, String nickname, String avatar, int volume) {
    this.id = id;
    this.password = password;
    this.nickname = nickname;
    this.avatar = avatar;
    this.volume = volume;
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getID() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
