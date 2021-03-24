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
  private Letter[] laydDownLetters;
  private Word[] words; // Array, that contains all Words, that result from the lay down letters
  private int turnScore;
  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");

  private static File file =
      new File(baseDir + "Collins Scrabble Words (2019) with definitions.txt");

  // calculate all Words, that result from the lay down letters
  public void calculateWords() {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

      Pattern p = Pattern.compile("\\w");
      String line;
      while ((line = br.readLine()) != null) {
        Matcher m = p.matcher(line);
        m.find();
        String s = line.substring(m.regionStart(), m.end());
        System.out.println(s);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // calculate the Score resulting from all emerging Words
  public void calculateTurnScore(Turn turn) {

    // calculate word score
    for (Word w : words) {
      int localWordMultiplier = 1;
      int singleWordScore = 0;
      for (Letter l : w.getLetters()) {
        singleWordScore += l.getLetterValue() * l.getField().getLetterMultiplier();
        localWordMultiplier = localWordMultiplier * l.getField().getWordMultiplier();
      }
      singleWordScore = singleWordScore * localWordMultiplier;
      turn.turnScore = turn.turnScore + singleWordScore;
    }

    // set all multipliers to 1
    for (Word w : words) {
      for (Letter l : w.getLetters()) {
        l.getField().setLetterMultiplier(1);
        l.getField().setWordMultiplier(1);
      }
    }
  }


  public void setLaydDownLetters(Letter[] laydDownLetters) {
    this.laydDownLetters = laydDownLetters;
  }

  public Letter[] getLaydDownLetters() {
    return laydDownLetters;
  }
  /*
   * Field l2, l3, l4, l5; Field l1 = new Field(1, 2, 1, 1); l2 = new Field(2, 1, 1, 1); l3 = new
   * Field(1, 1, 1, 1); l4 = new Field(1, 1, 1, 1); l5 = new Field(1, 1, 1, 1);
   */

}

