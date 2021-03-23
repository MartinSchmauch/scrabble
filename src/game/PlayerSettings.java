package game;

public class PlayerSettings {
  private String avatar;
  private String nickname;
  private boolean soundsOn;

  public PlayerSettings(String avatar, String nickname, boolean soundsOn) {
    this.avatar = avatar;
    this.nickname = nickname;
    this.soundsOn = soundsOn;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public boolean isSoundsOn() {
    return soundsOn;
  }

  public void setSoundsOn(boolean soundsOn) {
    this.soundsOn = soundsOn;
  }

}
