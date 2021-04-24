package util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.GameSettings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mechanic.Field;
import mechanic.Letter;
import mechanic.Player;

/**
 * This class uses the ObjectMapper of the Jackson library to initialize Java objects from a Json
 * file and saving Java files to a Json.
 *
 * @author ldreyer
 */

public class JsonHandler {

  private ObjectMapper objectMapper;

  public JsonHandler() {
    this.objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /** This method loads a player profile from a file. */

  public Player loadPlayerProfile(String path) {
    Player player = null;
    try {
      player = objectMapper.readValue(new File(path), Player.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return player;
  }

  /** This method saves a player profile to a file. */

  public boolean savePlayerProfile(String path, Player player) {
    try {
      objectMapper.writeValue(new File(path), player);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }


  /**
   * This method reads all defined GameSettings from a Json File to into the static GameSettings
   * class.
   */

  public void loadGameSettings(String path) {
    try {
      JsonNode jsonNode = objectMapper.readTree(new File(path));

      Map.Entry<String, JsonNode> letterNode;

      HashMap<Character, Letter> letters = new HashMap<Character, Letter>();
      Iterator<Entry<String, JsonNode>> nodes = jsonNode.get("letters").fields();
      
      while (nodes.hasNext()) {
        letterNode = (Map.Entry<String, JsonNode>) nodes.next();

        letters.put(letterNode.getKey().charAt(0),
            new Letter(letterNode.getKey().charAt(0),
                letterNode.getValue().get("letterValue").asInt(),
                letterNode.getValue().get("count").asInt()));
      }

      JsonNode fieldNode;
      int wordMultiplier;
      int letterMultiplier;
      List<Field> specialFields = new ArrayList<Field>();

      Iterator<JsonNode> it = jsonNode.get("specialFields").elements();
      while (it.hasNext()) {
        wordMultiplier = 1;
        letterMultiplier = 1;
        fieldNode = it.next();

        if (fieldNode.has("wordMultiplier")) {
          wordMultiplier = fieldNode.get("wordMultiplier").asInt();
        }
        if (fieldNode.has("letterMultiplier")) {
          letterMultiplier = fieldNode.get("letterMultiplier").asInt();
        }

        specialFields.add(new Field(letterMultiplier, wordMultiplier,
            fieldNode.get("xCoordinate").asInt(),
            fieldNode.get("yCoordinate").asInt()));
      }

      GameSettings.setTimePerPlayer(jsonNode.get("timePerPlayer").asInt());
      GameSettings.setMaxOvertime(jsonNode.get("maxOvertime").asInt());
      GameSettings.setMaxScore(jsonNode.get("maxScore").asInt());
      GameSettings.setDictionary(jsonNode.get("dictionary").asText());
      GameSettings.setBingo(jsonNode.get("bingo").asInt());
      GameSettings.setLetters(letters);
      GameSettings.setSpecialFields(specialFields);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void saveGameSettings(String path) {
    GameSettings settings = new GameSettings();
    try {
      objectMapper.writeValue(new File(path), settings);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



}
