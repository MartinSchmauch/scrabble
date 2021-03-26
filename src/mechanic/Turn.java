// ** @author lurny

package mechanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Turn {
  private Tile[] laydDownTiles;
  private Word[] words; // Array, that contains all Words, that result from the lay down letters
  private int turnScore;

  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");

  private static File file =
      new File(baseDir + "Collins Scrabble Words (2019) with definitions.txt");


  public Turn(Tile[] laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
  }


  // calculate all Words, that result from the lay down letters
  public void calculateWords() {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

      Pattern p = Pattern.compile("\\w*");
      String line;
      while ((line = br.readLine()) != null) {
        Matcher m = p.matcher(line);
        if (!line.isEmpty()) {
          m.find();
          String w = line.substring(m.regionStart(), m.end());
          String explanation = line.substring(m.end() + 1);
          System.out.println(w + " " + explanation);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // calculate the Score resulting from all emerging Words
  public void calculateTurnScore() {

    // calculate word score
    for (Word w : this.words) {
      int localWordMultiplier = 1;
      int singleWordScore = 0;

      for (Tile t : w.getTiles()) {
        singleWordScore += t.getValue() * t.getField().getLetterMultiplier();
        localWordMultiplier = localWordMultiplier * t.getField().getWordMultiplier();
      }
      singleWordScore = singleWordScore * localWordMultiplier;
      this.turnScore = this.turnScore + singleWordScore;
    }

    // set all multipliers to 1
    for (Word w : words) {
      for (Tile t : w.getTiles()) {
        t.getField().setLetterMultiplier(1);
        t.getField().setWordMultiplier(1);
      }
    }
  }


  public void setLaydDownLetters(Tile[] laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
  }

  public Tile[] getLaydDownTiles() {
    return laydDownTiles;
  }

  public Word[] getWords() {
    return words;
  }


  public void setWords(Word[] words) {
    this.words = words;
  }

  public int getTurnScore() {
    return turnScore;
  }


  public void setTurnScore(int turnScore) {
    this.turnScore = turnScore;
  }



}

