package mechanic;

// ** @author ldreyer

public class Player {
  private final String ID;
  private String nickname;
  private String avatar;
  private int volume;
  private Tile[] rack;
  static final int RACK_FIELDS = 7;


  public Player(String id, String nickname, String avatar, int volume) {
    this.ID = id;
    this.nickname = nickname;
    this.avatar = avatar;

    this.volume = volume;

    this.rack = new Tile[RACK_FIELDS];
  }


  public Tile[] getRack() {
    return rack;
  }


  public void setRack(Tile[] rack) {
    this.rack = rack;
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
    return ID;
  }

}
