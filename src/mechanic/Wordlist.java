package mechanic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author pkoenig
 *
 */
public class Wordlist {

  private HashSet<WordOnList> words;
  private File wordlistFile;

  public Wordlist(File file) {
    words = new HashSet<WordOnList>();
    String currentLine;
    String[] currentSplittedString;
    WordOnList currentWord;
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
      while ((currentLine = br.readLine()) != null) {
        currentSplittedString = currentLine.split("\\W", 2);
        currentWord = new WordOnList(currentSplittedString[0], currentSplittedString[1]);
        words.add(currentWord);
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.wordlistFile = file;
  }

  /**
   * @return the wordlistFile
   */
  public File getWordlistFile() {
    return wordlistFile;
  }

  /**
   * @param wordlistFile the wordlistFile to set
   */
  public void setWordlistFile(File wordlistFile) {
    this.wordlistFile = wordlistFile;
  }

  /**
   * @return the words
   */
  public HashSet<WordOnList> getWords() {
    return words;
  }
  
  /**
   * @param length
   * @return words, which are length long
   */
  public HashSet<WordOnList> getWordsWithLength(int length) {
    HashSet<WordOnList> result = new HashSet<WordOnList>() ;
    for (WordOnList word : this.words) {
      if (word.getWordString().length() == length) {
        result.add(word);
        //System.out.println(word.toString());
      }
    }
    //System.out.println("\n### -------------- ###\n");
    return result;
  }

  /**
   * @param words the words to set
   */
  public void setWords(HashSet<WordOnList> words) {
    this.words = words;
  }

  public void addWordToMemory(String word, String definition) {
    this.words.add(new WordOnList(word, definition));
  }

  public void addWordToMemoryAndToWordFile(String word, String definition) {
    // TODO not yet implemented
  }

  public static void main(String[] args) {
    // File file = new File(".." + File.separator + ".." + File.separator + "resources" +
    // File.separator + "CollinsScrabbleWords.txt");
    File file = new File("resources" + File.separator + "CollinsScrabbleWords.txt");
    Wordlist wordlist = new Wordlist(file);
  }
}
