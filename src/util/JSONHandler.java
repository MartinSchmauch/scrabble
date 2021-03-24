package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.GameSettings;
import mechanic.Field;
import mechanic.Letter;
import mechanic.Player;


// ** @author ldreyer
public class JSONHandler {

  private ObjectMapper objectMapper;

  public JSONHandler() {
    this.objectMapper = new ObjectMapper();
  }

  private Player loadPlayerProfile(String path) {
    BufferedReader br = null;
    Player player = null;

    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }

    StringBuffer json = new StringBuffer();
    String line;

    try {
      while ((line = br.readLine()) != null) {
        json.append(line);
      }
      br.close();

      JsonNode jsonNode = objectMapper.readTree(json.toString());

      player = new Player(jsonNode.get("playerId").asText(), jsonNode.get("password").asText(),
          jsonNode.get("nickname").asText(), jsonNode.get("avatar").asText(),
          jsonNode.get("volume").asInt());
    } catch (IOException e2) {
      e2.printStackTrace();
    }

    return player;
  }


  private boolean savePlayerProfile(String path, Player player) {
    try {
      objectMapper.writeValue(new File(path), player);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private GameSettings loadGameSettings(String path) {
    BufferedReader br = null;
    GameSettings settings = null;

    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }

    StringBuffer json = new StringBuffer();
    String line;

    try {
      while ((line = br.readLine()) != null) {
        json.append(line);
      }
      br.close();

      JsonNode jsonNode = objectMapper.readTree(json.toString());
      JsonNode letterNode;

      HashMap<Letter, Integer> letters = new HashMap<Letter, Integer>();
      while (jsonNode.get("letters").elements().hasNext()) {
        letterNode = jsonNode.get("letters").elements().next();
        letters.put(new Letter(letterNode.get("letter").asText().charAt(0),
            letterNode.get("value").asInt()), letterNode.get("count").asInt());
      }
      JsonNode fieldNode;
      int wordMultiplier, letterMultiplier;
      List<Field> specialFields = new ArrayList<Field>();

      while (jsonNode.get("specialFields").elements().hasNext()) {
        wordMultiplier = 1;
        letterMultiplier = 1;
        fieldNode = jsonNode.get("specialFields").elements().next();

        if (fieldNode.has("WordMultiplier")) {
          wordMultiplier = fieldNode.get("WordMultiplier").asInt();
        }
        if (fieldNode.has("LetterMultiplier")) {
          letterMultiplier = fieldNode.get("LetterMultiplier").asInt();
        }

        specialFields.add(new Field(letterMultiplier, wordMultiplier, fieldNode.get("x").asInt(),
            fieldNode.get("y").asInt()));
      }

      settings = new GameSettings(jsonNode.get("timePerPlayer").asInt(),
          jsonNode.get("maxOvertime").asInt(), jsonNode.get("maxScore").asInt(),
          jsonNode.get("gameBoardSize").asInt(), jsonNode.get("dictionary").asText(),
          jsonNode.get("AIdifficulty").asText(), jsonNode.get("gameCountdown").asInt(),
          jsonNode.get("bingo").asInt(), letters, specialFields);

    } catch (IOException e2) {
      e2.printStackTrace();
    }

    return settings;
  }
}
