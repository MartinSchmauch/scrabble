package mechanic;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class WordListTest {

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testgetWordsWithLength() {
    File file = new File("resources" + File.separator + "CollinsScrabbleWords.txt");
    Wordlist wordlist = new Wordlist(file);
    wordlist.getWordsWithLength(1);
    wordlist.getWordsWithLength(2);
  }

}
