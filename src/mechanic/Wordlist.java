package mechanic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;

public class Wordlist {
  
  HashSet<WordOnList> words;
  
  public Wordlist(File file) {
    words = new HashSet<WordOnList>();
    String currentString;
    String[] currentSplittedString;
    WordOnList currentWord;
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
      while ((currentString = br.readLine()) != null) {
        System.out.println(currentString);
        currentSplittedString = currentString.split("^\\S*");
        System.out.println(currentSplittedString);
        currentWord = new WordOnList(currentSplittedString[0], currentSplittedString[1]);
        words.add(currentWord);
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    //File file = new File(".." + File.separator + ".." + File.separator + "resources" + File.separator + "CollinsScrabbleWords.txt");
    File file = new File("resources" + File.separator + "CollinsScrabbleWords.txt");
    Wordlist wordlist = new Wordlist(file);
  }
}
