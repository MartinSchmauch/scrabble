// ** @author lurny

package mechanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Turn {
  private List<Tile> laydDownTiles;
  private List<Word> words; // Array, that contains all words, that result from the lay down letters
  private int turnScore;
  private static String baseDir = System.getProperty("user.dir")
      + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator");
  private static File file =
      new File(baseDir + "Collins Scrabble Words (2019) with definitions.txt");


  public Turn(List<Tile> laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
    this.words = new ArrayList<Word>();
  }


  // calculate all Words, that result from the layed down letters
  public boolean calculateWords() {
	//wordTiles describes the Tiles that build the word
	List<Tile> wordTiles = new ArrayList<Tile>();
	
	for(Tile t: this.laydDownTiles) {
		//find Top Letter
		while(t.getTopTile() != null) {
			t= t.getTopTile();
		}
		//Go from Top to Bottom to build word
		wordTiles.add(t);
		while(t.getBottomTile() != null) {
			t = t.getBottomTile();
			wordTiles.add(t);
		}
		
		//Check if Word is larger than two characters
		if(wordTiles.size()>=2) {
			if(this.checkIfWordAlreadyExists(wordTiles) == false) {
				//Make Deep Copy of ArrayList wordTiles
				List<Tile> helpList =  new ArrayList<Tile>();
				for(Tile element: wordTiles) {
					helpList.add(element);
				}
				this.words.add(new Word(helpList));
			}
		}
		wordTiles.clear();
	}
	
	wordTiles.clear();
	for(Tile t: this.laydDownTiles) { 
		
		//find leftest Letter
		while(t.getLeftTile() != null) {
			t= t.getLeftTile();
		}
		//Go from left to right to build word
		wordTiles.add(t);
		while(t.getRightTile() != null) {
			t = t.getRightTile();
			wordTiles.add(t);
		}
		if(wordTiles.size()>=2) {
			if(this.checkIfWordAlreadyExists(wordTiles) == false) {
				//Make Deep Copy of ArrayList wordTiles
				List<Tile> helpList =  new ArrayList<Tile>();
				for(Tile element: wordTiles) {
					helpList.add(element);
				}
				this.words.add(new Word(helpList));
			}
		}	
		wordTiles.clear();
	}
	
	
	
	//veryfy words
	boolean help2=false;
	if(words != null) {
		for(Word tileList: this.words) {
			
			//String representation of the ArrayList "tileList"
			String wordString = tileList.toString();
			
		    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		      Pattern p = Pattern.compile("\\w*");
		      String line;
		      while ((line = br.readLine()) != null) {
		        Matcher m = p.matcher(line);
		        if (!line.isEmpty()) {
		          m.find();
		          String w = line.substring(m.regionStart(), m.end());
		          if(wordString.equalsIgnoreCase(w)) {
		        	  help2 = true;
		          }
		        }
		      }
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    if(help2 == false) {
		    	return help2;
		    }
		    
		}
		return true;
	}
	return false;
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
  
  //Methods checks, if a new Word already exists in the List Turn.words
  public boolean checkIfWordAlreadyExists(List<Tile> wordTiles) {
	  boolean check = false;
	  for(Word w: this.words) {
		  if(w.getTiles().size() == wordTiles.size()) {
			  check = true;
			  for(int i=0; i<wordTiles.size(); i++) {
				  if(w.getTiles().get(i).getField().getxCoordinate() == wordTiles.get(i).getField().getxCoordinate()
					&& w.getTiles().get(i).getField().getyCoordinate() == wordTiles.get(i).getField().getyCoordinate()) {
					  check = check & true;
				  }
				  else {
					  check = false;
				  }
			  }
			  if(check) {
				  return check;
			  }
		  }
	  }
	  return check;
  }


  public void setLaydDownLetters(List<Tile> laydDownTiles) {
    this.laydDownTiles = laydDownTiles;
  }

  public List<Tile> getLaydDownTiles() {
    return laydDownTiles;
  }

  public List<Word> getWords() {
    return words;
  }


  public int getTurnScore() {
    return turnScore;
  }


  public void setTurnScore(int turnScore) {
    this.turnScore = turnScore;
  }



}
