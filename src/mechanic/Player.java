package mechanic;

import java.util.ArrayList;
import java.util.List;

/** @author ldreyer */

public class Player {
  private static int totalPlayerCount = 1;
  private final int ID;
  private String nickname;
  private String avatar;
  private int volume;

  private Field[] rack;
  public String customGameSettings;

  static final int TILE_COUNT_PER_PLAY = 7;
  static final int RACK_FIELDS = 12;

  public Player(String nickname) {
    this.ID = totalPlayerCount;
    totalPlayerCount++;
    this.nickname = nickname;

    this.rack = new Field[RACK_FIELDS];
    for (int i = 0; i < RACK_FIELDS; i++) {
      this.rack[i] = new Field(i, -1);
    }
  }

  public Player(String nickname, String avatar, int volume) {
    this.ID = totalPlayerCount;
    totalPlayerCount++;
    this.nickname = nickname;
    this.avatar = avatar;

    this.volume = volume;

    this.rack = new Field[RACK_FIELDS];
    for (int i = 0; i < RACK_FIELDS; i++) {
      this.rack[i] = new Field(i, -1);
    }
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

  public List<Tile> getRackTiles() {
    List<Tile> tiles = new ArrayList<Tile>();
    for (int i = 0; i < rack.length; i++) {
      if (rack[i].getTile() != null) {
        tiles.add(rack[i].getTile());
      }
    }
    return tiles;
  }

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
   * VOLUME GETTER SETTER
   */

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  /*
   * PROFILE ATTRIBUTES
   */

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

  public String getCustomGameSettings() {
    return customGameSettings;
  }

  public void setCustomGameSettings(String customGameSettings) {
    this.customGameSettings = customGameSettings;
  }


  public int getID() {
    return ID;
  }

}
