package util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import game.GameSettings;
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

  /**
   * The constructor initializes an object mapper for further json handling.
   */

  public JsonHandler() {
    this.objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
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
            fieldNode.get("xCoordinate").asInt(), fieldNode.get("yCoordinate").asInt()));
      }

      GameSettings.setTimePerPlayer(jsonNode.get("timePerPlayer").asInt());
      GameSettings.setMaxOvertime(jsonNode.get("maxOvertime").asInt());
      GameSettings.setMaxScore(jsonNode.get("maxScore").asInt());
      GameSettings.setDictionary(jsonNode.get("dictionary").asText());
      GameSettings.setBingo(jsonNode.get("bingo").asInt());
      GameSettings.setAiDifficulty(jsonNode.get("difficulty").asText());
      GameSettings.setLetters(letters);
      GameSettings.setSpecialFields(specialFields);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method writes all defined GameSettings to a Json File from the static GameSettings class.
   */

  public void saveGameSettings(String path) {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    ObjectNode rootNode = factory.objectNode();

    rootNode.put("timePerPlayer", GameSettings.getTimePerPlayer());
    rootNode.put("maxOvertime", GameSettings.getMaxOvertime());
    rootNode.put("maxScore", GameSettings.getMaxScore());
    rootNode.put("dictionary", GameSettings.getDictionary());
    rootNode.put("bingo", GameSettings.getBingo());
    rootNode.put("difficulty", GameSettings.getAiDifficulty());

    ObjectNode letter = factory.objectNode();
    ObjectNode letterInfo;

    for (Letter l : GameSettings.getLetters().values()) {
      letterInfo = factory.objectNode();
      letterInfo.put("letterValue", l.getLetterValue());
      letterInfo.put("count", l.getCount());
      letter.set(l.getCharacter() + "", letterInfo);
    }
    rootNode.set("letters", letter);


    ArrayNode specialFields = factory.arrayNode();
    ObjectNode field = factory.objectNode();

    for (Field f : GameSettings.getSpecialFields()) {
      field = factory.objectNode();
      field.put("xCoordinate", f.getxCoordinate());
      field.put("yCoordinate", f.getyCoordinate());
      field.put("wordMultiplier", f.getWordMultiplier());
      field.put("letterMultiplier", f.getLetterMultiplier());
      specialFields.add(field);
    }

    rootNode.set("specialFields", specialFields);

    FileWriter fileWriter;
    try {
      fileWriter = new FileWriter(path);
      fileWriter.write(rootNode.toPrettyString());
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
}

