package mechanic;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.GameSettings;
import gui.GamePanelController;
import javafx.application.Platform;
import network.client.ClientProtocol;
import network.messages.AddTileMessage;
import network.messages.RemoveTileMessage;
import network.server.Server;

/**
 * The Player Class includes PlayerData, containing nickname and avatar. Local GameSettings that
 * should be loaded automatically like volume settings are Player attributes. Also a player's rack
 * is kept local and managed with attributes and methods of this class. A player object can be read
 * from and stored to a JSON file, ignoring the rack methods and attributes.
 * 
 * @author ldreyer
 */

public class Player {

  private PlayerData info;

  private int volume;
  private String customGameSettings;

  @JsonIgnore
  private Field[] rack;
  @JsonIgnore
  private ClientProtocol client = null;
  @JsonIgnore
  private Server server = null;
  @JsonIgnore
  private GamePanelController gpc = null;

  static final int TILE_COUNT_PER_PLAY = 7;
  static final int RACK_FIELDS = 12;

  public Player(String nickname) {
    this.info = new PlayerData(nickname);

    this.rack = new Field[RACK_FIELDS];
    for (int i = 0; i < RACK_FIELDS; i++) {
      this.rack[i] = new Field(i, -1);
    }
  }

  /**
   * This constructor is used for initializing a Player Object with an ObjectMapper from a JSON
   * file.
   */

  @JsonCreator
  public Player(@JsonProperty("nickname") String nickname, @JsonProperty("avatar") String avatar,
      @JsonProperty("volume") int volume, @JsonProperty("gameCount") int gameCount,
      @JsonProperty("bestTurn") int bestTurn, @JsonProperty("score") int score,
      @JsonProperty("bestWord") String bestWord, @JsonProperty("playTime") int playTime,
      @JsonProperty("wins") int wins, @JsonProperty("playedTiles") int playedTiles) {
    info = new PlayerData(nickname);
    info.setAvatar(avatar);
    this.volume = volume;

    info.setStatistics(gameCount, bestTurn, bestWord, score, playTime, wins, playedTiles);
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

  public PlayerStatistics getStatistics() {
    return this.info.getPlayerStatistics();
  }

  public int getGameCount() {
    return getStatistics().getGameCount();
  }

  public int getBestTurn() {
    return this.getStatistics().getBestTurn();
  }

  public String getBestWord() {
    return this.getStatistics().getBestWord();
  }

  public int getPlayTime() {
    return this.getStatistics().getPlayTime();
  }

  public int getScore() {
    return this.getStatistics().getScore();
  }

  public int getWins() {
    return this.getStatistics().getWins();
  }

  public int getPlayedTiles() {
    return this.getStatistics().getPlayedTiles();
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

  /**
   * This method searches the rack for the first field, that is not covered by a tile.
   * 
   * @return emptyField
   */

  @JsonIgnore
  public Field getFreeRackField() { // rack koordinate fortlaufen in xCoordinate
    int i = 0;
    while (rack[i].getTile() != null) {
      i++;
    }

    return rack[i];
  }

  @JsonIgnore
  public Field getRackField(int index) {
    return rack[index];
  }

  public void setRackTileToNone(int index) {
    this.rack[index].setTileOneDirection(null);
  }

  /**
   * This method takes a tile and puts it on a free field on the player's rack.
   * 
   */
  public void addTileToRack(Tile tile) {
    tile.setField(getFreeRackField());
    tile.setOnRack(true);
    tile.setOnGameBoard(false);
  }

  /**
   * This method adds a List of tileson the player's rack.
   * 
   * @author lurny
   */
  public void addTilesToRack(List<Tile> tileList) {
    for (Tile t : tileList) {
      addTileToRack(t);
    }
  }

  public Tile removeRackTile(int index) {
    Tile tile = rack[index].getTile();
    rack[index].setTileOneDirection(null);
    return tile;
  }

  /**
   * This method creates a list of all tiles on the rack, ignoring empty fields.
   * 
   * @return List<Tile>
   */

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

  /**
   * Takes indices of two rack fields and moves the tile from the before-index to the after-index.
   * If the operation was successful the method returns true.
   */

  public void reorganizeRackTile(int indexBefore, int indexAfter) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        if (rack[indexBefore].getTile() == null || rack[indexAfter].getTile() != null) {
          gpc.indicateInvalidTurn(getNickname(), "Invalid Field Selection.");
          return;
        }
        Tile t = rack[indexBefore].getTile();
        removeRackTile(indexBefore);
        setRackTile(indexAfter, t);
        t.setField(rack[indexAfter]);
        gpc.moveToRack(t, indexBefore, -1);
      }
    });
  }


  public void moveToRack(Tile tile, int newIndex) {
    if (newIndex == -1 && getFreeRackField() != null) {
      newIndex = getFreeRackField().getxCoordinate();
    }

    if (newIndex == -1 || rack[newIndex].getTile() != null) {
      gpc.indicateInvalidTurn(this.getNickname(), "Field on Rack not free.");
      return;
    }

    RemoveTileMessage rtm = new RemoveTileMessage(this.getNickname(),
        tile.getField().getxCoordinate(), tile.getField().getyCoordinate());

    if (this.isHost()) {
      server.sendToAll(rtm);
      server.getGameController().removeTileFromGameBoard(this.getNickname(),
          tile.getField().getxCoordinate(), tile.getField().getyCoordinate());

      tile.setField(getRackField(newIndex));
      tile.setOnRack(true);
      tile.setOnGameBoard(false);
      AddTileMessage atm = new AddTileMessage(this.getNickname(), tile, newIndex, -1);
      server.updateServerUi(atm);
    } else {
      client.sendToServer(rtm);
      gpc.removeTile(rtm.getX(), rtm.getY(), (rtm.getY() == -1));
      tile.setField(getRackField(newIndex));
      tile.setOnRack(true);
      tile.setOnGameBoard(false);
      gpc.addTile(tile);
    }
  }


  public void moveToGameBoard(int oldIndex, int newX, int newY) {
    Tile t = rack[oldIndex].getTile();
    if (t == null) {
      gpc.indicateInvalidTurn(this.getNickname(), "Selcted field on Rack is empty.");
      return;
    }

    AddTileMessage atm = new AddTileMessage(this.getNickname(), t, newX, newY);

    if (this.isHost()) {
      server.handleAddTileToGameBoard(atm);
    } else {
      client.sendToServer(atm);
    }
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

  /*
   * NETWORK
   */

  public boolean isHost() {
    return this.info.isHost();
  }

  public void setHost(boolean host) {
    this.info.setHost(host);
  }

  public void setServer(Server s) {
    this.server = s;
  }

  public Server getServer() {
    return this.server;
  }

  public ClientProtocol getClientProtocol() {
    return this.client;
  }

  @JsonIgnore
  public void setGamePanelController(GamePanelController gpc) {
    this.gpc = gpc;
  }

  /** @author nilbecke */

  public void host() {

    this.getPlayerInfo().setHost(true);
    this.server = new Server(this, this.customGameSettings);

    Runnable r = new Runnable() {
      public void run() {
        server.listen();
      }
    };
    new Thread(r).start();
  }

  /** @author nilbecke */

  public void connect(String ip) {
    this.getPlayerInfo().setHost(false);

    this.client = new ClientProtocol(ip, GameSettings.port, this);

    if (this.client.isOK()) {
      this.client.start();

    }
  }

}
