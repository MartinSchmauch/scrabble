package game;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

// ** @author ldreyer

public class Player {
  private final String playerId;
  private String password;
  private String nickname;
  private String avatar;

  public Player(String playerId, String password, String config) {
    this.playerId = playerId;
    this.password = password;

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      PlayerSettings settings = objectMapper.readValue(config, PlayerSettings.class);
      this.nickname = settings.getNickname();
      this.avatar = settings.getAvatar();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    return playerId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
