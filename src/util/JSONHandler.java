package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.GameSettings;
import mechanic.Field;
import mechanic.Letter;
import mechanic.Player;

/** @author ldreyer */

public class JSONHandler {

	private ObjectMapper objectMapper;

	public JSONHandler() {
		this.objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public Player loadPlayerProfile(String path) {
		Player player = null;
		try {
			player = objectMapper.readValue(new File(path), Player.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return player;
	}

	public boolean savePlayerProfile(String path, Player player) {
		try {
			objectMapper.writeValue(new File(path), player);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public GameSettings loadGameSettings(String path) {
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

			HashMap<Character, Letter> letters = new HashMap<Character, Letter>();
			Iterator<JsonNode> it = jsonNode.get("letters").elements();
			while (it.hasNext()) {
				letterNode = it.next();
				letters.put(letterNode.get("letter").asText().charAt(0),
						new Letter(letterNode.get("letter").asText().charAt(0), letterNode.get("value").asInt(),
								letterNode.get("count").asInt()));
			}
			JsonNode fieldNode;
			int wordMultiplier, letterMultiplier;
			List<Field> specialFields = new ArrayList<Field>();

			it = jsonNode.get("specialFields").elements();
			while (it.hasNext()) {
				wordMultiplier = 1;
				letterMultiplier = 1;
				fieldNode = it.next();

				if (fieldNode.has("WordMultiplier")) {
					wordMultiplier = fieldNode.get("WordMultiplier").asInt();
				}
				if (fieldNode.has("LetterMultiplier")) {
					letterMultiplier = fieldNode.get("LetterMultiplier").asInt();
				}

				specialFields.add(new Field(letterMultiplier, wordMultiplier, fieldNode.get("x").asInt(),
						fieldNode.get("y").asInt()));
			}

			GameSettings.setTimePerPlayer(jsonNode.get("timePerPlayer").asInt());
			GameSettings.setMaxOvertime(jsonNode.get("maxOvertime").asInt());
			GameSettings.setMaxScore(jsonNode.get("maxScore").asInt());
			GameSettings.setGameBoardSize(jsonNode.get("gameBoardSize").asInt());
			GameSettings.setDictionary(jsonNode.get("dictionary").asText());
			GameSettings.setAiDifficulty(jsonNode.get("AIdifficulty").asText());
			GameSettings.setGameCountdown(jsonNode.get("gameCountdown").asInt());
			GameSettings.setBingo(jsonNode.get("bingo").asInt());
			GameSettings.setLetters(letters);
			GameSettings.setSpecialFields(specialFields);

		} catch (IOException e2) {
			e2.printStackTrace();
		}

		return settings;
	}
}
