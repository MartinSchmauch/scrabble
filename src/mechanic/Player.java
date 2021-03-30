package mechanic;


import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/** @author ldreyer */

public class Player {

  private PlayerData info;
  private int volume;

  @JsonIgnore
  private Field[] rack;
  public String customGameSettings;

  static final int TILE_COUNT_PER_PLAY = 7;
  static final int RACK_FIELDS = 12;

  public Player(String nickname) {
    this.info = new PlayerData(nickname);

    this.rack = new Field[RACK_FIELDS];
    for (int i = 0; i < RACK_FIELDS; i++) {
      this.rack[i] = new Field(i, -1);
    }
  }

  @JsonCreator
  public Player(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume) {
    info = new PlayerData(nickname);
    info.setAvatar(avatar);
    this.volume = volume;

    this.rack = new Field[RACK_FIELDS];
    for (int i = 0; i < RACK_FIELDS; i++) {
      this.rack[i] = new Field(i, -1);
    }
  }

  /*
   * PLAYER INFO
   */

  public PlayerData getPlayerInfo() {
    return this.info;
  }

  public void setNickname(String nickname) {
    this.info.setNickname(nickname);
  }

  public String getNickname() {
    return this.info.getNickname();
  }

  public void setAvatar(String avatar) {
    this.info.setAvatar(avatar);
  }

  public String getAvatar() {
    return this.info.getAvatar();
  }



  /*
   * RACK METHODS
   */

  public Tile getRackTile(int index) {
    return this.rack[index].getTile();
  }

  public void setRackTile(int index, Tile tile) {
    this.rack[index].setTile(tile);
  }

  @JsonIgnore
  public Field getFreeRackField() {
    int i = 0;
    while (rack[i].getTile() != null) {
      i++;
    }

    return rack[i];
  }

  public void addTileToRack(Tile t) {
    t.setField(getFreeRackField());
    t.setOnRack(true);
  }

  public Tile removeRackTile(int index) {
    Tile tile = this.rack[index].getTile();
    this.rack[index].setTile(null);

    return tile;
  }

  @JsonIgnore
  public List<Tile> getRackTiles() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < rack.length; i++) {
      if (rack[i].getTile() != null) {
        tiles.add(rack[i].getTile());
      }
    }
    return tiles;
  }

  @JsonIgnore
  public int getTileCountOnRack() {
    return this.getRackTiles().size();
  }

  public boolean reorganizeRackTile(int indexBefore, int indexAfter) {
    if (rack[indexAfter].getTile() != null) {
      return false;
    }

    rack[indexAfter].setTile(removeRackTile(indexBefore));
    return true;
  }


  /*
   * PLAYER SETTINGS
   */

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public String getCustomGameSettings() {
    return customGameSettings;
  }

  public void setCustomGameSettings(String customGameSettings) {
    this.customGameSettings = customGameSettings;
  }


}
